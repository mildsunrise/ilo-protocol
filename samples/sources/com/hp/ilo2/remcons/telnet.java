/*      */ package com.hp.ilo2.remcons;
/*      */ 
/*      */ import com.hp.ilo2.virtdevs.VErrorDialog;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketException;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.Locale;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JPanel;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class telnet
/*      */   extends JPanel
/*      */   implements Runnable, MouseListener, FocusListener, KeyListener
/*      */ {
/*      */   public static final int TELNET_PORT = 23;
/*      */   public static final int TELNET_ENCRYPT = 192;
/*      */   public static final int TELNET_CHG_ENCRYPT_KEYS = 193;
/*      */   public static final int TELNET_SE = 240;
/*      */   public static final int TELNET_NOP = 241;
/*      */   public static final int TELNET_DM = 242;
/*      */   public static final int TELNET_BRK = 243;
/*      */   public static final int TELNET_IP = 244;
/*      */   public static final int TELNET_AO = 245;
/*      */   public static final int TELNET_AYT = 246;
/*      */   public static final int TELNET_EC = 247;
/*      */   public static final int TELNET_EL = 248;
/*      */   public static final int TELNET_GA = 249;
/*      */   public static final int TELNET_SB = 250;
/*      */   public static final int TELNET_WILL = 251;
/*      */   public static final int TELNET_WONT = 252;
/*      */   public static final int TELNET_DO = 253;
/*      */   public static final int TELNET_DONT = 254;
/*      */   public static final int TELNET_IAC = 255;
/*      */   public static final int JAP_VK_OPEN_BRACKET = 194;
/*      */   public static final int JAP_VK_BACK_SLASH = 195;
/*      */   public static final int JAP_VK_CLOSE_BRACKET = 196;
/*      */   public static final int JAP_VK_COLON = 197;
/*      */   public static final int JAP_VK_RO = 198;
/*      */   private static final int CMD_TS_AVAIL = 194;
/*      */   private static final int CMD_TS_NOT_AVAIL = 195;
/*      */   private static final int CMD_TS_STARTED = 196;
/*      */   private static final int CMD_TS_STOPPED = 197;
/*      */   protected dvcwin screen;
/*      */   public JLabel status_box;
/*      */   protected Thread receiver;
/*      */   protected Socket s;
/*      */   protected DataInputStream in;
/*      */   protected DataOutputStream out;
/*  135 */   protected String login = "";
/*      */ 
/*      */   
/*  138 */   protected String host = "";
/*      */ 
/*      */   
/*  141 */   protected int port = 23;
/*      */ 
/*      */   
/*  144 */   protected int connected = 0;
/*      */ 
/*      */   
/*      */   protected int fore;
/*      */ 
/*      */   
/*      */   protected int back;
/*      */ 
/*      */   
/*      */   protected int hi_fore;
/*      */ 
/*      */   
/*      */   protected int hi_back;
/*      */ 
/*      */   
/*      */   protected String escseq;
/*      */ 
/*      */   
/*      */   protected String curr_num;
/*      */ 
/*      */   
/*  165 */   protected int[] escseq_val = new int[10];
/*      */ 
/*      */   
/*  168 */   protected int escseq_val_count = 0;
/*      */   
/*      */   private boolean crlf_enabled = false;
/*      */   
/*      */   public boolean mirror = false;
/*      */   
/*      */   private RC4 RC4decrypter;
/*      */   
/*      */   private Aes aes128decrypter;
/*      */   private Aes aes256decrypter;
/*  178 */   protected byte[] decrypt_key = new byte[16];
/*      */   private boolean decryption_active = false;
/*      */   protected boolean encryption_enabled = false;
/*  181 */   private Process rdpProc = null;
/*      */   private boolean enable_terminal_services = false;
/*  183 */   private int terminalServicesPort = 3389;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int ts_type;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean tbm_mode = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean dvc_mode = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean dvc_encryption = false;
/*      */ 
/*      */ 
/*      */   
/*  207 */   private int total_count = 0;
/*      */   
/*  209 */   public byte[] sessionKey = new byte[32];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  220 */   public String st_fld1 = "";
/*  221 */   public String st_fld2 = "";
/*  222 */   public String st_fld3 = "";
/*  223 */   public String st_fld4 = "";
/*      */   
/*      */   public boolean post_complete = false;
/*      */   private boolean seized = false;
/*  227 */   public int dbg_print = 0;
/*      */   
/*      */   public cmd cmdObj;
/*      */   
/*      */   public remcons remconsObj;
/*  232 */   LocaleTranslator translator = new LocaleTranslator();
/*      */   
/*  234 */   private int[] keyMap = new int[256];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  243 */   private int japanese_kbd = 0;
/*  244 */   public final int PWR_OPTION_PULSE = 0;
/*  245 */   public final int PWR_OPTION_HOLD = 1;
/*  246 */   public final int PWR_OPTION_CYCLE = 2;
/*  247 */   public final int PWR_OPTION_RESET = 3;
/*      */   
/*  249 */   public int cipher = 0;
/*      */ 
/*      */ 
/*      */   
/*  253 */   public final int CIPHER_NONE = 0;
/*  254 */   public final int CIPHER_RC4 = 1;
/*  255 */   public final int CIPHER_AES128 = 2;
/*  256 */   public final int CIPHER_AES256 = 3;
/*      */   
/*  258 */   public final int AES_BITSIZE_128 = 0;
/*  259 */   public final int AES_BITSIZE_192 = 1;
/*  260 */   public final int AES_BITSIZE_256 = 2;
/*      */   
/*  262 */   public final int REQ_LOGIN_KEY = 0;
/*  263 */   public final int REQ_GET_AUTH = 1;
/*  264 */   public final int REQ_SHARE = 2;
/*  265 */   public final int REQ_SEIZE = 3;
/*  266 */   public final int REQ_DONE = 4;
/*      */ 
/*      */   
/*  269 */   public final int CONNECT_CANCEL = 0;
/*  270 */   public final int CONNECT_SEIZE = 1;
/*  271 */   public final int CONNECT_SHARE = 2;
/*      */ 
/*      */   
/*  274 */   public final int KEY_STATE_PRESSED = 0;
/*  275 */   public final int KEY_STATE_TYPED = 1;
/*  276 */   public final int KEY_STATE_RELEASED = 2;
/*      */   
/*      */   private boolean screenFocusLost = false;
/*      */   
/*      */   private int[] winkey_to_hid;
/*      */   
/*      */   private Locale lo;
/*      */   
/*      */   private String keyboardLayout;
/*      */   
/*      */   public void setLocale(String paramString) {
/*  287 */     this.translator.selectLocale(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getLocalString(int paramInt) {
/*  293 */     String str = "";
/*      */     try {
/*  295 */       str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*      */     } catch (Exception exception) {
/*      */       
/*  298 */       System.out.println("telnet:getLocalString" + exception.getMessage());
/*      */     } 
/*  300 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void enable_debug() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disable_debug() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void startRdp() {
/*  393 */     if (this.rdpProc == null) {
/*  394 */       String str1; Runtime runtime = Runtime.getRuntime();
/*      */ 
/*      */       
/*  397 */       if (this.ts_type == 0) {
/*  398 */         str1 = "mstsc";
/*      */       }
/*  400 */       else if (this.ts_type == 1) {
/*  401 */         str1 = "vnc";
/*      */       } else {
/*      */         
/*  404 */         str1 = "type" + this.ts_type;
/*      */       } 
/*      */       
/*  407 */       String str2 = remcons.prop.getProperty(str1 + ".program");
/*  408 */       System.out.println(str1 + " = " + str2);
/*  409 */       if (str2 != null) {
/*  410 */         str2 = percent_sub(str2);
/*  411 */         System.out.println("exec: " + str2);
/*      */         try {
/*  413 */           this.rdpProc = runtime.exec(str2);
/*      */         
/*      */         }
/*      */         catch (SecurityException securityException) {
/*      */ 
/*      */           
/*  419 */           System.out.println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch " + str2);
/*      */         } catch (IOException iOException) {
/*      */           
/*  422 */           System.out.println("IOException: " + iOException.getMessage() + ":: " + str2);
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*  428 */       boolean bool = false;
/*      */       
/*      */       try {
/*  431 */         System.out.println("Executing mstsc. Port is " + this.terminalServicesPort);
/*      */         
/*  433 */         this.rdpProc = runtime.exec("mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort);
/*      */ 
/*      */       
/*      */       }
/*      */       catch (SecurityException securityException) {
/*      */ 
/*      */         
/*  440 */         System.out.println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch mstsc.");
/*      */       } catch (IOException iOException) {
/*      */         
/*  443 */         System.out.println("IOException: " + iOException.getMessage() + ":: mstsc not found in system directory. Looking in \\Program Files\\Remote Desktop.");
/*  444 */         bool = true;
/*      */       } 
/*      */       
/*  447 */       if (bool) {
/*  448 */         bool = false;
/*  449 */         String[] arrayOfString = { "\\Program Files\\Remote Desktop\\mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort };
/*      */         
/*      */         try {
/*  452 */           this.rdpProc = runtime.exec(arrayOfString);
/*      */ 
/*      */         
/*      */         }
/*      */         catch (SecurityException securityException) {
/*      */ 
/*      */ 
/*      */           
/*  460 */           System.out.println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch mstsc.");
/*      */         } catch (IOException iOException) {
/*      */           
/*  463 */           System.out.println("IOException: " + iOException.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
/*  464 */           bool = true;
/*      */         } 
/*      */       } 
/*  467 */       if (bool) {
/*  468 */         String[] arrayOfString = { "\\Program Files\\Terminal Services Client\\mstsc" };
/*      */         
/*      */         try {
/*  471 */           this.rdpProc = runtime.exec(arrayOfString);
/*      */ 
/*      */         
/*      */         }
/*      */         catch (SecurityException securityException) {
/*      */ 
/*      */ 
/*      */           
/*  479 */           System.out.println("SecurityException: " + securityException.getMessage() + ":: Attempting to launch mstsc.");
/*      */         } catch (IOException iOException) {
/*      */           
/*  482 */           System.out.println("IOException: " + iOException.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void keyTyped(KeyEvent paramKeyEvent) {
/*  496 */     String str = "";
/*  497 */     sendKey(paramKeyEvent, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void keyPressed(KeyEvent paramKeyEvent) {
/*  510 */     String str = "";
/*      */ 
/*      */     
/*  513 */     sendKey(paramKeyEvent, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void keyReleased(KeyEvent paramKeyEvent) {
/*  529 */     String str = "";
/*  530 */     sendKey(paramKeyEvent, 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_auto_alive_msg() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void focusGained(FocusEvent paramFocusEvent) {
/*  552 */     if (paramFocusEvent.getComponent() == this.screen) {
/*  553 */       if (this.screenFocusLost) {
/*      */         
/*  555 */         this.remconsObj.remconsInstallKeyboardHook();
/*  556 */         this.screenFocusLost = false;
/*      */       } 
/*      */     } else {
/*      */       
/*  560 */       this.screen.requestFocus();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void focusLost(FocusEvent paramFocusEvent) {
/*  574 */     if (paramFocusEvent.getComponent() == this.screen && 
/*  575 */       paramFocusEvent.isTemporary()) {
/*      */       
/*  577 */       this.remconsObj.remconsUnInstallKeyboardHook();
/*  578 */       this.screenFocusLost = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseClicked(MouseEvent paramMouseEvent) {
/*  594 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mousePressed(MouseEvent paramMouseEvent) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseReleased(MouseEvent paramMouseEvent) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseEntered(MouseEvent paramMouseEvent) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseExited(MouseEvent paramMouseEvent) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void addNotify() {
/*  650 */     super.addNotify();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void set_status(int paramInt, String paramString) {
/*  666 */     switch (paramInt) {
/*      */       case 1:
/*  668 */         this.st_fld1 = paramString;
/*      */         break;
/*      */       case 2:
/*  671 */         this.st_fld2 = paramString;
/*      */         break;
/*      */       case 3:
/*  674 */         this.st_fld3 = paramString;
/*      */         break;
/*      */       case 4:
/*  677 */         this.st_fld4 = paramString;
/*      */         break;
/*      */     } 
/*  680 */     this.status_box.setText(this.st_fld1 + " " + this.st_fld2 + "      " + this.st_fld3 + "      " + this.st_fld4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void reinit_vars() {
/*  688 */     this.dvc_encryption = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setup_decryption(byte[] paramArrayOfbyte) {
/*  696 */     System.arraycopy(paramArrayOfbyte, 0, this.decrypt_key, 0, 16);
/*      */     
/*  698 */     this.RC4decrypter = new RC4(paramArrayOfbyte);
/*  699 */     this.encryption_enabled = true;
/*  700 */     this.aes128decrypter = new Aes(0, paramArrayOfbyte);
/*  701 */     this.aes256decrypter = new Aes(0, paramArrayOfbyte);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, remcons paramremcons) {
/*  722 */     this.enable_terminal_services = ((paramInt2 & 0x1) == 1);
/*  723 */     this.ts_type = paramInt2 >> 8;
/*      */     
/*  725 */     if (paramInt3 != 0) {
/*  726 */       this.terminalServicesPort = paramInt3;
/*      */     }
/*      */     
/*  729 */     if (this.connected == 0) {
/*  730 */       this.screen.start_updates();
/*      */       
/*  732 */       this.connected = 1;
/*  733 */       this.host = paramString1;
/*  734 */       this.login = paramString2;
/*  735 */       this.port = paramInt1;
/*  736 */       this.remconsObj = paramremcons;
/*      */       
/*  738 */       requestFocus();
/*      */ 
/*      */       
/*  741 */       this.sessionKey = paramremcons.ParentApp.getParameter("RCINFO1").getBytes();
/*  742 */       String str = paramremcons.ParentApp.rc_port;
/*  743 */       if (str != null) {
/*      */         try {
/*  745 */           this.port = Integer.parseInt(str);
/*  746 */           System.out.println("RC port number " + this.port);
/*      */         } catch (NumberFormatException numberFormatException) {
/*      */           
/*  749 */           System.out.println("Failed to read rcport from parameters");
/*  750 */           this.port = 23;
/*      */         } 
/*      */       }
/*      */       
/*      */       try {
/*  755 */         set_status(1, getLocalString(12296));
/*  756 */         System.out.println("updated: connecting to " + this.host + ":" + this.port);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/*  763 */           Thread.currentThread(); Thread.sleep(1000L);
/*      */         } catch (InterruptedException interruptedException) {
/*      */           
/*  766 */           System.out.println("connect Thread interrupted..");
/*      */         } 
/*      */         
/*  769 */         this.s = new Socket(this.host, this.port);
/*      */         
/*      */         try {
/*  772 */           this.s.setSoLinger(true, 0);
/*      */           
/*  774 */           System.out.println("set TcpNoDelay");
/*  775 */           this.s.setTcpNoDelay(true);
/*      */         } catch (SocketException socketException) {
/*      */           
/*  778 */           System.out.println("telnet.connect() linger SocketException: " + socketException);
/*      */         } 
/*      */         
/*  781 */         this.in = new DataInputStream(this.s.getInputStream());
/*  782 */         this.out = new DataOutputStream(this.s.getOutputStream());
/*      */ 
/*      */         
/*  785 */         byte b = this.in.readByte();
/*  786 */         if (b == 80) {
/*      */           
/*  788 */           set_status(1, getLocalString(12297));
/*      */           
/*  790 */           boolean bool = false;
/*  791 */           System.out.println("Received hello byte. Requesting remote connection...");
/*  792 */           char c = ' ';
/*  793 */           bool = requestRemoteConnection(c);
/*      */           
/*  795 */           if (bool) {
/*  796 */             this.receiver = new Thread(this);
/*  797 */             this.receiver.setName("telnet_rcvr");
/*  798 */             this.receiver.start();
/*      */ 
/*      */             
/*  801 */             this.cmdObj.connectCmd(this.remconsObj, this.host, this.port);
/*      */           } else {
/*      */             
/*  804 */             paramremcons.ParentApp.stop();
/*      */           } 
/*      */         } else {
/*      */           
/*  808 */           set_status(1, getLocalString(12298));
/*  809 */           System.out.println("Socket connection failure... ");
/*      */         } 
/*      */       } catch (SocketException socketException) {
/*      */         
/*  813 */         System.out.println("telnet.connect() SocketException: " + socketException);
/*  814 */         set_status(1, socketException.toString());
/*  815 */         this.s = null;
/*  816 */         this.in = null;
/*  817 */         this.out = null;
/*  818 */         this.receiver = null;
/*  819 */         this.connected = 0;
/*      */       } catch (UnknownHostException unknownHostException) {
/*      */         
/*  822 */         System.out.println("telnet.connect() UnknownHostException: " + unknownHostException);
/*  823 */         set_status(1, unknownHostException.toString());
/*  824 */         this.s = null;
/*  825 */         this.in = null;
/*  826 */         this.out = null;
/*  827 */         this.receiver = null;
/*  828 */         this.connected = 0;
/*      */       } catch (IOException iOException) {
/*      */         
/*  831 */         System.out.println("telnet.connect() IOException: " + iOException);
/*  832 */         set_status(1, iOException.toString());
/*  833 */         this.s = null;
/*  834 */         this.in = null;
/*  835 */         this.out = null;
/*  836 */         this.receiver = null;
/*  837 */         this.connected = 0;
/*      */       } 
/*      */     } else {
/*      */       
/*  841 */       requestFocus();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean requestRemoteConnection(int paramInt) {
/*  849 */     boolean bool = false;
/*  850 */     byte b = 0;
/*  851 */     byte[] arrayOfByte = new byte[2];
/*  852 */     byte b1 = 0;
/*      */     
/*  854 */     while (b != 4) {
/*  855 */       byte[] arrayOfByte1; String str1; boolean bool1; byte[] arrayOfByte2; String str2; byte[] arrayOfByte3; String str3; switch (b) {
/*      */         case false:
/*  857 */           arrayOfByte[0] = (byte)(paramInt & 0xFF);
/*  858 */           arrayOfByte[1] = (byte)((paramInt & 0xFF00) >>> 8);
/*      */           
/*  860 */           if (this.remconsObj.ParentApp.optional_features.indexOf("ENCRYPT_KEY") != -1) {
/*      */             
/*  862 */             for (byte b2 = 0; b2 < this.sessionKey.length; b2++)
/*      */             {
/*  864 */               this.sessionKey[b2] = (byte)(this.sessionKey[b2] ^ (byte)this.remconsObj.ParentApp.enc_key.charAt(b2 % this.remconsObj.ParentApp.enc_key.length()));
/*      */             }
/*  866 */             if (this.remconsObj.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
/*      */               
/*  868 */               arrayOfByte[1] = (byte)(arrayOfByte[1] | 0x40);
/*      */             }
/*      */             else {
/*      */               
/*  872 */               arrayOfByte[1] = (byte)(arrayOfByte[1] | 0x80);
/*      */             } 
/*      */           } 
/*      */ 
/*      */           
/*  877 */           arrayOfByte1 = new byte[arrayOfByte.length + this.sessionKey.length];
/*  878 */           System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, arrayOfByte.length);
/*  879 */           System.arraycopy(this.sessionKey, 0, arrayOfByte1, arrayOfByte.length, this.sessionKey.length);
/*      */ 
/*      */           
/*  882 */           str1 = new String(arrayOfByte1);
/*      */           
/*  884 */           transmit(str1);
/*  885 */           b = 1;
/*      */         
/*      */         case true:
/*      */           try {
/*  889 */             b1 = this.in.readByte();
/*      */           } catch (IOException iOException) {
/*      */             
/*  892 */             bool = false;
/*  893 */             b = 4;
/*  894 */             System.out.println("Socket Read failed.");
/*      */             continue;
/*      */           } 
/*  897 */           switch (b1) {
/*      */             case 81:
/*  899 */               System.out.println("Access denied.");
/*  900 */               set_status(1, getLocalString(12299));
/*  901 */               if (null != this.remconsObj.ParentApp.dispFrame) {
/*  902 */                 new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8239), getLocalString(8287), true);
/*  903 */                 this.remconsObj.ParentApp.dispFrame.setVisible(false);
/*      */               } else {
/*      */                 
/*  906 */                 new VErrorDialog(getLocalString(8287), true);
/*      */               } 
/*  908 */               bool = false;
/*  909 */               b = 4;
/*  910 */               this.remconsObj.ParentApp.stop();
/*      */               continue;
/*      */             case 82:
/*  913 */               set_status(1, getLocalString(12300));
/*  914 */               System.out.println("Authenticated");
/*  915 */               bool = true;
/*  916 */               this.remconsObj.licensed = true;
/*  917 */               b = 4;
/*      */               continue;
/*      */             
/*      */             case 83:
/*      */             case 89:
/*  922 */               System.out.println("Authenticated, but busy, negotiating");
/*      */               
/*  924 */               if (0 == this.remconsObj.retry_connection_count) {
/*  925 */                 bool1 = negotiateBusy();
/*  926 */                 System.out.println("negotiateResult:" + bool1);
/*      */               }
/*      */               else {
/*      */                 
/*  930 */                 System.out.println("Overriding seize option for internal retry");
/*  931 */                 bool1 = true;
/*      */               } 
/*  933 */               switch (bool1) {
/*      */                 case false:
/*  935 */                   System.out.println("Connection cancelled by user");
/*  936 */                   if (null != this.remconsObj.ParentApp.dispFrame) {
/*  937 */                     this.remconsObj.ParentApp.dispFrame.setVisible(false);
/*      */                   }
/*  939 */                   bool = false;
/*  940 */                   b = 4;
/*      */                   continue;
/*      */                 case true:
/*  943 */                   arrayOfByte[0] = 85;
/*  944 */                   arrayOfByte[1] = 0;
/*      */                   
/*  946 */                   arrayOfByte2 = new byte[arrayOfByte.length];
/*  947 */                   System.arraycopy(arrayOfByte, 0, arrayOfByte2, 0, arrayOfByte.length);
/*      */                   
/*  949 */                   System.out.println("Seizing connection, sending command 0x0055");
/*      */ 
/*      */                   
/*  952 */                   str2 = new String(arrayOfByte2);
/*  953 */                   transmit(str2);
/*      */                   
/*  955 */                   b = 3;
/*      */                   
/*  957 */                   set_status(1, getLocalString(12568));
/*      */                   continue;
/*      */ 
/*      */                 
/*      */                 case true:
/*  962 */                   arrayOfByte[0] = 86;
/*  963 */                   arrayOfByte[1] = 0;
/*      */                   
/*  965 */                   System.out.println("Sharing connection, sending command 0x0056");
/*      */                   
/*  967 */                   arrayOfByte3 = new byte[arrayOfByte.length];
/*  968 */                   System.arraycopy(arrayOfByte, 0, arrayOfByte3, 0, arrayOfByte.length);
/*      */ 
/*      */                   
/*  971 */                   str3 = new String(arrayOfByte3);
/*  972 */                   transmit(str3);
/*      */                   
/*  974 */                   b = 2;
/*      */                   continue;
/*      */               } 
/*      */               
/*      */               continue;
/*      */             case 87:
/*  980 */               System.out.println("Received No License Notification");
/*  981 */               this.remconsObj.licensed = false;
/*  982 */               bool = false;
/*  983 */               b = 4;
/*      */               continue;
/*      */             case 88:
/*  986 */               System.out.println("No free Sessions Notification");
/*  987 */               if (null != this.remconsObj.ParentApp.dispFrame) {
/*  988 */                 new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8239), getLocalString(8240), true);
/*  989 */                 this.remconsObj.ParentApp.dispFrame.setVisible(false);
/*      */               } else {
/*      */                 
/*  992 */                 new VErrorDialog(getLocalString(8240), true);
/*      */               } 
/*  994 */               bool = false;
/*  995 */               b = 4;
/*  996 */               this.remconsObj.ParentApp.stop();
/*      */               continue;
/*      */           } 
/*      */           
/* 1000 */           System.out.println("rqrmtconn default: " + b1);
/* 1001 */           bool = true;
/* 1002 */           b = 4;
/*      */ 
/*      */ 
/*      */         
/*      */         case true:
/* 1007 */           bool = false;
/* 1008 */           b = 4;
/*      */         
/*      */         case true:
/* 1011 */           b = 4;
/*      */           try {
/* 1013 */             b1 = this.in.readByte();
/*      */           } catch (IOException iOException) {
/*      */             
/* 1016 */             bool = false;
/* 1017 */             b = 4;
/* 1018 */             System.out.println("Socket Read failed.");
/*      */             continue;
/*      */           } 
/* 1021 */           switch (b1) {
/*      */             case 81:
/* 1023 */               if (null != this.remconsObj.ParentApp.dispFrame) {
/* 1024 */                 new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8239), getLocalString(8263), true);
/* 1025 */                 this.remconsObj.ParentApp.dispFrame.setVisible(false);
/*      */               } else {
/*      */                 
/* 1028 */                 new VErrorDialog(getLocalString(8263), true);
/*      */               } 
/* 1030 */               bool = false;
/*      */             
/*      */             case 82:
/* 1033 */               this.remconsObj.ParentApp.moveUItoInit(true);
/* 1034 */               bool = true;
/*      */           } 
/*      */         
/*      */         
/*      */       } 
/*      */     
/*      */     } 
/* 1041 */     return bool;
/*      */   }
/*      */ 
/*      */   
/*      */   public int negotiateBusy() {
/* 1046 */     boolean bool = false;
/*      */     
/* 1048 */     this.remconsObj.ParentApp.moveUItoInit(false);
/*      */     
/* 1050 */     VSeizeDialog vSeizeDialog = new VSeizeDialog(this.remconsObj);
/* 1051 */     switch (vSeizeDialog.getUserInput()) {
/*      */       case 2:
/* 1053 */         bool = true;
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 0:
/* 1060 */         bool = false;
/*      */         break;
/*      */     } 
/* 1063 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, remcons paramremcons) {
/* 1077 */     connect(paramString1, paramString2, this.port, paramInt1, paramInt2, paramremcons);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void connect(String paramString, int paramInt1, int paramInt2, remcons paramremcons) {
/* 1089 */     connect(paramString, this.login, this.port, paramInt1, paramInt2, paramremcons);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void disconnect() {
/* 1099 */     this.remconsObj.remconsUnInstallKeyboardHook();
/* 1100 */     if (this.connected == 1) {
/* 1101 */       this.screen.stop_updates();
/* 1102 */       this.connected = 0;
/*      */       
/* 1104 */       if (this.receiver != null && this.receiver.isAlive()) {
/* 1105 */         this.receiver.stop();
/*      */       }
/* 1107 */       this.receiver = null;
/*      */       
/* 1109 */       if (this.s != null) {
/*      */         try {
/* 1111 */           System.out.println("Closing socket");
/* 1112 */           this.s.close();
/*      */         } catch (IOException iOException) {
/*      */           
/* 1115 */           System.out.println("telnet.disconnect() IOException: " + iOException);
/* 1116 */           set_status(1, iOException.toString());
/*      */         } 
/*      */       }
/* 1119 */       this.s = null;
/* 1120 */       this.in = null;
/* 1121 */       this.out = null;
/*      */       
/* 1123 */       if (this.cmdObj != null) {
/* 1124 */         this.cmdObj.disconnectCmd();
/*      */       }
/*      */       
/* 1127 */       set_status(1, getLocalString(12301));
/* 1128 */       reinit_vars();
/*      */       
/* 1130 */       this.decryption_active = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void transmit(String paramString) {
/* 1143 */     if (this.out == null) {
/*      */       return;
/*      */     }
/* 1146 */     if (paramString.length() != 0) {
/* 1147 */       byte[] arrayOfByte = new byte[paramString.length()];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1156 */       for (byte b = 0; b < paramString.length(); b++) {
/* 1157 */         arrayOfByte[b] = (byte)paramString.charAt(b);
/*      */       }
/*      */       
/*      */       try {
/* 1161 */         this.out.write(arrayOfByte, 0, arrayOfByte.length);
/*      */       } catch (IOException iOException) {
/*      */         
/* 1164 */         System.out.println("telnet.transmit() IOException: " + iOException);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized String translate_key(KeyEvent paramKeyEvent) {
/* 1183 */     char c = paramKeyEvent.getKeyChar();
/*      */     
/* 1185 */     switch (c)
/*      */     { case '\n':
/*      */       case '\r':
/* 1188 */         if (paramKeyEvent.isShiftDown()) {
/* 1189 */           str = "\n";
/*      */         } else {
/*      */           
/* 1192 */           str = "\r";
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1204 */         return str;case '\t': str = ""; return str; }  String str = this.translator.translate(c); return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized String translate_special_key(KeyEvent paramKeyEvent) {
/* 1218 */     String str = "";
/*      */     
/* 1220 */     switch (paramKeyEvent.getKeyCode()) {
/*      */       case 9:
/* 1222 */         paramKeyEvent.consume();
/* 1223 */         str = "\t";
/*      */         break;
/*      */     } 
/*      */     
/* 1227 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized String translate_special_key_release(KeyEvent paramKeyEvent) {
/* 1233 */     return "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean process_dvc(char paramChar) {
/* 1241 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void run() {
/* 1251 */     boolean bool1 = false;
/* 1252 */     byte b1 = 0;
/* 1253 */     boolean bool2 = false;
/* 1254 */     boolean bool3 = false;
/* 1255 */     byte[] arrayOfByte = new byte[1024];
/*      */ 
/*      */ 
/*      */     
/* 1259 */     byte b2 = 0;
/*      */ 
/*      */ 
/*      */     
/* 1263 */     this.dvc_mode = true;
/*      */     
/* 1265 */     System.out.println("Starting receiver run");
/*      */ 
/*      */     
/*      */     try {
/*      */       while (true) {
/*      */         byte b;
/*      */         
/*      */         try {
/* 1273 */           if (this.s == null || this.in == null) {
/* 1274 */             System.out.println("telnet.run() s or in is null");
/*      */             break;
/*      */           } 
/* 1277 */           this.s.setSoTimeout(1000);
/*      */           
/* 1279 */           b = this.in.read(arrayOfByte);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         }
/* 1285 */         catch (InterruptedIOException interruptedIOException) {
/*      */ 
/*      */           
/*      */           continue;
/*      */         
/*      */         }
/*      */         catch (Exception exception) {
/*      */           
/* 1293 */           b = -1;
/* 1294 */           b2++;
/*      */         } 
/*      */         
/* 1297 */         if (b < 0) {
/*      */ 
/*      */           
/* 1300 */           if (b2 > 1) {
/* 1301 */             System.out.println("Reading from stream failed for  " + b2 + "times");
/* 1302 */             b2 = 0;
/*      */ 
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*      */           continue;
/*      */         } 
/*      */ 
/*      */         
/* 1315 */         for (byte b3 = 0; b3 < b; b3++) {
/*      */ 
/*      */           
/* 1318 */           if (this.dbg_print == 1000) {
/* 1319 */             this.dbg_print = 0;
/*      */           }
/*      */ 
/*      */           
/* 1323 */           this.dbg_print++;
/*      */           
/* 1325 */           this.remconsObj.fdConnState = this.remconsObj.ParentApp.virtdevsObj.fdConnected;
/* 1326 */           this.remconsObj.cdConnState = this.remconsObj.ParentApp.virtdevsObj.cdConnected;
/*      */           
/* 1328 */           char c = (char)arrayOfByte[b3];
/* 1329 */           c = (char)(c & 0xFF);
/*      */           
/* 1331 */           if (this.dvc_mode) {
/*      */ 
/*      */             
/* 1334 */             if (this.dvc_encryption) {
/* 1335 */               char c1; switch (this.cipher) {
/*      */                 case 1:
/* 1337 */                   c1 = (char)(this.RC4decrypter.randomValue() & 0xFF);
/* 1338 */                   c = (char)(c ^ c1);
/*      */                   break;
/*      */                 case 2:
/* 1341 */                   c1 = (char)(this.aes128decrypter.randomValue() & 0xFF);
/* 1342 */                   c = (char)(c ^ c1);
/*      */                   break;
/*      */                 case 3:
/* 1345 */                   c1 = (char)(this.aes256decrypter.randomValue() & 0xFF);
/* 1346 */                   c = (char)(c ^ c1);
/*      */                   break;
/*      */                 default:
/* 1349 */                   c1 = Character.MIN_VALUE;
/* 1350 */                   System.out.println("Unknown encryption"); break;
/*      */               } 
/* 1352 */               c = (char)(c & 0xFF);
/*      */             } 
/*      */             
/* 1355 */             this.dvc_mode = process_dvc(c);
/* 1356 */             if (!this.dvc_mode) {
/* 1357 */               System.out.println("DVC mode turned off");
/* 1358 */               set_status(1, getLocalString(12302));
/*      */             
/*      */             }
/*      */ 
/*      */           
/*      */           }
/* 1364 */           else if (c == '\033') {
/* 1365 */             b1 = 1;
/* 1366 */           } else if (b1 == 1 && c == '[') {
/* 1367 */             b1 = 2;
/* 1368 */           } else if (b1 == 2 && c == 'R') {
/*      */             
/* 1370 */             this.dvc_mode = true;
/* 1371 */             this.dvc_encryption = true;
/* 1372 */             set_status(1, getLocalString(12303));
/*      */           }
/* 1374 */           else if (b1 == 2 && c == 'r') {
/*      */             
/* 1376 */             this.dvc_mode = true;
/* 1377 */             this.dvc_encryption = false;
/* 1378 */             set_status(1, getLocalString(12292));
/*      */           } else {
/*      */             
/* 1381 */             b1 = 0;
/*      */           }
/*      */         
/*      */         } 
/*      */       } 
/*      */     } catch (Exception exception) {
/*      */       
/* 1388 */       System.out.println("telnet.run() Exception, class:" + exception.getClass() + "  msg:" + exception.getMessage());
/* 1389 */       exception.printStackTrace();
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/* 1394 */       if (!this.seized) {
/* 1395 */         if (this.remconsObj.retry_connection_count < 3) {
/* 1396 */           this.screen.clearScreen();
/* 1397 */           System.out.println("Retrying connection");
/* 1398 */           set_status(1, getLocalString(12305));
/*      */         } else {
/*      */           
/* 1401 */           this.screen.clearScreen();
/* 1402 */           System.out.println("offline");
/* 1403 */           set_status(1, getLocalString(12301));
/*      */         } 
/* 1405 */         set_status(2, "");
/* 1406 */         set_status(3, "");
/* 1407 */         set_status(4, "");
/* 1408 */         System.out.println("Actually Retrying connection");
/* 1409 */         this.remconsObj.retry_connection_flag = true;
/*      */       } 
/*      */     } 
/* 1412 */     System.out.println("Completed receiver run");
/*      */   }
/*      */ 
/*      */   
/*      */   public void change_key() {
/* 1417 */     this.RC4decrypter.update_key();
/*      */   }
/*      */ 
/*      */   
/*      */   void focusTraversalKeysDisable(Object paramObject) {
/* 1422 */     Class[] arrayOfClass = { boolean.class };
/* 1423 */     Object[] arrayOfObject1 = { Boolean.TRUE };
/* 1424 */     Object[] arrayOfObject2 = { Boolean.FALSE };
/*      */     
/*      */     try {
/* 1427 */       paramObject.getClass().getMethod("setFocusTraversalKeysEnabled", arrayOfClass).invoke(paramObject, arrayOfObject2);
/*      */     }
/* 1429 */     catch (Throwable throwable) {}
/*      */ 
/*      */     
/*      */     try {
/* 1433 */       paramObject.getClass().getMethod("setFocusCycleRoot", arrayOfClass).invoke(paramObject, arrayOfObject1);
/*      */     }
/* 1435 */     catch (Throwable throwable) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void stop_rdp() {
/* 1442 */     if (this.rdpProc != null) {
/*      */       try {
/* 1444 */         this.rdpProc.exitValue();
/*      */       } catch (IllegalThreadStateException illegalThreadStateException) {
/*      */         
/* 1447 */         System.out.println("IllegalThreadStateException thrown. Destroying TS.");
/* 1448 */         this.rdpProc.destroy();
/*      */       } 
/* 1450 */       this.rdpProc = null;
/*      */     } 
/*      */ 
/*      */     
/* 1454 */     System.out.println("TS stop.");
/*      */   }
/*      */ 
/*      */   
/*      */   public void seize() {
/* 1459 */     System.out.println("Received seize command. halting RC.");
/* 1460 */     this.seized = true;
/* 1461 */     this.screen.clearScreen();
/* 1462 */     set_status(1, getLocalString(12306));
/* 1463 */     set_status(2, "");
/* 1464 */     set_status(3, "");
/* 1465 */     set_status(4, "");
/*      */   }
/*      */ 
/*      */   
/*      */   public void fwUpgrade() {
/* 1470 */     System.out.println("Received FW Upgrade notification. Halting RC.");
/* 1471 */     this.seized = true;
/* 1472 */     this.screen.clearScreen();
/* 1473 */     set_status(1, getLocalString(12307));
/* 1474 */     set_status(2, "");
/* 1475 */     set_status(3, "");
/* 1476 */     set_status(4, "");
/*      */   }
/*      */ 
/*      */   
/*      */   public void UnlicensedAccess() {
/* 1481 */     System.out.println("Received UnlicensedAccess. Halting RC.");
/* 1482 */     this.seized = true;
/* 1483 */     this.screen.clearScreen();
/* 1484 */     set_status(1, getLocalString(8236));
/* 1485 */     set_status(2, "");
/* 1486 */     set_status(3, "");
/* 1487 */     set_status(4, "");
/*      */   }
/*      */ 
/*      */   
/*      */   public void unAuthAccess() {
/* 1492 */     System.out.println("Received unAuthAccess notification. Halting RC.");
/* 1493 */     this.seized = true;
/* 1494 */     this.screen.clearScreen();
/* 1495 */     set_status(1, getLocalString(12308));
/* 1496 */     set_status(2, "");
/* 1497 */     set_status(3, "");
/* 1498 */     set_status(4, "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String percent_sub(String paramString) {
/* 1505 */     StringBuffer stringBuffer = new StringBuffer();
/*      */     
/* 1507 */     for (byte b = 0; b < paramString.length(); b++) {
/* 1508 */       char c = paramString.charAt(b);
/* 1509 */       if (c == '%') {
/* 1510 */         c = paramString.charAt(++b);
/* 1511 */         if (c == 'h') {
/* 1512 */           stringBuffer.append(this.host);
/*      */         }
/* 1514 */         else if (c == 'p') {
/* 1515 */           stringBuffer.append(this.terminalServicesPort);
/*      */         } else {
/*      */           
/* 1518 */           stringBuffer.append(c);
/*      */         } 
/*      */       } else {
/*      */         
/* 1522 */         stringBuffer.append(c);
/*      */       } 
/* 1524 */     }  return stringBuffer.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getSessionKey() {
/* 1530 */     String str = "0123456789abcdef0123456789abcdef";
/*      */     
/* 1532 */     return str.getBytes();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getSessionKey(String paramString) {
/* 1540 */     String str = parseParameter(paramString, "sessionKey");
/* 1541 */     if (str == "") {
/* 1542 */       System.out.println("Parsing failed.");
/*      */     }
/* 1544 */     byte[] arrayOfByte = str.getBytes();
/* 1545 */     System.out.println("sessionKey : " + str);
/* 1546 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendHidKeyCode(KeyEvent paramKeyEvent) {
/* 1554 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1564 */     byte b = 0;
/* 1565 */     int i = 0;
/*      */     
/* 1567 */     int j = 0;
/*      */ 
/*      */ 
/*      */     
/* 1571 */     i = paramKeyEvent.getKeyCode();
/* 1572 */     j = this.keyMap[i];
/* 1573 */     this.keyMap[i] = 1;
/*      */ 
/*      */     
/* 1576 */     if (j != this.keyMap[i])
/*      */     {
/* 1578 */       for (byte b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
/* 1579 */         if (this.keyMap[b1] == 1) {
/*      */           
/* 1581 */           b = (byte)this.winkey_to_hid[b1];
/* 1582 */           arrayOfByte[4 + b2] = b;
/* 1583 */           b2++;
/* 1584 */           if (b2 == 6) {
/* 1585 */             b2 = 5;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 1591 */     String str = new String(arrayOfByte);
/*      */     
/* 1593 */     transmit(str);
/* 1594 */     paramKeyEvent.consume();
/*      */   }
/*      */ 
/*      */   
/*      */   public void sendHidSpecialKeyCode(KeyEvent paramKeyEvent) {
/* 1599 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1609 */     byte b = 0;
/* 1610 */     char c = Character.MIN_VALUE;
/*      */     
/* 1612 */     int i = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1617 */     c = paramKeyEvent.getKeyChar();
/* 1618 */     i = this.keyMap[c];
/* 1619 */     this.keyMap[c] = 1;
/*      */ 
/*      */     
/* 1622 */     if (i != this.keyMap[c])
/*      */     {
/* 1624 */       for (byte b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
/* 1625 */         if (this.keyMap[b1] == 1) {
/*      */           
/* 1627 */           b = (byte)this.winkey_to_hid[b1];
/* 1628 */           arrayOfByte[4 + b2] = b;
/* 1629 */           b2++;
/* 1630 */           if (b2 == 6) {
/* 1631 */             b2 = 5;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 1637 */     String str = new String(arrayOfByte);
/*      */     
/* 1639 */     transmit(str);
/* 1640 */     paramKeyEvent.consume();
/*      */   }
/*      */ 
/*      */   
/*      */   public void clearKeyPress(KeyEvent paramKeyEvent) {
/* 1645 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1658 */     int i = paramKeyEvent.getKeyCode();
/* 1659 */     this.keyMap[i] = 0;
/* 1660 */     String str = new String(arrayOfByte);
/* 1661 */     transmit(str);
/* 1662 */     paramKeyEvent.consume();
/*      */   }
/*      */ 
/*      */   
/*      */   public telnet(remcons paramremcons) {
/* 1667 */     this.winkey_to_hid = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 42, 43, 40, 0, 0, 40, 0, 0, 225, 224, 226, 72, 57, 0, 0, 0, 0, 0, 41, 41, 138, 139, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 54, 45, 55, 56, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 51, 0, 46, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 47, 49, 48, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 159, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 76, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 36, 37, 52, 54, 70, 73, 0, 0, 0, 0, 55, 47, 48, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, 46, 54, 45, 55, 56, 53, 135, 48, 137, 50, 52, 135, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 47, 49, 48, 52, 0, 96, 90, 92, 94, 0, 0, 0, 0, 0, 0, 0, 139, 0, 0, 0, 0, 136, 136, 136, 53, 53, 0, 0, 0, 0, 0, 0, 0, 0, 138, 0, 255 };
/*      */     this.status_box = new JLabel();
/*      */     this.cmdObj = new cmd();
/*      */     this.remconsObj = paramremcons;
/*      */     this.screen = new dvcwin(1024, 768, this.remconsObj);
/*      */     System.out.println("Screen: " + this.screen);
/*      */     this.screen.addMouseListener(this);
/*      */     addFocusListener(this);
/*      */     this.screen.addFocusListener(this);
/*      */     this.screen.addKeyListener(this);
/*      */     focusTraversalKeysDisable(this.screen);
/*      */     focusTraversalKeysDisable(this);
/*      */     setBackground(Color.black);
/*      */     setLayout(new BorderLayout());
/*      */     add(this.screen, "North");
/*      */     set_status(1, getLocalString(12301));
/*      */     set_status(2, "          ");
/*      */     set_status(3, "          ");
/*      */     set_status(4, "          ");
/*      */     if (System.getProperty("os.name").toLowerCase().startsWith("windows") && !this.translator.windows) {
/*      */       this.translator.selectLocale("en_US");
/*      */     }
/*      */     for (byte b = 0; b < 'Ā'; b++) {
/*      */       this.keyMap[b] = 0;
/*      */     }
/*      */     this.lo = Locale.getDefault();
/*      */     this.keyboardLayout = this.lo.toString();
/*      */     System.out.println("telent lang: Keyboard layout is " + this.keyboardLayout);
/*      */     if (this.keyboardLayout.startsWith("ja")) {
/*      */       System.out.println("JAPANESE LANGUAGE \n");
/*      */       this.japanese_kbd = 1;
/*      */     } else {
/*      */       this.japanese_kbd = 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendCtrlAltDel() {
/* 1709 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1711 */     arrayOfByte[2] = 5;
/* 1712 */     arrayOfByte[4] = 76;
/* 1713 */     String str1 = new String(arrayOfByte);
/* 1714 */     transmit(str1);
/*      */     
/*      */     try {
/* 1717 */       Thread.currentThread(); Thread.sleep(500L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1720 */       System.out.println("Thread interrupted..");
/*      */     } 
/* 1722 */     arrayOfByte[2] = 0;
/* 1723 */     arrayOfByte[4] = 0;
/* 1724 */     String str2 = new String(arrayOfByte);
/* 1725 */     transmit(str2);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendPower(int paramInt) {
/* 1731 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1733 */     arrayOfByte[0] = 0;
/* 1734 */     arrayOfByte[1] = 0;
/*      */     
/* 1736 */     switch (paramInt) {
/*      */       case 0:
/* 1738 */         arrayOfByte[2] = 0;
/*      */         break;
/*      */       case 1:
/* 1741 */         arrayOfByte[2] = 1;
/*      */         break;
/*      */       case 2:
/* 1744 */         arrayOfByte[2] = 2;
/*      */         break;
/*      */       case 3:
/* 1747 */         arrayOfByte[2] = 3;
/*      */         break;
/*      */     } 
/* 1750 */     arrayOfByte[3] = 0;
/*      */     
/* 1752 */     String str = new String(arrayOfByte);
/* 1753 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void sendKey(KeyEvent paramKeyEvent, int paramInt) {
/* 1764 */     if (this.remconsObj.kbHookInstalled != true || true != this.remconsObj.kbHookDataRcvd)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1770 */       handleKey(paramKeyEvent, paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handleKey(KeyEvent paramKeyEvent, int paramInt) {
/* 1778 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1788 */     int i = 0;
/*      */     
/* 1790 */     boolean bool1 = false;
/* 1791 */     boolean bool2 = false;
/* 1792 */     int j = 0;
/* 1793 */     int k = 0;
/*      */     
/* 1795 */     j = paramKeyEvent.getKeyCode();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1800 */     if (this.japanese_kbd == 1 && (j == 92 || j == 91 || j == 93 || j == 513)) {
/*      */ 
/*      */       
/* 1803 */       switch (j) {
/*      */         case 91:
/* 1805 */           j = 194;
/*      */           break;
/*      */         
/*      */         case 92:
/* 1809 */           if ('_' == paramKeyEvent.getKeyChar()) {
/* 1810 */             j = 198;
/*      */             break;
/*      */           } 
/* 1813 */           j = 195;
/*      */           break;
/*      */         case 93:
/* 1816 */           j = 196;
/*      */           break;
/*      */         case 513:
/* 1819 */           j = 197;
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1826 */     } else if (j > 255) {
/*      */       
/* 1828 */       switch (j) {
/*      */         case 512:
/* 1830 */           j = 91;
/*      */           break;
/*      */         case 514:
/* 1833 */           j = 61;
/*      */           break;
/*      */         case 513:
/* 1836 */           j = 93;
/*      */           break;
/*      */         case 515:
/* 1839 */           j = 52;
/*      */           break;
/*      */         case 517:
/* 1842 */           j = 49;
/*      */           break;
/*      */         case 519:
/* 1845 */           j = 57;
/*      */           break;
/*      */         case 520:
/* 1848 */           j = 51;
/*      */           break;
/*      */         case 521:
/* 1851 */           j = 61;
/*      */           break;
/*      */         case 522:
/* 1854 */           j = 48;
/*      */           break;
/*      */         case 523:
/* 1857 */           j = 45;
/*      */           break;
/*      */         case 260:
/* 1860 */           j = 242;
/*      */           break;
/*      */         case 259:
/* 1863 */           j = 241;
/*      */           break;
/*      */         default:
/* 1866 */           System.out.println("Unknown key " + j);
/* 1867 */           j = 0;
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 1875 */     if (j != 0) {
/* 1876 */       if (paramInt == 1) {
/*      */         
/* 1878 */         j = 0;
/*      */       }
/*      */       else {
/*      */         
/* 1882 */         if (paramInt == 0) {
/*      */           
/* 1884 */           this.keyMap[j] = 1;
/*      */ 
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */ 
/*      */           
/* 1892 */           if (9 == j && 0 == this.keyMap[j]) {
/*      */             return;
/*      */           }
/* 1895 */           this.keyMap[j] = 0;
/* 1896 */           if (paramKeyEvent.isAltDown() && j == 154) {
/* 1897 */             sendAltSysReq();
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/* 1902 */         if (!paramKeyEvent.isAltDown() && 0 != this.keyMap[18]) {
/* 1903 */           this.keyMap[18] = 0;
/*      */         }
/*      */ 
/*      */         
/* 1907 */         if (isSpecialReleaseKey(j)) {
/* 1908 */           this.keyMap[j] = 1;
/*      */         }
/*      */         
/*      */         byte b1;
/*      */         byte b2;
/* 1913 */         for (b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
/* 1914 */           if (this.keyMap[b1] == 1) {
/* 1915 */             i = (byte)this.winkey_to_hid[b1];
/* 1916 */             if (i == 224) {
/* 1917 */               k |= 0x1;
/*      */             }
/* 1919 */             if (i == 226) {
/* 1920 */               k |= 0x2;
/*      */             }
/* 1922 */             if (i == 76) {
/* 1923 */               k |= 0x4;
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 1928 */             if (((byte)i & 0xE0) == 224) {
/* 1929 */               i = (byte)i ^ 0xE0;
/* 1930 */               arrayOfByte[2] = (byte)(arrayOfByte[2] | (byte)(1 << (byte)i));
/*      */             
/*      */             }
/*      */             else {
/*      */               
/* 1935 */               arrayOfByte[4 + b2] = (byte)i;
/*      */ 
/*      */ 
/*      */               
/* 1939 */               b2++;
/* 1940 */               if (b2 == 6) {
/* 1941 */                 b2 = 5;
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/* 1947 */         if (k == 7) {
/*      */ 
/*      */ 
/*      */           
/* 1951 */           for (b1 = 0; b1 < 'Ā'; b1++) {
/* 1952 */             this.keyMap[b1] = 0;
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/* 1960 */           transmitb(arrayOfByte, arrayOfByte.length);
/* 1961 */           if (isSpecialReleaseKey(j)) {
/*      */ 
/*      */ 
/*      */             
/* 1965 */             this.keyMap[j] = 0;
/* 1966 */             arrayOfByte[9] = 0; arrayOfByte[8] = 0; arrayOfByte[7] = 0; arrayOfByte[6] = 0; arrayOfByte[5] = 0; arrayOfByte[4] = 0;
/* 1967 */             for (b1 = 0, b2 = 0; b1 < 'Ā'; b1++) {
/* 1968 */               if (this.keyMap[b1] == 1) {
/* 1969 */                 i = (byte)this.winkey_to_hid[b1];
/*      */ 
/*      */                 
/* 1972 */                 arrayOfByte[4 + b2] = (byte)i;
/* 1973 */                 b2++;
/* 1974 */                 if (b2 == 6) {
/* 1975 */                   b2 = 5;
/*      */                 }
/*      */               } 
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1983 */             transmitb(arrayOfByte, arrayOfByte.length);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1994 */     paramKeyEvent.consume();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isSpecialReleaseKey(int paramInt) {
/* 1999 */     boolean bool = false;
/*      */     
/* 2001 */     switch (paramInt) {
/*      */       case 28:
/*      */       case 29:
/*      */       case 240:
/*      */       case 243:
/*      */       case 244:
/* 2007 */         bool = true;
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2013 */     return bool;
/*      */   } class statusUpdateTimer implements TimerListener { private final telnet this$0;
/*      */     statusUpdateTimer(telnet this$0) {
/* 2016 */       this.this$0 = this$0;
/*      */     }
/*      */     
/*      */     public void timeout(Object param1Object) {
/* 2020 */       System.out.println("Video data reception timeout occurred. Clearing status.");
/* 2021 */       this.this$0.set_status(1, " ");
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   public void printByteArray(byte[] paramArrayOfbyte, int paramInt) {
/* 2027 */     if (paramInt < 0) {
/*      */       return;
/*      */     }
/*      */     
/* 2031 */     for (byte b = 0; b < paramInt; b++) {
/* 2032 */       System.out.print("0x" + Integer.toHexString(paramArrayOfbyte[b]) + " ");
/*      */     }
/* 2034 */     System.out.println("\n");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String parseParameter(String paramString1, String paramString2) {
/* 2041 */     String str1 = "[&]";
/* 2042 */     String str2 = "[=]";
/* 2043 */     String str3 = "";
/*      */     
/* 2045 */     System.out.println("Invoking url's query: " + paramString1);
/*      */     
/* 2047 */     if (paramString1 == null) {
/* 2048 */       return str3;
/*      */     }
/*      */     
/* 2051 */     String[] arrayOfString = paramString1.split(str1);
/*      */ 
/*      */     
/* 2054 */     for (byte b = 0; b < arrayOfString.length; b++) {
/* 2055 */       String[] arrayOfString1 = arrayOfString[b].split(str2);
/* 2056 */       if (arrayOfString1[0] == paramString2) {
/* 2057 */         str3 = arrayOfString1[1];
/*      */         break;
/*      */       } 
/*      */     } 
/* 2061 */     return str3;
/*      */   }
/*      */ 
/*      */   
/*      */   public void sendAltSysReq() {
/* 2066 */     byte[] arrayOfByte = new byte[10];
/*      */     
/* 2068 */     arrayOfByte[0] = 1;
/* 2069 */     arrayOfByte[1] = 0;
/* 2070 */     arrayOfByte[2] = 0;
/* 2071 */     arrayOfByte[3] = 0;
/* 2072 */     arrayOfByte[4] = 0;
/* 2073 */     arrayOfByte[5] = 0;
/* 2074 */     arrayOfByte[6] = 0;
/* 2075 */     arrayOfByte[7] = 0;
/* 2076 */     arrayOfByte[8] = 0;
/* 2077 */     arrayOfByte[9] = 0;
/*      */     
/* 2079 */     arrayOfByte[2] = 4;
/* 2080 */     arrayOfByte[4] = 70;
/* 2081 */     this.remconsObj.session.transmitb(arrayOfByte, arrayOfByte.length);
/*      */     try {
/* 2083 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (Exception exception) {
/*      */       
/* 2086 */       System.out.println("sendAltSysReq: Failed wait");
/*      */     } 
/* 2088 */     arrayOfByte[4] = 0;
/* 2089 */     this.remconsObj.session.transmitb(arrayOfByte, arrayOfByte.length);
/*      */   }
/*      */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/telnet.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */