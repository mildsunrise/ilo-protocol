package com.hp.ilo2.remcons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class cmd implements Runnable {
    protected Thread receiver;
    protected Socket s;
    protected DataInputStream in;
    protected DataOutputStream out;
    protected String login = "";

    protected String host = "";

    public static final int TELNET_PORT = 23;

    protected int port = 23;

    protected int connected = 0;

    remcons cmdHandler;

    public synchronized void transmit(String paramString) {
        System.out.println("in cmd::transmit");
        if (this.out == null) {
            return;
        }
        if (paramString.length() != 0) {
            byte[] arrayOfByte = new byte[paramString.length()];

            for (byte b = 0; b < paramString.length(); b++) {
                arrayOfByte[b] = (byte) paramString.charAt(b);
            }

            try {
                this.out.write(arrayOfByte, 0, arrayOfByte.length);
            } catch (IOException iOException) {

                System.out.println("telnet.transmit() IOException: " + iOException);
            }
        }
    }

    public String getLocalString(int paramInt) {
        String str = "";
        try {
            str = this.cmdHandler.ParentApp.locinfoObj.getLocString(paramInt);
        } catch (Exception exception) {
            System.out.println("VSeizeDialog:getLocalString" + exception.getMessage());
        }
        return str;
    }

    public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {
        try {
            this.out.write(paramArrayOfbyte, 0, paramInt);
        } catch (IOException iOException) {

            System.out.println("cmd.transmitb() IOException: " + iOException);
        }
    }

    public void sendBool(boolean paramBoolean) {
        byte[] arrayOfByte = new byte[4];
        if (paramBoolean == true) {
            arrayOfByte[0] = 4;
        } else {
            arrayOfByte[0] = 3;
        }
        arrayOfByte[1] = 0;
        arrayOfByte[2] = 0;
        arrayOfByte[3] = 0;
        transmitb(arrayOfByte, arrayOfByte.length);
    }

    public void run() {
        byte[] arrayOfByte1 = new byte[12];
        byte[] arrayOfByte2 = new byte[1];
        byte[] arrayOfByte3 = new byte[4];
        byte[] arrayOfByte4 = new byte[128];

        int i = 0;

        short s2 = 0;
        short s1 = 0;
        try {
            while (true) {
                String str1, str2, str3, str4, str5;
                boolean bool;
                byte b = 0;
                int j = b;
                while (b < 12) {
                    j = this.in.read(arrayOfByte2, 0, 1);
                    if (j == 1) {
                        arrayOfByte1[b++] = arrayOfByte2[0];
                    }
                }
                byte b1 = arrayOfByte1[0];
                byte b2 = arrayOfByte1[4];
                s1 = (short) arrayOfByte1[8];
                s2 = (short) arrayOfByte1[10];

                switch (b1) {
                    case 2:
                        System.out.println("Received Post complete notification\n");
                        this.cmdHandler.session.post_complete = true;
                        this.cmdHandler.session.set_status(4, "");
                        break;

                    case 3:
                        if (b2 != 1)
                            System.out.println("Invalid size for cmd: " + b1 + " size:" + b2);
                        this.in.read(arrayOfByte2, 0, 1);
                        this.cmdHandler.setPwrStatusPower(arrayOfByte2[0]);
                        break;
                    case 4:
                        if (b2 != 1)
                            System.out.println("Invalid size for cmd: " + b1 + " size:" + b2);
                        this.in.read(arrayOfByte2, 0, 1);
                        this.cmdHandler.setPwrStatusHealth(arrayOfByte2[0]);
                        break;
                    case 5:
                        if (!this.cmdHandler.session.post_complete) {
                            StringBuffer stringBuffer = new StringBuffer(16);

                            j = this.in.read(arrayOfByte4, 0, 2);
                            String str7 = Integer.toHexString(0xFF & arrayOfByte4[1]).toUpperCase();
                            String str8 = Integer.toHexString(0xFF & arrayOfByte4[0]).toUpperCase();
                            String str6 = stringBuffer.append(this.cmdHandler.getLocalString(12582)).append(str7)
                                    .append(str8).toString();

                            this.cmdHandler.session.set_status(4, str6);
                        }
                        break;
                    case 6:
                        System.out.println("Seized command notification\n");

                        j = this.in.read(arrayOfByte4, 0, 128);
                        str1 = "UNKNOWN";
                        str2 = "UNKNOWN";
                        System.out.println("Data rcvd for acquire " + arrayOfByte4 + "rd count " + j);
                        if (j > 0) {
                            String str = new String(arrayOfByte4);
                            System.out.println("Pakcet " + str);
                            str1 = str.substring(0, 63).trim();
                            str2 = str.substring(64, 127).trim();
                            if (str1.length() <= 0) {
                                str1 = "UNKNOWN";
                            }
                            if (str2.length() <= 0) {
                                str2 = "UNKNOWN";
                            }
                        } else {

                            System.out.println("Invalid acquire info");
                        }
                        i = this.cmdHandler.seize_dialog(str1, str2, s2);
                        if (i == 0) {
                            sendBool(true);
                            this.cmdHandler.seize_confirmed();
                            break;
                        }
                        sendBool(false);
                        break;

                    case 7:
                        this.in.read(arrayOfByte3, 0, 4);
                        this.cmdHandler.ack(arrayOfByte3[0], arrayOfByte3[1], arrayOfByte3[2], arrayOfByte3[3]);
                        break;

                    case 8:
                        System.out.println("Playback not supported now.\n");
                        break;

                    case 9:
                        System.out.println("Share command notification\n");

                        j = this.in.read(arrayOfByte4, 0, 128);
                        str3 = "UNKNOWN";
                        str4 = "UNKNOWN";

                        if (j > 0) {
                            String str = new String(arrayOfByte4);
                            System.out.println("Pakcet " + str);
                            str3 = str.substring(0, 63).trim();
                            str4 = str.substring(64, 127).trim();
                            if (str3.length() <= 0) {
                                str3 = "UNKNOWN";
                            }
                            if (str4.length() <= 0) {
                                str4 = "UNKNOWN";
                            }
                        } else {

                            System.out.println("Invalid acquire info");
                        }
                        sendBool(false);

                        this.cmdHandler.shared(str4, str3);
                        break;

                    case 10:
                        System.out.println("Firmware upgrade in progress notification\n");
                        this.cmdHandler.firmwareUpgrade();
                        break;
                    case 11:
                        System.out.println("Un authorized action performed\n");
                        str5 = "";
                        bool = false;
                        switch (s2) {
                            case 2:
                                str5 = getLocalString(8293);
                                bool = true;
                                break;
                            case 3:
                                str5 = getLocalString(8294);
                                break;

                            case 4:
                                str5 = getLocalString(8295);
                                break;
                            default:
                                str5 = "{0x" + s2 + "}";
                                break;
                        }
                        this.cmdHandler.unAuthorized(str5, bool);
                        break;

                    case 13:
                        this.in.read(arrayOfByte2, 0, 1);
                        System.out.println("VM notification from firmware\n");
                        break;

                    case 14:
                        System.out.println("Unlicensed notification from firmware\n");
                        this.cmdHandler.UnlicensedShutdown();
                        break;
                    case 15:
                        System.out.println("Reset notification from firmware\n");
                        this.cmdHandler.resetShutdown();
                        break;
                }

                b1 = 0;
            }
        } catch (Exception exception) {

            System.out.println("CMD exception: " + exception.toString());
            return;
        }
    }

    public boolean connectCmd(remcons paramremcons, String host, int port) {
        try {
            this.cmdHandler = paramremcons;

            byte[] sessionKey = new byte[32];
            byte[] header = new byte[2];

            this.s = new Socket(host, port);
            try {
                this.s.setSoLinger(true, 0);
            } catch (SocketException socketException) {

                System.out.println("connectCmd linger SocketException: " + socketException);
            }

            this.in = new DataInputStream(this.s.getInputStream());
            this.out = new DataOutputStream(this.s.getOutputStream());

            byte b = this.in.readByte();
            if (b == 80) {

                header[0] = 2;
                header[1] = 32;
                sessionKey = paramremcons.ParentApp.getParameter("RCINFO1").getBytes();

                if (paramremcons.ParentApp.optional_features.indexOf("ENCRYPT_KEY") != -1) {

                    for (byte b1 = 0; b1 < sessionKey.length; b1++) {
                        sessionKey[b1] = (byte) (sessionKey[b1] ^ (byte) paramremcons.ParentApp.enc_key
                                .charAt(b1 % paramremcons.ParentApp.enc_key.length()));
                    }
                    if (paramremcons.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
                        header[1] = (byte) (header[1] | 0x40);
                    } else {
                        header[1] = (byte) (header[1] | 0x80);
                    }
                }

                byte[] transmitBuf = new byte[header.length + sessionKey.length];
                System.arraycopy(header, 0, transmitBuf, 0, header.length);
                System.arraycopy(sessionKey, 0, transmitBuf, header.length, sessionKey.length);
                String str = new String(transmitBuf);
                transmit(str);
                b = this.in.readByte();
                if (b == 82) {
                    this.receiver = new Thread(this);
                    this.receiver.setName("cmd_rcvr");
                    this.receiver.start();
                } else {

                    System.out.println("login failed. read data" + b);
                }
            } else {

                System.out.println("Socket connection failure... ");
            }

        } catch (SocketException socketException) {

            System.out.println("telnet.connect() SocketException: " + socketException);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
            this.connected = 0;
        } catch (UnknownHostException unknownHostException) {

            System.out.println("telnet.connect() UnknownHostException: " + unknownHostException);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
            this.connected = 0;
        } catch (IOException iOException) {

            System.out.println("telnet.connect() IOException: " + iOException);
            this.s = null;
            this.in = null;
            this.out = null;
            this.receiver = null;
            this.connected = 0;
        }

        return true;
    }

    public void disconnectCmd() {
        if (this.receiver != null && this.receiver.isAlive()) {
            this.receiver.stop();
        }
        this.receiver = null;

        if (this.s != null) {
            try {
                System.out.println("Closing socket");
                this.s.close();
            } catch (IOException iOException) {

                System.out.println("telnet.disconnect() IOException: " + iOException);
            }
        }

        if (this.cmdHandler != null) {
            this.cmdHandler.setPwrStatusHealth(3);
            this.cmdHandler.setPwrStatusPower(0);
            this.cmdHandler = null;
        }
        this.s = null;
        this.in = null;
        this.out = null;
    }
}
