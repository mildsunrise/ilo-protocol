import { Cipher, createCipheriv } from 'crypto'

import { truncateBuf } from '../utils'


/**
 * Outer connection protocol.
 * Has two modes, DVC and normal (telnet?). Starts in DVC mode.
 * 
 * This layer basically takes care of encryption / decryption.
 */
export class Telnet {
    protected decrypter?: DvcCipherSet
    protected encrypter?: DvcCipherSet

    protected cipher: DvcEncryption
    protected state: {
        dvcMode: false
        ctr1: number
    } | {
        dvcMode: true
        encryption: boolean
    }

    constructor(key: Buffer) {
        this.cipher = DvcEncryption.NONE
        this.state = { dvcMode: true, encryption: false }
        if (key.length !== 16)
            throw Error('invalid key length...')
        this.decrypter = new DvcCipherSet(key)
        this.encrypter = new DvcCipherSet(key)
    }

    public receive(b: number) {
        if (this.state.dvcMode === true) {
            if (this.state.encryption)
                b = this.decrypter.process(Buffer.of(b), this.cipher)[0]
            const remainDvc = this.receiveDvc(b)
            if (!remainDvc)
                this.state = { dvcMode: false, ctr1: 0 }
        } else if (this.state.dvcMode === false) {
            if (this.state.ctr1 === 0 && b === 0x1B)
                this.state.ctr1++
            else if (this.state.ctr1 === 1 && b === 0x5B) // '['
                this.state.ctr1++
            else if (this.state.ctr1 === 2 && b === 0x52) // 'R'
                this.state = { dvcMode: true, encryption: true }
            else if (this.state.ctr1 === 2 && b === 0x72) // 'r'
                this.state = { dvcMode: true, encryption: false }
            else
                this.state.ctr1 = 0
        } else {
            throw new Error()
        }
    }

    public sendDvc(data: Buffer) {
        if (this.state.dvcMode !== true)
            throw Error('not in DVC mode')
        if (this.state.encryption)
            data = this.encrypter.process(data, this.cipher)
        this.send(data)
    }

    public setEncryption(enc: DvcEncryption) {
        this.cipher = enc
        this.state = { dvcMode: true, encryption: enc !== DvcEncryption.NONE }
    }


    // Methods for subclasses to implement:

    /** returns false to end DVC mode */
    protected receiveDvc(b: number): boolean { return true }

    protected send(data: Buffer) {}
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
class DvcCipherSet {
    static mappings: { [key: number]: [string, number, number] } = {
        [DvcEncryption.RC4]: ['rc4', 16, 0],
        [DvcEncryption.AES128]: ['aes-128-ofb', 16, 16],
        [DvcEncryption.AES256]: ['aes-256-ofb', 32, 16],
    }

    readonly ciphers: { [key: number]: Cipher }
    constructor(key: Buffer) {
        this.ciphers = {}
        for (const [enc, [name, keySize, ivSize]] of Object.entries(DvcCipherSet.mappings))
            this.ciphers[enc] = createCipheriv(name, truncateBuf(key, keySize), Buffer.alloc(ivSize))
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
