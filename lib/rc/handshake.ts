/**
 * This module handles the initial handshake (i.e. authentication
 * and session allocation) on the remote console port. After the
 * handshake completes, `telnet` and `video` would be used.
 */
/** */

import { Duplex } from 'stream'
import { once } from 'events'

import { Features, RemoteConsoleInfo } from './rest'


/**
 * Commands sent to server are a 16-bit LE followed by optional arguments
 * The 8 upper bits seem to have flags; the 8 lower bits seem to be
 * an ASCII character defining command
 */
export function formatCommand(command: number, args?: Uint8Array) {
    const buf = Buffer.alloc(2)
    buf.writeUInt16LE(command, 0)
    return args ? Buffer.concat([buf, args]) : buf
}

export enum Command {
    RC_CONNECT = 0x2001,
    CMD_CONNECT = 0x2002,
    SEIZE_CONNECTION = 0x0055,
    SHARE_CONNECTION = 0x0056,

    REFRESH_SCREEN = 0x0005,
    ACK = 0x000c,
}

export enum ServerStatus {
    HELLO              = 80, // 'P'
    ACCESS_DENIED      = 81, // 'Q'
    OK                 = 82, // 'R'
    BUSY_1             = 83, // 'S'

    NO_LICENSE         = 87, // 'W'
    NO_FREE_SESSIONS   = 88, // 'X'
    BUSY_2             = 89, // 'Y'
}

/**
 * Negotiates a console / command connection.
 * 
 * Returns if correct (afterwards you can start the `telnet`
 * receiver) or throws error.
 * 
 * If the server returns busy, the `negotiateBusy` option will be
 * called. It must return a promise resolving to either `'seize'`,
 * `'share'`, or anything else (throws error).
 * 
 * @param cmd `true` if negotiating a command connection, `false` if negotiating a remote console connection.
 * @param socket recently-opened socket (or duplex stream)
 * @param sessionKey 
 * @param rcinfo 
 */
export async function negotiateConnection(cmd: boolean, socket: Duplex, sessionKey: Uint8Array, rcinfo: RemoteConsoleInfo, options?: {
    negotiateBusy?: () => PromiseLike<'share' | 'seize'>
}) {
    const { negotiateBusy } = options || {}
    // Note that encKey is a hex string. We do not store as Buffer
    // because the server mixes case, and that info matters in the XOR.
    const { encKey, optionalFeatures } = rcinfo
    // FIXME: timeout when waiting for responses

    // wait for hello
    const helloByte = await read(socket)
    if (helloByte !== ServerStatus.HELLO)
        throw Error('did not receive hello from server')

    // send command
    let command = cmd ? Command.CMD_CONNECT : Command.RC_CONNECT
    let sessionKeyHex: Uint8Array = Buffer.from(Buffer.from(sessionKey).toString('hex'))

    if (optionalFeatures.has(Features.ENCRYPT_KEY)) {
        sessionKeyHex = sessionKeyHex.map((x, i) => x ^ encKey[i % encKey.length])
        command |= optionalFeatures.has(Features.ENCRYPT_VMKEY) ? 0x4000 : 0x8000
    }

    socket.write(formatCommand(command, sessionKeyHex))

    // process response
    const response = await read(socket)
    if (response === ServerStatus.OK) {
        // authenticated, we're good to go. (start telnet receiver now)
        return
    } else if (cmd) {
        throw Error(`unexpected response ${response}`)
    } else if (response === ServerStatus.BUSY_1
            || response === ServerStatus.BUSY_2) {
        // authenticated but busy, negotiate (i.e. ask user)...
        const negotiateResult = negotiateBusy && await negotiateBusy()
        if (negotiateResult === 'seize') {
            socket.write(formatCommand(Command.SEIZE_CONNECTION))
            const seizeResponse = await read(socket)
            if (seizeResponse === ServerStatus.OK) {
                return true
            } else if (seizeResponse === ServerStatus.ACCESS_DENIED) {
                throw Error('could not seize connection')
            } else {
                throw Error(`unexpected response ${seizeResponse}`)
            }
        } else if (negotiateResult === 'share') {
            socket.write(formatCommand(Command.SHARE_CONNECTION))
            // share doesn't seem to have a response
            return true
        } else {
            throw Error('chose disconnected')
        }
    } else if (response === ServerStatus.ACCESS_DENIED) {
        throw Error('access denied')
    } else if (response === ServerStatus.NO_FREE_SESSIONS) {
        throw Error('no free sessions')
    } else if (response === ServerStatus.NO_LICENSE) {
        throw Error('no license')
    } else {
        throw Error(`unexpected response ${response}`)
    }
}