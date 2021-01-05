import { createCipheriv } from 'crypto'

import { truncateBuf } from '../utils'


/**
 * Outer connection protocol.
 * Has two modes, DVC and normal (telnet?). Starts in DVC mode.
 * 
 * This layer basically takes care of encryption / decryption.
 */
export class Telnet {
    protected key: Buffer
    protected ciphers: { [enc: number]: [CipherFn, CipherFn] } = {}

    protected cipher: DvcEncryption
    protected state: {
        dvcMode: false
        ctr1: number
    } | {
        dvcMode: true
        encryption: boolean
    }

    constructor(key: Buffer) {
        if (key.length !== 16)
            throw Error('invalid key length...')
        this.key = key
        this.setEncryption(DvcEncryption.NONE)
    }

    public receive(b: number) {
        if (this.state.dvcMode === true) {
            if (this.state.encryption)
                b = this.ciphers[this.cipher][0](Buffer.of(b))[0]
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
            data = this.ciphers[this.cipher][1](data)
        this.send(data)
    }

    public setEncryption(enc: DvcEncryption) {
        this.initializeCipherInstances(enc)
        this.cipher = enc
        this.state = { dvcMode: true, encryption: enc !== DvcEncryption.NONE }
    }

    public initializeCipherInstances(enc: DvcEncryption) {
        if (Object.hasOwnProperty.call(this.ciphers, enc))
            return  // already initialized, nothing to do
        if (!Object.hasOwnProperty.call(cipherImpls, enc))
            throw Error(`Cipher ${enc} unknown or not supported`)
        const decrypter = cipherImpls[enc](this.key)
        const encrypter = cipherImpls[enc](this.key)
        this.ciphers[enc] = [ decrypter, encrypter ]
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

type CipherFn = (data: Buffer) => Buffer
const cipherImpls: { [enc: number]: (key: Buffer) => CipherFn } = {}

const cryptoImpl = (cipher: string, keyLen: number, ivLen: number) => (key: Buffer) => {
    key = truncateBuf(key, keyLen)
    const cipherObj = createCipheriv(cipher, key, Buffer.alloc(ivLen))
    return (data: Buffer) => {
        const odata = cipherObj.update(data)
        if (odata.length !== data.length)
            throw Error(`Unexpected returned length (expected ${data.length}, got ${odata.length})`)
        return odata
    }
}

cipherImpls[DvcEncryption.NONE] = (key) => (data) => data
cipherImpls[DvcEncryption.RC4] = cryptoImpl('rc4', 16, 0)
cipherImpls[DvcEncryption.AES128] = cryptoImpl('aes-128-ofb', 16, 16)
cipherImpls[DvcEncryption.AES256] = cryptoImpl('aes-256-ofb', 32, 16)
