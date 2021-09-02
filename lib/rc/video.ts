import { BitQueue, getU8LeftLUT, getU8RightLUT, LRUCache } from '../utils'
import { DvcEncryption } from './telnet'

// look up tables for uint8
const dvc_right = getU8RightLUT()        // amount of zeros starting from the right (8 if all bits zero)
const dvc_left = getU8LeftLUT()          // amount of zeros starting from the left (8 if all bits zero)


enum ServerCommand {
    GO_TO_N37 = 1,
    START_STRING = 2,
    SET_FRAMERATE = 3,
    POWER_ON = 4,
    POWER_OFF = 5,
    NO_VIDEO = 6,
    SET_TS_TYPE = 7,
    KEYCHG = 9,
    SEIZE = 10,
    SET_BITS_PER_COLOR = 11,
    SET_ENCRYPTION = 12,
    SET_INFO = 13,
    PING = 16,
    INVALIDATE_SCREEN = 128,
}

export enum Command2 {
    MOUSE_MOVE = 208,
    BUTTON_PRESS = 209,
    BUTTON_RELEASE = 210,
    BUTTON_CLICK = 211,
    BYTE = 212,
    SET_MODE = 213,
    ENCRYPT = 192,
}

export enum MessageChannel {
    STATUS_FIELD_3 = 1,
    STATUS_FIELD_4 = 2,
    CONSOLE = 3,  // i.e. console.log()
    REPLACE_SCREEN = 4, // replaces the screen with an image of this text
}


// Machine states and their associated data

export enum State {
    RESET = 0,
    BEGIN = 1,
    PIXEL_BEGIN = 2,
    PIXCODE_1 = 3,
    PIXCODE_0 = 4,
    PIXCODE_1B = 5,
    PIXCODE_2B = 6,
    PIXCODE_3B = 7,
    SET_GRAYSCALE = 8,
    SET_RED = 9,
    AFTER_COLOR = 10,
    PIXEL_FILL = 11,
    PIXEL_FILL2 = 12,
    PIXEL_FILL_PLUS8 = 13,
    PIXEL_FILL_N = 14,
    OTHER = 15,
    OTHER1 = 16,
    SET_POSITION = 17,
    OTHER2 = 18,
    SET_X = 19,
    SET_X_RELATIVE = 20,
    SET_X_ABSOLUTE = 21,
    ADVANCE_BLOCKS = 22,
    OTHER3 = 23,
    COMMAND_READ = 24,
    OTHER4 = 25,
    SET_WIDTH = 26,
    N_27 = 27,
    ADVANCE_BLOCKS2 = 28,
    ADVANCE_BLOCKS_PLUS2 = 29,
    ADVANCE_BLOCKS_N = 30,
    PIXCODE_OR_RGB = 31,
    PIXCODE_4B = 32,
    PIXEL_FILL_1 = 33,
    ADVANCE_BLOCKS_1 = 34,
    BEGIN_RGB = 36,
    N_37 = 37,
    FATAL_ERROR = 38,
    SET_POSITION_Y = 39,
    SET_HEIGHT = 40,
    SET_GREEN = 41,
    SET_BLUE = 42,
    RESYNCING = 43,
    STRING_START = 44,
    STRING_READ = 45,
    COMMAND_MORE = 46,
    SET_YCLIPPED = 47,
}

interface StateData {
    bits: number
    next: State
    next0?: State
}

export const STATE_DATA: { [state: number]: StateData } = {
    [State.RESET]: { bits: 0, next: State.BEGIN },
    [State.BEGIN]: { bits: 1, next: State.OTHER, next0: State.PIXEL_BEGIN },


    // Pixel loop ( PIXEL_BEGIN -> {pixcode | rgb} -> AFTER_COLOR -> [fill] -> PIXEL_BEGIN )

    [State.PIXEL_BEGIN]: { bits: 1, next: State.PIXCODE_1, next0: State.PIXCODE_OR_RGB }, // + BEGIN
    [State.PIXCODE_OR_RGB]: { bits: 1, next: State.BEGIN_RGB }, // + PIXCODE_* except PIXCODE_1

    [State.PIXCODE_1]:  { bits: 0, next: State.AFTER_COLOR },
    [State.PIXCODE_0]:  { bits: 0, next: State.AFTER_COLOR },
    [State.PIXCODE_1B]: { bits: 1, next: State.AFTER_COLOR },
    [State.PIXCODE_2B]: { bits: 2, next: State.AFTER_COLOR },
    [State.PIXCODE_3B]: { bits: 3, next: State.AFTER_COLOR },
    [State.PIXCODE_4B]: { bits: 4, next: State.AFTER_COLOR },

    [State.BEGIN_RGB]: { bits: 1, next: State.SET_RED, next0: State.SET_GRAYSCALE },
    [State.SET_RED]: { bits: 5, next: State.SET_GREEN },
    [State.SET_GREEN]: { bits: 5, next: State.SET_BLUE },
    [State.SET_BLUE]: { bits: 5, next: State.AFTER_COLOR },
    [State.SET_GRAYSCALE]: { bits: 5, next: State.AFTER_COLOR },

    [State.AFTER_COLOR]: { bits: 1, next: State.PIXEL_FILL, next0: State.PIXEL_BEGIN },

    [State.PIXEL_FILL]: { bits: 1, next: State.PIXEL_FILL2, next0: State.PIXEL_FILL_1 },
    [State.PIXEL_FILL2]:      { bits: 3, next: State.PIXEL_BEGIN }, // + PIXEL_FILL_PLUS8, PIXEL_FILL_N
    [State.PIXEL_FILL_1]:     { bits: 0, next: State.PIXEL_BEGIN },
    [State.PIXEL_FILL_PLUS8]: { bits: 3, next: State.PIXEL_BEGIN },
    [State.PIXEL_FILL_N]:     { bits: 8, next: State.PIXEL_BEGIN },


    // OTHER -> ... -> { one of the Actions below } -> BEGIN
    [State.OTHER]: { bits: 1, next: State.SET_POSITION, next0: State.OTHER1 },
    [State.OTHER1]: { bits: 1, next: State.OTHER2, next0: State.SET_X },
    [State.OTHER2]: { bits: 1, next: State.OTHER3, next0: State.ADVANCE_BLOCKS },
    [State.OTHER3]: { bits: 1, next: State.COMMAND_READ, next0: State.OTHER4 },
    [State.OTHER4]: { bits: 1, next: State.N_27, next0: State.SET_WIDTH },

    // Actions

    [State.SET_X]: { bits: 1, next: State.SET_X_ABSOLUTE, next0: State.SET_X_RELATIVE },
    [State.SET_X_RELATIVE]: { bits: 3, next: State.BEGIN },
    [State.SET_X_ABSOLUTE]: { bits: 7, next: State.BEGIN },

    [State.SET_POSITION]: { bits: 7, next: State.SET_POSITION_Y },
    [State.SET_POSITION_Y]: { bits: 7, next: State.BEGIN },

    [State.SET_WIDTH]: { bits: 7, next: State.SET_HEIGHT },
    [State.SET_HEIGHT]: { bits: 7, next: State.SET_YCLIPPED },
    [State.SET_YCLIPPED]: { bits: 4, next: State.BEGIN },

    [State.ADVANCE_BLOCKS]: { bits: 1, next: State.ADVANCE_BLOCKS2, next0: State.ADVANCE_BLOCKS_1 },
    [State.ADVANCE_BLOCKS2]: { bits: 1, next: State.ADVANCE_BLOCKS_N, next0: State.ADVANCE_BLOCKS_PLUS2 },
    [State.ADVANCE_BLOCKS_1]: { bits: 0, next: State.BEGIN },
    [State.ADVANCE_BLOCKS_N]: { bits: 7, next: State.BEGIN },
    [State.ADVANCE_BLOCKS_PLUS2]: { bits: 3, next: State.BEGIN },

    [State.N_27]: { bits: 0, next: State.BEGIN },

    [State.COMMAND_READ]: { bits: 8, next: State.COMMAND_MORE },
    [State.COMMAND_MORE]: { bits: 1, next: State.COMMAND_READ, next0: State.BEGIN }, // + STRING_START, N_37

    [State.STRING_START]: { bits: 8, next: State.STRING_READ },
    [State.STRING_READ]: { bits: 8, next: State.STRING_READ, next0: State.BEGIN },


    // Other states

    [State.FATAL_ERROR]: { bits: 1, next: State.FATAL_ERROR },
    [State.RESYNCING]: { bits: 1, next: State.RESET, next0: State.RESYNCING },
    [State.N_37]: { bits: 0, next: State.N_37 }, // FIXME
}


/**
 * Video decoder state machine. Process incoming bitstream and
 * reconstructs screen blocks, etc. generating events that are
 * dispatched to the appropriate methods.
 * 
 * To use this, subclass and implement all methods after
 * the "Methods for subclasses to implement" marker at the end.
 */
export class DvcDecoder {
    debug = false

    byteCount!: number // bytes processed by consumeBits
    readonly bitQueue = new BitQueue()
    dvc_zero_count!: number // how many consecutive zeros we've pushed to the queue

    fatal_count!: number
    timeout_count!: number
    dvc_decoder_state!: State
    dvc_next_state!: State
    
    video_detected!: boolean
    size!: [number, number]
    position!: [number, number]
    dvc_newx!: number // temporary variable, rename to currentX

    bitsPerColor!: number
    dvc_pixcode!: number
    dvc_last_color!: number
    currentColor!: [number, number, number]
    cache = new LRUCache()

    block = new Uint32Array(16 * 16)
    blockWidth = 16
    blockHeight!: number
    dvc_pixel_count!: number

    currentCommand!: number[]
    currentString?: { channel: MessageChannel, data: string }

    protected initQueue() {
        this.byteCount = 0
        this.bitQueue.init()
        this.dvc_zero_count = 0
    }

    public init() {
        this.initQueue()

        this.fatal_count = 0
        this.timeout_count = -1
        this.dvc_decoder_state = this.dvc_next_state = State.RESET
        
        this.video_detected = false
        this.size = [0, 0]
        this.position = [0, 0]
        this.dvc_newx = 0

        this.bitsPerColor = 5
        this.dvc_pixcode = State.FATAL_ERROR
        this.dvc_last_color = 0
        this.currentColor = [0, 0, 0]
        this.cache.init()

        this.blockHeight = 16
        this.dvc_pixel_count = 0

        this.currentCommand = []
        this.currentString = undefined
    }

    constructor() {
        this.init()
    }

    protected consumeBits(byte: number) {
        this.byteCount++
        this.bitQueue.push(byte)
        
        // If we've seen more than 30 consecutive zeros at any point, go into RESYNCING state
        this.dvc_zero_count += dvc_right[byte]
        if (this.dvc_zero_count > 30) {
            // if (!debug_msgs || this.dvc_decoder_state != 38 || fatal_count >= 40 || fatal_count > 0) ;
            this.dvc_decoder_state = this.dvc_next_state = State.RESYNCING
        }
        if (byte != 0)
            this.dvc_zero_count = dvc_left[byte]

        while (!this.processBits());
    }
    
    /** Main method: iteration of the state machine. Returns `true` to stop */
    protected processBits() {
        if (!Object.hasOwnProperty.call(STATE_DATA, this.dvc_decoder_state))
            throw Error(`Unknown state ${this.dvc_decoder_state}`)
        const stateData = STATE_DATA[this.dvc_decoder_state]
        const offset = this.byteCount * 8 - this.bitQueue.nbits
        
        // Attempt to read bits
        const nbits = this.getBitsToRead(stateData.bits)
        if (this.bitQueue.nbits < nbits)
            return true
        let dvc_code = this.bitQueue.pop(nbits)
        
        // Calculate next state
        this.dvc_next_state = (dvc_code === 0 && stateData.next0 !== undefined) ?
            stateData.next0 : stateData.next
        
        if (this.debug) {
            let codeStr = nbits > 0 ? dvc_code.toString(2).padStart(nbits, '0') : '.'
            const formatState = (x: State) => `${State[x]}(${x})`
            console.log(`[debug] offset = ${Math.floor(offset/8)}.${offset % 8}, state = ${formatState(this.dvc_decoder_state)}, bits = ${codeStr}, next = ${formatState(this.dvc_next_state)}`)
        }
        
        switch (this.dvc_decoder_state) {
        case State.PIXCODE_1:
        case State.PIXCODE_0:
        case State.PIXCODE_1B:
        case State.PIXCODE_2B:
        case State.PIXCODE_3B:
        case State.PIXCODE_4B:
            if (this.cache.nentries === 1) {
                dvc_code = this.cache.lastUsed[0]
            } else if (this.dvc_decoder_state === State.PIXCODE_0) {
                dvc_code = 0
            } else if (this.dvc_decoder_state === State.PIXCODE_1) {
                dvc_code = 1
            } else if (dvc_code !== 0) {
                dvc_code++
            }

            const _col = this.cache.find(dvc_code)
            if (_col === undefined) {
                this.dvc_next_state = State.FATAL_ERROR
                break
            }
            this.dvc_last_color = _col

            this.addPixel()
            break

        case State.PIXEL_FILL_1:
            this.addPixel()
            break

        case State.PIXEL_FILL2:
            if (dvc_code == 7) {
                this.dvc_next_state = State.PIXEL_FILL_N
                break
            }
            if (dvc_code == 6) {
                this.dvc_next_state = State.PIXEL_FILL_PLUS8
                break
            }

            dvc_code += 2
            for (let i = 0; i < dvc_code; i++) {
                if (this.addPixel()) break;
            }
            break

        case State.PIXEL_FILL_PLUS8:
            dvc_code += 8
        case State.PIXEL_FILL_N:
            //if (!debug_msgs || this.dvc_decoder_state != 14 || dvc_code < 16) ;
            for (let i = 0; i < dvc_code; i++) {
                if (this.addPixel()) break;
            }
            break

        case State.PIXCODE_OR_RGB:
            if (dvc_code !== 0)
                this.dvc_next_state = this.dvc_pixcode
            break

        case State.SET_RED:
            this.currentColor[0] = dvc_code << (8 - this.bitsPerColor)
            break

        case State.SET_GREEN:
            this.currentColor[1] = dvc_code << (8 - this.bitsPerColor)
            break

        case State.SET_GRAYSCALE:
            this.currentColor[0] = dvc_code << (8 - this.bitsPerColor)
            this.currentColor[1] = dvc_code << (8 - this.bitsPerColor)
        case State.SET_BLUE:
            this.currentColor[2] = dvc_code << (8 - this.bitsPerColor)

            this.dvc_last_color = (this.currentColor[0] << 16) | (this.currentColor[1] << 8) | this.currentColor[2]
            let alreadyThere = this.cache.add(this.dvc_last_color)
            this.setPixcodeFromCacheEntries()
            if (alreadyThere) {
                // if (!debug_msgs || count_bytes > 6L) ;
                this.dvc_next_state = State.FATAL_ERROR
                break
            }

            this.addPixel()
            break

        case State.SET_POSITION:
            if (dvc_code > this.size[0]) {
                //if (debug_msgs) ;
                dvc_code = 0
            }
            this.dvc_newx = dvc_code
            break

        case State.SET_POSITION_Y:
            this.position = [this.dvc_newx, dvc_code]
            if (this.blockHeight == 16)
                this.position[1] &= 0x7F

            //if (dvc_lasty <= dvc_size_y || debug_msgs) ;
            this.repaintScreen()
            break

        case State.SET_X_RELATIVE:
            dvc_code += this.position[0] + 1
            //if (dvc_code <= dvc_size_x || debug_msgs) ;
        case State.SET_X_ABSOLUTE:
            this.position[0] = dvc_code
            if (this.blockHeight == 16)
                this.position[0] &= 0x7F
            //if (dvc_lastx <= dvc_size_x || debug_msgs) ;
            break

        case State.N_27:
            if (this.timeout_count == this.byteCount - 1)
                this.dvc_next_state = State.FATAL_ERROR

            const nbits = this.bitQueue.nbits % 8
            if (nbits != 0)
                dvc_code = this.bitQueue.pop(nbits)
            this.timeout_count = this.byteCount

            this.repaintScreen()
            break

        case State.COMMAND_READ:
            this.currentCommand.push(dvc_code)
            break

        case State.COMMAND_MORE:
            if (dvc_code == 0) {
                const command = Buffer.from(this.currentCommand)
                this.currentCommand = []
                this.processCommand(command)
            }
            break

        case State.STRING_START:
            this.currentString = { channel: dvc_code, data: '' }
            break

        case State.STRING_READ:
            if (dvc_code != 0)
                this.currentString!.data += String.fromCharCode(dvc_code)
            else
                this.printString(this.currentString!.channel, this.currentString!.data)
            break

        case State.RESET:
            this.cache.init()
            this.dvc_pixel_count = 0
            this.position = [0, 0]
            this.currentColor = [0, 0, 0]
            this.dvc_last_color = 0 // FIXME: wasn't actually there but... shouldn't matter

            this.fatal_count = 0
            this.timeout_count = -1

            this.currentCommand = []
            break

        case State.FATAL_ERROR:
            // if (this.fatal_count === 0) {
            //     debug_lastx = dvc_lastx;
            //     debug_lasty = dvc_lasty;
            //     debug_show_block = 1;
            // }
            if (this.fatal_count === 40) {
                this.requestResync()
            }
            if (this.fatal_count === 11680) {
                this.requestResync()
            }
            this.fatal_count++
            if (this.fatal_count === 120000) {
                this.requestResync()
            }
            if (this.fatal_count === 12000000) {
                this.requestResync()
                this.fatal_count = 41
            }
            break

        case State.ADVANCE_BLOCKS_1:
            this.next_block(1)
            break

        case State.ADVANCE_BLOCKS_PLUS2:
            dvc_code += 2
        case State.ADVANCE_BLOCKS_N:
            this.next_block(dvc_code)
            break

        case State.SET_WIDTH:
            this.dvc_newx = dvc_code
            break

        case State.SET_HEIGHT:
            this.size = [this.dvc_newx, dvc_code]
            break

        case State.SET_YCLIPPED:
            this.position = [0, 0]
            this.dvc_pixel_count = 0
            this.cache.init()

            // dvc_y_clipped = (dvc_code > 0) ? (256 - 16 * dvc_code) : 0

            this.video_detected = (this.size[0] !== 0 && this.size[1] !== 0)
            if (this.video_detected) {
                const width = this.size[0] * this.blockWidth
                const height = this.size[1] * 16 + dvc_code
                this.setScreenDimensions(width, height)
                this.blockHeight = this.size[1] > 101 ? 8 : 16
            } else {
                this.noVideo()
            }
            break

        case State.RESYNCING:
            if (dvc_code !== 0)
                this.initQueue()
            break

        case State.N_37:
            return true // FIXME
        }


        if (this.dvc_next_state === State.PIXEL_BEGIN &&
            this.dvc_pixel_count === this.blockHeight * this.blockWidth) {
            this.next_block(1)
            this.cache.prune()
            this.setPixcodeFromCacheEntries()
        }

        if (this.dvc_decoder_state === this.dvc_next_state &&
            this.dvc_decoder_state !== State.STRING_READ &&
            this.dvc_decoder_state !== State.FATAL_ERROR &&
            this.dvc_decoder_state !== State.RESYNCING) {
            console.log(`Machine hung in state ${this.dvc_decoder_state}`)
            return true
        }

        this.dvc_decoder_state = this.dvc_next_state
    }


    /**
     * Process a received command
     * @param command command bytes (minimum length 1)
     */
    protected processCommand(commandBuffer: Buffer) {
        const command = commandBuffer[commandBuffer.length - 1]
        const args = commandBuffer.slice(0, commandBuffer.length - 1)

        switch (command) {
        case ServerCommand.GO_TO_N37:
            this.dvc_next_state = State.N_37
            break

        case ServerCommand.START_STRING:
            this.dvc_next_state = State.STRING_START
            break

        case ServerCommand.SET_FRAMERATE:
            this.setFramerate(args.length >= 1 ? args[0] : 0)
            break

        case ServerCommand.POWER_ON:
            this.setPowerStatus(true)
            break

        case ServerCommand.POWER_OFF:
            this.setPowerStatus(false)

            this.clearScreen()
            this.dvc_newx = 50
            this.dvc_pixcode = State.FATAL_ERROR
            break

        case ServerCommand.NO_VIDEO:
            this.noVideo()
            break

        case ServerCommand.SET_TS_TYPE:
            if (args.length < 1)
                throw Error('SET_TS_TYPE without argument')
            this.setTsType(args[0])
            break

        case ServerCommand.KEYCHG:
            console.log('received keychg and cleared bits')
            this.bitQueue.pop(this.bitQueue.nbits % 8)
            break

        case ServerCommand.SEIZE:
            this.seize()
            break

        case ServerCommand.SET_BITS_PER_COLOR:
            if (args.length < 1)
                throw Error('SET_BITS_PER_COLOR without argument')
            this.setBitsPerColor(args[0])
            break

        case ServerCommand.SET_ENCRYPTION:
            if (args.length < 1)
                throw Error('SET_ENCRYPTION without argument')
            this.setVideoDecryption(args[0])
            break

        case ServerCommand.SET_INFO:
            if (args.length < 4)
                throw Error('SET_INFO without argument')
            this.setBitsPerColor(args[0])
            this.setVideoDecryption(args[1])
            this.setInfo(args[2], args[3])
            break

        case ServerCommand.PING:
            this.ping()
            break

        case ServerCommand.INVALIDATE_SCREEN:
            this.invalidateScreen()
            break

        default:
            console.log(`Unknown command: ${command} ${args}`)
            break
        }
    }

    protected setPixcodeFromCacheEntries() {       
        const entries = this.cache.nentries 
        if (entries < 2) {
            this.dvc_pixcode = State.FATAL_ERROR
        } else if (entries === 2) {
            this.dvc_pixcode = State.PIXCODE_0
        } else if (entries === 3) {
            this.dvc_pixcode = State.PIXCODE_1B
        } else if (entries < 6) {
            this.dvc_pixcode = State.PIXCODE_2B
        } else if (entries < 10) {
            this.dvc_pixcode = State.PIXCODE_3B
        } else {
            this.dvc_pixcode = State.PIXCODE_4B
        }
    }

    protected setBitsPerColor(arg: number) {
        this.bitsPerColor = 5 - (arg & 3)
    }

    protected getBitsToRead(n: number) {
        switch (this.dvc_decoder_state) {
        case State.SET_RED:
        case State.SET_GREEN:
        case State.SET_BLUE:
        case State.SET_GRAYSCALE:
            return this.bitsPerColor

        case State.SET_X_ABSOLUTE:
        case State.SET_POSITION:
        case State.SET_POSITION_Y:
        case State.ADVANCE_BLOCKS_N:
            return this.blockHeight === 16 ? 7 : 8
        }
        return n
    }

    protected addPixel() {
        if (this.dvc_pixel_count >= this.blockHeight * this.blockWidth) {
            this.dvc_next_state = State.FATAL_ERROR
            return true
        }
        this.block[this.dvc_pixel_count++] = this.dvc_last_color
    }

    protected next_block(nblocks: number) {
        // FIXME: fill or cut rest of block with black, if we're on the clipping row
        this.dvc_pixel_count = 0
        this.dvc_next_state = State.BEGIN

        for (let i = 0; i < nblocks; i++) {
            if (this.video_detected)
                this.renderBlock(
                    this.block.subarray(0, this.blockWidth * this.blockHeight),
                    this.position[0] * this.blockWidth,
                    this.position[1] * this.blockHeight,
                    this.blockWidth, this.blockHeight)

            this.position[0]++
            if (this.position[0] >= this.size[0])
                break
        }
    }

    // Public API

    public process(byte: number) {
        // FIXME: why are these things initialized there
        this.consumeBits(byte)
    }

    public get ok() {
        return this.dvc_decoder_state !== State.FATAL_ERROR
    }


    // Methods for subclasses to implement:
    // (keep in mind enum parameters are not validated, usually u8)
    // (coordinates and sizes are in pixels)
    // (block's items are 00RRGGBB colors)

    protected setVideoDecryption(cipher: DvcEncryption) {}
    protected setFramerate(n: number) {}
    protected setPowerStatus(hasPower: boolean) {}
    protected setInfo(licensed: number, flags: number) {}
    protected setTsType(t: number) {} // 0 -> RDP, 1 -> VNC, other?
    protected printString(channel: MessageChannel, data: string) {}

    protected noVideo() {}
    protected seize() {}
    protected ping() {} // send ACK
    protected requestResync() {} // request server to restart stream
    protected exitDvc() {} // tell Telnet to exit DVC mode

    protected setScreenDimensions(x: number, y: number) {}
    protected renderBlock(block: Uint32Array, x: number, y: number, width: number, height: number) {}
    protected clearScreen() {}
    protected repaintScreen() {}
    protected invalidateScreen() {} // includes synchronous repaint(?)
}

// FIXME: post_complete, image_source, console.logs
