import { Flags, formatHeader } from './header'

export enum ScsiCommand {
    FORMAT_UNIT = 4,
    INQUIRY = 18,
    MODE_SELECT_6 = 21,
    MODE_SELECT = 85,
    MODE_SENSE_6 = 26,
    MODE_SENSE = 90,
    PA_MEDIA_REMOVAL = 30,
    READ_10 = 40,
    READ_12 = 168,
    READ_CAPACITY = 37,
    READ_CAPACITIES = 35,
    REQUEST_SENSE = 3,
    REZERO_UNIT = 1,
    SEEK = 43,
    SEND_DIAGNOSTIC = 29,
    START_STOP_UNIT = 27,
    TEST_UNIT_READY = 0,
    VERIFY = 47,
    WRITE_10 = 42,
    WRITE_12 = 170,
    WRITE_VERIFY = 46,
    READ_CD = 190,
    READ_CD_MSF = 185,
    READ_HEADER = 68,
    READ_SUBCHANNEL = 66,
    READ_TOC = 67,
    STOP_PLAY_SCAN = 78,
    MECHANISM_STATUS = 189,
    GET_EVENT_STATUS = 74,
}

enum FddState {
    MEDIA_NOT_PRESENT = 0,
    MEDIA_CHANGED = 1,
    DEVICE_READY = 2,
}

// FIXME: this implements CDROM behaviour, implement the rest too (i.e. R/W)

export class VirtualDevice {
    keepaliveTime = 1e3

    fdd_state = FddState.MEDIA_NOT_PRESENT
    event_state = 0

    protected async processCommand(buf: Buffer) {
        if (buf[0] == 254) {
            const header = formatHeader(0, 0, 0)
            buf.subarray(4).copy(header, 8)
            this.send(header)
            return true
        }

        // Update state
        if (!this.mediaIsPresent()) {
            this.media = 0
            this.fdd_state = FddState.MEDIA_NOT_PRESENT
            this.event_state = 4
        } else {
            this.media = 1
            this.fdd_state = Math.min(this.fdd_state + 1, 2)

            if (this.event_state == 4)
                this.event_state = 0
            this.event_state = Math.min(this.event_state + 1, 2)
        }

        let ok = true

        switch (buf[0]) {
            case ScsiCommand.PA_MEDIA_REMOVAL:
                this.handlePaMediaRemoval(buf)
                break
            case ScsiCommand.READ_CAPACITY:
                await this.handleReadCapacity()
                break
            case ScsiCommand.SEND_DIAGNOSTIC:
                this.handleSendDiagnostics()
                break
            case ScsiCommand.TEST_UNIT_READY:
                this.handleTestUnitReady()
                break
            case ScsiCommand.READ_10:
            case ScsiCommand.READ_12:
                await this.handleRead(buf)
                break
            case ScsiCommand.START_STOP_UNIT:
                ok = this.handleStartStopUnit(buf)
                break
            case ScsiCommand.READ_TOC:
                await this.handleReadToc(buf)
                break
            case ScsiCommand.MODE_SENSE:
                this.handleModeSense(buf)
                break
            case ScsiCommand.GET_EVENT_STATUS:
                this.handleGetEventStatus(buf)
                break
            default:
                console.log(`unknown command received`)
                this.sendCommand(5, 36, 0)
        }
        // FIXME: interrupt?
        return ok
    }

    protected handleSendDiagnostics() {
    }

    protected async handleRead(buf: Buffer) {
        const read12 = buf[0] === ScsiCommand.READ_12;

        let pos = buf.readUInt32BE(2)
        let len = read12 ? buf.readUInt32BE(2) : buf.readUInt16BE(7)
        pos *= 2048, len *= 2048

        if (this.fdd_state === FddState.MEDIA_NOT_PRESENT) {
            return this.sendCommand(2, 58, 0)
        } else if (this.fdd_state === FddState.MEDIA_CHANGED) {
            this.fdd_state = FddState.DEVICE_READY
            return this.sendCommand(6, 40, 0)
        }

        const data = Buffer.alloc(len)
        const outOfBounds = await this.mediaRead(data, pos)
        if (outOfBounds)
            return this.sendCommand(5, 33, 0)
        this.sendCommand(0, 0, 0, data)
    }

    protected handlePaMediaRemoval(buf: Buffer) {
        this.notifyMediaRemoval(!!(buf[4] & 0x1))
        this.sendCommand(0, 0, 0)
    }

    protected handleStartStopUnit(buf: Buffer): boolean {
        this.sendCommand(0, 0, 0)

        if ((buf[4] & 0x3) == 2) {
            this.fdd_state = FddState.MEDIA_NOT_PRESENT
            this.event_state = 4
            this.notifyMediaEject()
            return false
        }
        return true
    }

    protected handleTestUnitReady() {
        if (this.fdd_state == FddState.MEDIA_NOT_PRESENT) {
            this.sendCommand(2, 58, 0)
        } else if (this.fdd_state == FddState.MEDIA_CHANGED) {
            this.fdd_state = FddState.DEVICE_READY
            this.sendCommand(6, 40, 0)
        } else {
            this.sendCommand(0, 0, 0)
        }
    }

    protected async handleReadCapacity() {
        if (this.fdd_state == 0) {
            return this.sendCommand(2, 58, 0)
        } else if (this.fdd_state == 1) {
            return this.sendCommand(6, 40, 0)
        }
        const sectors = Math.floor(await this.mediaSize() / 2048 - 1)
        const data = Buffer.alloc(8)
        data.writeUInt32BE(sectors, 0)
        data[6] = 8
        this.sendCommand(0, 0, 0, data)
    }

    protected async handleReadToc(buf: Buffer) {
        const kind = (buf[9] >> 6) & 0b11
        const useGeometry = (buf[1] & 0x2)
        const maxLen = buf.readUInt16BE(7)
        const data = Buffer.alloc(412)

        if (kind === 0) {
            Buffer.of( 0,18,1,1, 0,20,1,0, 0,0,0,0, 0,20,170,0 ).copy(data)

            const sectors = Math.floor(await this.mediaSize() / 2048 - 1)
            if (useGeometry) {
                data[10] = 2
                
                const d = sectors / 75 + 2
                const dint = Math.floor(d), dfrac = d - dint
                data[17] = Math.floor(dint / 60)
                data[18] = Math.floor(dint % 60)
                data[19] = Math.floor(dfrac * 75)
            } else {
                data.writeUIntBE(sectors, 17, 3)
            }
        } else if (kind === 1) {
            Buffer.of(0,10,1,1, 0,20,1,0, 0,0,0,0).copy(data)
            if (useGeometry)
                data[10] = 2
        }

        this.sendCommand(0, 0, 0, data.subarray(0, maxLen))
    }

    protected handleModeSense(buf: Buffer) {
        const data = Buffer.of(0,8,1,0,0,0,0,0)
        this.media = data[2]
        this.sendCommand(0, 0, 0, data)
    }

    protected handleGetEventStatus(buf: Buffer) {
        const maxLen = buf.readUInt16BE(7)
        let data

        if (!(buf[1] & 0x1))
            this.sendCommand(5, 36, 0)

        if (buf[4] & 0x10) {
            data = Buffer.alloc(8)
            Buffer.of(0,6,4,16).copy(data)

            if (this.event_state == 0) {
                Buffer.of(0,0).copy(data, 4)
            } else if (this.event_state == 1) {
                Buffer.of(4,2).copy(data, 4)
                if (maxLen > 4)
                    this.event_state = 2
            } else if (this.event_state == 4) {
                Buffer.of(3,0).copy(data, 4)
                if (maxLen > 4)
                    this.event_state = 0
            } else {
                Buffer.of(0,2).copy(data, 4)
            }
        } else {
            data = Buffer.of(0,2,128,16)
        }

        this.sendCommand(0, 0, 0, data.subarray(0, maxLen))
    }

    protected media = 0
    protected senseKey = 0
    protected asc = 0
    protected ascq = 0

    protected sendCommand(senseKey: number, asc: number, ascq: number, payload?: Buffer) {
        this.senseKey = senseKey
        this.asc = asc
        this.ascq = ascq
        this.send(formatHeader(senseKey, asc, ascq, this.media, 0, payload))
    }

    protected keepaliveTimeout: NodeJS.Timeout

    protected resetKeepaliveTimeout() {
        if (this.keepaliveTimeout)
            clearTimeout(this.keepaliveTimeout)
        this.keepaliveTimeout = setTimeout(() => {
            this.keepaliveTimeout = undefined
            this.send(formatHeader(this.senseKey, this.asc, this.ascq, this.media, Flags.KEEPALIVE))
            this.resetKeepaliveTimeout()
        }, this.keepaliveTime)
    }

    protected cmdQueue = Buffer.alloc(0)

    public async receive(chunk: Buffer) {
        this.resetKeepaliveTimeout()
        this.cmdQueue = Buffer.concat([this.cmdQueue, chunk])
        while (this.cmdQueue.length >= 12) {
            await this.processCommand(this.cmdQueue.subarray(0, 12))
            this.cmdQueue = this.cmdQueue.subarray(12)
        }
    }

    public sendDisconnect() {
        this.send(formatHeader(this.senseKey, this.asc, this.ascq, this.media, Flags.DISCONNECT))
    }


    // Methods for subclasses to implement:

    protected send(data: Buffer) {}

    protected mediaIsPresent(): boolean {
        return true
    }
    /** returns current device size in bytes */
    protected mediaSize(): Promise<number> {
        throw new Error('Not implemented')
    }
    /**
     * positioned read of `buffer.length` bytes on device.
     * `pos` is in bytes. data must be placed at the buffer.
     * if the position is out of bounds, return true
     */
    protected mediaRead(buffer: Buffer, pos: number): Promise<boolean> {
        throw new Error('Not implemented')
    }

    protected notifyMediaEject() {}
    protected notifyMediaRemoval(prevented: boolean) {}
}
