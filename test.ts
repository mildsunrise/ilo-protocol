import { connect } from 'net'
import { once } from 'events'
import { writeFileSync } from 'fs'

import { RestAPIClient } from './lib/rest'
import { negotiateConnection, formatCommand, Command } from './lib/rc/handshake'
import { DvcEncryption, Telnet } from './lib/rc/telnet'
import { DvcDecoder, MessageChannel } from './lib/rc/video'

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
        }
        protected seize() {
            console.log('Connection seized (TODO)')
        }
        protected ping() {
            telnet.sendDvc(formatCommand(Command.ACK))
        }
        protected requestScreenRefresh() {
            telnet.sendDvc(formatCommand(Command.REFRESH_SCREEN))
        }

        protected setScreenDimensions(x: number, y: number) {
            console.log(`Screen dimensions: ${x} x ${y}`)
            canvasW = x
            canvasH = y
            canvas = new Uint32Array(canvasW * canvasH)
        }
        protected renderBlock(block: Uint32Array, x: number, y: number, width: number, height: number) {
            if (x >= canvasW || y >= canvasH)
                throw Error()
            const scan = width
            width = Math.min(canvasW - x, width)
            height = Math.min(canvasH - y, height)
            for (let blockOffset = 0, canvasOffset = y * canvasW + x;
                blockOffset < block.length;
                blockOffset += scan, canvasOffset += canvasW) {
                canvas.set(block.subarray(blockOffset, blockOffset + width), canvasOffset)
            }
        }
        protected clearScreen() {
            console.log('Clear screen')
        }
        public repaintScreen() {
            //console.log('Repaint screen')
            //writeFrame()
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

    let canvasW: number, canvasH: number
    let canvas: Uint32Array
    const getCanvasBuf = () => Buffer.from(canvas.buffer, canvas.byteOffset, canvas.byteLength)

    setInterval(() => {
        if (!decoder.ok || !decoder.video_detected)
            return
        writeFileSync('/tmp/frame.raw', getCanvasBuf())
    }, 2000)

}

main().catch(e => process.nextTick(() => { throw e }))
