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
    /** keyboard input. argument is an 8-byte typical USB keyboard report, see [[formatKeyboardCommand]] */
    KEYBOARD = 0x0001,
    /** mouse input. argument is an 8-byte custom USB absolute mouse report, see [[formatMouseCommand]] */
    MOUSE = 0x0002,

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

/** Note: Excluding DV keycodes, only the first 6 keycodes in the array will be sent. */
export function formatKeyboardCommand(hidKeycodes: number[]) {
    const report = Buffer.alloc(8)
    let position = 2
    for (const keycode of hidKeycodes) {
        if (keycode !== (keycode & 0xFF))
            throw Error(`cannot send keycode ${keycode}`)
        if ((keycode & 0xF8) === 0xE0)
            report[0] |= (1 << (keycode & 0x07))
        else
            report[position++] = keycode
    }
    return formatCommand(Command.KEYBOARD, report)
}

/**
 * Screen coordinates should be divided by screen width / height first,
 * so they end up in range [0, 1].
 * 
 * `buttons` is a bitfield of the pressed buttons: bit 0 is the 'left'
 * button, bit 1 is the 'right' button, and bit 2 is the middle button.
 */
export function formatMouseCommand(x: number, y: number, buttons: number) {
    const report = Buffer.alloc(8)
    const clampAndRound = (n: number) => Math.round(3000 * Math.min(1, Math.max(0, n)) )
    report.writeUInt16LE(clampAndRound(x), 0)
    report.writeUInt16LE(clampAndRound(y), 2)
    // bytes 4-5 used to hold relative X / Y coordinates, it seems
    report[6] = buttons
    // byte 7 is unused
    return formatCommand(Command.MOUSE, report)
}
