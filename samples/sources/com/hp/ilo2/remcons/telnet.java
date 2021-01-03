package com.hp.ilo2.remcons;

import com.hp.ilo2.virtdevs.VErrorDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class telnet extends JPanel implements Runnable, MouseListener, FocusListener, KeyListener {
  public static final int TELNET_PORT = 23;
  public static final int TELNET_ENCRYPT = 192;
  public static final int TELNET_CHG_ENCRYPT_KEYS = 193;
  public static final int TELNET_SE = 240;
  public static final int TELNET_NOP = 241;
  public static final int TELNET_DM = 242;
  public static final int TELNET_BRK = 243;
  public static final int TELNET_IP = 244;
  public static final int TELNET_AO = 245;
  public static final int TELNET_AYT = 246;
  public static final int TELNET_EC = 247;
  public static final int TELNET_EL = 248;
  public static final int TELNET_GA = 249;
  public static final int TELNET_SB = 250;
  public static final int TELNET_WILL = 251;
  public static final int TELNET_WONT = 252;
  public static final int TELNET_DO = 253;
  public static final int TELNET_DONT = 254;
  public static final int TELNET_IAC = 255;
  public static final int JAP_VK_OPEN_BRACKET = 194;
  public static final int JAP_VK_BACK_SLASH = 195;
  public static final int JAP_VK_CLOSE_BRACKET = 196;
  public static final int JAP_VK_COLON = 197;
  public static final int JAP_VK_RO = 198;
  private static final int CMD_TS_AVAIL = 194;
  private static final int CMD_TS_NOT_AVAIL = 195;
  private static final int CMD_TS_STARTED = 196;
  private static final int CMD_TS_STOPPED = 197;
  protected dvcwin screen;
  public JLabel status_box;
  protected Thread receiver;
  protected Socket s;
  protected DataInputStream in;
  protected DataOutputStream out;
  protected String login = "";

  protected String host = "";

  protected int port = 23;

  protected int connected = 0;

  protected int fore;

  protected int back;

  protected int hi_fore;

  protected int hi_back;

  protected String escseq;

  protected String curr_num;

  protected int[] escseq_val = new int[10];

  protected int escseq_val_count = 0;

  private boolean crlf_enabled = false;

  public boolean mirror = false;

  private RC4 RC4decrypter;

  private Aes aes128decrypter;
  private Aes aes256decrypter;
  protected byte[] decrypt_key = new byte[16];
  private boolean decryption_active = false;
  protected boolean encryption_enabled = false;
  private Process rdpProc = null;
  private boolean enable_terminal_services = false;
  private int terminalServicesPort = 3389;

  int ts_type;

  private boolean tbm_mode = false;

  protected boolean dvc_mode = false;

  protected boolean dvc_encryption = false;

  private int total_count = 0;

  public byte[] sessionKey = new byte[32];

  public String st_fld1 = "";
  public String st_fld2 = "";
  public String st_fld3 = "";
  public String st_fld4 = "";

  public boolean post_complete = false;
  private boolean seized = false;
  public int dbg_print = 0;

  public cmd cmdObj;

  public remcons remconsObj;
  LocaleTranslator translator = new LocaleTranslator();

  private int[] keyMap = new int[256];

  private int japanese_kbd = 0;
  public final int PWR_OPTION_PULSE = 0;
  public final int PWR_OPTION_HOLD = 1;
  public final int PWR_OPTION_CYCLE = 2;
  public final int PWR_OPTION_RESET = 3;

  public int cipher = 0;

  public final int CIPHER_NONE = 0;
  public final int CIPHER_RC4 = 1;
  public final int CIPHER_AES128 = 2;
  public final int CIPHER_AES256 = 3;

  public final int AES_BITSIZE_128 = 0;
  public final int AES_BITSIZE_192 = 1;
  public final int AES_BITSIZE_256 = 2;

  public final int REQ_LOGIN_KEY = 0;
  public final int REQ_GET_AUTH = 1;
  public final int REQ_SHARE = 2;
  public final int REQ_SEIZE = 3;
  public final int REQ_DONE = 4;

  public final int CONNECT_CANCEL = 0;
  public final int CONNECT_SEIZE = 1;
  public final int CONNECT_SHARE = 2;

  public final int KEY_STATE_PRESSED = 0;
  public final int KEY_STATE_TYPED = 1;
  public final int KEY_STATE_RELEASED = 2;

  private boolean screenFocusLost = false;

  private int[] winkey_to_hid;

  private Locale lo;

  private String keyboardLayout;

  public void setLocale(String paramString) {
    this.translator.selectLocale(paramString);
  }

  public String getLocalString(int paramInt) {
    String str = "";
    try {
      str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
    } catch (Exception exception) {
      System.out.println("telnet:getLocalString" + exception.getMessage());
    }
    return str;
  }

  public void enable_debug() {
  }

  public void disable_debug() {
  }

  public void startRdp() {
    if (this.rdpProc == null) {
      String str1;
      Runtime runtime = Runtime.getRuntime();
      if (this.ts_type == 0) {
        str1 = "mstsc";
      } else if (this.ts_type == 1) {
        str1 = "vnc";
      } else {
        str1 = "type" + this.ts_type;
      }
      String str2 = remcons.prop.getProperty(str1 + ".program");
      System.out.println(str1 + " = " + str2);
      if (str2 != null) {
        str2 = percent_sub(str2);
        System.out.println("exec: " + str2);
        try {
          this.rdpProc = runtime.exec(str2);
        } catch (SecurityException securityException) {
          System.out
              .println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch " + str2);
        } catch (IOException iOException) {
          System.out.println("IOException: " + iOException.getMessage() + ":: " + str2);
        }
        return;
      }
      boolean bool = false;
      try {
        System.out.println("Executing mstsc. Port is " + this.terminalServicesPort);
        this.rdpProc = runtime.exec("mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort);
      } catch (SecurityException securityException) {
        System.out.println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch mstsc.");
      } catch (IOException iOException) {
        System.out.println("IOException: " + iOException.getMessage()
            + ":: mstsc not found in system directory. Looking in \\Program Files\\Remote Desktop.");
        bool = true;
      }
      if (bool) {
        bool = false;
        String[] arrayOfString = {
            "\\Program Files\\Remote Desktop\\mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort };
        try {
          this.rdpProc = runtime.exec(arrayOfString);
        } catch (SecurityException securityException) {
          System.out.println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch mstsc.");
        } catch (IOException iOException) {
          System.out.println("IOException: " + iOException.getMessage()
              + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
          bool = true;
        }
      }
      if (bool) {
        String[] arrayOfString = { "\\Program Files\\Terminal Services Client\\mstsc" };
        try {
          this.rdpProc = runtime.exec(arrayOfString);
        } catch (SecurityException securityException) {
          System.out.println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch mstsc.");
        } catch (IOException iOException) {
          System.out.println("IOException: " + iOException.getMessage()
              + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
        }
      }
    }
  }

  public void keyTyped(KeyEvent paramKeyEvent) {
    String str = "";
    sendKey(paramKeyEvent, 1);
  }

  public void keyPressed(KeyEvent paramKeyEvent) {
    String str = "";
    sendKey(paramKeyEvent, 0);
  }

  public void keyReleased(KeyEvent paramKeyEvent) {
    String str = "";
    sendKey(paramKeyEvent, 2);
  }

  public void send_auto_alive_msg() {
  }

  public synchronized void focusGained(FocusEvent paramFocusEvent) {
    if (paramFocusEvent.getComponent() == this.screen) {
      if (this.screenFocusLost) {
        this.remconsObj.remconsInstallKeyboardHook();
        this.screenFocusLost = false;
      }
    } else {
      this.screen.requestFocus();
    }
  }

  public synchronized void focusLost(FocusEvent paramFocusEvent) {
    if (paramFocusEvent.getComponent() == this.screen && paramFocusEvent.isTemporary()) {
      this.remconsObj.remconsUnInstallKeyboardHook();
      this.screenFocusLost = true;
    }
  }

  public synchronized void mouseClicked(MouseEvent paramMouseEvent) {
    requestFocus();
  }

  public synchronized void mousePressed(MouseEvent paramMouseEvent) {
  }

  public synchronized void mouseReleased(MouseEvent paramMouseEvent) {
  }

  public synchronized void mouseEntered(MouseEvent paramMouseEvent) {
  }

  public synchronized void mouseExited(MouseEvent paramMouseEvent) {
  }

  public synchronized void addNotify() {
    super.addNotify();
  }

  public synchronized void set_status(int paramInt, String paramString) {
    switch (paramInt) {
      case 1:
        this.st_fld1 = paramString;
        break;
      case 2:
        this.st_fld2 = paramString;
        break;
      case 3:
        this.st_fld3 = paramString;
        break;
      case 4:
        this.st_fld4 = paramString;
        break;
    }
    this.status_box.setText(this.st_fld1 + " " + this.st_fld2 + "      " + this.st_fld3 + "      " + this.st_fld4);
  }

  public void reinit_vars() {
    this.dvc_encryption = false;
  }

  public void setup_decryption(byte[] key) {
    System.arraycopy(key, 0, this.decrypt_key, 0, 16);
    this.RC4decrypter = new RC4(key);
    this.encryption_enabled = true;
    this.aes128decrypter = new Aes(0, key);
    this.aes256decrypter = new Aes(0, key);
  }

  public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2,
      int paramInt3, remcons paramremcons) {
    this.enable_terminal_services = ((paramInt2 & 0x1) == 1);
    this.ts_type = paramInt2 >> 8;
    if (paramInt3 != 0) {
      this.terminalServicesPort = paramInt3;
    }
    if (this.connected == 0) {
      this.screen.start_updates();
      this.connected = 1;
      this.host = paramString1;
      this.login = paramString2;
      this.port = paramInt1;
      this.remconsObj = paramremcons;
      requestFocus();
      this.sessionKey = paramremcons.ParentApp.getParameter("RCINFO1").getBytes();
      String str = paramremcons.ParentApp.rc_port;
      if (str != null) {
        try {
          this.port = Integer.parseInt(str);
          System.out.println("RC port number " + this.port);
        } catch (NumberFormatException numberFormatException) {
          System.out.println("Failed to read rcport from parameters");
          this.port = 23;
        }
      }
      try {
        set_status(1, getLocalString(12296));
        System.out.println("updated: connecting to " + this.host + ":" + this.port);
        try {
          Thread.currentThread();
          Thread.sleep(1000L);
        } catch (InterruptedException interruptedException) {
          System.out.println("connect Thread interrupted..");
        }
        this.s = new Socket(this.host, this.port);
        try {
          this.s.setSoLinger(true, 0);
          System.out.println("set TcpNoDelay");
          this.s.setTcpNoDelay(true);
        } catch (SocketException socketException) {
          System.out.println("telnet.connect() linger SocketException: " + socketException);
        }
        this.in = new DataInputStream(this.s.getInputStream());
        this.out = new DataOutputStream(this.s.getOutputStream());
        byte b = this.in.readByte();
        if (b == 80) {
          set_status(1, getLocalString(12297));
          boolean bool = false;
          System.out.println("Received hello byte. Requesting remote connection...");
          char c = 0x2001;
          bool = requestRemoteConnection(c);
          if (bool) {
            this.receiver = new Thread(this);
            this.receiver.setName("telnet_rcvr");
            this.receiver.start();
            this.cmdObj.connectCmd(this.remconsObj, this.host, this.port);
          } else {
            paramremcons.ParentApp.stop();
          }
        } else {
          set_status(1, getLocalString(12298));
          System.out.println("Socket connection failure... ");
        }
      } catch (SocketException socketException) {
        System.out.println("telnet.connect() SocketException: " + socketException);
        set_status(1, socketException.toString());
        this.s = null;
        this.in = null;
        this.out = null;
        this.receiver = null;
        this.connected = 0;
      } catch (UnknownHostException unknownHostException) {
        System.out.println("telnet.connect() UnknownHostException: " + unknownHostException);
        set_status(1, unknownHostException.toString());
        this.s = null;
        this.in = null;
        this.out = null;
        this.receiver = null;
        this.connected = 0;
      } catch (IOException iOException) {
        System.out.println("telnet.connect() IOException: " + iOException);
        set_status(1, iOException.toString());
        this.s = null;
        this.in = null;
        this.out = null;
        this.receiver = null;
        this.connected = 0;
      }
    } else {
      requestFocus();
    }
  }

  public boolean requestRemoteConnection(int headerInt) {
    boolean bool = false;
    byte state = 0;
    byte[] header = new byte[2];
    byte serverByte = 0;
    while (state != 4) {
      byte[] transmitBuf;
      String str1;
      boolean bool1;
      byte[] arrayOfByte2;
      String str2;
      byte[] arrayOfByte3;
      String str3;
      switch (state) {
        case 0: // INITIAL
          header[0] = (byte) (headerInt & 0xFF);
          header[1] = (byte) ((headerInt & 0xFF00) >>> 8);
          if (this.remconsObj.ParentApp.optional_features.indexOf("ENCRYPT_KEY") != -1) {
            for (byte b2 = 0; b2 < this.sessionKey.length; b2++) {
              this.sessionKey[b2] = (byte) (this.sessionKey[b2]
                  ^ (byte) this.remconsObj.ParentApp.enc_key.charAt(b2 % this.remconsObj.ParentApp.enc_key.length()));
            }
            if (this.remconsObj.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
              header[1] = (byte) (header[1] | 0x40);
            } else {
              header[1] = (byte) (header[1] | 0x80);
            }
          }
          transmitBuf = new byte[header.length + this.sessionKey.length];
          System.arraycopy(header, 0, transmitBuf, 0, header.length);
          System.arraycopy(this.sessionKey, 0, transmitBuf, header.length, this.sessionKey.length);
          str1 = new String(transmitBuf);
          transmit(str1);
          state = 1;
        case 1: // WAITING FOR RESPONSE
          try {
            serverByte = this.in.readByte();
          } catch (IOException iOException) {
            bool = false;
            state = 4;
            System.out.println("Socket Read failed.");
            continue;
          }
          switch (serverByte) {
            case 81:
              System.out.println("Access denied.");
              set_status(1, getLocalString(12299));
              if (null != this.remconsObj.ParentApp.dispFrame) {
                new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8239), getLocalString(8287), true);
                this.remconsObj.ParentApp.dispFrame.setVisible(false);
              } else {
                new VErrorDialog(getLocalString(8287), true);
              }
              bool = false;
              state = 4;
              this.remconsObj.ParentApp.stop();
              continue;
            case 82:
              set_status(1, getLocalString(12300));
              System.out.println("Authenticated");
              bool = true;
              this.remconsObj.licensed = true;
              state = 4;
              continue;
            case 83:
            case 89:
              System.out.println("Authenticated, but busy, negotiating");
              if (0 == this.remconsObj.retry_connection_count) {
                bool1 = negotiateBusy();
                System.out.println("negotiateResult:" + bool1);
              } else {
                System.out.println("Overriding seize option for internal retry");
                bool1 = true;
              }
              switch (bool1) {
                case false:
                  System.out.println("Connection cancelled by user");
                  if (null != this.remconsObj.ParentApp.dispFrame) {
                    this.remconsObj.ParentApp.dispFrame.setVisible(false);
                  }
                  bool = false;
                  state = 4;
                  continue;
                case true:
                  header[0] = 85;
                  header[1] = 0;
                  arrayOfByte2 = new byte[header.length];
                  System.arraycopy(header, 0, arrayOfByte2, 0, header.length);
                  System.out.println("Seizing connection, sending command 0x0055");
                  str2 = new String(arrayOfByte2);
                  transmit(str2);
                  state = 3;
                  set_status(1, getLocalString(12568));
                  continue;
                case true:
                  header[0] = 86;
                  header[1] = 0;
                  System.out.println("Sharing connection, sending command 0x0056");
                  arrayOfByte3 = new byte[header.length];
                  System.arraycopy(header, 0, arrayOfByte3, 0, header.length);
                  str3 = new String(arrayOfByte3);
                  transmit(str3);
                  state = 2;
                  continue;
              }
              continue;
            case 87:
              System.out.println("Received No License Notification");
              this.remconsObj.licensed = false;
              bool = false;
              state = 4;
              continue;
            case 88:
              System.out.println("No free Sessions Notification");
              if (null != this.remconsObj.ParentApp.dispFrame) {
                new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(0x202f), getLocalString(0x2030), true);
                this.remconsObj.ParentApp.dispFrame.setVisible(false);
              } else {
                new VErrorDialog(getLocalString(0x2030), true);
              }
              bool = false;
              state = 4;
              this.remconsObj.ParentApp.stop();
              continue;
          }
          System.out.println("rqrmtconn default: " + serverByte);
          bool = true;
          state = 4;
        case 2: // SHARING SENT // FIXME: verify bytecode
          bool = false;
          state = 4;
        case 3: // SEIZE SENT // FIXME: verify bytecode
          state = 4;
          try {
            serverByte = this.in.readByte();
          } catch (IOException iOException) {
            bool = false;
            state = 4;
            System.out.println("Socket Read failed.");
            continue;
          }
          switch (serverByte) {
            case 81:
              if (null != this.remconsObj.ParentApp.dispFrame) {
                new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8239), getLocalString(8263), true);
                this.remconsObj.ParentApp.dispFrame.setVisible(false);
              } else {
                new VErrorDialog(getLocalString(8263), true);
              }
              bool = false;
            case 82:
              this.remconsObj.ParentApp.moveUItoInit(true);
              bool = true;
          }
      }
    }
    return bool;
  }

  public int negotiateBusy() {
    boolean bool = false;
    this.remconsObj.ParentApp.moveUItoInit(false);
    VSeizeDialog vSeizeDialog = new VSeizeDialog(this.remconsObj);
    switch (vSeizeDialog.getUserInput()) {
      case 2:
        bool = true;
        break;
      case 0:
        bool = false;
        break;
    }
    return bool;
  }

  public void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, remcons paramremcons) {
    connect(paramString1, paramString2, this.port, paramInt1, paramInt2, paramremcons);
  }

  public void connect(String paramString, int paramInt1, int paramInt2, remcons paramremcons) {
    connect(paramString, this.login, this.port, paramInt1, paramInt2, paramremcons);
  }

  public synchronized void disconnect() {
    this.remconsObj.remconsUnInstallKeyboardHook();
    if (this.connected == 1) {
      this.screen.stop_updates();
      this.connected = 0;
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
          set_status(1, iOException.toString());
        }
      }
      this.s = null;
      this.in = null;
      this.out = null;
      if (this.cmdObj != null) {
        this.cmdObj.disconnectCmd();
      }
      set_status(1, getLocalString(12301));
      reinit_vars();
      this.decryption_active = false;
    }
  }

  public synchronized void transmit(String data) {
    if (this.out == null) {
      return;
    }
    if (data.length() != 0) {
      byte[] dataBuf = new byte[data.length()];
      for (byte i = 0; i < data.length(); i++) {
        dataBuf[i] = (byte) data.charAt(i);
      }
      try {
        this.out.write(dataBuf, 0, dataBuf.length);
      } catch (IOException iOException) {
        System.out.println("telnet.transmit() IOException: " + iOException);
      }
    }
  }

  public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {
  }

  protected synchronized String translate_key(KeyEvent paramKeyEvent) {
    char c = paramKeyEvent.getKeyChar();
    switch (c) {
      case '\n':
      case '\r':
        if (paramKeyEvent.isShiftDown()) {
          str = "\n";
        } else {
          str = "\r";
        }
        return str;
      case '\t':
        str = "";
        return str;
    }
    String str = this.translator.translate(c);
    return str;
  }

  protected synchronized String translate_special_key(KeyEvent paramKeyEvent) {
    String str = "";
    switch (paramKeyEvent.getKeyCode()) {
      case 9:
        paramKeyEvent.consume();
        str = "\t";
        break;
    }
    return str;
  }

  protected synchronized String translate_special_key_release(KeyEvent paramKeyEvent) {
    return "";
  }

  boolean process_dvc(char paramChar) {
    return true;
  }

  public void run() {
    boolean bool1 = false;
    byte b1 = 0;
    boolean bool2 = false;
    boolean bool3 = false;
    byte[] arrayOfByte = new byte[1024];
    byte b2 = 0;
    this.dvc_mode = true;
    System.out.println("Starting receiver run");
    try {
      while (true) {
        int b;
        try {
          if (this.s == null || this.in == null) {
            System.out.println("telnet.run() s or in is null");
            break;
          }
          this.s.setSoTimeout(1000);
          b = this.in.read(arrayOfByte);
        } catch (InterruptedIOException interruptedIOException) {
          continue;
        } catch (Exception exception) {
          b = -1;
          b2++;
        }
        if (b < 0) {
          if (b2 > 1) {
            System.out.println("Reading from stream failed for  " + b2 + "times");
            b2 = 0;
            break;
          }
          continue;
        }
        for (byte b3 = 0; b3 < b; b3++) {
          if (this.dbg_print == 1000) {
            this.dbg_print = 0;
          }
          this.dbg_print++;
          this.remconsObj.fdConnState = this.remconsObj.ParentApp.virtdevsObj.fdConnected;
          this.remconsObj.cdConnState = this.remconsObj.ParentApp.virtdevsObj.cdConnected;
          char c = (char) arrayOfByte[b3];
          c = (char) (c & 0xFF);
          if (this.dvc_mode) {
            if (this.dvc_encryption) {
              char c1;
              switch (this.cipher) {
                case 1:
                  c1 = (char) (this.RC4decrypter.randomValue() & 0xFF);
                  c = (char) (c ^ c1);
                  break;
                case 2:
                  c1 = (char) (this.aes128decrypter.randomValue() & 0xFF);
                  c = (char) (c ^ c1);
                  break;
                case 3:
                  c1 = (char) (this.aes256decrypter.randomValue() & 0xFF);
                  c = (char) (c ^ c1);
                  break;
                default:
                  c1 = Character.MIN_VALUE;
                  System.out.println("Unknown encryption");
                  break;
              }
              c = (char) (c & 0xFF);
            }
            this.dvc_mode = process_dvc(c);
            if (!this.dvc_mode) {
              System.out.println("DVC mode turned off");
              set_status(1, getLocalString(12302));
            }
          } else if (c == '\033') {
            b1 = 1;
          } else if (b1 == 1 && c == '[') {
            b1 = 2;
          } else if (b1 == 2 && c == 'R') {
            this.dvc_mode = true;
            this.dvc_encryption = true;
            set_status(1, getLocalString(12303));
          } else if (b1 == 2 && c == 'r') {
            this.dvc_mode = true;
            this.dvc_encryption = false;
            set_status(1, getLocalString(12292));
          } else {
            b1 = 0;
          }
        }
      }
    } catch (Exception exception) {
      System.out.println("telnet.run() Exception, class:" + exception.getClass() + "  msg:" + exception.getMessage());
      exception.printStackTrace();
    } finally {
      if (!this.seized) {
        if (this.remconsObj.retry_connection_count < 3) {
          this.screen.clearScreen();
          System.out.println("Retrying connection");
          set_status(1, getLocalString(12305));
        } else {
          this.screen.clearScreen();
          System.out.println("offline");
          set_status(1, getLocalString(12301));
        }
        set_status(2, "");
        set_status(3, "");
        set_status(4, "");
        System.out.println("Actually Retrying connection");
        this.remconsObj.retry_connection_flag = true;
      }
    }
    System.out.println("Completed receiver run");
  }

  public void change_key() {
    this.RC4decrypter.update_key();
  }

  void focusTraversalKeysDisable(Object paramObject) {
    Class[] arrayOfClass = { boolean.class };
    Object[] arrayOfObject1 = { Boolean.TRUE };
    Object[] arrayOfObject2 = { Boolean.FALSE };
    try {
      paramObject.getClass().getMethod("setFocusTraversalKeysEnabled", arrayOfClass).invoke(paramObject,
          arrayOfObject2);
    } catch (Throwable throwable) {
    }
    try {
      paramObject.getClass().getMethod("setFocusCycleRoot", arrayOfClass).invoke(paramObject, arrayOfObject1);
    } catch (Throwable throwable) {
    }
  }

  public void stop_rdp() {
    if (this.rdpProc != null) {
      try {
        this.rdpProc.exitValue();
      } catch (IllegalThreadStateException illegalThreadStateException) {
        System.out.println("IllegalThreadStateException thrown. Destroying TS.");
        this.rdpProc.destroy();
      }
      this.rdpProc = null;
    }
    System.out.println("TS stop.");
  }

  public void seize() {
    System.out.println("Received seize command. halting RC.");
    this.seized = true;
    this.screen.clearScreen();
    set_status(1, getLocalString(12306));
    set_status(2, "");
    set_status(3, "");
    set_status(4, "");
  }

  public void fwUpgrade() {
    System.out.println("Received FW Upgrade notification. Halting RC.");
    this.seized = true;
    this.screen.clearScreen();
    set_status(1, getLocalString(12307));
    set_status(2, "");
    set_status(3, "");
    set_status(4, "");
  }

  public void UnlicensedAccess() {
    System.out.println("Received UnlicensedAccess. Halting RC.");
    this.seized = true;
    this.screen.clearScreen();
    set_status(1, getLocalString(8236));
    set_status(2, "");
    set_status(3, "");
    set_status(4, "");
  }

  public void unAuthAccess() {
    System.out.println("Received unAuthAccess notification. Halting RC.");
    this.seized = true;
    this.screen.clearScreen();
    set_status(1, getLocalString(12308));
    set_status(2, "");
    set_status(3, "");
    set_status(4, "");
  }

  public String percent_sub(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '%') {
        c = paramString.charAt(++b);
        if (c == 'h') {
          stringBuffer.append(this.host);
        } else if (c == 'p') {
          stringBuffer.append(this.terminalServicesPort);
        } else {
          stringBuffer.append(c);
        }
      } else {
        stringBuffer.append(c);
      }
    }
    return stringBuffer.toString();
  }

  public byte[] getSessionKey() {
    String str = "0123456789abcdef0123456789abcdef";
    return str.getBytes();
  }

  public byte[] getSessionKey(String paramString) {
    String str = parseParameter(paramString, "sessionKey");
    if (str == "") {
      System.out.println("Parsing failed.");
    }
    byte[] arrayOfByte = str.getBytes();
    System.out.println("sessionKey : " + str);
    return arrayOfByte;
  }

  public void sendHidKeyCode(KeyEvent paramKeyEvent) {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    byte b = 0;
    int i = 0;
    int j = 0;
    i = paramKeyEvent.getKeyCode();
    j = this.keyMap[i];
    this.keyMap[i] = 1;
    if (j != this.keyMap[i]) {
      for (byte b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
        if (this.keyMap[b1] == 1) {
          b = (byte) this.winkey_to_hid[b1];
          arrayOfByte[4 + b2] = b;
          b2++;
          if (b2 == 6) {
            b2 = 5;
          }
        }
      }
    }
    String str = new String(arrayOfByte);
    transmit(str);
    paramKeyEvent.consume();
  }

  public void sendHidSpecialKeyCode(KeyEvent paramKeyEvent) {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    byte b = 0;
    char c = Character.MIN_VALUE;
    int i = 0;
    c = paramKeyEvent.getKeyChar();
    i = this.keyMap[c];
    this.keyMap[c] = 1;
    if (i != this.keyMap[c]) {
      for (byte b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
        if (this.keyMap[b1] == 1) {
          b = (byte) this.winkey_to_hid[b1];
          arrayOfByte[4 + b2] = b;
          b2++;
          if (b2 == 6) {
            b2 = 5;
          }
        }
      }
    }
    String str = new String(arrayOfByte);
    transmit(str);
    paramKeyEvent.consume();
  }

  public void clearKeyPress(KeyEvent paramKeyEvent) {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    int i = paramKeyEvent.getKeyCode();
    this.keyMap[i] = 0;
    String str = new String(arrayOfByte);
    transmit(str);
    paramKeyEvent.consume();
  }

  public telnet(remcons paramremcons) {
    this.winkey_to_hid = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 42, 43, 40, 0, 0, 40, 0, 0, 225, 224, 226, 72, 57, 0, 0, 0,
        0, 0, 41, 41, 138, 139, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 54, 45, 55, 56, 39, 30, 31, 32, 33,
        34, 35, 36, 37, 38, 0, 51, 0, 46, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
        23, 24, 25, 26, 27, 28, 29, 47, 49, 48, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 159, 86, 99, 84,
        58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 76, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        83, 71, 0, 0, 0, 0, 36, 37, 52, 54, 70, 73, 0, 0, 0, 0, 55, 47, 48, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, 46, 54, 45, 55, 56, 53, 135, 48, 137, 50, 52, 135, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 47, 49, 48, 52, 0, 96, 90, 92, 94, 0, 0, 0, 0, 0, 0, 0, 139, 0, 0, 0, 0, 136,
        136, 136, 53, 53, 0, 0, 0, 0, 0, 0, 0, 0, 138, 0, 255 };
    this.status_box = new JLabel();
    this.cmdObj = new cmd();
    this.remconsObj = paramremcons;
    this.screen = new dvcwin(1024, 768, this.remconsObj);
    System.out.println("Screen: " + this.screen);
    this.screen.addMouseListener(this);
    addFocusListener(this);
    this.screen.addFocusListener(this);
    this.screen.addKeyListener(this);
    focusTraversalKeysDisable(this.screen);
    focusTraversalKeysDisable(this);
    setBackground(Color.black);
    setLayout(new BorderLayout());
    add(this.screen, "North");
    set_status(1, getLocalString(12301));
    set_status(2, "          ");
    set_status(3, "          ");
    set_status(4, "          ");
    if (System.getProperty("os.name").toLowerCase().startsWith("windows") && !this.translator.windows) {
      this.translator.selectLocale("en_US");
    }
    for (byte b = 0; b < 'Ā'; b++) {
      this.keyMap[b] = 0;
    }
    this.lo = Locale.getDefault();
    this.keyboardLayout = this.lo.toString();
    System.out.println("telent lang: Keyboard layout is " + this.keyboardLayout);
    if (this.keyboardLayout.startsWith("ja")) {
      System.out.println("JAPANESE LANGUAGE \n");
      this.japanese_kbd = 1;
    } else {
      this.japanese_kbd = 0;
    }
  }

  public void sendCtrlAltDel() {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    arrayOfByte[2] = 5;
    arrayOfByte[4] = 76;
    String str1 = new String(arrayOfByte);
    transmit(str1);
    try {
      Thread.currentThread();
      Thread.sleep(500L);
    } catch (InterruptedException interruptedException) {
      System.out.println("Thread interrupted..");
    }
    arrayOfByte[2] = 0;
    arrayOfByte[4] = 0;
    String str2 = new String(arrayOfByte);
    transmit(str2);
  }

  public void sendPower(int paramInt) {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 0;
    switch (paramInt) {
      case 0:
        arrayOfByte[2] = 0;
        break;
      case 1:
        arrayOfByte[2] = 1;
        break;
      case 2:
        arrayOfByte[2] = 2;
        break;
      case 3:
        arrayOfByte[2] = 3;
        break;
    }
    arrayOfByte[3] = 0;
    String str = new String(arrayOfByte);
    transmit(str);
  }

  public synchronized void sendKey(KeyEvent paramKeyEvent, int paramInt) {
    if (this.remconsObj.kbHookInstalled != true || true != this.remconsObj.kbHookDataRcvd) {
      handleKey(paramKeyEvent, paramInt);
    }
  }

  public void handleKey(KeyEvent paramKeyEvent, int paramInt) {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    int i = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    int j = 0;
    int k = 0;
    j = paramKeyEvent.getKeyCode();
    if (this.japanese_kbd == 1 && (j == 92 || j == 91 || j == 93 || j == 513)) {
      switch (j) {
        case 91:
          j = 194;
          break;
        case 92:
          if ('_' == paramKeyEvent.getKeyChar()) {
            j = 198;
            break;
          }
          j = 195;
          break;
        case 93:
          j = 196;
          break;
        case 513:
          j = 197;
          break;
      }
    } else if (j > 255) {
      switch (j) {
        case 512:
          j = 91;
          break;
        case 514:
          j = 61;
          break;
        case 513:
          j = 93;
          break;
        case 515:
          j = 52;
          break;
        case 517:
          j = 49;
          break;
        case 519:
          j = 57;
          break;
        case 520:
          j = 51;
          break;
        case 521:
          j = 61;
          break;
        case 522:
          j = 48;
          break;
        case 523:
          j = 45;
          break;
        case 260:
          j = 242;
          break;
        case 259:
          j = 241;
          break;
        default:
          System.out.println("Unknown key " + j);
          j = 0;
          break;
      }
    }
    if (j != 0) {
      if (paramInt == 1) {
        j = 0;
      } else {
        if (paramInt == 0) {
          this.keyMap[j] = 1;
        } else {
          if (9 == j && 0 == this.keyMap[j]) {
            return;
          }
          this.keyMap[j] = 0;
          if (paramKeyEvent.isAltDown() && j == 154) {
            sendAltSysReq();
          }
        }
        if (!paramKeyEvent.isAltDown() && 0 != this.keyMap[18]) {
          this.keyMap[18] = 0;
        }
        if (isSpecialReleaseKey(j)) {
          this.keyMap[j] = 1;
        }
        byte b1;
        byte b2;
        for (b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
          if (this.keyMap[b1] == 1) {
            i = (byte) this.winkey_to_hid[b1];
            if (i == 224) {
              k |= 0x1;
            }
            if (i == 226) {
              k |= 0x2;
            }
            if (i == 76) {
              k |= 0x4;
            }
            if (((byte) i & 0xE0) == 224) {
              i = (byte) i ^ 0xE0;
              arrayOfByte[2] = (byte) (arrayOfByte[2] | (byte) (1 << (byte) i));
            } else {
              arrayOfByte[4 + b2] = (byte) i;
              b2++;
              if (b2 == 6) {
                b2 = 5;
              }
            }
          }
        }
        if (k == 7) {
          for (b1 = 0; b1 < 'Ā'; b1++) {
            this.keyMap[b1] = 0;
          }
        } else {
          transmitb(arrayOfByte, arrayOfByte.length);
          if (isSpecialReleaseKey(j)) {
            this.keyMap[j] = 0;
            arrayOfByte[9] = 0;
            arrayOfByte[8] = 0;
            arrayOfByte[7] = 0;
            arrayOfByte[6] = 0;
            arrayOfByte[5] = 0;
            arrayOfByte[4] = 0;
            for (b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
              if (this.keyMap[b1] == 1) {
                i = (byte) this.winkey_to_hid[b1];
                arrayOfByte[4 + b2] = (byte) i;
                b2++;
                if (b2 == 6) {
                  b2 = 5;
                }
              }
            }
            transmitb(arrayOfByte, arrayOfByte.length);
          }
        }
      }
    }
    paramKeyEvent.consume();
  }

  public boolean isSpecialReleaseKey(int paramInt) {
    boolean bool = false;
    switch (paramInt) {
      case 28:
      case 29:
      case 240:
      case 243:
      case 244:
        bool = true;
        break;
    }
    return bool;
  }

  class statusUpdateTimer implements TimerListener {
    private final telnet this$0;

    statusUpdateTimer(telnet this$0) {
      this.this$0 = this$0;
    }

    public void timeout(Object param1Object) {
      System.out.println("Video data reception timeout occurred. Clearing status.");
      this.this$0.set_status(1, " ");
    }
  }

  public void printByteArray(byte[] paramArrayOfbyte, int paramInt) {
    if (paramInt < 0) {
      return;
    }
    for (byte b = 0; b < paramInt; b++) {
      System.out.print("0x" + Integer.toHexString(paramArrayOfbyte[b]) + " ");
    }
    System.out.println("\n");
  }

  public String parseParameter(String paramString1, String paramString2) {
    String str1 = "[&]";
    String str2 = "[=]";
    String str3 = "";
    System.out.println("Invoking url's query: " + paramString1);
    if (paramString1 == null) {
      return str3;
    }
    String[] arrayOfString = paramString1.split(str1);
    for (byte b = 0; b < arrayOfString.length; b++) {
      String[] arrayOfString1 = arrayOfString[b].split(str2);
      if (arrayOfString1[0] == paramString2) {
        str3 = arrayOfString1[1];
        break;
      }
    }
    return str3;
  }

  public void sendAltSysReq() {
    byte[] arrayOfByte = new byte[10];
    arrayOfByte[0] = 1;
    arrayOfByte[1] = 0;
    arrayOfByte[2] = 0;
    arrayOfByte[3] = 0;
    arrayOfByte[4] = 0;
    arrayOfByte[5] = 0;
    arrayOfByte[6] = 0;
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[2] = 4;
    arrayOfByte[4] = 70;
    this.remconsObj.session.transmitb(arrayOfByte, arrayOfByte.length);
    try {
      Thread.currentThread();
      Thread.sleep(250L);
    } catch (Exception exception) {
      System.out.println("sendAltSysReq: Failed wait");
    }
    arrayOfByte[4] = 0;
    this.remconsObj.session.transmitb(arrayOfByte, arrayOfByte.length);
  }
}
