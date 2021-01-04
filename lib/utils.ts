export const getU8ReversedLUT = (): number[] => [...Array(256)].map((_, x) => {
    let result = 0
    for (let bit = 0; bit < 8; bit++, x >>= 1)
        result = (result << 1) | (x & 1)
    return result
})

export const getU8RightLUT = (): number[] => [...Array(256)].map((_, x) => {
    let minBit = 8
    for (let bit = 0; bit < 8; bit++, x >>= 1)
        (x & 1) && (minBit = Math.min(minBit, bit))
    return minBit
})

export const getU8LeftLUT = (): number[] => [...Array(256)].map((_, x) => {
    let maxBit = -1
    for (let bit = 0; bit < 8; bit++, x >>= 1)
        (x & 1) && (maxBit = Math.max(maxBit, bit))
    return 7 - maxBit
})

export const mask = (nbits: number) => (1 << nbits) - 1

/** Return a resized copy of the passed buffer, by cropping or adding zeros. */
export function truncateBuf(buf: Uint8Array, size: number) {
    const result = Buffer.alloc(size)
    result.set(buf)
    return result
}


// look up tables for uint8
const reversal = getU8ReversedLUT()  // bits reversed    

export class BitQueue {
    protected bits: number
    nbits: number

    constructor() {
        this.init()
    }

    init() {
        this.bits = 0
        this.nbits = 0
    }

    /** Push 8 bits to the queue. Bit 0 is pushed first, then 1, etc. */
    push(bits: number) {
        this.bits |= (bits << this.nbits)
        this.nbits += 8
        if (this.nbits > 32)
            throw Error('too many bits in the queue...') // FIXME: verify this won't happen
    }

    /**
     * Pop N bits from the queue.
     * First consumed bit is placed on bit N-1, then bit N-2, ... bit 0
     * If not enough bits are present,
     */
    pop(nbits: number) {
        if (nbits > this.nbits)
            throw Error('requested more bits than present')
        const result = this.bits & mask(nbits)
        this.bits >>>= nbits
        this.nbits -= nbits

        return reversal[result] >>> (8 - nbits)
    }
}


export class LRUCache {
    readonly lastUsed = new Uint32Array(17)
    protected readonly value = new Uint32Array(17)
    protected readonly blockNumber = new Uint32Array(17)
    nentries: number

    constructor() {
        this.init()
    }

    init() {
        this.nentries = 0
    }

    protected touch(entry: number) {
        const usage = this.lastUsed[entry]
        for (let i = 0; i < this.nentries; i++) {
            if (this.lastUsed[i] < usage)
                this.lastUsed[i]++
        }
        this.lastUsed[entry] = 0
        this.blockNumber[entry] = 1
    }

    /**
     * Add a value to the cache.
     * Returns true if an entry with that value already existed in the cache
     */
    add(n: number): boolean {
        let oldestEntry
        for (let i = 0; i < this.nentries; i++) {
            if (this.lastUsed[i] === this.nentries - 1)
                oldestEntry = i
            if (this.value[i] !== n) continue
            this.touch(i)
            return true
        }

        // add at the end or replace oldest entry
        let i = oldestEntry
        if (this.nentries < this.value.length) {
            i = this.nentries
            this.lastUsed[i] = this.nentries
            this.nentries++
        }
        this.value[i] = n
        this.touch(i)
    }

    /** Find an entry by its usage, then touch it and return its value, or undefined if not found */
    find(usage: number): number {
        for (let i = 0; i < this.nentries; i++) {
            if (this.lastUsed[i] !== usage) continue
            this.touch(i)
            return this.value[i]
        }
    }

    /** Delete entries referring to oldest block (block 0) */
    prune() {
        for (let i = 0; i < this.nentries; ) {
            if (this.blockNumber[i] > 0) {
                this.blockNumber[i]--
                i++
                continue
            }
            // delete this item
            this.nentries--
            this.blockNumber[i] = this.blockNumber[this.nentries]
            this.lastUsed[i] = this.lastUsed[this.nentries]
            this.value[i] = this.value[this.nentries]
        }
    }
}
