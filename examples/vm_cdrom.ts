import { connect } from 'net'
import { once } from 'events'
import { open } from 'fs/promises'
import { constants as fsConstants } from 'fs'

import { RestAPIClient } from '../dist/rest'
import { DeviceType, negotiateConnection } from '../dist/vm/handshake'
import { VirtualDevice } from '../dist/vm/scsi'

const { O_DIRECT, O_RDONLY } = fsConstants

const args = process.argv.slice(2)
if (args.length !== 3) {
    console.error(`Usage: ${process.argv[1]} <iLO hostname[:port]> <username:password> <device backing file>`)
    console.error('\nSimple program that mounts the supplied file as a CDROM device.')
    console.error('\n(Assumes iLO is serving HTTPS)')
    process.exit(2)
}
const [ address, creds, vmFilename ] = args
const [ username, password ] = /^(.+?):(.+)$/.exec(creds).slice(1)

async function main() {
    // Open the file
    const vmFile = await open(vmFilename, O_RDONLY /*| O_DIRECT*/) // FIXME: we should use O_DIRECT but then we have to align reads

    // Connect to iLO
    const { address, username, password } = require('./config.json')

    const client = new RestAPIClient(`https://${address}`)
    client.sessionKey = Buffer.from('f2ef21a47a7d9607e86b853b23eca0dc', 'hex')
    const encKey = 'Be06BaE021b0CE2eBDFBCE0A10fe38d6'
    //await client.loginSession(username, password)

    console.log(`Session key: ${client.sessionKey.toString('hex')}`)
    //const rcInfo = await client.getRcInfo()
    const rcInfo: any = { encKey, vmPort: 17988, rcPort: 17990, optionalFeatures: new Set(['ENCRYPT_KEY', 'ENCRYPT_CMD', 'ENCRYPT_VMKEY']), protocolVersion: '1.1',  }
    console.log(`Encryption key: ${rcInfo.encKey}`)
    console.log(`Optional features: ${Array.from(rcInfo.optionalFeatures).join(', ')}`)
    if (rcInfo.protocolVersion !== '1.1')
        console.warn(`Warning: Untested protocol version (${rcInfo.protocolVersion}), proceed with care`)

    // Set up the virtual media connection
    const vmSocket = connect({ host: address, port: rcInfo.vmPort })
    vmSocket.setNoDelay(true)
    await once(vmSocket, 'connect')
    const vmVersion = await negotiateConnection(vmSocket, client.sessionKey, rcInfo, {
        deviceType: DeviceType.CDROM,
        targetIsDevice: false,
    })
    console.log(`Connected to virtual media session. Version: ${vmVersion.join('.')}`)


    // Create the socket -> SCSI chain

    const scsi = new class extends VirtualDevice {
        protected send(data: Buffer) {
            vmSocket.write(data) // FIXME
        }

        protected async mediaRead(buffer: Buffer, pos: number) {
            const res = await vmFile.read(buffer, 0, buffer.length, pos)
            return res.bytesRead == 0  // if no bytes were read, we're out of bounds
        }
        protected async mediaSize() {
            const stat = await vmFile.stat()
            return stat.size
        }

        protected notifyMediaEject() {
            console.log('media ejected')
        }
        protected notifyMediaRemoval(prevented: boolean) {
            console.log(`media removal ${prevented ? 'prevented' : 'allowed'}`)
        }
    }

    scsi.receive(Buffer.alloc(0)) // to trigger keep-alive
    for await (const chunk of vmSocket) {
        await scsi.receive(chunk)
    }
    throw Error('stream ended')
}

main().catch(e => process.nextTick(() => { throw e }))
