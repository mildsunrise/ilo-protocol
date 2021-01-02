/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import com.hp.ilo2.intgapp.intgapp;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.Image;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketImpl;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Properties;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class virtdevs
/*     */   extends JPanel
/*     */   implements Runnable
/*     */ {
/*     */   public static final int UNQF_HIDEFLP = 1;
/*     */   public static int UID;
/*     */   static final int ImageDone = 39;
/*     */   String host;
/*     */   String base;
/*     */   String configuration;
/*     */   String dev_floppy;
/*     */   String dev_cdrom;
/*     */   String dev_auto;
/*  38 */   public int dev_cd_device = 0; public int dev_fd_device = 0; public int unq_feature = 0;
/*     */   
/*     */   boolean force_config = false;
/*     */   boolean thread_init = false;
/*  42 */   byte[] pre = new byte[16];
/*  43 */   byte[] key = new byte[32];
/*  44 */   int fdport = 17988;
/*     */   
/*     */   JFrame parent;
/*     */   public static boolean cd_support = true;
/*     */   public static Properties prop;
/*     */   public intgapp ParentApp;
/*     */   public boolean cdConnected;
/*     */   public boolean fdConnected;
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  54 */     String str = "";
/*     */     try {
/*  56 */       str = this.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*     */       
/*  59 */       System.out.println("virdevs:getLocalString" + exception.getMessage());
/*     */     } 
/*  61 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean stopFlag;
/*     */   
/*     */   protected boolean running;
/*     */   String hostAddress;
/*     */   
/*     */   public Image get(String paramString) {
/*  71 */     ClassLoader classLoader = getClass().getClassLoader();
/*  72 */     return this.ParentApp.getImage(classLoader.getResource("com/hp/ilo2/virtdevs/" + paramString));
/*     */   }
/*     */   public Connection fdConnection; public Connection cdConnection;
/*     */   public Thread fdThread;
/*     */   public Thread cdThread;
/*     */   
/*     */   public void init() {
/*  79 */     if (UID == 0) {
/*  80 */       UID = hashCode();
/*     */     }
/*  82 */     URL uRL = this.ParentApp.getDocumentBase();
/*  83 */     this.host = this.ParentApp.getParameter("hostAddress");
/*  84 */     if (this.host == null)
/*  85 */       this.host = uRL.getHost(); 
/*  86 */     this.base = uRL.getProtocol() + "://" + uRL.getHost();
/*  87 */     if (uRL.getPort() != -1)
/*  88 */       this.base += ":" + uRL.getPort(); 
/*  89 */     this.base += "/";
/*     */     
/*  91 */     String str1 = this.ParentApp.getParameter("INFO0");
/*  92 */     if (str1 != null) {
/*     */       try {
/*  94 */         for (byte b = 0; b < 16; b++) {
/*  95 */           this.pre[b] = (byte)Integer.parseInt(str1.substring(2 * b, 2 * b + 2), 16);
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException numberFormatException) {
/*     */         
/* 100 */         D.println(0, "Couldn't parse INFO0: " + numberFormatException);
/*     */       } 
/*     */     }
/*     */     
/*     */     try {
/* 105 */       if (null != this.ParentApp.vm_port) {
/* 106 */         this.fdport = Integer.parseInt(this.ParentApp.vm_port);
/*     */       }
/*     */     } catch (NumberFormatException numberFormatException) {
/*     */       
/* 110 */       D.println(0, "Couldn't parse INFO1: " + numberFormatException);
/*     */     } 
/*     */     
/* 113 */     this.configuration = this.ParentApp.getParameter("INFO2");
/* 114 */     if (this.configuration == null) {
/* 115 */       this.configuration = "auto";
/*     */     }
/* 117 */     this.dev_floppy = this.ParentApp.getParameter("floppy");
/* 118 */     this.dev_cdrom = this.ParentApp.getParameter("cdrom");
/* 119 */     this.dev_auto = this.ParentApp.getParameter("device");
/* 120 */     String str2 = this.ParentApp.getParameter("config");
/* 121 */     if (str2 != null) {
/* 122 */       this.configuration = str2;
/* 123 */       this.force_config = true;
/*     */     } 
/*     */     
/* 126 */     String str3 = this.ParentApp.getParameter("UNIQUE_FEATURES");
/*     */     try {
/* 128 */       if (str3 != null) {
/* 129 */         this.unq_feature = Integer.parseInt(str3);
/*     */       }
/*     */     } catch (NumberFormatException numberFormatException) {
/* 132 */       D.println(0, "Couldn't parse UNIQUE_FEATURES: " + numberFormatException);
/*     */     } 
/* 134 */     this.key = this.ParentApp.getParameter("RCINFO1").getBytes();
/*     */ 
/*     */     
/* 137 */     if (this.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1)
/*     */     {
/* 139 */       for (byte b = 0; b < this.key.length; b++)
/*     */       {
/* 141 */         this.key[b] = (byte)(this.key[b] ^ (byte)this.ParentApp.enc_key.charAt(b % this.ParentApp.enc_key.length()));
/*     */       }
/*     */     }
/*     */     
/* 145 */     this.parent = this.ParentApp.dispFrame;
/*     */   }
/*     */ 
/*     */   
/*     */   public void start() {
/* 150 */     Thread thread = new Thread(this);
/* 151 */     thread.start();
/*     */     try {
/* 153 */       Thread.sleep(1000L);
/*     */     } catch (InterruptedException interruptedException) {
/*     */       
/* 156 */       System.out.println("Exception: " + interruptedException);
/*     */     } 
/* 158 */     this.hostAddress = this.host;
/* 159 */     if (ui_init(this.base)) {
/* 160 */       if (this.force_config)
/* 161 */         updateconfig(); 
/* 162 */       show();
/*     */       
/* 164 */       if (this.dev_floppy != null)
/* 165 */         do_floppy(this.dev_floppy); 
/* 166 */       if (this.dev_cdrom != null) {
/* 167 */         do_cdrom(this.dev_cdrom);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void stop() {
/* 173 */     D.println(3, "Stop " + this);
/* 174 */     if (this.fdConnection != null) {
/*     */       try {
/* 176 */         this.fdConnection.close();
/* 177 */         this.fdThread = null;
/*     */       } catch (IOException iOException) {
/*     */         
/* 180 */         D.println(3, iOException.toString());
/*     */       } 
/*     */     }
/* 183 */     if (this.cdConnection != null) {
/*     */       try {
/* 185 */         this.cdConnection.close();
/* 186 */         this.cdThread = null;
/*     */       } catch (IOException iOException) {
/*     */         
/* 189 */         D.println(3, iOException.toString());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 196 */     Thread thread = new Thread(this);
/* 197 */     thread.start();
/*     */     try {
/* 199 */       Thread.sleep(1000L);
/*     */     } catch (InterruptedException interruptedException) {
/*     */       
/* 202 */       System.out.println("Exception: " + interruptedException);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void run() {
/* 209 */     if (!this.thread_init) {
/*     */       
/* 211 */       prop = new Properties();
/*     */       try {
/* 213 */         prop.load(new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".java" + System.getProperty("file.separator") + "hp.properties"));
/*     */       } catch (Exception exception) {
/*     */         
/* 216 */         System.out.println("Exception: " + exception);
/*     */       } 
/* 218 */       cd_support = Boolean.valueOf(prop.getProperty("com.hp.ilo2.virtdevs.cdimage", "true")).booleanValue();
/*     */       
/* 220 */       MediaAccess mediaAccess = new MediaAccess();
/* 221 */       mediaAccess.setup_DirectIO();
/* 222 */       this.thread_init = true;
/* 223 */       this.ParentApp.updateVdMenu();
/*     */     } else {
/*     */       
/* 226 */       MediaAccess.cleanup(this);
/* 227 */       this.thread_init = false;
/*     */     } 
/*     */   }
/*     */   public virtdevs(intgapp paramintgapp) {
/* 231 */     this.cdConnected = false;
/* 232 */     this.fdConnected = false;
/*     */     
/* 234 */     this.stopFlag = false;
/* 235 */     this.running = false;
/*     */     this.ParentApp = paramintgapp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean ui_init(String paramString) {
/* 245 */     MouseAdapter mouseAdapter = new MouseAdapter(this) { private final virtdevs this$0;
/*     */         
/*     */         public void mouseClicked(MouseEvent param1MouseEvent) {
/* 248 */           if ((param1MouseEvent.getModifiers() & 0x2) != 0) {
/* 249 */             D.debug++;
/* 250 */             System.out.println("Debug set to " + D.debug);
/*     */           } 
/* 252 */           if ((param1MouseEvent.getModifiers() & 0x8) != 0) {
/* 253 */             D.debug--;
/* 254 */             System.out.println("Debug set to " + D.debug);
/*     */           } 
/*     */         } }
/*     */       ;
/* 258 */     addMouseListener(mouseAdapter);
/* 259 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(Component paramComponent, GridBagConstraints paramGridBagConstraints, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 264 */     paramGridBagConstraints.gridx = paramInt1;
/* 265 */     paramGridBagConstraints.gridy = paramInt2;
/* 266 */     paramGridBagConstraints.gridwidth = paramInt3;
/* 267 */     paramGridBagConstraints.gridheight = paramInt4;
/* 268 */     add(paramComponent, paramGridBagConstraints);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createImage() {
/* 273 */     new CreateImage(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean do_floppy(String paramString) {
/* 279 */     if (!this.fdConnected) {
/*     */       int i; String str;
/*     */       try {
/* 282 */         this.fdConnection = new Connection(this.hostAddress, this.fdport, 1, paramString, 0, this.pre, this.key, this);
/*     */       } catch (Exception exception) {
/*     */         
/* 285 */         new VErrorDialog(this.parent, getLocalString(8212), exception.getMessage());
/* 286 */         return false;
/*     */       } 
/* 288 */       System.out.println("Starting fd non-Read-Only");
/*     */       
/* 290 */       this.fdConnection.setWriteProt(false);
/*     */       
/* 292 */       setCursor(Cursor.getPredefinedCursor(3));
/*     */       try {
/* 294 */         i = this.fdConnection.connect();
/*     */       } catch (Exception exception) {
/*     */         
/* 297 */         setCursor(Cursor.getPredefinedCursor(0));
/* 298 */         D.println(0, "Couldn't connect!\n");
/* 299 */         new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8197) + "(" + exception + ")");
/* 300 */         return false;
/*     */       } 
/* 302 */       setCursor(Cursor.getPredefinedCursor(0));
/*     */       
/* 304 */       switch (i) {
/*     */         case 0:
/*     */           break;
/*     */         
/*     */         case 33:
/* 309 */           this.ParentApp.lockFdMenu(true, "");
/* 310 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8198));
/* 311 */           return false;
/*     */         
/*     */         case 34:
/* 314 */           if (rekey("/html/java_irc.html")) {
/* 315 */             str = getLocalString(8199);
/*     */           } else {
/*     */             
/* 318 */             str = getLocalString(8200);
/*     */           } 
/* 320 */           new VErrorDialog(this.parent, getLocalString(8212), str);
/* 321 */           return false;
/*     */         case 35:
/* 323 */           this.ParentApp.lockFdMenu(true, "");
/* 324 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8201));
/* 325 */           return false;
/*     */         case 37:
/* 327 */           this.ParentApp.lockFdMenu(true, "");
/* 328 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8202));
/* 329 */           return false;
/*     */         case 38:
/* 331 */           this.ParentApp.lockFdMenu(true, "");
/* 332 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8203));
/* 333 */           return false;
/*     */         default:
/* 335 */           this.ParentApp.lockFdMenu(true, "");
/* 336 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8204) + "(" + Integer.toHexString(i) + ")." + getLocalString(8205));
/*     */           
/* 338 */           return false;
/*     */       } 
/*     */       
/* 341 */       this.fdThread = new Thread(this.fdConnection, "fdConnection");
/* 342 */       this.fdThread.start();
/* 343 */       this.fdConnected = true;
/*     */     } else {
/*     */       
/*     */       try {
/* 347 */         this.fdConnection.close();
/*     */       } catch (Exception exception) {
/*     */         
/* 350 */         D.println(0, "Exception during close: " + exception);
/*     */       } 
/*     */     } 
/* 353 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean do_cdrom(String paramString) {
/* 359 */     if (!this.cdConnected) {
/*     */       int i; String str;
/*     */       try {
/* 362 */         this.cdConnection = new Connection(this.hostAddress, this.fdport, 2, paramString, 0, this.pre, this.key, this);
/*     */       } catch (Exception exception) {
/*     */         
/* 365 */         new VErrorDialog(this.parent, getLocalString(8212), exception.getMessage());
/* 366 */         return false;
/*     */       } 
/* 368 */       this.cdConnection.setWriteProt(true);
/*     */       
/*     */       try {
/* 371 */         i = this.cdConnection.connect();
/*     */       } catch (Exception exception) {
/*     */         
/* 374 */         D.println(0, "Couldn't connect!\n");
/* 375 */         new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8206) + " (" + exception + ")");
/* 376 */         return false;
/*     */       } 
/*     */       
/* 379 */       switch (i) {
/*     */         case 0:
/*     */           break;
/*     */         
/*     */         case 33:
/* 384 */           this.ParentApp.lockCdMenu(true, "");
/* 385 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8198));
/* 386 */           return false;
/*     */         
/*     */         case 34:
/* 389 */           if (rekey("/html/java_irc.html")) {
/* 390 */             str = getLocalString(8199);
/*     */           } else {
/*     */             
/* 393 */             str = getLocalString(8200);
/*     */           } 
/* 395 */           new VErrorDialog(this.parent, getLocalString(8212), str);
/* 396 */           return false;
/*     */         case 35:
/* 398 */           this.ParentApp.lockCdMenu(true, "");
/* 399 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8201));
/* 400 */           return false;
/*     */         case 37:
/* 402 */           this.ParentApp.lockCdMenu(true, "");
/* 403 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8207));
/* 404 */           return false;
/*     */         case 38:
/* 406 */           this.ParentApp.lockCdMenu(true, "");
/* 407 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8203));
/* 408 */           return false;
/*     */         default:
/* 410 */           this.ParentApp.lockCdMenu(true, "");
/* 411 */           new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8204) + " (" + Integer.toHexString(i) + ")." + getLocalString(8205));
/*     */           
/* 413 */           return false;
/*     */       } 
/*     */       
/* 416 */       this.cdThread = new Thread(this.cdConnection, "cdConnection");
/* 417 */       this.cdThread.start();
/* 418 */       this.cdConnected = true;
/*     */     } else {
/*     */       
/*     */       try {
/* 422 */         this.cdConnection.close();
/*     */       } catch (Exception exception) {
/*     */         
/* 425 */         D.println(0, "Exception during close: " + exception);
/*     */       } 
/*     */     } 
/*     */     
/* 429 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void paint(Graphics paramGraphics) {
/* 434 */     paintComponent(paramGraphics);
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(Graphics paramGraphics) {
/* 439 */     paint(paramGraphics);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void updateconfig() {
/*     */     try {
/* 446 */       URL uRL = new URL(this.base + "modusb.cgi?usb=" + this.configuration);
/* 447 */       URLConnection uRLConnection = uRL.openConnection();
/* 448 */       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream())); String str;
/* 449 */       while ((str = bufferedReader.readLine()) != null) {
/* 450 */         D.println(3, "updcfg: " + str);
/*     */       }
/* 452 */       bufferedReader.close();
/*     */     } catch (Exception exception) {
/*     */       
/* 455 */       new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8208) + "(" + exception + ")");
/* 456 */       exception.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean rekey(String paramString) {
/* 462 */     String str = null;
/*     */     
/*     */     try {
/* 465 */       D.println(3, "Downloading new key: " + this.base + paramString);
/* 466 */       URL uRL = new URL(this.base + paramString);
/* 467 */       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream()));
/*     */       String str1;
/* 469 */       while ((str1 = bufferedReader.readLine()) != null) {
/*     */ 
/*     */         
/* 472 */         D.println(0, "rekey: " + str1);
/* 473 */         if (str1.startsWith("info0=\"")) {
/* 474 */           str = str1.substring(7, 39);
/*     */           break;
/*     */         } 
/*     */       } 
/* 478 */       bufferedReader.close();
/*     */     } catch (Exception exception) {
/*     */       
/* 481 */       D.println(0, "rekey: " + exception);
/* 482 */       new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8209));
/* 483 */       return false;
/*     */     } 
/* 485 */     if (str == null) {
/* 486 */       new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8209));
/* 487 */       return false;
/*     */     } 
/*     */     try {
/* 490 */       for (byte b = 0; b < 16; b++) {
/* 491 */         this.pre[b] = (byte)Integer.parseInt(str.substring(2 * b, 2 * b + 2), 16);
/*     */       }
/*     */     }
/*     */     catch (NumberFormatException numberFormatException) {
/*     */       
/* 496 */       D.println(0, "Couldn't parse new key: " + numberFormatException);
/* 497 */       new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8210));
/* 498 */       return false;
/*     */     } 
/* 500 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void change_disk(Connection paramConnection, String paramString) {
/*     */     try {
/* 506 */       paramConnection.change_disk(paramString);
/*     */     } catch (IOException iOException) {
/*     */       
/* 509 */       new VErrorDialog(this.parent, getLocalString(8212), getLocalString(8211) + " (" + iOException + ")");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void fdDisconnect() {
/* 515 */     this.fdThread = null;
/* 516 */     repaint();
/* 517 */     this.fdConnected = false;
/* 518 */     this.ParentApp.lockFdMenu(true, "");
/* 519 */     this.ParentApp.remconsObj.setvmAct(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void cdDisconnect() {
/* 524 */     this.cdThread = null;
/* 525 */     repaint();
/* 526 */     this.cdConnected = false;
/* 527 */     this.ParentApp.lockCdMenu(true, "");
/* 528 */     this.ParentApp.remconsObj.setvmAct(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getSockFd(Socket paramSocket) {
/* 537 */     int i = -1;
/* 538 */     Class clazz = null;
/* 539 */     Field field1 = null, field2 = null;
/*     */     
/*     */     try {
/* 542 */       clazz = Socket.class;
/* 543 */       Field[] arrayOfField = clazz.getDeclaredFields(); byte b;
/* 544 */       for (b = 0; b < arrayOfField.length; b++) {
/* 545 */         if (arrayOfField[b].getName().equals("impl")) {
/* 546 */           field1 = arrayOfField[b];
/* 547 */           field1.setAccessible(true);
/*     */ 
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */       
/* 554 */       SocketImpl socketImpl = (SocketImpl)field1.get(paramSocket);
/* 555 */       Class clazz2 = SocketImpl.class;
/* 556 */       arrayOfField = clazz2.getDeclaredFields();
/* 557 */       for (b = 0; b < arrayOfField.length; b++) {
/* 558 */         if (arrayOfField[b].getName().equals("fd")) {
/* 559 */           field2 = arrayOfField[b];
/* 560 */           field2.setAccessible(true);
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 565 */       FileDescriptor fileDescriptor = (FileDescriptor)field2.get(socketImpl);
/*     */       
/* 567 */       Class clazz1 = FileDescriptor.class;
/* 568 */       arrayOfField = clazz1.getDeclaredFields();
/* 569 */       for (b = 0; b < arrayOfField.length; b++) {
/* 570 */         if (arrayOfField[b].getName().equals("fd")) {
/* 571 */           field2 = arrayOfField[b];
/* 572 */           field2.setAccessible(true);
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 577 */       i = field2.getInt(fileDescriptor);
/*     */     } catch (Exception exception) {
/*     */       
/* 580 */       System.out.println("Ex: " + exception);
/*     */     } 
/* 582 */     return i;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/virtdevs.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */