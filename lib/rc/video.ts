import { DvcEncryption } from './telnet'
import { BitQueue, getU8LeftLUT, getU8RightLUT, LRUCache } from '../utils'

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

export enum Cursor {
    DEFAULT = 0,
    CROSSHAIR,
    NONE,
    SQUARE, // 2x2 gray (#808080) square, origin at (0,0)
    POINTER, // again in gray
}

export enum MessageChannel {
    STATUS_FIELD_3 = 1,
    STATUS_FIELD_4 = 2,
    CONSOLE = 3,  // i.e. console.log()
    REPLACE_SCREEN = 4, // replaces the screen with an image of this text
}


// Machine states and their associated data

enum State {
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
    GO_TO_PIXCODE_OR_BEGIN_RGB = 31,
    PIXCODE_4B = 32,
    PIXEL_FILL_1 = 33,
    ADVANCE_BLOCKS_1 = 34,
    BEGIN_RGB = 36,
    N_37 = 37,
    PIXEL_ERROR = 38,
    SET_POSITION_Y = 39,
    SET_HEIGHT = 40,
    SET_GREEN = 41,
    SET_BLUE = 42,
    DISCARD_QUEUE = 43,
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

const STATE_DATA: { [state: number]: StateData } = {
    [State.RESET]: { bits: 0, next: State.BEGIN },
    [State.BEGIN]: { bits: 1, next: State.OTHER, next0: State.PIXEL_BEGIN },


    // Pixel loop ( PIXEL_BEGIN -> {pixcode | rgb} -> AFTER_COLOR -> [fill] -> PIXEL_BEGIN )

    [State.PIXEL_BEGIN]: { bits: 1, next: State.PIXCODE_1, next0: State.GO_TO_PIXCODE_OR_BEGIN_RGB },
    [State.GO_TO_PIXCODE_OR_BEGIN_RGB]: { bits: 1, next: State.BEGIN_RGB },

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


    // Fail states

    [State.PIXEL_ERROR]: { bits: 1, next: State.PIXEL_ERROR },
    [State.DISCARD_QUEUE]: { bits: 1, next: State.RESET, next0: State.DISCARD_QUEUE },
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
    readonly blockWidth = 16
    readonly blockHeight = 16 // FIXME

    byteCount: number // bytes processed by consumeBits
    readonly bitQueue = new BitQueue()
    dvc_zero_count: number // how many consecutive zeros we've pushed to the queue

    fatal_count: number
    dvc_decoder_state: State
    dvc_next_state: State
    
    video_detected: boolean
    size: [number, number]
    position: [number, number]
    dvc_newx: number // temporary variable, rename to currentX

    bitsPerColor: number
    dvc_pixcode: number
    dvc_last_color: number
    currentColor: [number, number, number]
    cache = new LRUCache()

    currentCommand: number[]
    currentString: { channel: MessageChannel, data: string }

    protected initQueue() {
        this.byteCount = 0
        this.bitQueue.clear()
        this.dvc_zero_count = 0
    }

    public init() {
        this.initQueue()

        // FIXME

        this.currentCommand = []
        this.currentString = undefined
    }

    protected consumeBits(byte: number) {
        this.byteCount++
        this.bitQueue.push(byte)
        
        // If we've seen more than 30 consecutive zeros at any point, go to RESET state
        this.dvc_zero_count += dvc_right[byte]
        if (this.dvc_zero_count > 30) {
            // if (!debug_msgs || this.dvc_decoder_state != 38 || fatal_count >= 40 || fatal_count > 0) ;
            this.dvc_decoder_state = this.dvc_next_state = State.DISCARD_QUEUE
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
        
        // Attempt to read bits
        const nbits = this.getBitsToRead(stateData.bits)
        if (this.bitQueue.nbits < nbits)
            return true
        let dvc_code = this.bitQueue.pop(nbits)
        
        // Calculate next state
        this.dvc_next_state = (dvc_code === 0 && stateData.next0 !== undefined) ?
            stateData.next0 : stateData.next
        
        
        switch (this.dvc_decoder_state) {
        case State.PIXCODE_1:
        case State.PIXCODE_0:
        case State.PIXCODE_1B:
        case State.PIXCODE_2B:
        case State.PIXCODE_3B:
        case State.PIXCODE_4B:
            if (dvc_cc_active === 1) {
                dvc_code = dvc_cc_usage[0]
            } else if (this.dvc_decoder_state === State.PIXCODE_0) {
                dvc_code = 0
            } else if (this.dvc_decoder_state === State.PIXCODE_1) {
                dvc_code = 1
            } else if (dvc_code !== 0) {
                dvc_code++
            }

            this.dvc_last_color = cache_find(dvc_code)
            if (this.dvc_last_color === -1) {
                this.dvc_next_state = State.PIXEL_ERROR
                break
            }

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

        case State.GO_TO_PIXCODE_OR_BEGIN_RGB:
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

            this.dvc_last_color = (this.currentColor[0] << 16) | (this.currentColor[1] << 8) | this.currentColor[0]
            let cacheFail = cache_lru(this.dvc_last_color)
            if (cacheFail) {
                // if (!debug_msgs || count_bytes > 6L) ;
                this.dvc_next_state = State.PIXEL_ERROR
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
            this.size = [this.dvc_newx, dvc_code]
            if (this.blockHeight == 16)
                this.size[1] &= 0x7F

            //if (dvc_lasty <= dvc_size_y || debug_msgs) ;
            this.repaintScreen()
            break

        case State.SET_X_RELATIVE:
            dvc_code += this.size[0] + 1
            //if (dvc_code <= dvc_size_x || debug_msgs) ;
        case State.SET_X_ABSOLUTE:
            this.size[0] = dvc_code
            if (this.blockHeight == 16)
                this.size[0] &= 0x7F
            //if (dvc_lastx <= dvc_size_x || debug_msgs) ;
            break

        case State.N_27:
            if (timeout_count == this.count_bytes - 1)
                this.dvc_next_state = State.PIXEL_ERROR

            const nbits = this.bitQueue.nbits % 8
            if (nbits != 0)
                dvc_code = this.bitQueue.pop(nbits)
            timeout_count = this.count_bytes

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
                this.currentString.data += String.fromCharCode(dvc_code)
            else
                this.printString(this.currentString.channel, this.currentString.data)
            break

        case State.RESET:
            cache_reset();
            dvc_pixel_count = 0;
            this.position = [0, 0]
            this.currentColor = [0, 0, 0]
            this.dvc_last_color = 0 // FIXME: wasn't actually there but... shouldn't matter

            this.fatal_count = 0
            this.timeout_count = -1

            this.currentCommand = []
            break

        case State.PIXEL_ERROR:
            // if (this.fatal_count === 0) {
            //     debug_lastx = dvc_lastx;
            //     debug_lasty = dvc_lasty;
            //     debug_show_block = 1;
            // }
            if (this.fatal_count === 40) {
                this.requestScreenRefresh()
            }
            if (this.fatal_count === 11680) {
                this.requestScreenRefresh()
            }
            this.fatal_count++
            if (this.fatal_count === 120000) {
                this.requestScreenRefresh()
            }
            if (this.fatal_count === 12000000) {
                this.requestScreenRefresh()
                this.fatal_count = 41
            }
            break

        case State.ADVANCE_BLOCKS_1:
            next_block(1)
            break

        case State.ADVANCE_BLOCKS_PLUS2:
            dvc_code += 2
        case State.ADVANCE_BLOCKS_N:
            next_block(dvc_code)
            break

        case State.SET_WIDTH:
            this.dvc_newx = dvc_code
            break

        case State.SET_HEIGHT:
            this.size = [this.dvc_newx, dvc_code]
            break

        case State.SET_YCLIPPED:
            this.position = [0, 0]
            dvc_pixel_count = 0
            cache_reset()

            dvc_y_clipped = (dvc_code > 0) ? (256 - 16 * dvc_code) : 0

            this.video_detected = (this.size[0] !== 0 && this.size[1] !== 0)
            if (this.video_detected) {
                const width = this.size[0] * this.blockWidth
                const height = this.size[1] * 16 + dvc_code
                this.setScreenDimensions(width, height)
                SetHalfHeight()
            } else {
                this.noVideo()
            }
            break

        case State.DISCARD_QUEUE:
            if (dvc_code !== 0)
                this.initQueue()
            break

        case State.N_37:
            return true // FIXME
        }


        if (this.dvc_next_state === State.PIXEL_BEGIN &&
            dvc_pixel_count === this.blockHeight * this.blockWidth) {
            next_block(1)
            cache_prune()
        }

        if (this.dvc_decoder_state === this.dvc_next_state &&
            this.dvc_decoder_state !== State.STRING_READ &&
            this.dvc_decoder_state !== State.PIXEL_ERROR &&
            this.dvc_decoder_state !== State.DISCARD_QUEUE) {
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
        const [ command, ...args ] = commandBuffer

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
            this.dvc_pixcode = State.PIXEL_ERROR
            break

        case ServerCommand.NO_VIDEO:
            this.noVideo()
            break

        case ServerCommand.SET_TS_TYPE:
            if (args.length < 1)
                throw Error('SET_TS_TYPE without argument')
            this.tsType = args[0]
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

        // FIXME: default case
        }
    }

    protected setPixcodeFromEntries() {       
        const entries = this.dvc_cc_active 
        if (entries < 2) {
            this.dvc_pixcode = State.PIXEL_ERROR
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
        if (this.dvc_decoder_state === State.SET_RED ||
            this.dvc_decoder_state === State.SET_GREEN ||
            this.dvc_decoder_state === State.SET_BLUE ||
            this.dvc_decoder_state === State.SET_GRAYSCALE)
            return this.bitsPerColor
        return n
    }

    protected addPixel() {
        if (dvc_pixel_count >= this.blockHeight * this.blockWidth) {
            this.dvc_next_state = State.PIXEL_ERROR
            return true
        }

        block[dvc_pixel_count] = this.dvc_last_color
        dvc_pixel_count++
    }

    protected next_block(nblocks: number) {
        // TODO
    }


    // Methods for subclasses to implement:
    // (keep in mind enum parameters are not validated, usually u8)
    // (coordinates and sizes are in pixels)

    protected setVideoDecryption(cipher: DvcEncryption) {}
    protected setFramerate(n: number) {}
    protected setPowerStatus(hasPower: boolean) {}
    protected setInfo(licensed: number, flags: number) {}
    protected printString(channel: MessageChannel, data: string) {}

    protected noVideo() {}
    protected seize() {}
    protected ping() {} // send ACK
    protected requestScreenRefresh() {} // request server to refresh screen

    protected setScreenDimensions(x: number, y: number) {}
    protected clearScreen() {}
    protected repaintScreen() {}
    protected invalidateScreen() {} // includes synchronous repaint(?)
}

// FIXME: post_complete, image_source, console.logs
