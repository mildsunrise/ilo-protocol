/**
 * Commands sent to server are a 16-bit LE followed by optional arguments
 * The 8 upper bits seem to have flags.
 */
export function formatCommand(command: number, args?: Uint8Array) {
    const buf = Buffer.alloc(2)
    buf.writeUInt16LE(command, 0)
    return args ? Buffer.concat([buf, args]) : buf
}

export enum Command {
    // at handshake time

    /**
     * negotiate a remote console connection. argument is a 32-byte
     * session key in hex, optionally encrypted, see [negotiateConnection]
     */
    RC_CONNECT = 0x2001,
    /**
     * negotiate a command connection. argument is a 32-byte
     * session key in hex, optionally encrypted, see [negotiateConnection]
     */
    CMD_CONNECT = 0x2002,
    /** seize the session from the other user. no arguments */
    SEIZE_CONNECTION = 0x0055,
    /** share the session with the other user. no arguments */
    SHARE_CONNECTION = 0x0056,

    // input commands

    /** power button / status command. argument is u16, see [[powerStatusCommands]] */
    POWER_STATUS = 0x0000,
    /** keyboard input. argument is an 8-byte typical USB keyboard report */
    KEYBOARD = 0x0001,

    /** restart the video stream. no arguments */
    REFRESH_SCREEN = 0x0005,
    /** no arguments */
    ACK = 0x000c,
}

function u16(n: number) {
    const b = Buffer.alloc(2)
    b.writeUInt16LE(n)
    return b
}

export const powerStatusCommands = {
    MOMENTARY_PRESS: formatCommand(Command.POWER_STATUS, u16(0)),
    PRESS_AND_HOLD: formatCommand(Command.POWER_STATUS, u16(1)),
    POWER_CYCLE: formatCommand(Command.POWER_STATUS, u16(2)),
    SYSTEM_RESET: formatCommand(Command.POWER_STATUS, u16(3)),
}

export function formatKeyboardCommand(hidCodes: number[]) {
    const report = Buffer.alloc(8)
    let position = 2
    for (const hidCode of hidCodes) {
        if ((hidCode & 0xE0) === 0xE0 && (hidCode & 0x1F) < 8) {
            report[0] |= (1 << (hidCode & 0x1F))
            continue
        }
        report[position++] = hidCode
        if (position >= report.length)
            position = report.length - 1
    }
    return formatCommand(Command.KEYBOARD, report)
}
