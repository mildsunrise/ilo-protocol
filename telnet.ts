import { Cipher, createCipheriv } from 'crypto'
import { connect } from 'net'
import { Duplex } from 'stream'
import { once } from 'events'

import { Features, RemoteConsoleInfo, RestAPIClient } from './rest'


/**
 * Outside connection protocol.
 * Has two modes, DVC and normal (telnet?). Starts in DVC mode.
 */
export class Telnet {
    readonly send: (data: Buffer) => void

    decrypter?: DvcCipherSet
    encrypter?: DvcCipherSet

    state: {
        dvcMode: false
        ctr1: number
    } | {
        dvcMode: true
        encryption: DvcEncryption
    }

    constructor(key: Buffer, send: (data: Buffer) => void) {
        this.send = send
        this.state = { dvcMode: true, encryption: DvcEncryption.NONE }
        if (key.length !== 16)
            throw Error('invalid key length...')
        this.decrypter = new DvcCipherSet(key)
        this.encrypter = new DvcCipherSet(key)
    }

    receive(b: number) {
        if (this.state.dvcMode === true) {
            b = this.decryptDvc(b, this.state.encryption)
            if (!this.receiveDvc(b))
                this.state = { dvcMode: false, ctr1: 0 }
        } else if (this.state.dvcMode === false) {
            if (this.state.ctr1 === 0 && b === 0x1B)
                this.state.ctr1++
            else if (this.state.ctr1 === 1 && b === 0x5B) // '['
                this.state.ctr1++
            else if (this.state.ctr1 === 2 && b === 0x52) // 'R'
                this.state = { dvcMode: true, encryption: true }
            else if (this.state.ctr1 === 2 && b === 0x72) // 'r'
                this.state = { dvcMode: true, encryption: DvcEncryption.NONE }
            else
                this.state.ctr1 = 0
        }
        throw new Error()
    }

    sendDvc(data: Buffer) {
        if (this.state.dvcMode !== true)
            throw Error('not in DVC mode')
        this.send(this.encrypter.process(data, this.state.encryption))
    }

    decryptDvc(b: number, enc: DvcEncryption): number {
        return this.decrypter.process(Buffer.of(b), enc)[0]
    }

    /** returns false to end DVC mode */
    receiveDvc(b: number): boolean {
        return true
    }
}


/**
 * Defines the cipher used for encryption / decryption.
 * 
 * The cipher is always used as a **stream cipher**, i.e.
 * block ciphers are used in OFB mode (so decryption and
 * encryption are the same operation). IV is zero.
 */
export enum DvcEncryption {
    NONE = 0,
    RC4,
    AES128,
    AES256,
}

/** Initializes ciphers and calls the appropriate one */
export class DvcCipherSet {
    static mappings: { [key: number]: string } = {
        [DvcEncryption.RC4]: 'rc4',
        [DvcEncryption.AES128]: 'aes-128-ofb',
        [DvcEncryption.AES256]: 'aes-256-ofb',
    }

    readonly ciphers: { [key: number]: Cipher }
    constructor(key: Buffer) {
        this.ciphers = {}
        for (const enc of Object.keys(DvcCipherSet.mappings))
            this.ciphers[enc] = createCipheriv(DvcCipherSet.mappings[enc], key, '')
    }
    process(data: Buffer, enc: DvcEncryption): Buffer {
        if (enc === DvcEncryption.NONE)
            return data
        if (!Object.hasOwnProperty.call(this.ciphers, enc))
            throw Error(`Invalid or unsupported encryption ${enc}`)
        const odata = this.ciphers[enc].update(data)
        if (odata.length !== data.length)
            throw Error(`Unexpected returned length (expected ${data.length}, got ${odata.length})`)
        return odata
    }
}


/**
 * Commands sent to server are a 16-bit LE followed by optional arguments
 * The 8 upper bits seem to have flags; the 8 lower bits seem to be
 * an ASCII character defining command
 */
function formatCommand(command: number, args?: Uint8Array) {
    const buf = Buffer.alloc(2)
    buf.writeUInt16LE(command, 0)
    return args ? Buffer.concat([buf, args]) : buf
}

enum Command {
    RC_CONNECT = 0x2001,
    CMD_CONNECT = 0x2002,
    SEIZE_CONNECTION = 0x0055,
    SHARE_CONNECTION = 0x0056,

    REFRESH_SCREEN = 0x0005,
    ACK = 0x000c,
}

enum ServerStatus {
    HELLO              = 80, // 'P'
    ACCESS_DENIED      = 81, // 'Q'
    OK                 = 82, // 'R'
    BUSY_1             = 83, // 'S'

    NO_LICENSE         = 87, // 'W'
    NO_FREE_SESSIONS   = 88, // 'X'
    BUSY_2             = 89, // 'Y'
}

enum Command2 {
    MOUSE_MOVE = 208,
    BUTTON_PRESS = 209,
    BUTTON_RELEASE = 210,
    BUTTON_CLICK = 211,
    BYTE = 212,
    SET_MODE = 213,
    ENCRYPT = 192,
}

enum Cursor {
    DEFAULT = 0,
    CROSSHAIR,
    NONE,
    SQUARE, // 2x2 gray (#808080) square, origin at (0,0)
    POINTER, // again in gray
}

/**
 * Negotiates a console / command connection.
 * 
 * Returns if correct (afterwards you can start the `telnet`
 * receiver) or throws error.
 * 
 * @param cmd `true` if negotiating a command connection, `false` if negotiating a remote console connection.
 * @param socket recently-opened socket (or duplex stream)
 * @param sessionKey 
 * @param rcinfo 
 */
async function negotiateConnection(cmd: boolean, socket: Duplex, sessionKey: Uint8Array, rcinfo: RemoteConsoleInfo, options?: {
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



// look into setup_decryption and setVideoDecryption

async function main() {
    const { address, username, password } = require('./config.json')

    const client = new RestAPIClient(`https://${address}`)
    await client.loginSession(username, password)

    console.log(`Session key: ${client.sessionKey.toString('hex')}`)
    const rcInfo = await client.getRcInfo()
    console.log(`Encryption key: ${rcInfo.enc_key}`)

    // Set up the console connection
    const rcSocket = await connect({ host: address, port: rcInfo.rc_port })
    rcSocket.setNoDelay(true)
    // code also disables SO_LINGER
    await negotiateConnection(false, rcSocket, client.sessionKey, rcInfo)

    // Set up the command connection
    const cmdSocket = await connect({ host: address, port: rcInfo.rc_port })
    // code also disables SO_LINGER
    await negotiateConnection(true, cmdSocket, client.sessionKey, rcInfo)

    const encKey = Buffer.from(rcInfo.enc_key, 'hex')
    const telnet = new Telnet(encKey, x => rcSocket.write(x))

}
main().catch(e => process.nextTick(() => { throw e }))
