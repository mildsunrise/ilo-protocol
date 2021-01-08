package com.hp.ilo2.remcons;

import com.hp.ilo2.intgapp.intgapp;
import com.hp.ilo2.virtdevs.VErrorDialog;
import com.hp.ilo2.virtdevs.virtdevs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class remcons
  extends JPanel
  implements TimerListener, Runnable
{
  private static boolean dialogIsOpen = false;
  private static final int SESSION_TIMEOUT_DEFAULT = 900;
  private static final int KEEP_ALIVE_INTERVAL = 30;
  private static final int INFINITE_TIMEOUT = 2147483640;
  private static final int REMCONS_MAX_FN_KEYS = 12;
  private static final int LICENSE_RC = 1;
  private int session_timeout = 900;
  
  public cim session;
  
  public cmd telnetObj;
  public KeyboardHook kHook = null;
  public boolean kbHookInstalled = false;
  public boolean kbHookAvailable = false;
  public int keyData = 0;
  public int prevKeyData = 0;
  
  public boolean kbHookDataRcvd = false;
  private String term_svcs_label = "Terminal Svcs";
  
  Image[] img;
  
  static final int ImageDone = 39;
  
  public JPanel pwrStatusPanel;
  
  public JPanel ledStatusPanel;
  
  private Image pwrEncImgLock;
  
  private Image pwrEncImgUnlock;
  
  private Image pwrEncImg;
  
  private JPanel pwrEncCanvas;
  
  private Image vmActImgOn;
  private Image vmActImgOff;
  private Image vmActImg;
  private JPanel vmActCanvas;
  private Image pwrHealthImgGreen;
  private Image pwrHealthImgYellow;
  private Image pwrHealthImgRed;
  private Image pwrHealthImgOff;
  private Image pwrHealthImg;
  private JPanel pwrHealthCanvas;
  private Image pwrPowerImgOn;
  private Image pwrPowerImgOff;
  private Image pwrPowerImg;
  private JPanel pwrPowerCanvas;
  private JLabel pwrEncLabel;
  private String login;
  private Timer timer;
  private Timer keyBoardTimer;
  private int keyTimerTick = 20;
  public int timeout_countdown;
  private int port_num = 23;
  private boolean translate = false;
  private boolean debug_msg = false;
  private String session_ip = null;
  private int num_cursors = 0;
  private int mouse_mode = 0;
  
  private String rcErrMessage;
  
  private JFrame parent_frame;
  public int[] rndm_nums = new int[12];
  
  private int terminalServicesPort = 3389;
  private boolean launchTerminalServices = false;
  private int ts_param = 0;
  
  public boolean session_encryption_enabled = false;
  public byte[] session_encrypt_key = new byte[16];
  public byte[] session_decrypt_key = new byte[16];
  public int session_key_index = 0;
  
  private LocaleTranslator lt = new LocaleTranslator();
  
  public static Properties prop;
  
  private static final char[] base64 = new char[] { Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '>',
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '?', '4', '5', '6', '7', '8', '9', ':', ';',
          '<', '=', Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '\001', '\002', '\003',
          '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022',
          '\023', '\024', '\025', '\026', '\027', '\030', '\031', Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, '\032', '\033', '\034',
          '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0',
          '1', '2', '3', Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE,
          Character.MIN_VALUE };







  
  public int initialized = 0;
  
  public boolean retry_connection_flag = false;
  
  public int retry_connection_count = 0;
  
  public static final int RETRY_CONNECTION_MAX = 3;
  
  Thread locale_setter;
  
  public boolean licensed = false;
  
  public boolean halfHeightCapable = false;
  boolean fdConnState = false, cdConnState = false;
  boolean fdCachedConnState = false;
  boolean cdCachedConnState = false;
  private int localKbdLayoutId = 0;


  
  public intgapp ParentApp;


  
  public String getLocalString(int paramInt) {
    String str = "";
    try {
      str = this.ParentApp.locinfoObj.getLocString(paramInt);
    } catch (Exception exception) {
      
      System.out.println("remcons:getLocalString" + exception.getMessage());
    } 
    return str;
  }

  
  public remcons(intgapp paramintgapp) {
    this.ParentApp = paramintgapp;
  }

  
  public Image getImg(String paramString) {
    ClassLoader classLoader = getClass().getClassLoader();
    return this.ParentApp.getImage(classLoader.getResource("com/hp/ilo2/remcons/images/" + paramString));
  }


  
  void waitImage(Image paramImage, ImageObserver paramImageObserver) {
    int i;
    long l = System.currentTimeMillis();
    do {
      i = checkImage(paramImage, paramImageObserver);
      if ((i & 0xC0) != 0)
        break; 
      Thread.yield();
      if (System.currentTimeMillis() - l > 2000L) {
        break;
      }
    } while ((i & 0x27) != 39);
  }






  
  public void init() {
    this.img = new Image[22];
    this.img[0] = getImg("blank_cd.png");
    this.img[1] = getImg("blue.png");
    this.img[2] = getImg("CD_Drive.png");
    this.img[3] = getImg("FloppyDisk.png");
    this.img[4] = getImg("Folder.png");
    this.img[5] = getImg("green.png");
    this.img[6] = getImg("hold.png");
    this.img[7] = null;
    this.img[8] = null;
    this.img[9] = null;
    this.img[10] = null;
    this.img[11] = getImg("irc.png");
    this.img[12] = getImg("Keyboard.png");
    this.img[13] = getImg("off.png");
    this.img[14] = getImg("press.png");
    this.img[15] = getImg("ProtectFormHS.png");
    this.img[16] = getImg("pwr.png");
    this.img[17] = getImg("pwr_off.png");
    this.img[18] = getImg("red.png");
    this.img[19] = getImg("UnProtectFormHS.png");
    this.img[20] = getImg("Warning.png");
    this.img[21] = getImg("yellow.png");

    this.locale_setter = new Thread(this);
    this.locale_setter.start();
    
    init_params();

    boolean bool = false;
    String str1 = System.getProperty("os.name").toLowerCase();
    String str2 = System.getProperty("java.vm.name");
    String str3 = "unknown";
    if (str1.startsWith("windows") || str1.startsWith("linux")) {
      if (str1.startsWith("windows")) {
        if (str2.indexOf("64") != -1) {
          System.out.println("kbhookdll Detected win 64bit jvm");
          str3 = "HpqKbHook-x86-win64";
        } else {
          
          System.out.println("kbhookdll Detected win 32bit jvm");
          str3 = "HpqKbHook-x86-win32";
        }
      
      } else if (str1.startsWith("linux")) {
        if (str2.indexOf("64") != -1) {
          System.out.println("kbhookdll Detected 64bit linux jvm");
          str3 = "HpqKbHook-x86-linux-64";
        } else {
          
          System.out.println("kbhookdll Detected 32bit linux jvm");
          str3 = "HpqKbHook-x86-linux-32";
        } 
      } 
      
      bool = ExtractKeyboardDll(str3);
      if (bool == true) {
        
        this.kHook = new KeyboardHook();
        if (this.kHook == null) {
          System.out.println("remcons: kHook = null, Failed to initialize and load kHook");
        } else {
          
          this.kbHookAvailable = true;
          this.kHook.clearKeymap();
        }
      
      } else {
        
        System.out.println("ExtractKeyboardDll() returns false");
      } 
    } 
    
    this.session = new cim(this);
    this.telnetObj = new cmd();

    
    if (this.session_encryption_enabled) {
      this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
      this.session.setup_decryption(this.session_decrypt_key);
    } 
    
    this.session.set_mouse_protocol(this.mouse_mode);



    
    for (byte b = 0; b < 12; b++)
      this.rndm_nums[b] = (int)(Math.random() * 4.0D) * 85; 
    this.session.set_sig_colors(this.rndm_nums);
    
    if (this.debug_msg) {
      this.session.enable_debug();
    } else {
      
      this.session.disable_debug();
    } 
    
    this.pwrStatusPanel = new JPanel(new BorderLayout());
    this.ledStatusPanel = new JPanel(new BorderLayout());
    this.pwrHealthImgGreen = this.img[5];
    prepareImage(this.pwrHealthImgGreen, this.ledStatusPanel);
    this.pwrHealthImgYellow = this.img[21];
    prepareImage(this.pwrHealthImgYellow, this.ledStatusPanel);
    this.pwrHealthImgRed = this.img[18];
    prepareImage(this.pwrHealthImgRed, this.ledStatusPanel);
    this.pwrHealthImgOff = this.img[13];
    prepareImage(this.pwrHealthImgOff, this.ledStatusPanel);
    this.pwrEncImgLock = this.img[15];
    prepareImage(this.pwrEncImgLock, this.ledStatusPanel);
    this.pwrEncImgUnlock = this.img[19];
    prepareImage(this.pwrEncImgUnlock, this.ledStatusPanel);
    this.pwrEncImg = this.pwrEncImgUnlock;
    this.pwrHealthImg = this.pwrHealthImgOff;
    this.vmActImgOn = this.img[1];
    prepareImage(this.vmActImgOn, this.ledStatusPanel);
    this.vmActImgOff = this.img[13];
    prepareImage(this.vmActImgOff, this.ledStatusPanel);
    this.pwrPowerImgOn = this.img[16];
    prepareImage(this.pwrPowerImgOn, this.ledStatusPanel);
    this.pwrPowerImgOff = this.img[17];
    prepareImage(this.pwrPowerImgOff, this.ledStatusPanel);
    this.vmActImg = this.vmActImgOff;
    this.pwrPowerImg = this.pwrPowerImgOff;
    
    this.pwrStatusPanel.add(this.pwrEncCanvas = new JPanel(this) { private final remcons this$0;
          
          public void paintComponent(Graphics param1Graphics) {
            super.paintComponent(param1Graphics);
            if (this.this$0.pwrEncImg != null) {
              this.this$0.waitImage(this.this$0.pwrEncImg, this);
              
              param1Graphics.drawImage(this.this$0.pwrEncImg, 1, 4, null);
            } else {
              
              System.out.println("pwrEncCanvas Image not found");
            } 
          } }
        "West");
    
    setToolTipRecursively(this.pwrEncCanvas, getLocalString(16387));
    this.pwrEncCanvas.setPreferredSize(new Dimension(20, 20));
    this.pwrEncCanvas.setVisible(true);
    
    this.pwrStatusPanel.add(this.pwrEncLabel = new JLabel());
    this.pwrEncLabel.setText("         ");
    
    this.ledStatusPanel.add(this.pwrHealthCanvas = new JPanel(this) { private final remcons this$0;
          
          public void paintComponent(Graphics param1Graphics) {
            super.paintComponent(param1Graphics);
            if (this.this$0.pwrHealthImg != null) {
              this.this$0.waitImage(this.this$0.pwrHealthImg, this);
              param1Graphics.drawImage(this.this$0.pwrHealthImg, 1, 4, null);
            } else {
              
              System.out.println("pwrHealthCanvas Image not found");
            } 
          } }
        "West");
    
    setToolTipRecursively(this.pwrHealthCanvas, getLocalString(16386));
    this.pwrHealthCanvas.setPreferredSize(new Dimension(18, 25));
    this.pwrHealthCanvas.setVisible(true);
    
    this.ledStatusPanel.add(this.vmActCanvas = new JPanel(this) { private final remcons this$0;
          
          public void paintComponent(Graphics param1Graphics) {
            super.paintComponent(param1Graphics);
            if (this.this$0.vmActImg != null) {
              this.this$0.waitImage(this.this$0.vmActImg, this);
              
              param1Graphics.drawImage(this.this$0.vmActImg, 1, 4, null);
            } else {
              
              System.out.println("vmActCanvas Image not found");
            } 
          } }
        "Center");
    setToolTipRecursively(this.vmActCanvas, getLocalString(16388));
    this.vmActCanvas.setPreferredSize(new Dimension(18, 25));
    this.vmActCanvas.setVisible(true);
    
    this.ledStatusPanel.add(this.pwrPowerCanvas = new JPanel(this) { private final remcons this$0;
          
          public void paintComponent(Graphics param1Graphics) {
            super.paintComponent(param1Graphics);
            if (this.this$0.pwrPowerImg != null) {
              this.this$0.waitImage(this.this$0.pwrPowerImg, this);
              param1Graphics.drawImage(this.this$0.pwrPowerImg, 1, 4, null);
            } else {
              
              System.out.println("pwrPowerCanvas Image not found");
            } 
          } }
        "East");
    setToolTipRecursively(this.pwrPowerCanvas, getLocalString(16385));
    this.pwrPowerCanvas.setPreferredSize(new Dimension(18, 25));
    this.pwrPowerCanvas.setVisible(true);
    
    this.pwrStatusPanel.add(this.ledStatusPanel, "East");
    
    this.session.enable_keyboard();
    
    if (true == this.kbHookAvailable) {
      this.keyBoardTimer = new Timer(this.keyTimerTick, false, this.session);
      this.keyBoardTimer.setListener(new keyBoardTimerListener(this), null);
      this.keyBoardTimer.start();
      System.out.println("Keyboard Hook available and timer started...");
    } 
    this.initialized = 1;
  }




  
  public void start() {
    this.timeout_countdown = this.session_timeout;
    start_session();
    if (this.session_timeout == 2147483640) {
      System.out.println("Remote Console inactivity timeout = infinite.");
    } else {
      System.out.println("Remote Console inactivity timeout = " + (this.session_timeout / 60) + " minutes.");
    } 
  }



  
  public boolean ExtractKeyboardDll(String paramString) {
    String str1 = paramString;
    String str2 = System.getProperty("java.io.tmpdir");
    String str3 = System.getProperty("os.name").toLowerCase();
    String str4 = System.getProperty("file.separator");
    String str5 = " ";
    String str6 = " ";
    boolean bool = false;
    String str7 = "com/hp/ilo2/remcons/";
    
    if (str3.startsWith("windows") || str3.startsWith("linux")) {
      if (str2 == null) {
        str2 = str3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
      }
      
      File file1 = new File(str2);
      if (!file1.exists()) {
        file1.mkdir();
      }
      if (!str2.endsWith(str4)) {
        str2 = str2 + str4;
      }
      str2 = str2 + "HpqKbHook-" + Integer.toHexString(virtdevs.UID) + ".dll";
      System.out.println("checking for kbddll" + str2);
      File file2 = new File(str2);
      if (file2.exists()) {
        System.out.println(str1 + " already present ..");
        bool = true;
        return bool;
      } 
      
      System.out.println("Extracting " + str1 + "...");
      ClassLoader classLoader = getClass().getClassLoader();
      
      byte[] arrayOfByte = new byte[4096];
      str5 = str2;
      
      str6 = str7 + str1;
      
      try {
        InputStream inputStream = classLoader.getResourceAsStream(str6);
        FileOutputStream fileOutputStream = new FileOutputStream(str5);
        int i;
        while ((i = inputStream.read(arrayOfByte, 0, 4096)) != -1)
          fileOutputStream.write(arrayOfByte, 0, i); 
        System.out.println("Writing dll to " + str5 + "complete");
        inputStream.close();
        fileOutputStream.close();
        bool = true;
      } catch (IOException iOException) {
        
        System.out.println("dllExtract: " + iOException);
        bool = false;
      }
    
    } else {
      
      System.out.println("Cannot load keyboardHook DLL. Non Windows-Linux client system.");
      bool = false;
    } 
    return bool;
  }




  
  public void stop() {
    if (this.locale_setter != null && this.locale_setter.isAlive()) {
      this.locale_setter.stop();
    }
    this.locale_setter = null;
    stop_session();
    System.out.println("Applet stopped...");
  }

  
  public void destroy() {
    System.out.println("Hiding applet.");
    if (isVisible()) {
      setVisible(false);
    }
  }

  
  public void timeout(Object paramObject) {
    if (this.session.UI_dirty) {
      this.session.UI_dirty = false;
      this.timeout_countdown = this.session_timeout;
    
    }
    else {

      
      this.timeout_countdown -= 30;
      
      if (this.timeout_countdown <= 0 && 
        System.getProperty("java.version", "0").compareTo("1.2") < 0) {
        stop_session();
      }
    } 
  }

  
  private void start_session() {
    this.session.connect(this.session_ip, this.login, this.port_num, this.ts_param, this.terminalServicesPort, this);
    
    this.timer = new Timer(30000, false, this.session);
    this.timer.setListener(this, null);
    this.timer.start();
  }



  
  private void stop_session() {
    if (this.timer != null) {
      this.timer.stop();
      this.timer = null;
    } 
    this.session.disconnect();
  }


  
  public void setPwrStatusEnc(int paramInt) {
    if (paramInt == 0) {
      this.pwrEncImg = this.pwrEncImgUnlock;
    } else {
      this.pwrEncImg = this.pwrEncImgLock;
    } 
    this.pwrEncCanvas.invalidate();
    this.pwrEncCanvas.repaint();
  }

  
  public void setPwrStatusEncLabel(String paramString) {
    this.pwrEncLabel.setText(paramString + "       ");
  }

  
  public void setPwrStatusHealth(int paramInt) {
    switch (paramInt) {
      case 0:
        this.pwrHealthImg = this.pwrHealthImgGreen;
        break;
      case 1:
        this.pwrHealthImg = this.pwrHealthImgYellow;
        break;
      case 2:
        this.pwrHealthImg = this.pwrHealthImgRed;
        break;
      default:
        this.pwrHealthImg = this.pwrHealthImgOff;
        break;
    } 
    
    this.pwrHealthCanvas.invalidate();
    this.pwrHealthCanvas.repaint();
  }

  
  public void setPwrStatusPower(int paramInt) {
    if (paramInt == 0 && this.pwrPowerImgOff != this.pwrPowerImg) {
      this.pwrPowerImg = this.pwrPowerImgOff;
      this.ParentApp.updatePsMenu(paramInt);
      this.pwrPowerCanvas.invalidate();
      this.pwrPowerCanvas.repaint();
      System.out.println("Moving Power to Off state");
    }
    else if (paramInt != 0 && this.pwrPowerImgOn != this.pwrPowerImg) {
      this.pwrPowerImg = this.pwrPowerImgOn;
      this.ParentApp.updatePsMenu(paramInt);
      this.pwrPowerCanvas.invalidate();
      this.pwrPowerCanvas.repaint();
      System.out.println("Moving Power to ON state");
    } 
  }




  
  public void setvmAct(int paramInt) {
    if (this.vmActImg == this.vmActImgOn || paramInt == 0) {
      this.vmActImg = this.vmActImgOff;
      this.vmActCanvas.invalidate();
      this.vmActCanvas.repaint();
    } else if (this.vmActImg == this.vmActImgOff) {
      this.vmActImg = this.vmActImgOn;
      this.vmActCanvas.invalidate();
      this.vmActCanvas.repaint();
    } 
  }



  
  public int seize_dialog(String paramString1, String paramString2, int paramInt) {
    System.out.println("seize dialog invoked" + paramInt);
    VSeizeWaitDialog vSeizeWaitDialog = new VSeizeWaitDialog(this, paramString1, paramString2, paramInt);
    return vSeizeWaitDialog.getUserInput();
  }

  
  public void seize_confirmed() {
    this.ParentApp.moveUItoInit(false);
    this.ParentApp.virtdevsObj.stop();
    remconsUnInstallKeyboardHook();
    this.ParentApp.dispFrame.setVisible(false);
    this.session.seize();
    this.ParentApp.stop();
  }

  
  public void shared(String paramString1, String paramString2) {
    System.out.println("shared notification invoked");
    new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8230), getLocalString(8231) + " " + paramString2 + "@" + paramString1 + getLocalString(8232), false);
  }



  
  public void unAuthorized(String paramString, boolean paramBoolean) {
    new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8230), getLocalString(8233) + paramString + getLocalString(8234), false);
    
    if (paramBoolean) {
      this.session.unAuthAccess();
    }
  }

  
  public void firmwareUpgrade() {
    System.out.println("Firmware upgrade notification invoked");
    
    VErrorDialog vErrorDialog = new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8230), getLocalString(8235), false);
    this.ParentApp.moveUItoInit(false);
    this.ParentApp.virtdevsObj.stop();
    this.session.fwUpgrade();
    this.ParentApp.stop();
    if (vErrorDialog.getBoolean() == true) {
      System.exit(0);
    }
  }

  
  public void ack(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4) {
    if (paramByte1 == 0) {
      if (paramByte2 == 1) {

        
        if (paramByte4 == 1 && !this.ParentApp.fdSelected) {
          this.ParentApp.fdSelected = true;
          this.ParentApp.lockFdMenu(false, getLocalString(4131) + getLocalString(4106));
        }
        else if (paramByte4 == 0 && this.ParentApp.fdSelected == true) {
          this.ParentApp.fdSelected = false;
          this.ParentApp.lockFdMenu(true, "");
        }
      
      } else if (paramByte2 == 2) {

        
        if (paramByte4 == 1 && !this.ParentApp.cdSelected) {
          this.ParentApp.cdSelected = true;
          this.ParentApp.lockCdMenu(false, getLocalString(4131) + getLocalString(4107));
        } else if (paramByte4 == 0 && this.ParentApp.cdSelected == true) {
          this.ParentApp.cdSelected = false;
          this.ParentApp.lockCdMenu(true, "");
        } 
      } 
    }
  }












  
  protected void init_params() {
    this.login = null;
    this.port_num = 23;
    this.mouse_mode = 0;
    this.session_timeout = 900;
    
    this.session_encryption_enabled = true;
    
    this.session_key_index = 0;
    this.launchTerminalServices = false;
    this.terminalServicesPort = 0;
    this.debug_msg = true;
    
    this.session_ip = this.ParentApp.getCodeBase().getHost();
    
    this.num_cursors = 0;
    
    if (this.session_encryption_enabled) {
      if (null != this.ParentApp.enc_key) {
        System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_decrypt_key, 0, this.session_decrypt_key.length);
        System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_encrypt_key, 0, this.session_encrypt_key.length);
      } 
    } else {
      
      this.session_decrypt_key = null;
      this.session_encrypt_key = null;
    } 
  }











  
  private String parse_login(String paramString) {
    if (paramString.startsWith("Compaq-RIB-Login=")) {
      String str = "\033[!";
      
      try {
        str = str + paramString.substring(17, 73);
        str = str + '\r';
        str = str + paramString.substring(74, 106);
        str = str + '\r';
      } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
        
        return null;
      } 
      
      return str;
    } 
    
    return base64_decode(paramString);
  }













  
  private String base64_decode(String paramString) {
    byte b1 = 0;
    byte b2 = 0;
    String str = "";
    
    while (b1 + 3 < paramString.length() && !b2) {
      char c1 = base64[paramString.charAt(b1) & 0x7F];
      char c2 = base64[paramString.charAt(b1 + 1) & 0x7F];
      char c3 = base64[paramString.charAt(b1 + 2) & 0x7F];
      char c4 = base64[paramString.charAt(b1 + 3) & 0x7F];
      
      char c5 = (char)((c1 << 2) + (c2 >> 4));
      char c6 = (char)((c2 << 4) + (c3 >> 2));
      char c7 = (char)((c3 << 6) + c4);
      
      c5 = (char)(c5 & 0xFF);
      c6 = (char)(c6 & 0xFF);
      c7 = (char)(c7 & 0xFF);
      
      if (c5 == ':') {
        c5 = '\r';
      }
      if (c6 == ':') {
        c6 = '\r';
      }
      if (c7 == ':') {
        c7 = '\r';
      }
      str = str + c5;

      
      if (paramString.charAt(b1 + 2) == '=') {
        b2++;
      } else {
        
        str = str + c6;
      } 
      if (paramString.charAt(b1 + 3) == '=') {
        b2++;
      } else {
        
        str = str + c7;
      } 
      b1 += 4;
    } 
    if (str.length() != 0) {
      str = str + '\r';
    }
    return str;
  }





  
  public void paint(Graphics paramGraphics) {}




  
  public int getTimeoutValue() {
    return this.timeout_countdown;
  }


  
  public void run() {
    if (System.getProperty("os.name").toLowerCase().startsWith("windows") && 
      !this.lt.windows) {
      Locale.setDefault(Locale.US);
    }


    
    while (true) {
      if (true == this.retry_connection_flag) { this; if (3 >= this.retry_connection_count) {
          
          System.out.println("Retrying connection" + this.retry_connection_count);
          this.retry_connection_flag = false;
          this.retry_connection_count++;
          
          if (false == this.fdCachedConnState) {
            this.fdCachedConnState = this.fdConnState;
          }
          if (false == this.cdCachedConnState) {
            this.cdCachedConnState = this.cdConnState;
          }
          System.out.println("fd conn:" + this.fdConnState + " cd conn:" + this.cdConnState);
          System.out.println("fdcache:" + this.fdCachedConnState + " cdcache:" + this.cdCachedConnState);
          
          stop_session();
          try {
            sleepAtLeast(5000L);
          } catch (InterruptedException interruptedException) {
            
            System.out.println("Thread interrupted..");
          } 
          if (this.session_encryption_enabled && 
            null != this.ParentApp.enc_key) {
            System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_decrypt_key, 0, this.session_decrypt_key.length);
            System.arraycopy(this.ParentApp.enc_key_val, 0, this.session_encrypt_key, 0, this.session_encrypt_key.length);
          } 
          
          this.session.setup_decryption(this.session_decrypt_key);
          this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
          
          start_session();
          try {
            sleepAtLeast(2500L);
          } catch (InterruptedException interruptedException) {
            
            System.out.println("Thread interrupted..");
          } 
          if (null != this.session.receiver && false == this.retry_connection_flag) {
            this.retry_connection_count = 0;
            continue;
          } 
          this.retry_connection_flag = true; continue;
        }
      }
      
      if (true == this.retry_connection_flag) {
        System.out.println("Retry connection  - video maximum attempts exhausted");
        stop_session();
        this.retry_connection_flag = false;

        
        continue;
      } 

      
      try {
        sleepAtLeast(2500L);
      } catch (InterruptedException interruptedException) {
        System.out.println("Thread interrupted..");
      } 
    } 
  }



  
  public void sleepAtLeast(long paramLong) throws InterruptedException {
    long l1 = System.currentTimeMillis();
    long l2 = paramLong;
    while (l2 > 0L) {
      Thread.sleep(l2);
      long l = System.currentTimeMillis();
      l2 = paramLong - l - l1;
    } 
  }

  
  public void setDialogIsOpen(boolean paramBoolean) {
    dialogIsOpen = paramBoolean;
  }

  
  public void SetLicensed(int paramInt) {
    this.licensed = false;
    if ((paramInt & 0x1) != 0) {
      this.licensed = true;
    }
    System.out.println("SetLicensed: " + this.licensed);
  }


  
  void SetFlags(int paramInt) {
    if ((paramInt & 0x8) == 0) {
      this.halfHeightCapable = false;
      System.out.println("halfHeightCapable false");
    } else {
      
      this.halfHeightCapable = true;
      System.out.println("halfHeightCapable true");
    } 
  }


  
  public void UnlicensedShutdown() {
    String str = "<html>" + getLocalString(8213) + " " + getLocalString(8215) + " " + getLocalString(8237) + "<br><br>" + getLocalString(8238) + "</html>";
    
    System.out.println("Unlicensed notification invoked");
    VErrorDialog vErrorDialog = new VErrorDialog(this.ParentApp.dispFrame, getLocalString(8236), str, true);
    vErrorDialog.getBoolean();
    this.ParentApp.moveUItoInit(false);
    this.ParentApp.stop();
  }

  
  public void resetShutdown() {
    VErrorDialog vErrorDialog = new VErrorDialog(this.ParentApp.dispFrame, getLocalString(4103), getLocalString(8289), true);
    vErrorDialog.getBoolean();
    this.ParentApp.moveUItoInit(false);
    this.ParentApp.stop();
  }

  
  public int getInitialized() {
    return this.initialized;
  }


  
  private void get_terminal_svcs_label(int paramInt) {
    String str;
    if (paramInt == 0) {
      str = "mstsc";
    }
    else if (paramInt == 1) {
      str = "vnc";
    } else {
      
      str = "type" + paramInt;
    } 
    this.term_svcs_label = prop.getProperty(str + ".label", "Terminal Svcs");
  }
  
  static {
    prop = new Properties();
    try {
      prop.load(new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".java" + System.getProperty("file.separator") + "hp.properties"));
    } catch (Exception exception) {
      
      System.out.println("Exception: " + exception);
    } 
  }

  
  public void remconsInstallKeyboardHook() {
    String str = System.getProperty("os.name").toLowerCase();
    int i = -5;
    if (this.kHook == null) {
      System.out.println("remconsInstallKeyboardHook:KB Hook dll not loaded");
    }
    else if (!this.kbHookInstalled && this.kbHookAvailable == true && !dialogIsOpen) {
      
      this.kHook.clearKeymap();
      i = this.kHook.InstallKeyboardHook();
      
      if (!str.startsWith("windows") && -1412584499 == i) {


        
        this.kbHookInstalled = false;
        this.keyBoardTimer.stop();
        System.out.println("remconsInstallKeyboardHook: KB Hook install failed");
      } else {
        
        this.kHook.setKeyboardLayoutId(i);
        this.kbHookInstalled = true;
        this.prevKeyData = this.keyData = 0;
        if (!str.startsWith("windows")) {
          this.keyBoardTimer.start();
          this.kHook.setLocalKbdLayout(this.localKbdLayoutId);
        } 
      } 
    } 
  }





  
  public void remconsUnInstallKeyboardHook() {
    int i = -5;
    if (this.kHook != null)
    {
      
      if (this.kbHookInstalled == true && this.kbHookAvailable == true) {
        
        i = this.kHook.UnInstallKeyboardHook();
        if (i == 0) {
          this.kbHookInstalled = false;
          this.prevKeyData = this.keyData = 0;
          this.kHook.clearKeymap();
        
        }
        else {
          
          System.out.println("remconsUnInstallKeyboardHook: uninstall failed:" + i);
        } 
      } 
    }
  }



  
  public void setLocalKbdLayout(int paramInt) {
    if (this.kHook != null && this.kbHookInstalled == true) {
      System.out.println("setKbdLayoutHandler: set Layout - " + paramInt);
      this.kHook.setLocalKbdLayout(paramInt);
    } else {
      
      System.out.println("setKbdLayoutHandler: kHook not available. dbg caching..");
      this.localKbdLayoutId = paramInt;
    } 
  }
  class keyBoardTimerListener implements TimerListener {
    keyBoardTimerListener(remcons this$0) {
      this.this$0 = this$0;
    }

    
    private final remcons this$0;

    
    public synchronized void timeout(Object param1Object) {
      boolean bool1 = false;
      byte[] kcmd = new byte[10];
      boolean bool2 = false;
      
      if (!(this.this$0.kHook != null && this.this$0.kbHookInstalled == true))
        return;

      int retries = 995;
      do {
        this.this$0.prevKeyData = this.this$0.keyData;
        this.this$0.keyData = this.this$0.kHook.GetKeyData();
        
        if (this.this$0.keyData == this.this$0.prevKeyData || 0 == this.this$0.keyData) {
          continue;
        }
        
        int kd16 = (this.this$0.keyData & 0xFF0000) >> 16;
        int kd8 = (this.this$0.keyData & 0xFF00) >> 8;
        int kd0 = this.this$0.keyData & 0xFF;
        
        if ((kd16 & 0x90) == 144) {
          bool2 = true;
        } else if ((kd16 & 0x80) == 128) {
          bool1 = false;
          bool2 = false;
        } else {
          bool1 = true;
          bool2 = false;
        }

        kcmd = this.this$0.kHook.HandleHookKey(kd0, kd8, bool1, bool2);
        if (this.this$0.kHook.kcmdValid) {
          if (!this.this$0.kbHookDataRcvd)
            this.this$0.kbHookDataRcvd = true;
          this.this$0.session.transmitb(kcmd, kcmd.length);
        }
        
        retries = 0;
      } while (retries++ < 1000);
    }
  }


  
  public void setToolTipRecursively(JComponent paramJComponent, String paramString) {
    paramJComponent.setToolTipText(paramString);
  }

  
  public void viewHotKeys() {
    new hotKeysDialog(this);
  }

  
  public void viewAboutJirc() {
    new aboutJircDialog(this);
  }
}
