import got, { Options } from 'got'

export type Nboolean = 0 | 1

export interface SessionInfo {
    user_name: string,
    user_account: string,
    user_dn: string,
    user_type: string,
    user_ip: string,
    user_expires: string,
    login_priv: Nboolean,
    remote_cons_priv: Nboolean,
    virtual_media_priv: Nboolean,
    reset_priv: Nboolean,
    config_priv: Nboolean,
    user_priv: Nboolean
}

export interface LoginResponse extends SessionInfo {
    session_key: string
}

export interface VirtualMediaStatus {
    options: VirtualMediaOption[],
    port: number,
    device?: any,
    boot_option?: any,
    command?: any,
    legacy_bios: Nboolean,
}

export interface VirtualMediaOption {
    device: "FLOPPY" | "CDROM",
    image_inserted: Nboolean,
    boot_option: "NO_BOOT",
    write_protect_flag: Nboolean,
    vm_url_connected: Nboolean,
    vm_connected: Nboolean,
    image_url: "",
    image_url_file: "",
}

export interface RemoteConsoleInfo {
    enc_key: string,
    enc_type: 0,
    rc_port: number,
    vm_key: string,
    vm_port: number,
    cmd_enc_key: string,
    protocol_version: string, // '1.1'
    optional_features: string, // separated by ';'
    server_name: string,
    ilo_fqdn: string,
    blade: number,
    bay: null,
    enclosure: null
}

export enum Features {
    ENCRYPT_KEY = 'ENCRYPT_KEY',
    ENCRYPT_VMKEY = 'ENCRYPT_VMKEY',
    ENCRYPT_CMD = 'ENCRYPT_CMD',
}

/**
 * Client for the HTTPS API at /json/*. Call `loginSession`
 * first to get a session key. iLO blocks you for some time
 * if you fail three(?) times.
 * 
 * Attempting to call methods with an expired session key
 * will result in 403.
 */
export class RestAPIClient {
    base: string
    requestOptions: any
    sessionKey?: Buffer

    private _getOptions(): object {
        const opts = { ...this.requestOptions }
        if (this.sessionKey) {
            opts.headers = { ...opts.headers }
            opts.headers.cookie = `sessionKey=${this.sessionKey.toString('hex')}`
        }
        return opts
    }

    constructor(base: string, requestOptions?: any) {
        this.base = base
        if (!requestOptions)
            requestOptions = { https: { rejectUnauthorized: false } }
        this.requestOptions = requestOptions
    }

    /**
     * Logs in, returns session info (or fails with 403)
     * 
     * (also sets `sessionKey` internally for further requests)
     */
    async loginSession(username: string, password: string) {
        const response = await got.post(this.base + '/json/login_session', {
            ...this._getOptions(),
            json: {
                method: 'login',
                user_login: username,
                password: password,
            },
            responseType: 'json',
        })
        const body = response.body as LoginResponse
        if (typeof body.session_key !== 'string' || !/^[0-9a-f]{32}$/.test(body.session_key))
            throw new Error('expected session_key')
        this.sessionKey = Buffer.from(body.session_key, 'hex')
        return body
    }

    async getSessionInfo() {
        const response = await got(this.base + '/json/session_info', {
            ...this._getOptions(),
            responseType: 'json',
        })
        const body = response.body as SessionInfo
        return body
    }

    async getVmStatus() {
        const response = await got(this.base + '/json/vm_status', {
            ...this._getOptions(),
            responseType: 'json',
        })
        const body = response.body as VirtualMediaStatus
        return body
    }

    async getRcInfo() {
        const response = await got(this.base + '/json/rc_info', {
            ...this._getOptions(),
            responseType: 'json',
        })
        const body = response.body as RemoteConsoleInfo
        return body
    }
}
