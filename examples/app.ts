import { type as osType } from 'os'
import { connect } from 'net'
import { once } from 'events'

import { RestAPIClient } from '../dist/rest'
import { negotiateConnection } from '../dist/rc/handshake'
import { DvcEncryption, Telnet } from '../dist/rc/telnet'
import { DvcDecoder, MessageChannel } from '../dist/rc/video'
import { Command, formatCommand, formatKeyboardCommand, formatMouseCommand, powerStatusCommands } from '../dist/rc/command'

const gi = require('node-gtk')
const Gtk = gi.require('Gtk', '3.0')
const Gdk = gi.require('Gdk', '3.0')
const Cairo = gi.require('cairo', '2.0')

const args = process.argv.slice(2)
if (args.length !== 2) {
    console.error(`Usage: ${process.argv[1]} <iLO hostname[:port]> <username:password>`)
    console.error('\nOpens a graphical GTK+ remote console client.')
    console.error('\n(Assumes iLO is serving HTTPS)')
    process.exit(2)
}
const [ address, creds ] = args
const [ username, password ] = /^(.+?):(.+)$/.exec(creds).slice(1)

if (osType() !== 'Linux')
    console.warn('CAUTION: Keyboard input is implemented for Linux, and won\'t work correctly on other platforms.')

gi.startLoop()
Gtk.init()

// Based on "usb_kbd_keycode" from drivers/hid/usbhid/usbkbd.c @ 1a59d1b
// Maps KEY_BACKSLASH into 0x31 (not 0x32)
const linux_to_hid = [
    0, 41, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 45, 46, 42, 43,
   20, 26,  8, 21, 23, 28, 24, 12, 18, 19, 47, 48, 40,224,  4, 22,
    7,  9, 10, 11, 13, 14, 15, 51, 52, 53,225, 49, 29, 27,  6, 25,
    5, 17, 16, 54, 55, 56,229, 85,226, 44, 57, 58, 59, 60, 61, 62,
   63, 64, 65, 66, 67, 83, 71, 95, 96, 97, 86, 92, 93, 94, 87, 89,
   90, 91, 98, 99,  0,148,100, 68, 69,135,146,147,138,136,139,140,
   88,228, 84, 70,230,  0, 74, 82, 75, 80, 79, 77, 81, 78, 73, 76,
    0,127,129,128,102,103,  0, 72,  0,133,144,145,137,227,231,101,
  120,121,118,122,119,124,116,125,126,123,117,  0,251,  0,248,  0,
    0,  0,  0,  0,  0,  0,240,  0,249,  0,  0,  0,  0,  0,241,242,
    0,236,  0,235,232,234,233,  0,  0,  0,  0,  0,  0,250,  0,  0,
  247,245,246,  0,  0,  0,  0,104,105,106,107,108,109,110,111,112,
  113,114,115
]

function mapGdkKeycodeToHid(n: number) {
    // subtract 8 to get actual keycode
    n -= 8
    // we'll then assume this is an evdev keycode, and use the table above
    if (!linux_to_hid[n]) return
    return linux_to_hid[n]
}

async function main() {
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
            decoder.process(n)
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
        protected requestResync() {
            console.log('Requesting resync...')
            telnet.sendDvc(formatCommand(Command.REQUEST_RESYNC))
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
        }
    }



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

    // FIXME: what's wrong with gtk.alignment?
    const frameAlBox = new Gtk.Box()
    frameAlBox.orientation = Gtk.Orientation.HORIZONTAL
    frameAlBox.packStart(frame, true, false, 0)

    const mainBox = new Gtk.Box()
    mainBox.orientation = Gtk.Orientation.VERTICAL
    mainBox.spacing = 8
    mainBox.packStart(frameAlBox, true, false, 0)
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
    })
    win.on('delete-event', () => false)

    win.showAll()
    drawingArea.grabFocus()
    Gtk.main()

}

main().catch(e => process.nextTick(() => { throw e }))
