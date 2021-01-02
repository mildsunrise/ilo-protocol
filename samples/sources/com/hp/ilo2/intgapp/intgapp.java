/*      */ package com.hp.ilo2.intgapp;
/*      */ 
/*      */ import com.hp.ilo2.remcons.URLDialog;
/*      */ import com.hp.ilo2.remcons.remcons;
/*      */ import com.hp.ilo2.remcons.telnet;
/*      */ import com.hp.ilo2.virtdevs.MediaAccess;
/*      */ import com.hp.ilo2.virtdevs.VErrorDialog;
/*      */ import com.hp.ilo2.virtdevs.VFileDialog;
/*      */ import com.hp.ilo2.virtdevs.virtdevs;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.util.Arrays;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JApplet;
/*      */ import javax.swing.JCheckBoxMenuItem;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JMenuBar;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JScrollPane;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class intgapp
/*      */   extends JApplet
/*      */   implements Runnable, ActionListener, ItemListener
/*      */ {
/*      */   public virtdevs virtdevsObj;
/*      */   public remcons remconsObj;
/*      */   public locinfo locinfoObj;
/*      */   public jsonparser jsonObj;
/*      */   public String optional_features;
/*      */   public String moniker;
/*      */   public boolean moniker_cached = false;
/*      */   public JFrame dispFrame;
/*      */   public JPanel mainPanel;
/*      */   JMenuBar mainMenuBar;
/*      */   JMenu psMenu;
/*      */   JMenu vdMenu;
/*      */   JMenu kbMenu;
/*      */   JMenu kbCAFMenu;
/*      */   JMenu kbAFMenu;
/*      */   JMenu kbLangMenu;
/*      */   JMenu hlpMenu;
/*      */   int vdmenuIndx;
/*      */   int fdMenuItems;
/*      */   int cdMenuItems;
/*      */   private MediaAccess ma;
/*      */   JCheckBoxMenuItem[] vdMenuItems;
/*      */   public JMenuItem vdMenuItemCrImage;
/*      */   JMenuItem momPress;
/*   66 */   public int blade = 0; JMenuItem pressHold; JMenuItem powerCycle; JMenuItem sysReset; JMenuItem ctlAltDel; JMenuItem numLock; JMenuItem capsLock; JMenuItem ctlAltBack; JMenuItem hotKeys; JMenuItem aboutJirc; JMenuItem[] ctlAltFn; JMenuItem[] AltFn; JCheckBoxMenuItem[] localKbdLayout; JPanel dispStatusBar; JMenuItem mdebug1; JMenuItem mdebug2; JMenuItem mdebug3; JScrollPane scroller; public String enc_key; public String rc_port; public String vm_key; public String vm_port; public String server_name; public String ilo_fqdn; public String enclosure;
/*   67 */   public int bay = 0;
/*   68 */   public byte[] enc_key_val = new byte[16];
/*      */   
/*      */   String rcErrMessage;
/*      */   
/*      */   public int dwidth;
/*      */   public int dheight;
/*      */   public boolean exit = false;
/*      */   public boolean fdSelected = false;
/*      */   public boolean cdSelected = false;
/*      */   public boolean in_enclosure = false;
/*   78 */   private int REMCONS_MAX_FN_KEYS = 12;
/*   79 */   private int REMCONS_MAX_KBD_LAYOUT = 17;
/*      */ 
/*      */   
/*      */   public String getLocalString(int paramInt) {
/*   83 */     String str = "";
/*      */     try {
/*   85 */       str = this.locinfoObj.getLocString(paramInt);
/*      */     } catch (Exception exception) {
/*      */       
/*   88 */       System.out.println("remcons:getLocalString" + exception.getMessage());
/*      */     } 
/*   90 */     return str;
/*      */   }
/*      */ 
/*      */   
/*      */   public intgapp() {
/*   95 */     this.virtdevsObj = new virtdevs(this);
/*   96 */     this.remconsObj = new remcons(this);
/*   97 */     this.locinfoObj = new locinfo(this);
/*   98 */     this.jsonObj = new jsonparser(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public void init() {
/*  103 */     boolean bool = true;
/*      */ 
/*      */     
/*  106 */     System.out.println("Started Retrieving parameters from ILO..");
/*  107 */     String str = this.jsonObj.getJSONRequest("rc_info");
/*  108 */     if (str != null) {
/*      */       
/*  110 */       ApplyRcInfoParameters(str);
/*  111 */       System.out.println("Completed Retrieving parameters from ILO");
/*      */     } 
/*  113 */     bool = this.locinfoObj.initLocStrings();
/*      */     
/*  115 */     this.virtdevsObj.init();
/*  116 */     this.remconsObj.init();
/*      */     
/*  118 */     ui_init();
/*      */     
/*  120 */     if (null == str) {
/*  121 */       System.out.println("Failed to retrive parameters from ILO");
/*  122 */       new VErrorDialog(this.dispFrame, getLocalString(8212), this.rcErrMessage, true);
/*  123 */       this.dispFrame.setVisible(false);
/*      */     }
/*  125 */     else if (false == bool) {
/*  126 */       new VErrorDialog(this.dispFrame, getLocalString(8212), this.locinfoObj.rcErrMessage, true);
/*  127 */       this.dispFrame.setVisible(false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void start() {
/*      */     try {
/*  135 */       this.virtdevsObj.start();
/*  136 */       this.remconsObj.start();
/*      */       
/*  138 */       this.dispFrame.getContentPane().add(this.scroller, "Center");
/*  139 */       this.dispFrame.getContentPane().add(this.dispStatusBar, "South");
/*  140 */       this.scroller.validate();
/*  141 */       this.dispStatusBar.validate();
/*  142 */       this.dispFrame.validate();
/*      */ 
/*      */       
/*  145 */       System.out.println("Set Inital focus for session..");
/*  146 */       this.remconsObj.session.requestFocus();
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
/*  165 */       run();
/*      */     } catch (Exception exception) {
/*      */       
/*  168 */       System.out.println("FAILURE: exception starting applet");
/*  169 */       exception.printStackTrace();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void stop() {
/*  176 */     this.exit = true;
/*  177 */     this.virtdevsObj.stop();
/*  178 */     this.remconsObj.remconsUnInstallKeyboardHook();
/*  179 */     this.remconsObj.stop();
/*      */   }
/*      */ 
/*      */   
/*      */   public void destroy() {
/*  184 */     System.out.println("Destroying subsustems");
/*  185 */     this.exit = true;
/*  186 */     this.remconsObj.remconsUnInstallKeyboardHook();
/*  187 */     this.virtdevsObj.destroy();
/*  188 */     this.remconsObj.destroy();
/*  189 */     this.dispFrame.dispose();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void run() {
/*  200 */     byte b = 0;
/*  201 */     boolean bool = false;
/*      */     while (true) {
/*      */       try {
/*  204 */         b = 0;
/*  205 */         byte b1 = 0;
/*  206 */         this.ma = new MediaAccess();
/*  207 */         String[] arrayOfString = this.ma.devices();
/*  208 */         for (byte b2 = 0; arrayOfString != null && b2 < arrayOfString.length; b2++) {
/*  209 */           int i = this.ma.devtype(arrayOfString[b2]);
/*  210 */           if (i == 2 || i == 5) {
/*  211 */             b1++;
/*      */           }
/*      */         } 
/*      */         
/*  215 */         if (b1 > this.vdmenuIndx - 4) {
/*  216 */           ClassLoader classLoader = getClass().getClassLoader();
/*  217 */           for (byte b3 = 0; arrayOfString != null && b3 < arrayOfString.length; b3++) {
/*  218 */             bool = false;
/*  219 */             int i = this.ma.devtype(arrayOfString[b3]);
/*  220 */             for (byte b4 = 0; b4 < this.vdmenuIndx - 4; b4++) {
/*  221 */               if (arrayOfString[b3].equals(this.vdMenu.getItem(b4).getText())) {
/*  222 */                 bool = true;
/*  223 */                 b++;
/*      */               } 
/*      */             } 
/*  226 */             if (!bool) {
/*  227 */               if (i == 2) {
/*  228 */                 System.out.println("Device attached: " + arrayOfString[b3]);
/*  229 */                 this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b3]);
/*  230 */                 this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + arrayOfString[b3]);
/*  231 */                 this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/*  232 */                 if (arrayOfString[b3].equals("A:") || arrayOfString[b3].equals("B:")) {
/*  233 */                   this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
/*      */                 } else {
/*  235 */                   this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/usb.png"))));
/*      */                 } 
/*  237 */                 this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx], b);
/*  238 */                 this.vdMenu.updateUI();
/*  239 */                 this.vdmenuIndx++;
/*      */                 break;
/*      */               } 
/*  242 */               if (i == 5) {
/*  243 */                 System.out.println("CDROM Hot plug device auto-update no supported at this time");
/*      */               }
/*      */             }
/*      */           
/*      */           } 
/*  248 */         } else if (b1 < this.vdmenuIndx - 4) {
/*  249 */           for (byte b3 = 0; b3 < this.vdmenuIndx - 4; b3++) {
/*  250 */             bool = false;
/*  251 */             for (byte b4 = 0; arrayOfString != null && b4 < arrayOfString.length; b4++) {
/*  252 */               int i = this.ma.devtype(arrayOfString[b4]);
/*  253 */               if ((i == 2 || i == 5) && 
/*  254 */                 this.vdMenu.getItem(b3).getText().equals(arrayOfString[b4])) {
/*  255 */                 bool = true;
/*      */               }
/*      */             } 
/*      */             
/*  259 */             if (!bool) {
/*  260 */               System.out.println("Device removed: " + this.vdMenu.getItem(b3).getText());
/*  261 */               this.vdMenu.remove(b3);
/*  262 */               this.vdMenu.updateUI();
/*  263 */               this.vdmenuIndx--;
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         } 
/*  268 */         this.ma = null;
/*  269 */         this.remconsObj.session.set_status(3, "");
/*  270 */         this.remconsObj.sleepAtLeast(5000L);
/*  271 */         if (this.exit) {
/*      */           break;
/*      */         }
/*      */       } catch (InterruptedException interruptedException) {
/*      */         
/*  276 */         System.out.println("Exception on intgapp");
/*      */       } 
/*      */     } 
/*  279 */     System.out.println("Intgapp stopped running");
/*      */   }
/*      */ 
/*      */   
/*      */   public void paintComponent(Graphics paramGraphics) {
/*  284 */     paintComponents(paramGraphics);
/*  285 */     paramGraphics.drawString("Remote Console JApplet Loaded", 10, 50);
/*      */   }
/*      */ 
/*      */   
/*      */   public void ui_init() {
/*  290 */     String str1 = "";
/*      */     
/*  292 */     System.out.println("Message from ui_init55");
/*  293 */     this.dispFrame = new JFrame("JavaApplet IRC Window");
/*  294 */     this.dispFrame.getContentPane().setLayout(new BorderLayout());
/*  295 */     this.dispFrame.addWindowListener(new WindowCloser(this));
/*  296 */     this.mainMenuBar = new JMenuBar();
/*  297 */     this.dispStatusBar = new JPanel(new BorderLayout());
/*  298 */     this.dispStatusBar.add(((telnet)this.remconsObj.session).status_box, "West");
/*  299 */     this.dispStatusBar.add(this.remconsObj.pwrStatusPanel, "East");
/*      */ 
/*      */     
/*  302 */     String str3 = this.jsonObj.getJSONRequest("session_info");
/*  303 */     JPopupMenu.setDefaultLightWeightPopupEnabled(false);
/*  304 */     this.dispFrame.setJMenuBar(this.mainMenuBar);
/*  305 */     if (str3 != null) {
/*      */       
/*  307 */       makePsMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(str3, "reset_priv"));
/*  308 */       makeVdMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(str3, "virtual_media_priv"));
/*      */     } 
/*  310 */     makeKbMenu(this.mainMenuBar);
/*  311 */     String str2 = this.jsonObj.getJSONRequest("login_session");
/*      */     
/*  313 */     if (str2 != null) {
/*      */       
/*  315 */       str1 = this.jsonObj.getJSONObject(str2, "alt");
/*  316 */       if (str1 == null || (str1 != null && this.jsonObj.getJSONNumber(str1, "mode") == 0))
/*      */       {
/*  318 */         makeHlpMenu(this.mainMenuBar);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  323 */     this.scroller = new JScrollPane((Component)this.remconsObj.session, 20, 30);
/*  324 */     this.scroller.setVisible(true);
/*      */ 
/*      */     
/*      */     try {
/*  328 */       String str = getLocalString(4132) + " " + this.server_name + " " + getLocalString(4133) + " " + this.ilo_fqdn;
/*      */       
/*  330 */       if (this.blade == 1 && this.in_enclosure == true) {
/*  331 */         str = str + " " + getLocalString(4134) + " " + this.enclosure + " " + getLocalString(4135) + " " + this.bay;
/*      */       }
/*      */       
/*  334 */       this.dispFrame.setTitle(str);
/*      */     } catch (Exception exception) {
/*      */       
/*  337 */       this.dispFrame.setTitle(getLocalString(4132) + " " + getCodeBase().getHost());
/*  338 */       System.out.println("IRC title not available");
/*      */     } 
/*  340 */     int i = (Toolkit.getDefaultToolkit().getScreenSize()).width;
/*  341 */     int j = (Toolkit.getDefaultToolkit().getScreenSize()).height;
/*      */     
/*  343 */     boolean bool1 = (i < 1054) ? i : true;
/*  344 */     boolean bool2 = (j < 874) ? (j - 30) : true;
/*  345 */     boolean bool3 = (i > 1054) ? ((i - 1054) / 2) : false;
/*  346 */     boolean bool4 = (j > 874) ? ((j - 874) / 2) : false;
/*      */     
/*  348 */     this.dispFrame.setSize(bool1, bool2);
/*  349 */     this.dispFrame.setLocation(bool3, bool4);
/*  350 */     System.out.println("check dimensions " + bool1 + " " + bool2 + " " + bool3 + " " + bool4);
/*  351 */     this.dispFrame.setVisible(true);
/*      */     
/*      */     try {
/*  354 */       Insets insets = this.dispFrame.getInsets();
/*  355 */       ClassLoader classLoader = getClass().getClassLoader();
/*  356 */       if (str1 == null || (str1 != null && this.jsonObj.getJSONNumber(str1, "mode") == 0))
/*      */       {
/*  358 */         this.dispFrame.setIconImage(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/ilo_logo.png")));
/*      */       }
/*  360 */       Image image = this.dispFrame.getIconImage();
/*  361 */       if (image == null) {
/*  362 */         System.out.println("Dimage is null");
/*      */       }
/*      */     } catch (Exception exception) {
/*      */       
/*  366 */       System.out.println("JIRC icon not available");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void makeHlpMenu(JMenuBar paramJMenuBar) {
/*  376 */     this.hlpMenu = new JMenu(getLocalString(4136));
/*  377 */     this.aboutJirc = new JMenuItem(getLocalString(4137));
/*  378 */     this.aboutJirc.addActionListener(this);
/*  379 */     this.hlpMenu.add(this.aboutJirc);
/*  380 */     paramJMenuBar.add(this.hlpMenu);
/*      */   }
/*      */   
/*      */   protected void makeVdMenu(JMenuBar paramJMenuBar, int paramInt) {
/*  384 */     this.vdMenu = new JMenu(getLocalString(4098));
/*  385 */     if (paramInt == 1) {
/*  386 */       paramJMenuBar.add(this.vdMenu);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateVdMenu() {
/*  393 */     this.ma = new MediaAccess();
/*  394 */     ClassLoader classLoader = getClass().getClassLoader();
/*      */     
/*  396 */     String str1 = this.jsonObj.getJSONRequest("vm_status");
/*      */     
/*  398 */     String str2 = this.jsonObj.getJSONArray(str1, "options", 0);
/*      */     
/*  400 */     String str3 = this.jsonObj.getJSONArray(str1, "options", 1);
/*      */ 
/*      */     
/*  403 */     String[] arrayOfString = this.ma.devices();
/*  404 */     this.vdmenuIndx = 0;
/*  405 */     if (arrayOfString != null) {
/*  406 */       this.vdMenuItems = new JCheckBoxMenuItem[arrayOfString.length + 5];
/*  407 */       for (byte b = 0; b < arrayOfString.length; b++) {
/*  408 */         int i = this.ma.devtype(arrayOfString[b]);
/*  409 */         if (i == 5) {
/*  410 */           this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b]);
/*  411 */           this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + arrayOfString[b]);
/*  412 */           this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/*  413 */           this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/CD_Drive.png"))));
/*  414 */           this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/*  415 */           this.vdmenuIndx++;
/*      */         }
/*  417 */         else if (i == 2) {
/*  418 */           this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b]);
/*  419 */           this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + arrayOfString[b]);
/*  420 */           this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/*  421 */           if (arrayOfString[b].equals("A:") || arrayOfString[b].equals("B:")) {
/*  422 */             this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
/*      */           } else {
/*  424 */             this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/usb.png"))));
/*      */           } 
/*  426 */           this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/*  427 */           this.vdmenuIndx++;
/*      */         } 
/*      */       } 
/*      */     } else {
/*  431 */       this.vdMenuItems = new JCheckBoxMenuItem[5];
/*  432 */       System.out.println("Media Access not available...");
/*      */     } 
/*  434 */     this.ma = null;
/*      */ 
/*      */     
/*  437 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4130) + " " + getLocalString(4106));
/*  438 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + getLocalString(12567));
/*  439 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/*  440 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
/*  441 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/*  442 */     this.vdmenuIndx++;
/*      */     
/*  444 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4131) + getLocalString(4106));
/*  445 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("FLOPPY");
/*  446 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Network.png"))));
/*  447 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/*  448 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/*  449 */     this.vdmenuIndx++;
/*  450 */     if (this.jsonObj.getJSONNumber(str2, "vm_url_connected") == 1 && this.jsonObj.getJSONNumber(str2, "vm_connected") == 1) {
/*  451 */       this.fdSelected = true;
/*  452 */       lockFdMenu(false, "URL Removable Media");
/*      */     } 
/*      */ 
/*      */     
/*  456 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4130) + " " + getLocalString(4107));
/*  457 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + getLocalString(12567));
/*  458 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/*  459 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
/*  460 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/*  461 */     this.vdmenuIndx++;
/*      */     
/*  463 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4131) + getLocalString(4107));
/*  464 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("CDROM");
/*  465 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Network.png"))));
/*  466 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/*  467 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/*  468 */     this.vdmenuIndx++;
/*  469 */     if (this.jsonObj.getJSONNumber(str3, "vm_url_connected") == 1 && this.jsonObj.getJSONNumber(str3, "vm_connected") == 1) {
/*  470 */       this.cdSelected = true;
/*  471 */       lockCdMenu(false, "URL CD/DVD-ROM");
/*      */     } 
/*      */ 
/*      */     
/*  475 */     this.vdMenu.addSeparator();
/*  476 */     this.vdMenuItemCrImage = new JMenuItem(getLocalString(4109));
/*  477 */     this.vdMenuItemCrImage.setActionCommand("CreateDiskImage");
/*  478 */     this.vdMenuItemCrImage.addActionListener(this);
/*  479 */     this.vdMenu.add(this.vdMenuItemCrImage);
/*      */   }
/*      */ 
/*      */   
/*      */   public void lockCdMenu(boolean paramBoolean, String paramString) {
/*  484 */     byte b = 0;
/*      */     
/*  486 */     for (b = 0; b < this.vdmenuIndx; b++) {
/*  487 */       this.vdMenu.getItem(b).removeItemListener(this);
/*      */ 
/*      */       
/*  490 */       if (this.vdMenu.getItem(b).getActionCommand().startsWith("cd") || this.vdMenu.getItem(b).getActionCommand().equals("CDROM"))
/*      */       {
/*  492 */         if (paramString.equals(this.vdMenu.getItem(b).getText())) {
/*      */ 
/*      */           
/*  495 */           this.vdMenu.getItem(b).setSelected(!paramBoolean);
/*      */         } else {
/*      */           
/*  498 */           this.vdMenu.getItem(b).setSelected(false);
/*  499 */           this.vdMenu.getItem(b).setEnabled(paramBoolean);
/*      */         } 
/*      */       }
/*  502 */       this.vdMenu.getItem(b).addItemListener(this);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void lockFdMenu(boolean paramBoolean, String paramString) {
/*  508 */     byte b = 0;
/*      */     
/*  510 */     for (b = 0; b < this.vdmenuIndx; b++) {
/*  511 */       this.vdMenu.getItem(b).removeItemListener(this);
/*      */       
/*  513 */       if (this.vdMenu.getItem(b).getActionCommand().startsWith("fd") || this.vdMenu.getItem(b).getActionCommand().equals("FLOPPY"))
/*      */       {
/*  515 */         if (paramString.equals(this.vdMenu.getItem(b).getText())) {
/*      */ 
/*      */           
/*  518 */           this.vdMenu.getItem(b).setSelected(!paramBoolean);
/*      */         } else {
/*      */           
/*  521 */           this.vdMenu.getItem(b).setSelected(false);
/*  522 */           this.vdMenu.getItem(b).setEnabled(paramBoolean);
/*      */         } 
/*      */       }
/*  525 */       this.vdMenu.getItem(b).addItemListener(this);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void makePsMenu(JMenuBar paramJMenuBar, int paramInt) {
/*  531 */     ClassLoader classLoader = getClass().getClassLoader();
/*      */     
/*  533 */     this.psMenu = new JMenu(getLocalString(4097));
/*      */     
/*  535 */     this.momPress = new JMenuItem(getLocalString(4100));
/*  536 */     this.momPress.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/press.png"))));
/*  537 */     this.momPress.setActionCommand("psMomPress");
/*  538 */     this.momPress.addActionListener(this);
/*  539 */     this.psMenu.add(this.momPress);
/*      */     
/*  541 */     this.pressHold = new JMenuItem(getLocalString(4101));
/*  542 */     this.pressHold.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/hold.png"))));
/*  543 */     this.pressHold.setActionCommand("psPressHold");
/*  544 */     this.pressHold.addActionListener(this);
/*      */     
/*  546 */     this.powerCycle = new JMenuItem(getLocalString(4102));
/*  547 */     this.powerCycle.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/coldboot.png"))));
/*  548 */     this.powerCycle.setActionCommand("psPowerCycle");
/*  549 */     this.powerCycle.addActionListener(this);
/*      */     
/*  551 */     this.sysReset = new JMenuItem(getLocalString(4103));
/*  552 */     this.sysReset.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/reset.png"))));
/*  553 */     this.sysReset.setActionCommand("psSysReset");
/*  554 */     this.sysReset.addActionListener(this);
/*      */     
/*  556 */     if (paramInt == 1) {
/*  557 */       paramJMenuBar.add(this.psMenu);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void updatePsMenu(int paramInt) {
/*  563 */     if (0 == paramInt) {
/*  564 */       this.psMenu.remove(this.pressHold);
/*  565 */       this.psMenu.remove(this.powerCycle);
/*  566 */       this.psMenu.remove(this.sysReset);
/*      */     }
/*      */     else {
/*      */       
/*  570 */       this.psMenu.remove(this.pressHold);
/*  571 */       this.psMenu.remove(this.powerCycle);
/*  572 */       this.psMenu.remove(this.sysReset);
/*      */       
/*  574 */       this.psMenu.add(this.pressHold);
/*  575 */       this.psMenu.add(this.powerCycle);
/*  576 */       this.psMenu.add(this.sysReset);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void makeKbMenu(JMenuBar paramJMenuBar) {
/*  583 */     ClassLoader classLoader = getClass().getClassLoader();
/*  584 */     this.kbMenu = new JMenu(getLocalString(4099));
/*  585 */     this.kbCAFMenu = new JMenu("CTRL-ALT-Fn");
/*  586 */     this.kbAFMenu = new JMenu("ALT-Fn");
/*  587 */     this.kbLangMenu = new JMenu(getLocalString(4110));
/*      */     
/*  589 */     this.ctlAltDel = new JMenuItem(getLocalString(4104));
/*  590 */     this.ctlAltDel.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/*  591 */     this.ctlAltDel.setActionCommand("kbCtlAltDel");
/*  592 */     this.ctlAltDel.addActionListener(this);
/*  593 */     this.kbMenu.add(this.ctlAltDel);
/*      */     
/*  595 */     this.numLock = new JMenuItem(getLocalString(4105));
/*  596 */     this.numLock.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/*  597 */     this.numLock.setActionCommand("kbNumLock");
/*  598 */     this.numLock.addActionListener(this);
/*  599 */     this.kbMenu.add(this.numLock);
/*      */     
/*  601 */     this.capsLock = new JMenuItem(getLocalString(4128));
/*  602 */     this.capsLock.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/*  603 */     this.capsLock.setActionCommand("kbCapsLock");
/*  604 */     this.capsLock.addActionListener(this);
/*  605 */     this.kbMenu.add(this.capsLock);
/*      */     
/*  607 */     this.ctlAltBack = new JMenuItem("CTRL-ALT-BACKSPACE");
/*  608 */     this.ctlAltBack.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/*  609 */     this.ctlAltBack.setActionCommand("kbCtlAltBack");
/*  610 */     this.ctlAltBack.addActionListener(this);
/*      */ 
/*      */     
/*  613 */     this.ctlAltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS]; byte b;
/*  614 */     for (b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
/*  615 */       this.ctlAltFn[b] = new JMenuItem("CTRL-ALT-F" + (b + 1));
/*  616 */       this.ctlAltFn[b].setActionCommand("kbCtrlAltFn" + b);
/*      */ 
/*      */       
/*  619 */       this.ctlAltFn[b].addActionListener(this);
/*  620 */       this.kbCAFMenu.add(this.ctlAltFn[b]);
/*      */     } 
/*  622 */     this.AltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];
/*  623 */     for (b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
/*  624 */       this.AltFn[b] = new JMenuItem("ALT-F" + (b + 1));
/*  625 */       this.AltFn[b].setActionCommand("kbAltFn" + b);
/*  626 */       this.AltFn[b].addActionListener(this);
/*  627 */       this.kbAFMenu.add(this.AltFn[b]);
/*      */     } 
/*      */     
/*  630 */     this.localKbdLayout = new JCheckBoxMenuItem[this.REMCONS_MAX_KBD_LAYOUT];
/*  631 */     for (b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
/*  632 */       this.localKbdLayout[b] = new JCheckBoxMenuItem(getLocalString(4111 + b));
/*  633 */       this.localKbdLayout[b].setActionCommand("localKbdLayout" + b);
/*  634 */       this.localKbdLayout[b].addItemListener(this);
/*  635 */       this.kbLangMenu.add(this.localKbdLayout[b]);
/*      */     } 
/*  637 */     this.localKbdLayout[0].setSelected(true);
/*      */     
/*  639 */     String str = System.getProperty("os.name").toLowerCase();
/*  640 */     if (!str.startsWith("windows")) {
/*  641 */       this.kbMenu.add(this.ctlAltBack);
/*  642 */       this.kbMenu.add(this.kbCAFMenu);
/*  643 */       this.kbMenu.add(this.kbAFMenu);
/*  644 */       this.kbMenu.add(this.kbLangMenu);
/*      */     } 
/*      */     
/*  647 */     this.kbMenu.addSeparator();
/*  648 */     this.hotKeys = new JMenuItem(getLocalString(4129));
/*  649 */     this.hotKeys.addActionListener(this);
/*  650 */     this.kbMenu.add(this.hotKeys);
/*      */     
/*  652 */     paramJMenuBar.add(this.kbMenu);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void actionPerformed(ActionEvent paramActionEvent) {
/*  660 */     if (paramActionEvent.getSource() == this.momPress) {
/*  661 */       this.remconsObj.session.sendMomPress();
/*      */     }
/*  663 */     else if (paramActionEvent.getSource() == this.pressHold) {
/*  664 */       this.remconsObj.session.sendPressHold();
/*      */     }
/*  666 */     else if (paramActionEvent.getSource() == this.powerCycle) {
/*  667 */       this.remconsObj.session.sendPowerCycle();
/*      */     }
/*  669 */     else if (paramActionEvent.getSource() == this.sysReset) {
/*  670 */       this.remconsObj.session.sendSystemReset();
/*      */ 
/*      */     
/*      */     }
/*  674 */     else if (paramActionEvent.getSource() == this.ctlAltDel) {
/*  675 */       this.remconsObj.session.send_ctrl_alt_del();
/*      */     }
/*  677 */     else if (paramActionEvent.getSource() == this.numLock) {
/*  678 */       this.remconsObj.session.send_num_lock();
/*      */     }
/*  680 */     else if (paramActionEvent.getSource() == this.capsLock) {
/*  681 */       this.remconsObj.session.send_caps_lock();
/*      */     }
/*  683 */     else if (paramActionEvent.getSource() == this.ctlAltBack) {
/*  684 */       this.remconsObj.session.send_ctrl_alt_back();
/*      */     }
/*  686 */     else if (paramActionEvent.getSource() == this.hotKeys) {
/*  687 */       this.remconsObj.viewHotKeys();
/*      */ 
/*      */     
/*      */     }
/*  691 */     else if (paramActionEvent.getSource() == this.vdMenuItemCrImage) {
/*  692 */       this.virtdevsObj.createImage();
/*      */     
/*      */     }
/*  695 */     else if (paramActionEvent.getSource() == this.aboutJirc) {
/*  696 */       this.remconsObj.viewAboutJirc();
/*      */     }
/*      */     else {
/*      */       
/*  700 */       for (byte b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
/*  701 */         if (paramActionEvent.getSource() == this.ctlAltFn[b]) {
/*  702 */           this.remconsObj.session.send_ctrl_alt_fn(b);
/*      */           break;
/*      */         } 
/*  705 */         if (paramActionEvent.getSource() == this.AltFn[b]) {
/*  706 */           this.remconsObj.session.send_alt_fn(b);
/*      */           break;
/*      */         } 
/*      */       } 
/*  710 */       if (b >= this.REMCONS_MAX_FN_KEYS) {
/*  711 */         System.out.println("Unhandled ActionItem" + paramActionEvent.getActionCommand());
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void itemStateChanged(ItemEvent paramItemEvent) {
/*  719 */     boolean bool = false;
/*  720 */     JCheckBoxMenuItem jCheckBoxMenuItem = null;
/*  721 */     String str1 = null;
/*  722 */     String str2 = null;
/*  723 */     int i = paramItemEvent.getStateChange();
/*      */     
/*      */     byte b;
/*  726 */     for (b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
/*  727 */       if (this.localKbdLayout[b] == paramItemEvent.getSource() && i == 1) {
/*  728 */         System.out.println(b);
/*  729 */         this.localKbdLayout[b].setSelected(true);
/*  730 */         kbdLayoutMenuHandler(b);
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/*  736 */     for (b = 0; b < this.vdmenuIndx; b++) {
/*  737 */       if (this.vdMenuItems[b] == paramItemEvent.getSource()) {
/*  738 */         jCheckBoxMenuItem = this.vdMenuItems[b];
/*  739 */         str1 = jCheckBoxMenuItem.getActionCommand();
/*      */         
/*  741 */         str2 = jCheckBoxMenuItem.getLabel();
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*      */     
/*  747 */     if (jCheckBoxMenuItem == null || str1 == null) {
/*  748 */       System.out.println("Unhandled item event");
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  753 */     if (str1.equals("fd" + getLocalString(12567))) {
/*  754 */       String str = null;
/*  755 */       if (i == 2) {
/*  756 */         bool = this.virtdevsObj.do_floppy(str2);
/*  757 */         lockFdMenu(true, str2);
/*      */       }
/*  759 */       else if (i == 1) {
/*      */         
/*  761 */         this.dispFrame.setVisible(false);
/*  762 */         VFileDialog vFileDialog = new VFileDialog(getLocalString(8261), "*.img");
/*  763 */         str = vFileDialog.getString();
/*  764 */         this.dispFrame.setVisible(true);
/*      */         
/*  766 */         if (str != null) {
/*  767 */           if (this.virtdevsObj.fdThread != null)
/*  768 */             this.virtdevsObj.change_disk(this.virtdevsObj.fdConnection, str); 
/*  769 */           System.out.println("Image file: " + str);
/*  770 */           bool = this.virtdevsObj.do_floppy(str);
/*  771 */           if (!bool) {
/*      */             
/*  773 */             lockFdMenu(true, str2);
/*      */           } else {
/*  775 */             lockFdMenu(false, str2);
/*      */           } 
/*      */         } else {
/*  778 */           lockFdMenu(true, str2);
/*      */         } 
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  785 */     if (str1.equals("cd" + getLocalString(12567))) {
/*  786 */       String str = null;
/*  787 */       if (i == 2) {
/*  788 */         bool = this.virtdevsObj.do_cdrom(str2);
/*  789 */         lockCdMenu(true, str2);
/*      */       }
/*  791 */       else if (i == 1) {
/*      */         
/*  793 */         this.dispFrame.setVisible(false);
/*  794 */         VFileDialog vFileDialog = new VFileDialog(getLocalString(8261), "*.iso");
/*  795 */         str = vFileDialog.getString();
/*  796 */         this.dispFrame.setVisible(true);
/*      */         
/*  798 */         if (str != null) {
/*  799 */           if (this.virtdevsObj.cdThread != null)
/*  800 */             this.virtdevsObj.change_disk(this.virtdevsObj.cdConnection, str); 
/*  801 */           System.out.println("Image file: " + str);
/*  802 */           bool = this.virtdevsObj.do_cdrom(str);
/*  803 */           if (!bool) {
/*      */             
/*  805 */             lockCdMenu(true, str2);
/*      */           } else {
/*  807 */             lockCdMenu(false, str2);
/*      */           } 
/*      */         } else {
/*  810 */           lockCdMenu(true, str2);
/*      */         } 
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  817 */     if (str1.startsWith("cd")) {
/*  818 */       bool = this.virtdevsObj.do_cdrom(str2);
/*  819 */       if (bool)
/*      */       {
/*  821 */         lockCdMenu((i != 1), str2);
/*      */       }
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  827 */     if (str1.startsWith("fd")) {
/*  828 */       bool = this.virtdevsObj.do_floppy(str2);
/*  829 */       if (bool)
/*      */       {
/*  831 */         lockFdMenu((i != 1), str2);
/*      */       }
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  837 */     if (str1.equals("FLOPPY") || str1.equals("CDROM")) {
/*      */ 
/*      */       
/*  840 */       String str = "";
/*  841 */       boolean bool1 = false;
/*      */       
/*  843 */       if (i == 2) {
/*  844 */         String str3 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1 + "\", \"command\":\"EJECT\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";
/*  845 */         str = this.jsonObj.postJSONRequest("vm_status", str3);
/*  846 */         this.remconsObj.session.set_status(3, "Unmounted URL");
/*      */       }
/*  848 */       else if (i == 1) {
/*  849 */         this.remconsObj.setDialogIsOpen(true);
/*  850 */         URLDialog uRLDialog = new URLDialog(this.remconsObj);
/*  851 */         String str3 = uRLDialog.getUserInput();
/*      */         
/*  853 */         if (str3.compareTo("userhitcancel") == 0 || str3.compareTo("userhitclose") == 0) {
/*  854 */           str3 = null;
/*      */         }
/*      */         
/*  857 */         if (str3 != null) {
/*  858 */           str3 = str3.replaceAll("[\000-\037]", "");
/*  859 */           System.out.println("url:  " + str3);
/*      */         } 
/*      */         
/*  862 */         this.remconsObj.setDialogIsOpen(false);
/*  863 */         if (str3 != null) {
/*  864 */           String str4 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1 + "\", \"command\":\"INSERT\", \"url\":\"" + str3 + "\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";
/*      */           
/*  866 */           str = this.jsonObj.postJSONRequest("vm_status", str4);
/*  867 */           if (str == "Success") {
/*  868 */             str4 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1 + "\", \"boot_option\":\"CONNECT\", \"command\":\"SET\", \"url\":\"" + str3 + "\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";
/*      */             
/*  870 */             str = this.jsonObj.postJSONRequest("vm_status", str4);
/*      */           } 
/*      */           
/*  873 */           if (str == "SCSI_ERR_NO_LICENSE") {
/*  874 */             String str5 = "<html>" + getLocalString(8213) + " " + getLocalString(8214) + " " + getLocalString(8237) + "<br><br>" + getLocalString(8238) + "</html>";
/*      */             
/*  876 */             new VErrorDialog(this.dispFrame, getLocalString(8236), str5, true);
/*      */           }
/*  878 */           else if (str != "Success") {
/*  879 */             new VErrorDialog(this.dispFrame, getLocalString(8212), getLocalString(8292), true);
/*      */           } else {
/*  881 */             bool1 = true;
/*  882 */             this.remconsObj.session.set_status(3, getLocalString(12581));
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/*  887 */       if (str1.equals("FLOPPY")) {
/*  888 */         lockFdMenu(!bool1, str2);
/*      */       }
/*  890 */       else if (str1.equals("CDROM")) {
/*      */         
/*  892 */         lockCdMenu(!bool1, str2);
/*      */       } 
/*      */       return;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void kbdLayoutMenuHandler(int paramInt) {
/*  904 */     for (byte b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
/*  905 */       if (b != paramInt) {
/*  906 */         this.localKbdLayout[b].setSelected(false);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  911 */     this.remconsObj.setLocalKbdLayout(paramInt);
/*      */   } class WindowCloser extends WindowAdapter { private final intgapp this$0;
/*      */     WindowCloser(intgapp this$0) {
/*  914 */       this.this$0 = this$0;
/*      */     }
/*      */ 
/*      */     
/*      */     public void windowClosing(WindowEvent param1WindowEvent) {
/*  919 */       this.this$0.stop();
/*  920 */       this.this$0.exit = true;
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void ApplyRcInfoParameters(String paramString) {
/*  927 */     this.enc_key = this.rc_port = this.vm_key = this.vm_port = null;
/*  928 */     Arrays.fill(this.enc_key_val, (byte)0);
/*      */ 
/*      */     
/*  931 */     paramString = paramString.trim();
/*  932 */     paramString = paramString.substring(1, paramString.length() - 1);
/*  933 */     String[] arrayOfString = paramString.split(",");
/*      */     
/*  935 */     for (byte b = 0; b < arrayOfString.length; b++) {
/*  936 */       String[] arrayOfString1 = arrayOfString[b].split(":");
/*  937 */       if (arrayOfString1.length != 2) {
/*  938 */         System.out.println("Error in ApplyRcInfoParameters");
/*      */         
/*      */         return;
/*      */       } 
/*  942 */       String str1 = arrayOfString1[0].trim();
/*  943 */       str1 = str1.substring(1, str1.length() - 1);
/*      */       
/*  945 */       String str2 = arrayOfString1[1].trim();
/*  946 */       if (str2.charAt(0) == '"') {
/*  947 */         str2 = str2.substring(1, str2.length() - 1);
/*      */       }
/*      */       
/*  950 */       if (str1.compareToIgnoreCase("enc_key") == 0) {
/*      */ 
/*      */         
/*  953 */         this.enc_key = str2;
/*  954 */         for (byte b1 = 0; b1 < this.enc_key_val.length; b1++) {
/*  955 */           String str = this.enc_key.substring(b1 * 2, b1 * 2 + 2);
/*      */           try {
/*  957 */             this.enc_key_val[b1] = (byte)Integer.parseInt(str, 16);
/*      */           }
/*      */           catch (NumberFormatException numberFormatException) {
/*      */             
/*  961 */             System.out.println("Failed to Parse enc_key");
/*      */           }
/*      */         
/*      */         }
/*      */       
/*  966 */       } else if (str1.compareToIgnoreCase("rc_port") == 0) {
/*  967 */         System.out.println("rc_port:" + str2);
/*  968 */         this.rc_port = str2;
/*      */       }
/*  970 */       else if (str1.compareToIgnoreCase("vm_key") == 0) {
/*      */         
/*  972 */         this.vm_key = str2;
/*      */       }
/*  974 */       else if (str1.compareToIgnoreCase("vm_port") == 0) {
/*  975 */         System.out.println("vm_port:" + str2);
/*  976 */         this.vm_port = str2;
/*      */       }
/*  978 */       else if (str1.equalsIgnoreCase("optional_features")) {
/*  979 */         System.out.println("optional_features:" + str2);
/*  980 */         this.optional_features = str2;
/*      */       }
/*  982 */       else if (str1.compareToIgnoreCase("server_name") == 0) {
/*  983 */         System.out.println("server_name:" + str2);
/*  984 */         this.server_name = str2;
/*      */       }
/*  986 */       else if (str1.compareToIgnoreCase("ilo_fqdn") == 0) {
/*  987 */         System.out.println("ilo_fqdn:" + str2);
/*  988 */         this.ilo_fqdn = str2;
/*      */       }
/*  990 */       else if (str1.compareToIgnoreCase("blade") == 0) {
/*  991 */         this.blade = Integer.parseInt(str2);
/*  992 */         System.out.println("blade:" + this.blade);
/*      */       }
/*  994 */       else if (this.blade == 1 && str1.compareToIgnoreCase("enclosure") == 0) {
/*  995 */         if (!str2.equals("null")) {
/*  996 */           this.in_enclosure = true;
/*  997 */           System.out.println("enclosure:" + str2);
/*  998 */           this.enclosure = str2;
/*      */         }
/*      */       
/* 1001 */       } else if (this.blade == 1 && str1.compareToIgnoreCase("bay") == 0) {
/* 1002 */         this.bay = Integer.parseInt(str2);
/* 1003 */         System.out.println("bay:" + this.bay);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void moveUItoInit(boolean paramBoolean) {
/* 1010 */     System.out.println("Disable Menus\n");
/* 1011 */     this.psMenu.setEnabled(paramBoolean);
/* 1012 */     this.vdMenu.setEnabled(paramBoolean);
/* 1013 */     this.kbMenu.setEnabled(paramBoolean);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String rebrandToken(String paramString) {
/* 1019 */     if (!this.moniker_cached) {
/*      */       
/* 1021 */       String str1 = this.jsonObj.getJSONRequest("login_session");
/* 1022 */       if (str1 == null)
/*      */       {
/* 1024 */         return paramString;
/*      */       }
/* 1026 */       this.moniker = this.jsonObj.getJSONObject(str1, "moniker");
/* 1027 */       if (this.moniker == null)
/*      */       {
/* 1029 */         return paramString;
/*      */       }
/* 1031 */       this.moniker_cached = true;
/*      */     } 
/* 1033 */     String str = this.jsonObj.getJSONString(this.moniker, paramString);
/* 1034 */     if (str == "")
/*      */     {
/* 1036 */       return paramString;
/*      */     }
/* 1038 */     return str;
/*      */   }
/*      */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/intgapp/intgapp.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */