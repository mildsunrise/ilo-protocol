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


// look up tables for uint8
const reversal = getU8ReversedLUT()  // bits reversed    

export class BitQueue {
    bits: number
    nbits: number

    constructor() {
        this.clear()
    }

    clear() {
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

}
