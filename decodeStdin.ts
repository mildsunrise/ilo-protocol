
import { writeFile } from 'fs/promises'

import { DvcEncryption, Telnet } from './lib/rc/telnet'
import { DvcDecoder, MessageChannel } from './lib/rc/video'

/**
 * Decodes a DVC stream on stdin, feeding it to Telnet + DvcDecoder.
 * 
 * Most events are printed to the console, and the reconstructed frame
 * is written to disk at the end of the stream (as 'frame-0.raw').
 * 
 * This tool is invoked with the encryption key as argument, i.e.
 * 
 * ts-node decodeStdin dF8AA31a548213db3Ae7f3b5d8b97142 < stream
 * 
 * To see debug output from the state machine, set the M_DEBUG
 * environment variable.
 */

async function main() {
    const encKeyHex = process.argv[2]
    if (!/^[0-9a-fA-F]{32}$/.test(encKeyHex))
        throw Error(`Invalid encryption key: ${encKeyHex}`)
    const stream = process.stdin
    stream.pause()

    const encKey = Buffer.from(encKeyHex, 'hex')
    const telnet = new class extends Telnet {
        protected send(data: Buffer) {
            console.log(`Sending data: ${data.toString('hex')}`)
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
            console.log('Ping')
        }
        protected requestScreenRefresh() {
            console.log('Request screen refresh')
        }

        protected setScreenDimensions(x: number, y: number) {
            console.log(`Screen dimensions: ${x} x ${y}`)
            canvasW = x
            canvasH = y
            canvas = new Uint32Array(canvasW * canvasH)
        }
        protected renderBlock(block: Uint32Array, x: number, y: number, width: number, height: number) {
            //console.log(`Got block at ${x} x ${y}`)
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
        }
        protected invalidateScreen() {
            //console.log('Invalidate screen')
            //writeFrame()
        }
    }

    let canvasW: number, canvasH: number
    let canvas: Uint32Array
    let frameNum = 0
    async function writeFrame() {
        if (!canvas) return console.log(' (skipping)')
        const canvasBuf = Buffer.from(canvas.buffer, canvas.byteOffset, canvas.byteLength)
        await writeFile(`frame-${frameNum++}.raw`, canvasBuf)
    }

    const start = Date.now()
    decoder.debug = !!process.env.M_DEBUG
    await consumeStream()
    const time = Date.now() - start
    console.log(`Stream processed in ${time}ms`)
    await writeFrame()

    async function consumeStream() {
        for await (const data of stream) {
            for (const b of Array.from(data as Buffer)) {
                telnet.receive(b)
                if (decoder.dvc_decoder_state === 38 && decoder.fatal_count > 50) {
                    console.log('Error: Machine hung, ending')
                    return
                }
            }
        }
    }
}

main().catch(e => process.nextTick(() => { throw e }))
