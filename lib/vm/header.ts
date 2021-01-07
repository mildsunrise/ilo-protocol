const MAGIC = 0xBADC0DE

export enum Flags {
    WP = 1 << 0,
    KEEPALIVE = 1 << 1,
    DISCONNECT = 1 << 2,
}

export interface HeaderBase {
    flags: number
    senseKey: number
    asc: number
    ascq: number
    media: number
}
export interface Header extends HeaderBase {
    length: number
}

export function checkU32(n: number) {
    if (n !== (n >>> 0))
        throw Error(`invalid u32 ${n}`)
    return n
}

export function checkU8(n: number) {
    if (n !== (n & 0xFF))
        throw Error(`invalid u8 ${n}`)
    return n
}

export function formatHeaderOnly(data: Header): Buffer {
    const buf = Buffer.alloc(16)
    buf.writeUInt32LE(MAGIC, 0)
    buf.writeUInt32LE(checkU32(data.flags), 4)
    buf[8] = checkU8(data.media)
    buf[9] = checkU8(data.senseKey)
    buf[10] = checkU8(data.asc)
    buf[11] = checkU8(data.ascq)
    buf.writeUInt32LE(checkU32(data.length), 12)
    return buf
}

export function formatHeader(senseKey: number, asc: number, ascq: number, media?: number, flags?: number, payload?: Buffer): Buffer {
    const buf = formatHeaderOnly({
        flags: flags || 0,
        senseKey,
        asc,
        ascq,
        media: media || 0,
        length: payload ? payload.length : 0
    })
    return payload ? Buffer.concat([ buf, payload ]) : buf
}

export function parseHeader(buf: Buffer): Header {
    if (buf.length !== 16)
        throw Error(`unexpected header length (expected 16, got ${buf.length})`)
    const magic = buf.readUInt32LE(0)
    if (magic !== MAGIC)
        throw Error(`unexpected header length (expected 0xBADC0DE, got 0x${magic.toString(16).padStart(8, '0')})`)
    return {
        flags: buf.readUInt32LE(4),
        media: buf[8],
        senseKey: buf[9],
        asc: buf[10],
        ascq: buf[11],
        length: buf.readUInt32LE(12),
    }
}
