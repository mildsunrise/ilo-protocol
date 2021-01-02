/*      */ package com.hp.ilo2.remcons;
/*      */ 
/*      */ import com.hp.ilo2.intgapp.intgapp;
/*      */ import com.hp.ilo2.virtdevs.VErrorDialog;
/*      */ import com.hp.ilo2.virtdevs.virtdevs;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFrame;
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
/*      */ public class remcons
/*      */   extends JPanel
/*      */   implements TimerListener, Runnable
/*      */ {
/*      */   private static boolean dialogIsOpen = false;
/*      */   private static final int SESSION_TIMEOUT_DEFAULT = 900;
/*      */   private static final int KEEP_ALIVE_INTERVAL = 30;
/*      */   private static final int INFINITE_TIMEOUT = 2147483640;
/*      */   private static final int REMCONS_MAX_FN_KEYS = 12;
/*      */   private static final int LICENSE_RC = 1;
/*   53 */   private int session_timeout = 900;
/*      */   
/*      */   public cim session;
/*      */   
/*      */   public cmd telnetObj;
/*   58 */   public KeyboardHook kHook = null;
/*      */   public boolean kbHookInstalled = false;
/*      */   public boolean kbHookAvailable = false;
/*   61 */   public int keyData = 0;
/*   62 */   public int prevKeyData = 0;
/*      */   
/*      */   public boolean kbHookDataRcvd = false;
/*   65 */   private String term_svcs_label = "Terminal Svcs";
/*      */   
/*      */   Image[] img;
/*      */   
/*      */   static final int ImageDone = 39;
/*      */   
/*      */   public JPanel pwrStatusPanel;
/*      */   
/*      */   public JPanel ledStatusPanel;
/*      */   
/*      */   private Image pwrEncImgLock;
/*      */   
/*      */   private Image pwrEncImgUnlock;
/*      */   
/*      */   private Image pwrEncImg;
/*      */   
/*      */   private JPanel pwrEncCanvas;
/*      */   
/*      */   private Image vmActImgOn;
/*      */   private Image vmActImgOff;
/*      */   private Image vmActImg;
/*      */   private JPanel vmActCanvas;
/*      */   private Image pwrHealthImgGreen;
/*      */   private Image pwrHealthImgYellow;
/*      */   private Image pwrHealthImgRed;
/*      */   private Image pwrHealthImgOff;
/*      */   private Image pwrHealthImg;
/*      */   private JPanel pwrHealthCanvas;
/*      */   private Image pwrPowerImgOn;
/*      */   private Image pwrPowerImgOff;
/*      */   private Image pwrPowerImg;
/*      */   private JPanel pwrPowerCanvas;
/*      */   private JLabel pwrEncLabel;
/*      */   private String login;
/*      */   private Timer timer;
/*      */   private Timer keyBoardTimer;
/*  101 */   private int keyTimerTick = 20;
/*      */   public int timeout_countdown;
/*  103 */   private int port_num = 23;
/*      */   private boolean translate = false;
/*      */   private boolean debug_msg = false;
/*  106 */   private String session_ip = null;
/*  107 */   private int num_cursors = 0;
/*  108 */   private int mouse_mode = 0;
/*      */   
/*      */   private String rcErrMessage;
/*      */   
/*      */   private JFrame parent_frame;
/*  113 */   public int[] rndm_nums = new int[12];
/*      */   
/*  115 */   private int terminalServicesPort = 3389;
/*      */   private boolean launchTerminalServices = false;
/*  117 */   private int ts_param = 0;
/*      */   
/*      */   public boolean session_encryption_enabled = false;
/*  120 */   public byte[] session_encrypt_key = new byte[16];
/*  121 */   public byte[] session_decrypt_key = new byte[16];
/*  122 */   public int session_key_index = 0;
/*      */   
/*  124 */   private LocaleTranslator lt = new LocaleTranslator();
/*      */   
/*      */   public static Properties prop;
/*      */   
/*  128 */   private static final char[] base64 = new char[] { Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '>', Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '?', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE };
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
/*  139 */   public int initialized = 0;
/*      */   
/*      */   public boolean retry_connection_flag = false;
/*      */   
/*  143 */   public int retry_connection_count = 0;
/*      */   
/*      */   public static final int RETRY_CONNECTION_MAX = 3;
/*      */   
/*      */   Thread locale_setter;
/*      */   
/*      */   public boolean licensed = false;
/*      */   
/*      */   public boolean halfHeightCapable = false;
/*      */   boolean fdConnState = false, cdConnState = false;
/*      */   boolean fdCachedConnState = false;
/*      */   boolean cdCachedConnState = false;
/*  155 */   private int localKbdLayoutId = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   public intgapp ParentApp;
/*      */ 
/*      */ 
/*      */   
/*      */   public String getLocalString(int paramInt) {
/*  164 */     String str = "";
/*      */     try {
/*  166 */       str = this.ParentApp.locinfoObj.getLocString(paramInt);
/*      */     } catch (Exception exception) {
/*      */       
/*  169 */       System.out.println("remcons:getLocalString" + exception.getMessage());
/*      */     } 
/*  171 */     return str;
/*      */   }
/*      */ 
/*      */   
/*      */   public remcons(intgapp paramintgapp) {
/*  176 */     this.ParentApp = paramintgapp;
/*      */   }
/*      */ 
/*      */   
/*      */   public Image getImg(String paramString) {
/*  181 */     ClassLoader classLoader = getClass().getClassLoader();
/*  182 */     return this.ParentApp.getImage(classLoader.getResource("com/hp/ilo2/remcons/images/" + paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void waitImage(Image paramImage, ImageObserver paramImageObserver) {
/*      */     int i;
/*  189 */     long l = System.currentTimeMillis();
/*      */     do {
/*  191 */       i = checkImage(paramImage, paramImageObserver);
/*  192 */       if ((i & 0xC0) != 0)
/*      */         break; 
/*  194 */       Thread.yield();
/*  195 */       if (System.currentTimeMillis() - l > 2000L) {
/*      */         break;
/*      */       }
/*  198 */     } while ((i & 0x27) != 39);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void init() {
/*  208 */     this.img = new Image[22];
/*  209 */     this.img[0] = getImg("blank_cd.png");
/*  210 */     this.img[1] = getImg("blue.png");
/*  211 */     this.img[2] = getImg("CD_Drive.png");
/*  212 */     this.img[3] = getImg("FloppyDisk.png");
/*  213 */     this.img[4] = getImg("Folder.png");
/*  214 */     this.img[5] = getImg("green.png");
/*  215 */     this.img[6] = getImg("hold.png");
/*  216 */     this.img[7] = null;
/*  217 */     this.img[8] = null;
/*  218 */     this.img[9] = null;
/*  219 */     this.img[10] = null;
/*  220 */     this.img[11] = getImg("irc.png");
/*  221 */     this.img[12] = getImg("Keyboard.png");
/*  222 */     this.img[13] = getImg("off.png");
/*  223 */     this.img[14] = getImg("press.png");
/*  224 */     this.img[15] = getImg("ProtectFormHS.png");
/*  225 */     this.img[16] = getImg("pwr.png");
/*  226 */     this.img[17] = getImg("pwr_off.png");
/*  227 */     this.img[18] = getImg("red.png");
/*  228 */     this.img[19] = getImg("UnProtectFormHS.png");
/*  229 */     this.img[20] = getImg("Warning.png");
/*  230 */     this.img[21] = getImg("yellow.png");
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
/*  242 */     this.locale_setter = new Thread(this);
/*  243 */     this.locale_setter.start();
/*      */     
/*  245 */     init_params();
/*      */ 
/*      */ 
/*      */     
/*  249 */     boolean bool = false;
/*  250 */     String str1 = System.getProperty("os.name").toLowerCase();
/*  251 */     String str2 = System.getProperty("java.vm.name");
/*  252 */     String str3 = "unknown";
/*  253 */     if (str1.startsWith("windows") || str1.startsWith("linux")) {
/*  254 */       if (str1.startsWith("windows")) {
/*  255 */         if (str2.indexOf("64") != -1) {
/*  256 */           System.out.println("kbhookdll Detected win 64bit jvm");
/*  257 */           str3 = "HpqKbHook-x86-win64";
/*      */         } else {
/*      */           
/*  260 */           System.out.println("kbhookdll Detected win 32bit jvm");
/*  261 */           str3 = "HpqKbHook-x86-win32";
/*      */         }
/*      */       
/*  264 */       } else if (str1.startsWith("linux")) {
/*  265 */         if (str2.indexOf("64") != -1) {
/*  266 */           System.out.println("kbhookdll Detected 64bit linux jvm");
/*  267 */           str3 = "HpqKbHook-x86-linux-64";
/*      */         } else {
/*      */           
/*  270 */           System.out.println("kbhookdll Detected 32bit linux jvm");
/*  271 */           str3 = "HpqKbHook-x86-linux-32";
/*      */         } 
/*      */       } 
/*      */       
/*  275 */       bool = ExtractKeyboardDll(str3);
/*  276 */       if (bool == true) {
/*      */         
/*  278 */         this.kHook = new KeyboardHook();
/*  279 */         if (this.kHook == null) {
/*  280 */           System.out.println("remcons: kHook = null, Failed to initialize and load kHook");
/*      */         } else {
/*      */           
/*  283 */           this.kbHookAvailable = true;
/*  284 */           this.kHook.clearKeymap();
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/*  289 */         System.out.println("ExtractKeyboardDll() returns false");
/*      */       } 
/*      */     } 
/*      */     
/*  293 */     this.session = new cim(this);
/*  294 */     this.telnetObj = new cmd();
/*      */ 
/*      */     
/*  297 */     if (this.session_encryption_enabled) {
/*  298 */       this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
/*  299 */       this.session.setup_decryption(this.session_decrypt_key);
/*      */     } 
/*      */     
/*  302 */     this.session.set_mouse_protocol(this.mouse_mode);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  307 */     for (byte b = 0; b < 12; b++)
/*  308 */       this.rndm_nums[b] = (int)(Math.random() * 4.0D) * 85; 
/*  309 */     this.session.set_sig_colors(this.rndm_nums);
/*      */     
/*  311 */     if (this.debug_msg) {
/*  312 */       this.session.enable_debug();
/*      */     } else {
/*      */       
/*  315 */       this.session.disable_debug();
/*      */     } 
/*      */     
/*  318 */     this.pwrStatusPanel = new JPanel(new BorderLayout());
/*  319 */     this.ledStatusPanel = new JPanel(new BorderLayout());
/*  320 */     this.pwrHealthImgGreen = this.img[5];
/*  321 */     prepareImage(this.pwrHealthImgGreen, this.ledStatusPanel);
/*  322 */     this.pwrHealthImgYellow = this.img[21];
/*  323 */     prepareImage(this.pwrHealthImgYellow, this.ledStatusPanel);
/*  324 */     this.pwrHealthImgRed = this.img[18];
/*  325 */     prepareImage(this.pwrHealthImgRed, this.ledStatusPanel);
/*  326 */     this.pwrHealthImgOff = this.img[13];
/*  327 */     prepareImage(this.pwrHealthImgOff, this.ledStatusPanel);
/*  328 */     this.pwrEncImgLock = this.img[15];
/*  329 */     prepareImage(this.pwrEncImgLock, this.ledStatusPanel);
/*  330 */     this.pwrEncImgUnlock = this.img[19];
/*  331 */     prepareImage(this.pwrEncImgUnlock, this.ledStatusPanel);
/*  332 */     this.pwrEncImg = this.pwrEncImgUnlock;
/*  333 */     this.pwrHealthImg = this.pwrHealthImgOff;
/*  334 */     this.vmActImgOn = this.img[1];
/*  335 */     prepareImage(this.vmActImgOn, this.ledStatusPanel);
/*  336 */     this.vmActImgOff = this.img[13];
/*  337 */     prepareImage(this.vmActImgOff, this.ledStatusPanel);
/*  338 */     this.pwrPowerImgOn = this.img[16];
/*  339 */     prepareImage(this.pwrPowerImgOn, this.ledStatusPanel);
/*  340 */     this.pwrPowerImgOff = this.img[17];
/*  341 */     prepareImage(this.pwrPowerImgOff, this.ledStatusPanel);
/*  342 */     this.vmActImg = this.vmActImgOff;
/*  343 */     this.pwrPowerImg = this.pwrPowerImgOff;
/*      */     
/*  345 */     this.pwrStatusPanel.add(this.pwrEncCanvas = new JPanel(this) { private final remcons this$0;
/*      */           
/*      */           public void paintComponent(Graphics param1Graphics) {
/*  348 */             super.paintComponent(param1Graphics);
/*  349 */             if (this.this$0.pwrEncImg != null) {
/*  350 */               this.this$0.waitImage(this.this$0.pwrEncImg, this);
/*      */               
/*  352 */               param1Graphics.drawImage(this.this$0.pwrEncImg, 1, 4, null);
/*      */             } else {
/*      */               
/*  355 */               System.out.println("pwrEncCanvas Image not found");
/*      */             } 
/*      */           } }
/*      */         "West");
/*      */     
/*  360 */     setToolTipRecursively(this.pwrEncCanvas, getLocalString(16387));
/*  361 */     this.pwrEncCanvas.setPreferredSize(new Dimension(20, 20));
/*  362 */     this.pwrEncCanvas.setVisible(true);
/*      */     
/*  364 */     this.pwrStatusPanel.add(this.pwrEncLabel = new JLabel());
/*  365 */     this.pwrEncLabel.setText("         ");
/*      */     
/*  367 */     this.ledStatusPanel.add(this.pwrHealthCanvas = new JPanel(this) { private final remcons this$0;
/*      */           
/*      */           public void paintComponent(Graphics param1Graphics) {
/*  370 */             super.paintComponent(param1Graphics);
/*  371 */             if (this.this$0.pwrHealthImg != null) {
/*  372 */               this.this$0.waitImage(this.this$0.pwrHealthImg, this);
/*  373 */               param1Graphics.drawImage(this.this$0.pwrHealthImg, 1, 4, null);
/*      */             } else {
/*      */               
/*  376 */               System.out.println("pwrHealthCanvas Image not found");
/*      */             } 
/*      */           } }
/*      */         "West");
/*      */     
/*  381 */     setToolTipRecursively(this.pwrHealthCanvas, getLocalString(16386));
/*  382 */     this.pwrHealthCanvas.setPreferredSize(new Dimension(18, 25));
/*  383 */     this.pwrHealthCanvas.setVisible(true);
/*      */     
/*  385 */     this.ledStatusPanel.add(this.vmActCanvas = new JPanel(this) { private final remcons this$0;
/*      */           
/*      */           public void paintComponent(Graphics param1Graphics) {
/*  388 */             super.paintComponent(param1Graphics);
/*  389 */             if (this.this$0.vmActImg != null) {
/*  390 */               this.this$0.waitImage(this.this$0.vmActImg, this);
/*      */               
/*  392 */               param1Graphics.drawImage(this.this$0.vmActImg, 1, 4, null);
/*      */             } else {
/*      */               
/*  395 */               System.out.println("vmActCanvas Image not found");
/*      */             } 
/*      */           } }
/*      */         "Center");
/*  399 */     setToolTipRecursively(this.vmActCanvas, getLocalString(16388));
/*  400 */     this.vmActCanvas.setPreferredSize(new Dimension(18, 25));
/*  401 */     this.vmActCanvas.setVisible(true);
/*      */     
/*  403 */     this.ledStatusPanel.add(this.pwrPowerCanvas = new JPanel(this) { private final remcons this$0;
/*      */           
/*      */           public void paintComponent(Graphics param1Graphics) {
/*  406 */             super.paintComponent(param1Graphics);
/*  407 */             if (this.this$0.pwrPowerImg != null) {
/*  408 */               this.this$0.waitImage(this.this$0.pwrPowerImg, this);
/*  409 */               param1Graphics.drawImage(this.this$0.pwrPowerImg, 1, 4, null);
/*      */             } else {
/*      */               
/*  412 */               System.out.println("pwrPowerCanvas Image not found");
/*      */             } 
/*      */           } }
/*      */         "East");
/*  416 */     setToolTipRecursively(this.pwrPowerCanvas, getLocalString(16385));
/*  417 */     this.pwrPowerCanvas.setPreferredSize(new Dimension(18, 25));
/*  418 */     this.pwrPowerCanvas.setVisible(true);
/*      */     
/*  420 */     this.pwrStatusPanel.add(this.ledStatusPanel, "East");
/*      */     
/*  422 */     this.session.enable_keyboard();
/*      */     
/*  424 */     if (true == this.kbHookAvailable) {
/*  425 */       this.keyBoardTimer = new Timer(this.keyTimerTick, false, this.session);
/*  426 */       this.keyBoardTimer.setListener(new keyBoardTimerListener(this), null);
/*  427 */       this.keyBoardTimer.start();
/*  428 */       System.out.println("Keyboard Hook available and timer started...");
/*      */     } 
/*  430 */     this.initialized = 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void start() {
/*  438 */     this.timeout_countdown = this.session_timeout;
/*  439 */     start_session();
/*  440 */     if (this.session_timeout == 2147483640) {
/*  441 */       System.out.println("Remote Console inactivity timeout = infinite.");
/*      */     } else {
/*  443 */       System.out.println("Remote Console inactivity timeout = " + (this.session_timeout / 60) + " minutes.");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean ExtractKeyboardDll(String paramString) {
/*  451 */     String str1 = paramString;
/*  452 */     String str2 = System.getProperty("java.io.tmpdir");
/*  453 */     String str3 = System.getProperty("os.name").toLowerCase();
/*  454 */     String str4 = System.getProperty("file.separator");
/*  455 */     String str5 = " ";
/*  456 */     String str6 = " ";
/*  457 */     boolean bool = false;
/*  458 */     String str7 = "com/hp/ilo2/remcons/";
/*      */     
/*  460 */     if (str3.startsWith("windows") || str3.startsWith("linux")) {
/*  461 */       if (str2 == null) {
/*  462 */         str2 = str3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
/*      */       }
/*      */       
/*  465 */       File file1 = new File(str2);
/*  466 */       if (!file1.exists()) {
/*  467 */         file1.mkdir();
/*      */       }
/*  469 */       if (!str2.endsWith(str4)) {
/*  470 */         str2 = str2 + str4;
/*      */       }
/*  472 */       str2 = str2 + "HpqKbHook-" + Integer.toHexString(virtdevs.UID) + ".dll";
/*  473 */       System.out.println("checking for kbddll" + str2);
/*  474 */       File file2 = new File(str2);
/*  475 */       if (file2.exists()) {
/*  476 */         System.out.println(str1 + " already present ..");
/*  477 */         bool = true;
/*  478 */         return bool;
/*      */       } 
/*      */       
/*  481 */       System.out.println("Extracting " + str1 + "...");
/*  482 */       ClassLoader classLoader = getClass().getClassLoader();
/*      */       
/*  484 */       byte[] arrayOfByte = new byte[4096];
/*  485 */       str5 = str2;
/*      */       
/*  487 */       str6 = str7 + str1;
/*      */       
/*      */       try {
/*  490 */         InputStream inputStream = classLoader.getResourceAsStream(str6);
/*  491 */         FileOutputStream fileOutputStream = new FileOutputStream(str5);
/*      */         int i;
/*  493 */         while ((i = inputStream.read(arrayOfByte, 0, 4096)) != -1)
/*  494 */           fileOutputStream.write(arrayOfByte, 0, i); 
/*  495 */         System.out.println("Writing dll to " + str5 + "complete");
/*  496 */         inputStream.close();
/*  497 */         fileOutputStream.close();
/*  498 */         bool = true;
/*      */       } catch (IOException iOException) {
/*      */         
/*  501 */         System.out.println("dllExtract: " + iOException);
/*  502 */         bool = false;
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/*  507 */       System.out.println("Cannot load keyboardHook DLL. Non Windows-Linux client system.");
/*  508 */       bool = false;
/*      */     } 
/*  510 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void stop() {
/*  518 */     if (this.locale_setter != null && this.locale_setter.isAlive()) {
/*  519 */       this.locale_setter.stop();
/*      */     }
/*  521 */     this.locale_setter = null;
/*  522 */     stop_session();
/*  523 */     System.out.println("Applet stopped...");
/*      */   }
/*      */ 
/*      */   
/*      */   public void destroy() {
/*  528 */     System.out.println("Hiding applet.");
/*  529 */     if (isVisible()) {
/*  530 */       setVisible(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void timeout(Object paramObject) {
/*  536 */     if (this.session.UI_dirty) {
/*  537 */       this.session.UI_dirty = false;
/*  538 */       this.timeout_countdown = this.session_timeout;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  544 */       this.timeout_countdown -= 30;
/*      */       
/*  546 */       if (this.timeout_countdown <= 0 && 
/*  547 */         System.getProperty("java.version", "0").compareTo("1.2") < 0) {
/*  548 */         stop_session();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void start_session() {
/*  555 */     this.session.connect(this.session_ip, this.login, this.port_num, this.ts_param, this.terminalServicesPort, this);
/*      */     
/*  557 */     this.timer = new Timer(30000, false, this.session);
/*  558 */     this.timer.setListener(this, null);
/*  559 */     this.timer.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void stop_session() {
/*  566 */     if (this.timer != null) {
/*  567 */       this.timer.stop();
/*  568 */       this.timer = null;
/*      */     } 
/*  570 */     this.session.disconnect();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPwrStatusEnc(int paramInt) {
/*  576 */     if (paramInt == 0) {
/*  577 */       this.pwrEncImg = this.pwrEncImgUnlock;
/*      */     } else {
/*  579 */       this.pwrEncImg = this.pwrEncImgLock;
/*      */     } 
/*  581 */     this.pwrEncCanvas.invalidate();
/*  582 */     this.pwrEncCanvas.repaint();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPwrStatusEncLabel(String paramString) {
/*  587 */     this.pwrEncLabel.setText(paramString + "       ");
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPwrStatusHealth(int paramInt) {
/*  592 */     switch (paramInt) {
/*      */       case 0:
/*  594 */         this.pwrHealthImg = this.pwrHealthImgGreen;
/*      */         break;
/*      */       case 1:
/*  597 */         this.pwrHealthImg = this.pwrHealthImgYellow;
/*      */         break;
/*      */       case 2:
/*  600 */         this.pwrHealthImg = this.pwrHealthImgRed;
/*      */         break;
/*      */       default:
/*  603 */         this.pwrHealthImg = this.pwrHealthImgOff;
/*      */         break;
/*      */     } 
/*      */     
/*  607 */     this.pwrHealthCanvas.invalidate();
/*  608 */     this.pwrHealthCanvas.repaint();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPwrStatusPower(int paramInt) {
/*  613 */     if (paramInt == 0 && this.pwrPowerImgOff != this.pwrPowerImg) {
/*  614 */       this.pwrPowerImg = this.pwrPowerImgOff;
/*  615 */       this.ParentApp.updatePsMenu(paramInt);
/*  616 */       this.pwrPowerCanvas.invalidate();
/*  617 */       this.pwrPowerCanvas.repaint();
/*  618 */       System.out.println("Moving Power to Off state");
/*      */     }
/*  620 */     else if (paramInt != 0 && this.pwrPowerImgOn != this.pwrPowerImg) {
/*  621 */       this.pwrPowerImg = this.pwrPowerImgOn;
/*  622 */       this.ParentApp.updatePsMenu(paramInt);
/*  623 */       this.pwrPowerCanvas.invalidate();
/*  624 */       this.pwrPowerCanvas.repaint();
/*  625 */       System.out.println("Moving Power to ON state");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setvmAct(int paramInt) {
/*  634 */     if (this.vmActImg == this.vmActImgOn || paramInt == 0) {
/*  635 */       this.vmActImg = this.vmActImgOff;
/*  636 */       this.vmActCanvas.invalidate();
/*  637 */       this.vmActCanvas.repaint();
/*  638 */     } else if (this.vmActImg == this.vmActImgOff) {
/*  639 */       this.vmActImg = this.vmActImgOn;
/*  640 */       this.vmActCanvas.invalidate();
/*  641 */       this.vmActCanvas.repaint();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int seize_dialog(String paramString1, String paramString2, int paramInt) {
/*  649 */     System.out.println("seize dialog invoked" + paramInt);
/*  650 */     VSeizeWaitDialog vSeizeWaitDialog = new VSeizeWaitDialog(this, paramString1, paramString2, paramInt);
/*  651 */     return vSeizeWaitDialog.getUserInput();
/*      */   }
/*      */ 
/*      */   
/*      */   public void seize_confirmed() {
/*  656 */     this.ParentApp.moveUItoInit(false);
/*  657 */     this.ParentApp.virtdevsObj.stop();
/*  658 */     remconsUnInstallKeyboardHook();
/*  659 */     this.ParentApp.dispFrame.setVisible(false);
/*  660 */     this.session.seize();
/*  661 */     this.ParentApp.stop();
/*      */   }
/*      */ 
/*      */   
/*      */   public void shared(String paramString1, String paramString2) {
/*  666 */     System.out.println("shared notification invoked");
/*  667 */     new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8230), getLocalString(8231) + " " + paramString2 + "@" + paramString1 + getLocalString(8232), false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unAuthorized(String paramString, boolean paramBoolean) {
/*  674 */     new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8230), getLocalString(8233) + paramString + getLocalString(8234), false);
/*      */     
/*  676 */     if (paramBoolean) {
/*  677 */       this.session.unAuthAccess();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void firmwareUpgrade() {
/*  683 */     System.out.println("Firmware upgrade notification invoked");
/*      */     
/*  685 */     VErrorDialog vErrorDialog = new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8230), getLocalString(8235), false);
/*  686 */     this.ParentApp.moveUItoInit(false);
/*  687 */     this.ParentApp.virtdevsObj.stop();
/*  688 */     this.session.fwUpgrade();
/*  689 */     this.ParentApp.stop();
/*  690 */     if (vErrorDialog.getBoolean() == true) {
/*  691 */       System.exit(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void ack(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4) {
/*  697 */     if (paramByte1 == 0) {
/*  698 */       if (paramByte2 == 1) {
/*      */ 
/*      */         
/*  701 */         if (paramByte4 == 1 && !this.ParentApp.fdSelected) {
/*  702 */           this.ParentApp.fdSelected = true;
/*  703 */           this.ParentApp.lockFdMenu(false, getLocalString(4131) + getLocalString(4106));
/*      */         }
/*  705 */         else if (paramByte4 == 0 && this.ParentApp.fdSelected == true) {
/*  706 */           this.ParentApp.fdSelected = false;
/*  707 */           this.ParentApp.lockFdMenu(true, "");
/*      */         }
/*      */       
/*  710 */       } else if (paramByte2 == 2) {
/*      */ 
/*      */         
/*  713 */         if (paramByte4 == 1 && !this.ParentApp.cdSelected) {
/*  714 */           this.ParentApp.cdSelected = true;
/*  715 */           this.ParentApp.lockCdMenu(false, getLocalString(4131) + getLocalString(4107));
/*  716 */         } else if (paramByte4 == 0 && this.ParentApp.cdSelected == true) {
/*  717 */           this.ParentApp.cdSelected = false;
/*  718 */           this.ParentApp.lockCdMenu(true, "");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void init_params() {
/*  737 */     this.login = null;
/*  738 */     this.port_num = 23;
/*  739 */     this.mouse_mode = 0;
/*  740 */     this.session_timeout = 900;
/*      */     
/*  742 */     this.session_encryption_enabled = true;
/*      */     
/*  744 */     this.session_key_index = 0;
/*  745 */     this.launchTerminalServices = false;
/*  746 */     this.terminalServicesPort = 0;
/*  747 */     this.debug_msg = true;
/*      */     
/*  749 */     this.session_ip = this.ParentApp.getCodeBase().getHost();
/*      */     
/*  751 */     this.num_cursors = 0;
/*      */     
/*  753 */     if (this.session_encryption_enabled) {
/*  754 */       if (null != this.ParentApp.enc_key) {
/*  755 */         System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_decrypt_key, 0, this.session_decrypt_key.length);
/*  756 */         System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_encrypt_key, 0, this.session_encrypt_key.length);
/*      */       } 
/*      */     } else {
/*      */       
/*  760 */       this.session_decrypt_key = null;
/*  761 */       this.session_encrypt_key = null;
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
/*      */   private String parse_login(String paramString) {
/*  777 */     if (paramString.startsWith("Compaq-RIB-Login=")) {
/*  778 */       String str = "\033[!";
/*      */       
/*      */       try {
/*  781 */         str = str + paramString.substring(17, 73);
/*  782 */         str = str + '\r';
/*  783 */         str = str + paramString.substring(74, 106);
/*  784 */         str = str + '\r';
/*      */       } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
/*      */         
/*  787 */         return null;
/*      */       } 
/*      */       
/*  790 */       return str;
/*      */     } 
/*      */     
/*  793 */     return base64_decode(paramString);
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
/*      */   private String base64_decode(String paramString) {
/*  810 */     byte b1 = 0;
/*  811 */     byte b2 = 0;
/*  812 */     String str = "";
/*      */     
/*  814 */     while (b1 + 3 < paramString.length() && !b2) {
/*  815 */       char c1 = base64[paramString.charAt(b1) & 0x7F];
/*  816 */       char c2 = base64[paramString.charAt(b1 + 1) & 0x7F];
/*  817 */       char c3 = base64[paramString.charAt(b1 + 2) & 0x7F];
/*  818 */       char c4 = base64[paramString.charAt(b1 + 3) & 0x7F];
/*      */       
/*  820 */       char c5 = (char)((c1 << 2) + (c2 >> 4));
/*  821 */       char c6 = (char)((c2 << 4) + (c3 >> 2));
/*  822 */       char c7 = (char)((c3 << 6) + c4);
/*      */       
/*  824 */       c5 = (char)(c5 & 0xFF);
/*  825 */       c6 = (char)(c6 & 0xFF);
/*  826 */       c7 = (char)(c7 & 0xFF);
/*      */       
/*  828 */       if (c5 == ':') {
/*  829 */         c5 = '\r';
/*      */       }
/*  831 */       if (c6 == ':') {
/*  832 */         c6 = '\r';
/*      */       }
/*  834 */       if (c7 == ':') {
/*  835 */         c7 = '\r';
/*      */       }
/*  837 */       str = str + c5;
/*      */ 
/*      */       
/*  840 */       if (paramString.charAt(b1 + 2) == '=') {
/*  841 */         b2++;
/*      */       } else {
/*      */         
/*  844 */         str = str + c6;
/*      */       } 
/*  846 */       if (paramString.charAt(b1 + 3) == '=') {
/*  847 */         b2++;
/*      */       } else {
/*      */         
/*  850 */         str = str + c7;
/*      */       } 
/*  852 */       b1 += 4;
/*      */     } 
/*  854 */     if (str.length() != 0) {
/*  855 */       str = str + '\r';
/*      */     }
/*  857 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void paint(Graphics paramGraphics) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTimeoutValue() {
/*  872 */     return this.timeout_countdown;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void run() {
/*  878 */     if (System.getProperty("os.name").toLowerCase().startsWith("windows") && 
/*  879 */       !this.lt.windows) {
/*  880 */       Locale.setDefault(Locale.US);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  886 */       if (true == this.retry_connection_flag) { this; if (3 >= this.retry_connection_count) {
/*      */           
/*  888 */           System.out.println("Retrying connection" + this.retry_connection_count);
/*  889 */           this.retry_connection_flag = false;
/*  890 */           this.retry_connection_count++;
/*      */           
/*  892 */           if (false == this.fdCachedConnState) {
/*  893 */             this.fdCachedConnState = this.fdConnState;
/*      */           }
/*  895 */           if (false == this.cdCachedConnState) {
/*  896 */             this.cdCachedConnState = this.cdConnState;
/*      */           }
/*  898 */           System.out.println("fd conn:" + this.fdConnState + " cd conn:" + this.cdConnState);
/*  899 */           System.out.println("fdcache:" + this.fdCachedConnState + " cdcache:" + this.cdCachedConnState);
/*      */           
/*  901 */           stop_session();
/*      */           try {
/*  903 */             sleepAtLeast(5000L);
/*      */           } catch (InterruptedException interruptedException) {
/*      */             
/*  906 */             System.out.println("Thread interrupted..");
/*      */           } 
/*  908 */           if (this.session_encryption_enabled && 
/*  909 */             null != this.ParentApp.enc_key) {
/*  910 */             System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_decrypt_key, 0, this.session_decrypt_key.length);
/*  911 */             System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_encrypt_key, 0, this.session_encrypt_key.length);
/*      */           } 
/*      */           
/*  914 */           this.session.setup_decryption(this.session_decrypt_key);
/*  915 */           this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
/*      */           
/*  917 */           start_session();
/*      */           try {
/*  919 */             sleepAtLeast(2500L);
/*      */           } catch (InterruptedException interruptedException) {
/*      */             
/*  922 */             System.out.println("Thread interrupted..");
/*      */           } 
/*  924 */           if (null != this.session.receiver && false == this.retry_connection_flag) {
/*  925 */             this.retry_connection_count = 0;
/*      */             continue;
/*      */           } 
/*  928 */           this.retry_connection_flag = true; continue;
/*      */         }  }
/*      */       
/*  931 */       if (true == this.retry_connection_flag) {
/*  932 */         System.out.println("Retry connection  - video maximum attempts exhausted");
/*  933 */         stop_session();
/*  934 */         this.retry_connection_flag = false;
/*      */ 
/*      */         
/*      */         continue;
/*      */       } 
/*      */ 
/*      */       
/*      */       try {
/*  942 */         sleepAtLeast(2500L);
/*      */       } catch (InterruptedException interruptedException) {
/*      */         
/*  945 */         System.out.println("Thread interrupted..");
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sleepAtLeast(long paramLong) throws InterruptedException {
/*  954 */     long l1 = System.currentTimeMillis();
/*  955 */     long l2 = paramLong;
/*  956 */     while (l2 > 0L) {
/*  957 */       Thread.sleep(l2);
/*  958 */       long l = System.currentTimeMillis();
/*  959 */       l2 = paramLong - l - l1;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setDialogIsOpen(boolean paramBoolean) {
/*  965 */     dialogIsOpen = paramBoolean;
/*      */   }
/*      */ 
/*      */   
/*      */   public void SetLicensed(int paramInt) {
/*  970 */     this.licensed = false;
/*  971 */     if ((paramInt & 0x1) != 0) {
/*  972 */       this.licensed = true;
/*      */     }
/*  974 */     System.out.println("SetLicensed: " + this.licensed);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void SetFlags(int paramInt) {
/*  980 */     if ((paramInt & 0x8) == 0) {
/*  981 */       this.halfHeightCapable = false;
/*  982 */       System.out.println("halfHeightCapable false");
/*      */     } else {
/*      */       
/*  985 */       this.halfHeightCapable = true;
/*  986 */       System.out.println("halfHeightCapable true");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void UnlicensedShutdown() {
/*  993 */     String str = "<html>" + getLocalString(8213) + " " + getLocalString(8215) + " " + getLocalString(8237) + "<br><br>" + getLocalString(8238) + "</html>";
/*      */     
/*  995 */     System.out.println("Unlicensed notification invoked");
/*  996 */     VErrorDialog vErrorDialog = new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8236), str, true);
/*  997 */     vErrorDialog.getBoolean();
/*  998 */     this.ParentApp.moveUItoInit(false);
/*  999 */     this.ParentApp.stop();
/*      */   }
/*      */ 
/*      */   
/*      */   public void resetShutdown() {
/* 1004 */     VErrorDialog vErrorDialog = new VErrorDialog(this.ParentApp.dispFrame, getLocalString(4103), getLocalString(8289), true);
/* 1005 */     vErrorDialog.getBoolean();
/* 1006 */     this.ParentApp.moveUItoInit(false);
/* 1007 */     this.ParentApp.stop();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInitialized() {
/* 1012 */     return this.initialized;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void get_terminal_svcs_label(int paramInt) {
/*      */     String str;
/* 1019 */     if (paramInt == 0) {
/* 1020 */       str = "mstsc";
/*      */     }
/* 1022 */     else if (paramInt == 1) {
/* 1023 */       str = "vnc";
/*      */     } else {
/*      */       
/* 1026 */       str = "type" + paramInt;
/*      */     } 
/* 1028 */     this.term_svcs_label = prop.getProperty(str + ".label", "Terminal Svcs");
/*      */   }
/*      */   
/*      */   static {
/* 1032 */     prop = new Properties();
/*      */     try {
/* 1034 */       prop.load(new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".java" + System.getProperty("file.separator") + "hp.properties"));
/*      */     } catch (Exception exception) {
/*      */       
/* 1037 */       System.out.println("Exception: " + exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void remconsInstallKeyboardHook() {
/* 1043 */     String str = System.getProperty("os.name").toLowerCase();
/* 1044 */     int i = -5;
/* 1045 */     if (this.kHook == null) {
/* 1046 */       System.out.println("remconsInstallKeyboardHook:KB Hook dll not loaded");
/*      */     }
/* 1048 */     else if (!this.kbHookInstalled && this.kbHookAvailable == true && !dialogIsOpen) {
/*      */       
/* 1050 */       this.kHook.clearKeymap();
/* 1051 */       i = this.kHook.InstallKeyboardHook();
/*      */       
/* 1053 */       if (!str.startsWith("windows") && -1412584499 == i) {
/*      */ 
/*      */ 
/*      */         
/* 1057 */         this.kbHookInstalled = false;
/* 1058 */         this.keyBoardTimer.stop();
/* 1059 */         System.out.println("remconsInstallKeyboardHook: KB Hook install failed");
/*      */       } else {
/*      */         
/* 1062 */         this.kHook.setKeyboardLayoutId(i);
/* 1063 */         this.kbHookInstalled = true;
/* 1064 */         this.prevKeyData = this.keyData = 0;
/* 1065 */         if (!str.startsWith("windows")) {
/* 1066 */           this.keyBoardTimer.start();
/* 1067 */           this.kHook.setLocalKbdLayout(this.localKbdLayoutId);
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
/*      */   public void remconsUnInstallKeyboardHook() {
/* 1079 */     int i = -5;
/* 1080 */     if (this.kHook != null)
/*      */     {
/*      */       
/* 1083 */       if (this.kbHookInstalled == true && this.kbHookAvailable == true) {
/*      */         
/* 1085 */         i = this.kHook.UnInstallKeyboardHook();
/* 1086 */         if (i == 0) {
/* 1087 */           this.kbHookInstalled = false;
/* 1088 */           this.prevKeyData = this.keyData = 0;
/* 1089 */           this.kHook.clearKeymap();
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 1094 */           System.out.println("remconsUnInstallKeyboardHook: uninstall failed:" + i);
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLocalKbdLayout(int paramInt) {
/* 1104 */     if (this.kHook != null && this.kbHookInstalled == true) {
/* 1105 */       System.out.println("setKbdLayoutHandler: set Layout - " + paramInt);
/* 1106 */       this.kHook.setLocalKbdLayout(paramInt);
/*      */     } else {
/*      */       
/* 1109 */       System.out.println("setKbdLayoutHandler: kHook not available. dbg caching..");
/* 1110 */       this.localKbdLayoutId = paramInt;
/*      */     } 
/*      */   }
/*      */   class keyBoardTimerListener implements TimerListener { keyBoardTimerListener(remcons this$0) {
/* 1114 */       this.this$0 = this$0;
/*      */     }
/*      */ 
/*      */     
/*      */     private final remcons this$0;
/*      */ 
/*      */     
/*      */     public synchronized void timeout(Object param1Object) {
/* 1122 */       boolean bool1 = false;
/* 1123 */       byte[] arrayOfByte = new byte[10];
/* 1124 */       boolean bool2 = false;
/* 1125 */       char c = 'ϣ';
/*      */       
/* 1127 */       if (this.this$0.kHook != null && this.this$0.kbHookInstalled == true) {
/*      */         do
/*      */         {
/* 1130 */           this.this$0.prevKeyData = this.this$0.keyData;
/* 1131 */           this.this$0.keyData = this.this$0.kHook.GetKeyData();
/*      */           
/* 1133 */           if (this.this$0.keyData == this.this$0.prevKeyData || 0 == this.this$0.keyData) {
/*      */             continue;
/*      */           }
/*      */           
/* 1137 */           int i = (this.this$0.keyData & 0xFF0000) >> 16;
/* 1138 */           int k = (this.this$0.keyData & 0xFF00) >> 8;
/* 1139 */           int j = this.this$0.keyData & 0xFF;
/*      */           
/* 1141 */           if ((i & 0x90) == 144) {
/*      */ 
/*      */ 
/*      */             
/* 1145 */             bool2 = true;
/*      */           }
/* 1147 */           else if ((i & 0x80) == 128) {
/*      */ 
/*      */             
/* 1150 */             bool1 = false;
/* 1151 */             bool2 = false;
/*      */           
/*      */           }
/*      */           else {
/*      */             
/* 1156 */             bool1 = true;
/* 1157 */             bool2 = false;
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1162 */           arrayOfByte = this.this$0.kHook.HandleHookKey(j, k, bool1, bool2);
/* 1163 */           if (this.this$0.kHook.kcmdValid) {
/* 1164 */             if (false == this.this$0.kbHookDataRcvd) {
/* 1165 */               this.this$0.kbHookDataRcvd = true;
/*      */             }
/* 1167 */             this.this$0.session.transmitb(arrayOfByte, arrayOfByte.length);
/*      */           } 
/*      */           
/* 1170 */           c = Character.MIN_VALUE;
/*      */         
/*      */         }
/* 1173 */         while (c++ < 'Ϩ');
/*      */       }
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setToolTipRecursively(JComponent paramJComponent, String paramString) {
/* 1180 */     paramJComponent.setToolTipText(paramString);
/*      */   }
/*      */ 
/*      */   
/*      */   public void viewHotKeys() {
/* 1185 */     new hotKeysDialog(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public void viewAboutJirc() {
/* 1190 */     new aboutJircDialog(this);
/*      */   }
/*      */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/remcons.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */