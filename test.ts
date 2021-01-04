import { connect } from 'net'
import { once } from 'events'

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
    console.log(`Encryption key: ${rcInfo.enc_key}`)
    console.log(`Optional features: ${rcInfo.optional_features}`)
    if (rcInfo.protocol_version !== '1.1')
        console.warn(`Warning: Untested protocol version (${rcInfo.protocol_version}), proceed with care`)

    // Set up the console connection
    const rcSocket = connect({ host: address, port: rcInfo.rc_port })
    rcSocket.setNoDelay(true)
    // code also disables SO_LINGER
    await once(rcSocket, 'connect')
    await negotiateConnection(false, rcSocket, client.sessionKey, rcInfo)

    // Set up the command connection
    const cmdSocket = connect({ host: address, port: rcInfo.rc_port })
    // code also disables SO_LINGER
    await once(cmdSocket, 'connect')
    await negotiateConnection(true, cmdSocket, client.sessionKey, rcInfo)


    // Create the socket -> decrypter -> decoder chain

    rcSocket.on('end', () => {
        throw Error('Socket disconnected')
    })
    rcSocket.on('data', data => {
        data.forEach(n => telnet.receive(n))
    })

    const encKey = Buffer.from(rcInfo.enc_key, 'hex')
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
            telnet.setEncryption(enc)
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
        }
        protected clearScreen() {
            console.log('Clear screen')
        }
        protected repaintScreen() {
            console.log('Repaint screen')
        }
        protected invalidateScreen() {
            console.log('Invalidate screen')
        }
    }

}

main().catch(e => process.nextTick(() => { throw e }))
