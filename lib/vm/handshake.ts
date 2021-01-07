import { Duplex, Readable } from 'stream'
import { once } from 'events'

import { Features, RemoteConsoleInfo } from '../rest'
import { formatCommand } from '../rc/handshake'

export enum DeviceType {
    FLOPPY = 1,
    CDROM,
    USBKEY,
}

const CONNECT_COMMAND = 0x0010
const targetNotDevice_flag = 0x8000

const OK_RESPONSE = 0x0020
const AUTHFAIL_RESPONSE = 0x0022

async function read(s: Readable, n: number): Promise<Buffer> {
    if (!s.readable)
        throw Error('not readable')
    while (s.readableLength < n)
        await once(s, 'readable')
    return s.read(n) // FIXME: it could return multiple chunks
    // FIXME: implement this correctly
}

/**
 * Negotiates a virtual media connection.
 * 
 * Returns protocol version if correct (afterwards you can start
 * the receiver) or throws error.
 * 
 * Warning: Apparently, `sessionKey` and `rcInfo` must be taken
 * from an already-running remote console session, otherwise AUTHFAIL.
 * 
 * @param socket recently-opened socket (or duplex stream)
 * @param sessionKey 
 * @param rcinfo 
 * @returns `[major, minor]` version tuple
 */
export async function negotiateConnection(socket: Duplex, sessionKey: Uint8Array, rcinfo: RemoteConsoleInfo, options: {
    deviceType: DeviceType
    targetIsDevice?: boolean
}) {
    const { targetIsDevice, deviceType } = options || {}
    if (deviceType !== (deviceType & 0x7F))
        throw Error(`invalid device type ${deviceType}`)

    // Note that encKey is a hex string. We do not store as Buffer
    // because the server mixes case, and that info matters in the XOR.
    const { encKey, optionalFeatures } = rcinfo
    // FIXME: timeout when waiting for responses

    // send command
    let command = CONNECT_COMMAND
    let sessionKeyHex: Uint8Array = Buffer.from(Buffer.from(sessionKey).toString('hex'))
    if (optionalFeatures.has(Features.ENCRYPT_VMKEY))
        sessionKeyHex = sessionKeyHex.map((x, i) => x ^ encKey.charCodeAt(i % encKey.length))
    command |= deviceType << 8
    if (!targetIsDevice)
        command |= targetNotDevice_flag

    socket.write(formatCommand(command, sessionKeyHex))

    // process response
    const response = (await read(socket, 2)).readUInt16LE()
    if (response === OK_RESPONSE) {
        // authenticated, we're good to go. (start telnet receiver now)
        const vBuf = await read(socket, 2)
        return [vBuf[1], vBuf[0]]
    } else {
        throw Error(`unexpected response ${response}`)
    }
}
