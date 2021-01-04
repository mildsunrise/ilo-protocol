import { DvcEncryption } from './telnet'
import { BitQueue, getU8LeftLUT, getU8RightLUT } from '../utils'

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
    INITIAL = 1,
    N_2 = 2,
    N_3 = 3,
    N_4 = 4,
    N_5 = 5,
    N_6 = 6,
    N_7 = 7,
    SET_RED_GREEN = 8,
    SET_RED = 9,
    N_10 = 10,
    N_11 = 11,
    PIXEL_FILL_PLUS2 = 12,
    PIXEL_FILL_PLUS8 = 13,
    PIXEL_FILL = 14,
    N_15 = 15,
    N_16 = 16,
    N_17 = 17,
    N_18 = 18,
    N_19 = 19,
    N_20 = 20,
    N_21 = 21,
    N_22 = 22,
    N_23 = 23,
    COMMAND_READ = 24,
    N_25 = 25,
    N_26 = 26,
    N_27 = 27,
    N_28 = 28,
    N_29 = 29,
    N_30 = 30,
    N_31 = 31,
    N_32 = 32,
    PIXEL_FILL1 = 33,
    N_34 = 34,
    N_35 = 35,
    N_36 = 36,
    N_37 = 37,
    PIXEL_FAIL = 38,
    N_39 = 39,
    N_40 = 40,
    SET_GREEN = 41,
    SET_BLUE = 42,
    RELOAD = 43,
    STRING_START = 44,
    STRING_READ = 45,
    COMMAND_MORE = 46,
    N_47 = 47,
}

interface StateData {
    bits: number
    next: State
    next0?: State
}

const STATE_DATA: { [state: number]: StateData } = {
    [State.RESET]: { bits: 0, next: State.INITIAL },
    [State.INITIAL]: { bits: 1, next: State.N_15, next0: State.N_2 },
    [State.PIXEL_FAIL]: { bits: 1, next: State.PIXEL_FAIL },
    [State.N_2]: { bits: 1, next: State.N_3, next0: State.N_31 },
    [State.N_3]: { bits: 1, next: State.N_11, next0: State.N_2 },
    [State.N_4]: { bits: 1, next: State.N_11, next0: State.N_2 },
    [State.N_5]: { bits: 1, next: State.N_10 },
    [State.N_6]: { bits: 2, next: State.N_10 },
    [State.N_7]: { bits: 3, next: State.N_10 },
    [State.N_10]: { bits: 1, next: State.N_11, next0: State.N_2 },
    [State.N_11]: { bits: 1, next: State.PIXEL_FILL_PLUS2, next0: State.PIXEL_FILL1 },
    [State.PIXEL_FILL1]: { bits: 0, next: State.N_2 },
    [State.PIXEL_FILL_PLUS2]: { bits: 3, next: State.N_2 },
    [State.PIXEL_FILL_PLUS8]: { bits: 3, next: State.N_2 },
    [State.PIXEL_FILL]: { bits: 8, next: State.N_2 },
    [State.N_15]: { bits: 1, next: State.N_17, next0: State.N_16 },
    [State.N_16]: { bits: 1, next: State.N_18, next0: State.N_19 },
    [State.N_17]: { bits: 7, next: State.N_39 },
    [State.N_18]: { bits: 1, next: State.N_23, next0: State.N_22 },
    [State.N_19]: { bits: 1, next: State.N_21, next0: State.N_20 },
    [State.N_20]: { bits: 3, next: State.INITIAL },
    [State.N_21]: { bits: 7, next: State.INITIAL },
    [State.N_22]: { bits: 1, next: State.N_28, next0: State.N_34 },
    [State.N_23]: { bits: 1, next: State.COMMAND_READ, next0: State.N_25 },
    [State.N_25]: { bits: 1, next: State.N_27, next0: State.N_26 },
    [State.N_26]: { bits: 7, next: State.N_40 },
    [State.N_27]: { bits: 0, next: State.INITIAL },
    [State.N_28]: { bits: 1, next: State.N_30, next0: State.N_29 },
    [State.N_29]: { bits: 3, next: State.INITIAL },
    [State.N_30]: { bits: 7, next: State.INITIAL },
    [State.N_31]: { bits: 1, next: State.N_35, next0: State.N_36 },
    [State.N_32]: { bits: 4, next: State.N_10 },
    [State.N_34]: { bits: 0, next: State.INITIAL },
    [State.N_35]: { bits: 0, next: State.N_35 },
    [State.N_36]: { bits: 1, next: State.SET_RED, next0: State.SET_RED_GREEN },
    [State.N_37]: { bits: 0, next: State.N_37 },
    [State.N_39]: { bits: 7, next: State.INITIAL },
    [State.N_40]: { bits: 7, next: State.N_47 },
    [State.SET_RED_GREEN]: { bits: 5, next: State.N_10 },
    [State.SET_RED]: { bits: 5, next: State.SET_GREEN },
    [State.SET_GREEN]: { bits: 5, next: State.SET_BLUE },
    [State.SET_BLUE]: { bits: 5, next: State.N_10 },
    [State.RELOAD]: { bits: 1, next: State.RESET, next0: State.RELOAD },
    [State.STRING_START]: { bits: 8, next: State.STRING_READ },
    [State.STRING_READ]: { bits: 8, next: State.STRING_READ, next0: State.INITIAL },
    [State.COMMAND_READ]: { bits: 8, next: State.COMMAND_MORE },
    [State.COMMAND_MORE]: { bits: 1, next: State.COMMAND_READ, next0: State.INITIAL },
    [State.N_47]: { bits: 4, next: State.INITIAL },
}


// Video decoder state machine

export class DvcDecoder {
    byteCount: number // bytes processed by consumeBits
    readonly bitQueue = new BitQueue()
    dvc_zero_count: number // how many consecutive zeros we've pushed to the queue

    dvc_decoder_state: State
    dvc_next_state: State
    
    currentCommand: number[]
    currentString: { channel: MessageChannel, data: string }

    public init() {
        this.byteCount = 0
        this.bitQueue.clear()
        this.dvc_zero_count = 0

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
            this.dvc_decoder_state = this.dvc_next_state = State.RELOAD
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
        case State.N_3:
        case State.N_4:
        case State.N_5:
        case State.N_6:
        case State.N_7:
        case State.N_32:
            if (dvc_cc_active === 1) {
                dvc_code = dvc_cc_usage[0]
            } else if (this.dvc_decoder_state === State.N_4) {
                dvc_code = 0
            } else if (this.dvc_decoder_state === State.N_3) {
                dvc_code = 1
            } else if (dvc_code !== 0) {
                dvc_code++
            }

            let dvc_color = cache_find(dvc_code)
            if (dvc_color === -1) {
                this.dvc_next_state = State.PIXEL_FAIL
                break
            }

            this.dvc_last_color = this.color_remap_table[dvc_color];
            this.addPixel()
            break

        case State.PIXEL_FILL_PLUS2:
            if (dvc_code == 7) {
                this.dvc_next_state = State.PIXEL_FILL
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
            // FIXME: fallthrough

        case State.PIXEL_FILL:
            //if (!debug_msgs || this.dvc_decoder_state != 14 || dvc_code < 16) ;
            for (let i = 0; i < dvc_code; i++) {
                if (this.addPixel()) break;
            }
            break

        case State.PIXEL_FILL1:
            this.addPixel()
            break

        case State.N_35:
            dvc_next_state = dvc_pixcode
            break

        case State.SET_RED:
            dvc_red = dvc_code << this.bitsPerColor * 2
            break

        case State.SET_GREEN:
            dvc_green = dvc_code << this.bitsPerColor
            break

        case State.SET_RED_GREEN:
            dvc_red = dvc_code << this.bitsPerColor * 2
            dvc_green = dvc_code << this.bitsPerColor

        case State.SET_BLUE:
            dvc_blue = dvc_code
            let dvc_color = dvc_red | dvc_green | dvc_blue
            let cacheFail = cache_lru(dvc_color);
            if (cacheFail) {
                // if (!debug_msgs || count_bytes > 6L) ;
                this.dvc_next_state = State.PIXEL_FAIL
                break
            }

            dvc_last_color = this.color_remap_table[dvc_color];
            this.addPixel()
            break

        case State.N_17:
        case State.N_26:
            dvc_newx = dvc_code
            if (this.dvc_decoder_state === State.N_17 && dvc_newx > dvc_size_x) {
                //if (debug_msgs) ;
                dvc_newx = 0
            }
            break

        case State.N_39:
            dvc_newy = dvc_code
            if (this.blockHeight == 16)
                dvc_newy &= 0x7F

            dvc_lastx = dvc_newx
            dvc_lasty = dvc_newy

            //if (dvc_lasty <= dvc_size_y || debug_msgs) ;
            this.screen.repaint_it(1);
            break

        case State.N_20:
            dvc_code = dvc_lastx + dvc_code + 1
            //if (dvc_code <= dvc_size_x || debug_msgs) ;
            // FIXME: fallthrough?

        case State.N_21:
            dvc_lastx = dvc_code
            if (this.blockHeight == 16)
                dvc_lastx &= 0x7F
            //if (dvc_lastx <= dvc_size_x || debug_msgs) ;
            break

        case State.N_27:
            if (timeout_count == count_bytes - 1L)
                this.dvc_next_state = State.PIXEL_FAIL

            const nbits = this.bitQueue.nbits % 8
            if (nbits != 0)
                dvc_code = this.bitQueue.pop(nbits)
            timeout_count = count_bytes

            this.screen.repaint_it(1);
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
            dvc_lastx = 0;
            dvc_lasty = 0;
            dvc_red = 0;
            dvc_green = 0;
            dvc_blue = 0;
            fatal_count = 0;
            timeout_count = -1L;

            this.currentCommand = []
            break

        case State.PIXEL_FAIL:
            if (fatal_count === 0) {
                debug_lastx = dvc_lastx;
                debug_lasty = dvc_lasty;
                debug_show_block = 1;
            }
            if (fatal_count === 40) {
                refresh_screen()
            }
            if (fatal_count === 11680) {
                refresh_screen()
            }
            fatal_count++
            if (fatal_count === 120000) {
                refresh_screen()
            }
            if (fatal_count === 12000000) {
                refresh_screen()
                fatal_count = 41
            }
            break

        case State.N_34:
            next_block(1)
            break

        case State.N_29:
            dvc_code += 2
            break

        case State.N_30:
            next_block(dvc_code)
            break

        case State.N_40:
            dvc_size_x = dvc_newx
            dvc_size_y = dvc_code
            break

        case State.N_47:
            dvc_lastx = 0
            dvc_lasty = 0
            dvc_pixel_count = 0
            cache_reset()
            this.scale_x = 1
            this.scale_y = 1
            this.screen_x = dvc_size_x * this.blockWidth
            this.screen_y = dvc_size_y * 16 + dvc_code

            dvc_y_clipped = (dvc_code > 0) ? (256 - 16 * dvc_code) : 0

            video_detected = (this.screen_x !== 0 && this.screen_y !== 0)
            if (video_detected) {
                this.setScreenDimensions(this.screen_x, this.screen_y)
                SetHalfHeight()
            } else {
                this.noVideo()
                console.log(`No video. image_source = ${this.image_source}`)
            }
            break

        case State.RELOAD:
            if (this.dvc_next_state != this.dvc_decoder_state) {
                this.bitQueue = new BitQueue()
                this.dvc_zero_count = 0
            }
            break

        case State.N_37:
            return true // FIXME
        }

        
        if (this.dvc_next_state === State.N_2 &&
            this.dvc_pixel_count === this.blockHeight * this.blockWidth) {
            next_block(1)
            cache_prune()
        }
        
        if (this.dvc_decoder_state === this.dvc_next_state &&
            this.dvc_decoder_state !== State.STRING_READ &&
            this.dvc_decoder_state !== State.PIXEL_FAIL &&
            this.dvc_decoder_state !== State.RELOAD) {
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
            dvc_newx = 50
            this.dvc_next_state = State.PIXEL_FAIL
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

    protected setBitsPerColor(arg: number) {
        this.bitsPerColor = 5 - (arg & 3)
    }

    protected getBitsToRead(n: number) {
        if (this.dvc_decoder_state === State.SET_RED ||
            this.dvc_decoder_state === State.SET_GREEN ||
            this.dvc_decoder_state === State.SET_RED_GREEN ||
            this.dvc_decoder_state === State.SET_BLUE)
            return this.bitsPerColor
        return n
    }

    protected addPixel() {
        if (dvc_pixel_count >= this.blockHeight * this.blockWidth) {
            this.dvc_next_state = State.PIXEL_FAIL
            return true
        }

        block[dvc_pixel_count] = dvc_last_color
        dvc_pixel_count++
    }


    // Methods for subclasses to implement:
    // (keep in mind enum parameters are not validated, usually u8)

    protected setVideoDecryption(cipher: DvcEncryption) {}
    protected setFramerate(n: number) {}
    protected setPowerStatus(hasPower: boolean) {}
    protected setInfo(licensed: number, flags: number) {}
    protected printString(channel: MessageChannel, data: string) {}

    protected noVideo() {}
    protected seize() {}
    protected ping() {} // send ACK

    protected setScreenDimensions(x: number, y: number) {}
    protected clearScreen() {}
    protected repaintScreen() {}
    protected invalidateScreen() {} // includes repaint(?)
}

// FIXME: post_complete, image_source, console.logs
