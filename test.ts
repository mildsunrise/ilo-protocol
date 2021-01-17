import { connect } from 'net'
import { once } from 'events'

import { RestAPIClient } from './lib/rest'
import { negotiateConnection } from './lib/rc/handshake'
import { DvcEncryption, Telnet } from './lib/rc/telnet'
import { DvcDecoder, MessageChannel } from './lib/rc/video'
import { Command, formatCommand, formatKeyboardCommand, formatMouseCommand, powerStatusCommands } from './lib/rc/command'

const gi = require('node-gtk')
const Gtk = gi.require('Gtk', '3.0')
const Gdk = gi.require('Gdk', '3.0')
const Cairo = gi.require('cairo', '2.0')

gi.startLoop()
Gtk.init()

// Based on "usb_kbd_keycode" from drivers/hid/usbhid/usbkbd.c @ 1a59d1b
// FIXME: does 43 map to 49 or 50?
const LINUX_KEYCODE_TO_HID = { 30: 4, 48: 5, 46: 6, 32: 7, 18: 8, 33: 9, 34: 10, 35: 11, 23: 12, 36: 13, 37: 14, 38: 15, 50: 16, 49: 17, 24: 18, 25: 19, 16: 20, 19: 21, 31: 22, 20: 23, 22: 24, 47: 25, 17: 26, 45: 27, 21: 28, 44: 29, 2: 30, 3: 31, 4: 32, 5: 33, 6: 34, 7: 35, 8: 36, 9: 37, 10: 38, 11: 39, 28: 40, 1: 41, 14: 42, 15: 43, 57: 44, 12: 45, 13: 46, 26: 47, 27: 48, 43: 49, 39: 51, 40: 52, 41: 53, 51: 54, 52: 55, 53: 56, 58: 57, 59: 58, 60: 59, 61: 60, 62: 61, 63: 62, 64: 63, 65: 64, 66: 65, 67: 66, 68: 67, 87: 68, 88: 69, 99: 70, 70: 71, 119: 72, 110: 73, 102: 74, 104: 75, 111: 76, 107: 77, 109: 78, 106: 79, 105: 80, 108: 81, 103: 82, 69: 83, 98: 84, 55: 85, 74: 86, 78: 87, 96: 88, 79: 89, 80: 90, 81: 91, 75: 92, 76: 93, 77: 94, 71: 95, 72: 96, 73: 97, 82: 98, 83: 99, 86: 100, 127: 101, 116: 102, 117: 103, 183: 104, 184: 105, 185: 106, 186: 107, 187: 108, 188: 109, 189: 110, 190: 111, 191: 112, 192: 113, 193: 114, 194: 115, 134: 116, 138: 117, 130: 118, 132: 119, 128: 120, 129: 121, 131: 122, 137: 123, 133: 124, 135: 125, 136: 126, 113: 127, 115: 128, 114: 129, 121: 133, 89: 135, 93: 136, 124: 137, 92: 138, 94: 139, 95: 140, 122: 144, 123: 145, 90: 146, 91: 147, 85: 148, 29: 224, 42: 225, 56: 226, 125: 227, 97: 228, 54: 229, 100: 230, 126: 231, 164: 232, 166: 233, 165: 234, 163: 235, 161: 236, 150: 240, 158: 241, 159: 242, 177: 245, 178: 246, 176: 247, 142: 248, 152: 249, 173: 250, 140: 251 }

function mapGdkKeycodeToHid(n: number) {
    // we'll assume this is an X11 keycode (FIXME),
    // then we can supposedly get the Linux keycode by subtracting 8
    n -= 8
    // we'll then map this to HID keycode
    if (!Object.hasOwnProperty.call(LINUX_KEYCODE_TO_HID, n))
        return
    return LINUX_KEYCODE_TO_HID[n]
}

async function main() {
    const { address, username, password } = require('./config.json')

    const client = new RestAPIClient(`https://${address}`)
    await client.loginSession(username, password)

    console.log(`Session key: ${client.sessionKey.toString('hex')}`)
    const rcInfo = await client.getRcInfo()
    console.log(`Encryption key: ${rcInfo.encKey}`)
    console.log(`Optional features: ${Array.from(rcInfo.optionalFeatures).join(', ')}`)
    if (rcInfo.protocolVersion !== '1.1')
        console.warn(`Warning: Untested protocol version (${rcInfo.protocolVersion}), proceed with care`)

    // Set up the console connection
    const rcSocket = connect({ host: address, port: rcInfo.rcPort })
    rcSocket.setNoDelay(true)
    // code also disables SO_LINGER
    await once(rcSocket, 'connect')
    await negotiateConnection(false, rcSocket, client.sessionKey, rcInfo)
    console.log('Connected to remote console.')

    // Set up the command connection
    const cmdSocket = connect({ host: address, port: rcInfo.rcPort })
    // code also disables SO_LINGER
    await once(cmdSocket, 'connect')
    await negotiateConnection(true, cmdSocket, client.sessionKey, rcInfo)
    console.log('Connected to command session.')


    // Create the socket -> decrypter -> decoder chain

    rcSocket.on('end', () => {
        if (!quitting)
            throw Error('Socket disconnected')
    })
    rcSocket.on('data', data => {
        data.forEach(n => telnet.receive(n))
    })

    const encKey = Buffer.from(rcInfo.encKey, 'hex')
    const telnet = new class extends Telnet {
        protected send(data: Buffer) {
            rcSocket.write(data)
        }

        protected receiveDvc(n: number) {
            return decoder.process(n)
        }
    }(encKey)

    const decoder = new class extends DvcDecoder {
        protected setVideoDecryption(enc: DvcEncryption) {
            console.log(`Setting encryption to ${DvcEncryption[enc]}`)
            telnet.setDvcWithEncryption(enc)
        }
        protected setFramerate(n: number) {
            console.log(`Setting framerate to ${n}`)
        }
        protected setPowerStatus(hasPower: boolean) {
            console.log(hasPower ? '- POWER ON -' : '- POWER OFF -')
        }
        protected setInfo(licensed: number, flags: number) {
            console.log(`Licensed: ${licensed}  Flags: ${flags}`)
        }
        protected setTsType(t: number) {
            console.log(`TS type: ${t}`)
        }
        protected printString(channel: MessageChannel, data: string) {
            console.log(`Message (channel ${MessageChannel[channel]}): ${JSON.stringify(data)}`)
        }

        protected noVideo() {
            console.log('No video')
            screenSize = surface = surfaceCr = blockImage = undefined
        }
        protected seize() {
            console.log('Connection seized (TODO)')
        }
        protected ping() {
            console.log('Ping')
            telnet.sendDvc(formatCommand(Command.ACK))
        }
        protected requestScreenRefresh() {
            telnet.sendDvc(formatCommand(Command.REFRESH_SCREEN))
            console.log('Requesting resync...')
        }
        protected exitDvc() {
            console.log('Exiting DVC mode...')
            telnet.exitDvc()
        }

        protected setScreenDimensions(x: number, y: number) {
            console.log(`Screen dimensions: ${x} x ${y}`)
            screenSize = [x, y]
            surface = win.window.createSimilarSurface(Cairo.Content.COLOR, x, y)
            surfaceCr = new Cairo.Context(surface)
            surfaceCr.setSourceRgb(0, 0, 0)
            surfaceCr.paint()
            drawingArea.setSizeRequest(x, y)
            blockImage = Cairo.ImageSurface.createForData(decoder.block, Cairo.Format.RGB24, decoder.blockWidth, decoder.blockHeight, decoder.blockWidth*4)
        }
        protected renderBlock(block: Uint32Array, x: number, y: number, width: number, height: number) {
            if (!surface)
                return console.error('painting before setting screen dimensions')
            blockImage.markDirty()
            surfaceCr.setSourceSurface(blockImage, x, y)
            surfaceCr.paint()
            drawingArea.queueDrawArea(x, y, width, height)
        }
        protected clearScreen() {
            console.log('Clear screen')
        }
        public repaintScreen() {
            //console.log('Repaint screen')
        }
        protected invalidateScreen() {
            if (invalidateTrackTimer === undefined)
                console.log('Screen is now being refreshed')
            else
                clearTimeout(invalidateTrackTimer)
            invalidateTrackTimer = setTimeout(() => {
                console.log('Screen no longer being refreshed')
                invalidateTrackTimer = undefined
            }, 350)
        }
    }

    let invalidateTrackTimer: NodeJS.Timeout


    // Screen widget

    let screenSize, surface, surfaceCr, blockImage

    const drawingArea = new Gtk.DrawingArea()
    // FIXME: drawingArea.on('configure-event', () => {})
    drawingArea.on('draw', (cr) => {
        if (!surface)
            return
        cr.setSourceSurface(surface, 0, 0)
        cr.paint()
    })

    drawingArea.canFocus = true
    drawingArea.sensitive = true
    drawingArea.addEvents(0
        | Gdk.EventMask.KEY_PRESS_MASK
        | Gdk.EventMask.KEY_RELEASE_MASK
        | Gdk.EventMask.BUTTON_PRESS_MASK
        | Gdk.EventMask.BUTTON_RELEASE_MASK
        | Gdk.EventMask.POINTER_MOTION_MASK
    )

    // keyboard
    drawingArea.on('key-press-event', keyHandler)
    drawingArea.on('key-release-event', keyHandler)
    const keysPressed = new Set<number>()
    function keyHandler(event) {
        const pressed = event.type === Gdk.EventType.KEY_PRESS
        const hidCode = mapGdkKeycodeToHid(event.hardwareKeycode)
        if (hidCode !== undefined) {
            keysPressed.delete(hidCode)
            pressed && keysPressed.add(hidCode)
            sendKeys()
        }
        return true // we consumed the event
    }
    function sendKeys() {
        // We reverse the array because we want the last pressed keys
        // first, so that formatKeyboardCommand will preserve them
        const hidCodes = Array.from(keysPressed).reverse()
        telnet.sendDvc(formatKeyboardCommand(hidCodes))
    }

    // mouse
    drawingArea.on('button-press-event', mouseEvent)
    drawingArea.on('button-release-event', mouseEvent)
    drawingArea.on('motion-notify-event', mouseEvent)
    let mousePosition = [0, 0]
    let buttonsPressed = 0
    function mouseEvent(event) {
        if (event.type === Gdk.EventType.BUTTON_PRESS)
            drawingArea.grabFocus()
        if (!screenSize)
            return
        if (event.type === Gdk.EventType.BUTTON_PRESS ||
            event.type === Gdk.EventType.BUTTON_RELEASE) {
            let bit
                 if (event.button === 1) bit = 1 << 0
            else if (event.button === 3) bit = 1 << 1
            else if (event.button === 2) bit = 1 << 2
            else return true

            if (event.type === Gdk.EventType.BUTTON_PRESS)
                buttonsPressed |= bit
            else
                buttonsPressed &= ~bit
            sendMouse()
        }
        if (event.type === Gdk.EventType.MOTION_NOTIFY) {
            mousePosition = [event.x / screenSize[0], event.y / screenSize[1]]
            sendMouse()
        }
        return true // we consumed the event
    }
    const sendMouse = () => 
        telnet.sendDvc(formatMouseCommand(mousePosition[0], mousePosition[1], buttonsPressed))


    // Rest of UI

    let quitting = false

    const frame = new Gtk.Frame()
    frame.shadowType = Gtk.ShadowType.IN
    frame.add(drawingArea)

    const buttonsBox = new Gtk.Box()
    buttonsBox.orientation = Gtk.Orientation.HORIZONTAL
    buttonsBox.spacing = 8
    const cmds = powerStatusCommands
    const btnData: [Buffer, string][] = [
        [cmds.MOMENTARY_PRESS, 'momentary press'],
        [cmds.PRESS_AND_HOLD, 'press and hold'],
        [cmds.POWER_CYCLE, 'power cycle'],
        [cmds.SYSTEM_RESET, 'system reset'],
    ]
    for (const [ data, label ] of btnData) {
        const btn = new Gtk.Button()
        btn.label = label
        btn.focusOnClick = false
        btn.on('clicked', () => telnet.sendDvc(data))
        buttonsBox.packStart(btn, false, true, 0)
    }

    const mainBox = new Gtk.Box()
    mainBox.orientation = Gtk.Orientation.VERTICAL
    mainBox.spacing = 8
    mainBox.packStart(frame, true, true, 0)
    mainBox.packStart(buttonsBox, false, true, 0)

    const win = new Gtk.Window()
    win.title = 'Console viewer'
    win.add(mainBox)
    win.borderWidth = 8
    win.on('destroy', () => {
        quitting = true
        Gtk.mainQuit()
        rcSocket.end()
        cmdSocket.end()
        if (invalidateTrackTimer)
            clearTimeout(invalidateTrackTimer)
    })
    win.on('delete-event', () => false)

    win.showAll()
    drawingArea.grabFocus()
    Gtk.main()

}

main().catch(e => process.nextTick(() => { throw e }))
