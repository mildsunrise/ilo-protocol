package com.hp.ilo2.remcons;

import com.hp.ilo2.virtdevs.VErrorDialog;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.lang.reflect.Method;


public class cim
  extends telnet
  implements MouseSyncListener
{
  private static final int CMD_MOUSE_MOVE = 208;
  private static final int CMD_BUTTON_PRESS = 209;
  private static final int CMD_BUTTON_RELEASE = 210;
  private static final int CMD_BUTTON_CLICK = 211;
  private static final int CMD_BYTE = 212;
  private static final int CMD_SET_MODE = 213;
  private static final char MOUSE_USBABS = '\001';
  private static final char MOUSE_USBREL = '\002';
  static final int CMD_ENCRYPT = 192;
  public static final int MOUSE_BUTTON_LEFT = 4;
  public static final int MOUSE_BUTTON_CENTER = 2;
  public static final int MOUSE_BUTTON_RIGHT = 1;
  private char prev_char = ' ';

  
  private boolean disable_kbd = false;

  
  private boolean altlock = false;

  
  private static final int block_width = 16;

  
  private static final int block_height = 16;

  
  public int[] color_remap_table = new int[32768];




  
  private int scale_x = 1;
  private int scale_y = 1;

  
  private int screen_x = 1;
  private int screen_y = 1;
  private int mouse_protocol = 0;
  
  protected MouseSync mouse_sync = new MouseSync(this);



  
  public boolean UI_dirty = false;


  
  private boolean sending_encrypt_command = false;


  
  public byte[] encrypt_key = new byte[16];
  private RC4 RC4encrypter;
  private Aes Aes128encrypter;
  private Aes Aes256encrypter;
  private int key_index = 0;

  
  private int bitsPerColor = 5;



  
  public Point mousePrevPosn = new Point(0, 0);
  
  private byte mouseBtnState = 0;
  
  private static final int RESET = 0;
  
  private static final int START = 1;
  
  private static final int PIXELS = 2;
  
  private static final int PIXLRU1 = 3;
  
  private static final int PIXLRU0 = 4;
  
  private static final int PIXCODE1 = 5;
  
  private static final int PIXCODE2 = 6;
  private static final int PIXCODE3 = 7;
  private static final int PIXGREY = 8;
  private static final int PIXRGBR = 9;
  private static final int PIXRPT = 10;
  private static final int PIXRPT1 = 11;
  private static final int PIXRPTSTD1 = 12;
  private static final int PIXRPTSTD2 = 13;
  private static final int PIXRPTNSTD = 14;
  private static final int CMD = 15;
  private static final int CMD0 = 16;
  private static final int MOVEXY0 = 17;
  private static final int EXTCMD = 18;
  private static final int CMDX = 19;
  private static final int MOVESHORTX = 20;
  private static final int MOVELONGX = 21;
  private static final int BLKRPT = 22;
  private static final int EXTCMD1 = 23;
  private static final int FIRMWARE = 24;
  private static final int EXTCMD2 = 25;
  private static final int MODE0 = 26;
  private static final int TIMEOUT = 27;
  private static final int BLKRPT1 = 28;
  private static final int BLKRPTSTD = 29;
  private static final int BLKRPTNSTD = 30;
  private static final int PIXFAN = 31;
  private static final int PIXCODE4 = 32;
  private static final int PIXDUP = 33;
  private static final int BLKDUP = 34;
  private static final int PIXCODE = 35;
  private static final int PIXSPEC = 36;
  private static final int EXIT = 37;
  private static final int LATCHED = 38;
  private static final int MOVEXY1 = 39;
  private static final int MODE1 = 40;
  private static final int PIXRGBG = 41;
  private static final int PIXRGBB = 42;
  private static final int HUNT = 43;
  private static final int PRINT0 = 44;
  private static final int PRINT1 = 45;
  private static final int CORP = 46;
  private static final int MODE2 = 47;
  private static final int SIZE_OF_ALL = 48;

  private static int[] dvc_getmask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255 };
  private static int[] dvc_reversal = new int[256];
  private static int[] dvc_left = new int[256];
  private static int[] dvc_right = new int[256];

  private static int[] dvc_lru_lengths = new int[] { 0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 };
  

  private static int dvc_ib_acc = 0;
  private static int dvc_ib_bcnt = 0;



  private static int[] bits_to_read = new int[] { 0, 1, 1, 1, 1, 1, 2, 3, 5, 5, 1, 1, 3, 3, 8, 1, 1, 7, 1, 1, 3, 7, 1,
      1, 8, 1, 7, 0, 1, 3, 7, 1, 4, 0, 0, 0, 1, 0, 1, 7, 7, 5, 5, 1, 8, 8, 1, 4 };

  private static int[] next_0 = new int[] { 1, 2, 31, 2, 2, 10, 10, 10, 10, 41, 2, 33, 2, 2, 2, 16, 19, 39, 22, 20, 1, 1, 34, 25, 46, 26, 40, 1, 29, 1, 1, 36, 10, 2, 1, 35, 8, 37, 38, 1, 47, 42, 10, 43, 45, 45, 1, 1 };

  private static int[] next_1 = new int[] { 1, 15, 3, 11, 11, 10, 10, 10, 10, 41, 11, 12, 2, 2, 2, 17, 18, 39, 23, 21, 1, 1, 28, 24, 46, 27, 40, 1, 30, 1, 1, 35, 10, 2, 1, 35, 9, 37, 38, 1, 47, 42, 10, 0, 45, 45, 24, 1 };
  
  private static int dvc_cc_active = 0;
  private static int[] dvc_cc_color = new int[17];
  private static int[] dvc_cc_usage = new int[17];
  private static int[] dvc_cc_block = new int[17];
  
  private static int dvc_pixel_count;
  
  private static int dvc_size_x;
  
  private static int dvc_size_y;
  private static int dvc_y_clipped;
  private static int dvc_lastx;
  private static int dvc_lasty;
  private static int dvc_newx;
  private static int dvc_newy;
  private static int dvc_color;
  private static int dvc_last_color;
  
  private static int dvc_zero_count = 0;
  
  private static int dvc_decoder_state = 0;
  
  private static int dvc_next_state = 0;
  
  private static int dvc_pixcode = 38;
  
  private static int dvc_code = 0;
  private static int[] block = new int[256];
  private static int dvc_red;
  private static int dvc_green;
  private static int dvc_blue;
  private static int fatal_count;
  private static int printchan = 0;
  private static String printstring = "";
  private static long count_bytes = 0L;
  private static int[] cmd_p_buff = new int[256];
  private static int cmd_p_count = 0;
  private static int cmd_last = 0;
  
  private static int framerate = 30;
  
  private static boolean debug_msgs = false;
  
  private static char last_bits = Character.MIN_VALUE;
  private static char last_bits2 = Character.MIN_VALUE;
  private static char last_bits3 = Character.MIN_VALUE;
  private static char last_bits4 = Character.MIN_VALUE;
  private static char last_bits5 = Character.MIN_VALUE;
  private static char last_bits6 = Character.MIN_VALUE;
  private static char last_bits7 = Character.MIN_VALUE;
  private static int last_len = 0;
  private static int last_len1 = 0;
  private static int last_len2 = 0;
  private static int last_len3 = 0;
  private static int last_len4 = 0;
  private static int last_len5 = 0;
  private static int last_len6 = 0;
  private static int last_len7 = 0;
  private static int last_len8 = 0;
  private static int last_len9 = 0;
  private static int last_len10 = 0;
  private static int last_len11 = 0;
  private static int last_len12 = 0;
  private static int last_len13 = 0;
  private static int last_len14 = 0;
  private static int last_len15 = 0;
  private static int last_len16 = 0;
  private static int last_len17 = 0;
  private static int last_len18 = 0;
  private static int last_len19 = 0;
  private static int last_len20 = 0;
  private static int last_len21 = 0;
  private static char dvc_new_bits = Character.MIN_VALUE;
  private static int debug_lastx = 0;
  private static int debug_lasty = 0;
  private static int debug_show_block = 0;
  private static long timeout_count = 0L;
  private static long dvc_counter_block = 0L;
  private static long dvc_counter_bits = 0L;
  private static boolean show_bitsblk_count = false;
  private static long show_slices = 0L;

  private static boolean dvc_process_inhibit = false;
  
  private static boolean video_detected = true;
  
  private boolean ignore_next_key = false;
  
  private int blockHeight = 16;
  private int blockWidth = 16; private boolean unsupportedVideoModeWarned = false;
  private static final int B = -16777216;
  private static final int W = -8355712;
  
  public String getLocalString(int paramInt) {
    String str = "";
    try {
      str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
    } catch (Exception exception) {
      
      System.out.println("cim:getLocalString" + exception.getMessage());
    } 
    return str;
  }
  
  public cim(remcons paramremcons) {
    super(paramremcons);
    dvc_reversal[255] = 0;
    this.current_cursor = Cursor.getDefaultCursor();
    this.screen.addMouseListener(this.mouse_sync);
    this.screen.addMouseMotionListener(this.mouse_sync);
    this.screen.addMouseWheelListener(this.mouse_sync);
    this.mouse_sync.setListener(this);
  }
  
  public void setup_encryption(byte[] paramArrayOfbyte, int paramInt) {
    System.arraycopy(paramArrayOfbyte, 0, this.encrypt_key, 0, 16);
    
    this.RC4encrypter = new RC4(paramArrayOfbyte);
    this.Aes128encrypter = new Aes(0, paramArrayOfbyte);
    this.Aes256encrypter = new Aes(0, paramArrayOfbyte);
    this.key_index = paramInt;
  }

  public void reinit_vars() {
    super.reinit_vars();
    
    dvc_code = 0;
    dvc_ib_acc = 0;
    dvc_ib_bcnt = 0;
    dvc_counter_bits = 0L;
    
    this.prev_char = ' ';
    this.disable_kbd = false;
    this.altlock = false;
    
    dvc_reversal[255] = 0;
    
    this.scale_x = 1;
    this.scale_y = 1;
    
    this.mouse_sync.restart();
    
    dvc_process_inhibit = false;
  }

  public void enable_debug() {
    debug_msgs = true;
    super.enable_debug();
    this.mouse_sync.enableDebug();
  }

  public void disable_debug() {
    debug_msgs = false;
    super.disable_debug();
    this.mouse_sync.disableDebug();
  }

  public void sync_start() {
    this.mouse_sync.sync();
  }
  
  public void serverMove(int paramInt1, int paramInt2, int xcoord, int ycoord) {
    if (paramInt1 < -128) {
      paramInt1 = -128;
    }
    else if (paramInt1 > 127) {
      paramInt1 = 127;
    } 
    if (paramInt2 < -128) {
      paramInt2 = -128;
    }
    else if (paramInt2 > 127) {
      paramInt2 = 127;
    } 
    this.UI_dirty = true;
    
    if (this.screen_x > 0 && this.screen_y > 0) {
      xcoord = 3000 * xcoord / this.screen_x;
      ycoord = 3000 * ycoord / this.screen_y;
    } else {
      xcoord = 3000 * xcoord / 1;
      ycoord = 3000 * ycoord / 1;
    } 

    byte[] buf = new byte[10];
    
    buf[0] = 2;
    buf[1] = 0;
    buf[2] = (byte)(xcoord & 0xFF);
    buf[3] = (byte)(xcoord >> 8);
    buf[4] = (byte)(ycoord & 0xFF);
    buf[5] = (byte)(ycoord >> 8);
    buf[6] = 0;
    buf[7] = 0;

    buf[8] = this.mouseBtnState;
    buf[9] = 0;
    
    String str = new String(buf);
    transmit(str);
  }

  public void mouse_mode_change(boolean paramBoolean) {
    boolean bool = paramBoolean ? true : true;
  }
  
  public synchronized void mouseEntered(MouseEvent paramMouseEvent) {
    this.UI_dirty = true;
    
    super.mouseEntered(paramMouseEvent);
  }

  public void serverPress(int paramInt) {
    this.UI_dirty = true;
    send_mouse_press(paramInt);
  }
  
  public void serverRelease(int paramInt) {
    this.UI_dirty = true;
    send_mouse_release(paramInt);
  }

  public void serverClick(int paramInt1, int paramInt2) {
    this.UI_dirty = true;
    send_mouse_click(paramInt1, paramInt2);

    this.mouseBtnState = mouseButtonState(paramInt1);
  }

  public synchronized void mouseExited(MouseEvent paramMouseEvent) {
    super.mouseExited(paramMouseEvent);
    setCursor(Cursor.getDefaultCursor());
  }

  public void disable_keyboard() {
    this.disable_kbd = true;
  }

  public void enable_keyboard() {
    this.disable_kbd = false;
  }
  
  public void disable_altlock() {
    this.altlock = false;
  }

  public void enable_altlock() {
    this.altlock = true;
  }

  public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, remcons paramremcons) {
    char[] arrayOfChar = { 'ÿ', 'À' }; // 0xFF, 0xC0

    super.connect(paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramremcons);
  }
  
  public synchronized void transmit(String paramString) {
    if (this.out == null || paramString == null) {
      return;
    }
    
    if (paramString.length() != 0) {
      byte[] arrayOfByte = new byte[paramString.length()];
      
      for (byte b = 0; b < paramString.length(); b++) {
        arrayOfByte[b] = (byte)paramString.charAt(b);
        
        if (this.dvc_encryption) {
          char c; switch (this.cipher) {
            case 1:
              c = (char)(this.RC4encrypter.randomValue() & 0xFF);
              arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
              break;
            case 2:
              c = (char)(this.Aes128encrypter.randomValue() & 0xFF);
              arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
              break;
            case 3:
              c = (char)(this.Aes256encrypter.randomValue() & 0xFF);
              arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
              break;
            default:
              c = Character.MIN_VALUE;
              System.out.println("Unknown encryption"); break;
          } 
          arrayOfByte[b] = (byte)(arrayOfByte[b] & 0xFF);
        } 
      } 
      try {
        this.out.write(arrayOfByte, 0, arrayOfByte.length);
      } catch (IOException iOException) {
        
        System.out.println("telnet.transmit() IOException: " + iOException);
      } 
    } 
  }
  
  public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramInt);
    
    for (byte b = 0; b < paramInt; b++) {
      
      if (this.dvc_encryption) {
        char c; switch (this.cipher) {
          case 1:
            c = (char)(this.RC4encrypter.randomValue() & 0xFF);
            arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
            break;
          case 2:
            c = (char)(this.Aes128encrypter.randomValue() & 0xFF);
            arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
            break;
          case 3:
            c = (char)(this.Aes256encrypter.randomValue() & 0xFF);
            arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
            break;
          default:
            c = Character.MIN_VALUE;
            System.out.println("Unknown encryption"); break;
        } 
        arrayOfByte[b] = (byte)(arrayOfByte[b] & 0xFF);
      } 
    } 
    
    try {
      if (null != this.out) {
        this.out.write(arrayOfByte, 0, paramInt);
      }
    } catch (IOException iOException) {
      
      System.out.println("telnet.transmitb() IOException: " + iOException);
    } 
  }

  protected String translate_key(KeyEvent paramKeyEvent) {
    String str = "";
    char c = paramKeyEvent.getKeyChar();
    byte b = 0;
    boolean bool = true;
    
    if (this.disable_kbd) {
      return "";
    }
    
    if (this.ignore_next_key) {
      this.ignore_next_key = false;
      return "";
    } 
    
    this.UI_dirty = true;
    if (paramKeyEvent.isShiftDown()) {
      b = 1;
    }
    else if (paramKeyEvent.isControlDown()) {
      b = 2;
    }
    else if (this.altlock || paramKeyEvent.isAltDown()) {
      b = 3;
      if (paramKeyEvent.isAltDown()) {
        paramKeyEvent.consume();
      }
    } 
    
    switch (c) {
      case '\033':
        bool = false;
        break;
      
      case '\n':
      case '\r':
        switch (b) {
          case 0:
            str = "\r";
            break;
          
          case 1:
            str = "\033[3\r";
            break;
          
          case 2:
            str = "\n";
            break;
          
          case 3:
            str = "\033[1\r";
            break;
        } 
        bool = false;
        break;

      
      case '\b':
        switch (b) {
          case 0:
            str = "\b";
            break;
          
          case 1:
            str = "\033[3\b";
            break;
          
          case 2:
            str = "";
            break;
          
          case 3:
            str = "\033[1\b";
            break;
        } 
        bool = false;
        break;
      
      default:
        str = super.translate_key(paramKeyEvent);
        break;
    } 
    
    if (bool == true && str.length() != 0 && b == 3) {
      str = "\033[1" + str;
    }
    return str;
  }

  protected String translate_special_key(KeyEvent paramKeyEvent) {
    String str = "";
    boolean bool = true;
    byte b = 0;
    
    if (this.disable_kbd) {
      return "";
    }
    
    this.UI_dirty = true;
    if (paramKeyEvent.isShiftDown()) {
      b = 1;
    }
    else if (paramKeyEvent.isControlDown()) {
      b = 2;
    }
    else if (this.altlock || paramKeyEvent.isAltDown()) {
      b = 3;
    } 
    
    switch (paramKeyEvent.getKeyCode()) {
      case 27:
        str = "\033";
        break;
      
      case 9:
        paramKeyEvent.consume();
        str = "\t";
        break;
      
      case 127:
        if (paramKeyEvent.isControlDown() && (this.altlock || paramKeyEvent.isAltDown())) {
          
          send_ctrl_alt_del();
          return "";
        } 


        
        if (System.getProperty("java.version", "0").compareTo("1.4.2") < 0) {
          str = "";
        }
        break;
      
      case 36:
        str = "\033[H";
        break;
      
      case 35:
        str = "\033[F";
        break;
      
      case 33:
        str = "\033[I";
        break;
      
      case 34:
        str = "\033[G";
        break;
      
      case 155:
        str = "\033[L";
        break;
      
      case 38:
        str = "\033[A";
        break;
      
      case 40:
        str = "\033[B";
        break;
      
      case 37:
        str = "\033[D";
        break;
      
      case 39:
        str = "\033[C";
        break;
      
      case 112:
        switch (b) {
          case 0:
            str = "\033[M";
            break;
          
          case 1:
            str = "\033[Y";
            break;
          
          case 2:
            str = "\033[k";
            break;
          
          case 3:
            str = "\033[w";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 113:
        switch (b) {
          case 0:
            str = "\033[N";
            break;
          
          case 1:
            str = "\033[Z";
            break;
          
          case 2:
            str = "\033[l";
            break;
          
          case 3:
            str = "\033[x";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 114:
        switch (b) {
          case 0:
            str = "\033[O";
            break;
          
          case 1:
            str = "\033[a";
            break;
          
          case 2:
            str = "\033[m";
            break;
          
          case 3:
            str = "\033[y";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 115:
        switch (b) {
          case 0:
            str = "\033[P";
            break;
          
          case 1:
            str = "\033[b";
            break;
          
          case 2:
            str = "\033[n";
            break;
          
          case 3:
            str = "\033[z";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 116:
        switch (b) {
          case 0:
            str = "\033[Q";
            break;
          
          case 1:
            str = "\033[c";
            break;
          
          case 2:
            str = "\033[o";
            break;
          
          case 3:
            str = "\033[@";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 117:
        switch (b) {
          case 0:
            str = "\033[R";
            break;
          
          case 1:
            str = "\033[d";
            break;
          
          case 2:
            str = "\033[p";
            break;
          
          case 3:
            str = "\033[[";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 118:
        switch (b) {
          case 0:
            str = "\033[S";
            break;
          
          case 1:
            str = "\033[e";
            break;
          
          case 2:
            str = "\033[q";
            break;
          
          case 3:
            str = "\033[\\";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 119:
        switch (b) {
          case 0:
            str = "\033[T";
            break;
          
          case 1:
            str = "\033[f";
            break;
          
          case 2:
            str = "\033[r";
            break;
          
          case 3:
            str = "\033[]";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 120:
        switch (b) {
          case 0:
            str = "\033[U";
            break;
          
          case 1:
            str = "\033[g";
            break;
          
          case 2:
            str = "\033[s";
            break;
          
          case 3:
            str = "\033[^";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 121:
        switch (b) {
          case 0:
            str = "\033[V";
            break;
          
          case 1:
            str = "\033[h";
            break;
          
          case 2:
            str = "\033[t";
            break;
          
          case 3:
            str = "\033[_";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 122:
        switch (b) {
          case 0:
            str = "\033[W";
            break;
          
          case 1:
            str = "\033[i";
            break;
          
          case 2:
            str = "\033[u";
            break;
          
          case 3:
            str = "\033[`";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      case 123:
        switch (b) {
          case 0:
            str = "\033[X";
            break;
          
          case 1:
            str = "\033[j";
            break;
          
          case 2:
            str = "\033[v";
            break;
          
          case 3:
            str = "\033['";
            break;
        } 
        paramKeyEvent.consume();
        bool = false;
        break;
      
      default:
        bool = false;
        str = super.translate_special_key(paramKeyEvent);
        break;
    } 
    
    if (str.length() != 0 && 
      bool == true) {
      switch (b) {
        case 1:
          str = "\033[3" + str;
          break;
        
        case 2:
          str = "\033[2" + str;
          break;
        
        case 3:
          str = "\033[1" + str;
          break;
      }
    }
    return str;
  }
  
  protected String translate_special_key_release(KeyEvent paramKeyEvent) {
    String str = "";
    int i = 0;
    
    if (paramKeyEvent.isShiftDown()) {
      i = 1;
    }
    
    if (this.altlock || paramKeyEvent.isAltDown()) {
      i += true;
    }
    
    if (paramKeyEvent.isControlDown()) {
      i += true;
    }
    
    switch (paramKeyEvent.getKeyCode()) {
      case 243:
      case 244:
      case 263:
        i += 128;
        break;
      case 29:
        i += 136;
        break;
      case 28:
      case 256:
      case 257:
        i += 144;
        break;
      case 241:
      case 242:
      case 245:
        i += 152;
        break;
    }
    
    if (i > 127) {
      str = "" + (char)i;
    } else {
      str = "";
    } 
    
    return str;
  }
  
  public void send_ctrl_alt_del() {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    
    arrayOfByte[2] = 5;
    arrayOfByte[4] = 0x4c;
    String str1 = new String(arrayOfByte);
    transmit(str1);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[4] = 0;
    String str2 = new String(arrayOfByte);
    transmit(str2);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[2] = 0;
    String str3 = new String(arrayOfByte);
    transmit(str3);
    
    requestFocus();
  }

  
  public void send_num_lock() {
    System.out.println("sending num lock");
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    
    arrayOfByte[4] = 83;
    String str1 = new String(arrayOfByte);
    transmit(str1);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[4] = 0;
    String str2 = new String(arrayOfByte);
    transmit(str2);
  }

  
  public void send_caps_lock() {
    System.out.println("sending caps lock");
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    
    arrayOfByte[4] = 57;
    String str1 = new String(arrayOfByte);
    transmit(str1);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[4] = 0;
    String str2 = new String(arrayOfByte);
    transmit(str2);
  }
  
  public void send_ctrl_alt_back() {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    
    arrayOfByte[2] = 5;
    arrayOfByte[4] = 42;
    String str1 = new String(arrayOfByte);
    transmit(str1);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[4] = 0;
    String str2 = new String(arrayOfByte);
    transmit(str2);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[2] = 0;
    String str3 = new String(arrayOfByte);
    transmit(str3);
    
    requestFocus();
  }
  
  public void send_ctrl_alt_fn(int fnIndex) {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    byte b = 0;

    switch (fnIndex + 1) {
      case 1:
        b = 58;
        break;
      case 2:
        b = 59;
        break;
      case 3:
        b = 60;
        break;
      case 4:
        b = 61;
        break;
      case 5:
        b = 62;
        break;
      case 6:
        b = 63;
        break;
      case 7:
        b = 64;
        break;
      case 8:
        b = 65;
        break;
      case 9:
        b = 66;
        break;
      case 10:
        b = 67;
        break;
      case 11:
        b = 68;
        break;
      case 12:
        b = 69;
        break;
      default:
        b = 64;
        break;
    } 
    
    arrayOfByte[2] = 5;
    arrayOfByte[4] = b;
    
    String str1 = new String(arrayOfByte);
    transmit(str1);

    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[4] = 0;
    String str2 = new String(arrayOfByte);
    transmit(str2);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[2] = 0;
    String str3 = new String(arrayOfByte);
    transmit(str3);
    
    requestFocus();
  }
  
  public void send_alt_fn(int paramInt) {
    byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    byte b = 0;

    switch (paramInt + 1) {
      case 1:
        b = 58;
        break;
      case 2:
        b = 59;
        break;
      case 3:
        b = 60;
        break;
      case 4:
        b = 61;
        break;
      case 5:
        b = 62;
        break;
      case 6:
        b = 63;
        break;
      case 7:
        b = 64;
        break;
      case 8:
        b = 65;
        break;
      case 9:
        b = 66;
        break;
      case 10:
        b = 67;
        break;
      case 11:
        b = 68;
        break;
      case 12:
        b = 69;
        break;
      default:
        b = 64;
        break;
    } 
    
    arrayOfByte[2] = 4;
    arrayOfByte[4] = b;
    
    String str1 = new String(arrayOfByte);
    transmit(str1);

    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[4] = 0;
    String str2 = new String(arrayOfByte);
    transmit(str2);
    
    try {
      Thread.currentThread(); Thread.sleep(250L);
    } catch (InterruptedException interruptedException) {
      
      System.out.println("Thread interrupted..");
    } 
    
    arrayOfByte[2] = 0;
    String str3 = new String(arrayOfByte);
    transmit(str3);
    
    requestFocus();
  }

  public void sendMomPress() {
    this.post_complete = false;
    byte[] arrayOfByte = new byte[4];
    
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 0;
    arrayOfByte[2] = 0;
    arrayOfByte[3] = 0;
    
    String str = new String(arrayOfByte);
    transmit(str);
  }

  public void sendPressHold() {
    this.post_complete = false;
    byte[] arrayOfByte = new byte[4];
    
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 0;
    arrayOfByte[2] = 1;
    arrayOfByte[3] = 0;
    
    String str = new String(arrayOfByte);
    transmit(str);
  }

  public void sendPowerCycle() {
    this.post_complete = false;
    byte[] arrayOfByte = new byte[4];
    
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 0;
    arrayOfByte[2] = 2;
    arrayOfByte[3] = 0;
    
    String str = new String(arrayOfByte);
    transmit(str);
  }

  public void sendSystemReset() {
    this.post_complete = false;
    byte[] arrayOfByte = new byte[4];
    
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 0;
    arrayOfByte[2] = 3;
    arrayOfByte[3] = 0;
    
    String str = new String(arrayOfByte);
    transmit(str);
  }

  public void send_mouse_press(int paramInt) {}  
  public void send_mouse_release(int paramInt) {}
  public void send_mouse_click(int paramInt1, int paramInt2) {}
  public void send_mouse_byte(int paramInt) {}
  
  public void refresh_screen() {
    byte[] arrayOfByte = new byte[2];
    
    arrayOfByte[0] = 5;
    arrayOfByte[1] = 0;
    String str = new String(arrayOfByte);
    
    transmit(str);
    
    requestFocus();
  }

  public void send_keep_alive_msg() {}

  public static String byteToHex(byte paramByte) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(toHexChar(paramByte >>> 4 & 0xF));
    stringBuffer.append(toHexChar(paramByte & 0xF));
    return stringBuffer.toString();
  }
  
  public static String intToHex(int paramInt) {
    byte b = (byte)paramInt;
    return byteToHex(b);
  }
  
  public static String intToHex4(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(byteToHex((byte)(paramInt / 256)));
    stringBuffer.append(byteToHex((byte)(paramInt & 0xFF)));
    return stringBuffer.toString();
  }

  public static String charToHex(char paramChar) {
    byte b = (byte)paramChar;
    return byteToHex(b);
  }

  public static char toHexChar(int paramInt) {
    if (0 <= paramInt && paramInt <= 9) {
      return (char)(48 + paramInt);
    }
    
    return (char)(65 + paramInt - 10);
  }
  
  protected synchronized void set_framerate(int paramInt) {
    framerate = paramInt;
    this.screen.set_framerate(paramInt);
    set_status(3, "" + framerate);
  }

  protected void show_error(String paramString) {
    System.out.println("dvc:" + paramString + ": state " + dvc_decoder_state + " code " + dvc_code);
    System.out.println("dvc:error at byte count " + count_bytes);
  }

  final void cache_reset() {
    dvc_cc_active = 0;
  }

  final boolean cache_lru(int color) {
    int index = 0;
    boolean found = false;
    
    for (byte b = 0; b < dvc_cc_active; b++) {
      if (dvc_cc_color[b] == color) {
        index = b;
        found = true;
        break;
      } 
      if (dvc_cc_usage[b] == dvc_cc_active - 1) {
        index = b;
      }
    } 
    
    int k = dvc_cc_usage[index];
    
    if (!found) {
      if (dvc_cc_active < 17) {
        index = dvc_cc_active;
        k = dvc_cc_active;
        dvc_cc_active++;
        
        setPixcodeFromEntries();
      }
      dvc_cc_color[index] = color;
    } 
    
    dvc_cc_block[index] = 1;
    
    for (byte b = 0; b < dvc_cc_active; b++) {
      if (dvc_cc_usage[b] < k)
        dvc_cc_usage[b]++;
    }
    dvc_cc_usage[index] = 0;
    return found;
  }
  
  final int cache_find(int usage) {
    for (byte b = 0; b < dvc_cc_active; b++) {
      if (dvc_cc_usage[b] == usage) {
        for (byte b1 = 0; b1 < dvc_cc_active; b1++) {
          if (dvc_cc_usage[b1] < usage)
            dvc_cc_usage[b1] = dvc_cc_usage[b1] + 1;
        } 
        dvc_cc_usage[b] = 0;
        dvc_cc_block[b] = 1;
        return dvc_cc_color[b];
      }
    } 
    return -1;
  }

  final void cache_prune() {
    // block 0 is being removed, block 1 now points to block 0, etc.
    for (byte b = 0; b < dvc_cc_active; ) {
      if (dvc_cc_block[b] == 0) { // remove this item (swap with last, remove last)
        dvc_cc_active--;
        dvc_cc_block[b] = dvc_cc_block[dvc_cc_active];
        dvc_cc_color[b] = dvc_cc_color[dvc_cc_active];
        dvc_cc_usage[b] = dvc_cc_usage[dvc_cc_active];
      } else {
        dvc_cc_block[b]--;
        b++;
      }
    } 
    
    // set dvc_pixcode according to how many cache entries are left
    setPixcodeFromEntries();
  }

  protected void setPixcodeFromEntries() {
    if (dvc_cc_active < 2) {
      dvc_pixcode = 38;
    } else if (dvc_cc_active == 2) {
      dvc_pixcode = 4;
    } else if (dvc_cc_active == 3) {
      dvc_pixcode = 5;
    } else if (dvc_cc_active < 6) {
      dvc_pixcode = 6;
    } else if (dvc_cc_active < 10) {
      dvc_pixcode = 7;
    } else {
      dvc_pixcode = 32;
    }
    next_1[31] = dvc_pixcode;
  }

  
  protected void next_block(int blocks) {
    if (dvc_pixel_count != 0) {
      if (dvc_y_clipped > 0 && dvc_lasty == dvc_size_y) {
        for (int k = dvc_y_clipped; k < 256; k++)
          block[k] = this.color_remap_table[0];
      } 
    }
    
    dvc_pixel_count = 0;
    dvc_next_state = 1;
    
    int i = dvc_lastx * this.blockWidth;
    int j = dvc_lasty * this.blockHeight;
    while (blocks != 0) {
      if (video_detected)
        this.screen.paste_array(block, i, j, 16, this.blockHeight);
      
      dvc_lastx++;
      i += 16;

      if (dvc_lastx >= dvc_size_x)
        break; 
      blocks--;
    } 
  }

  
  protected void init_reversal() {
    for (byte b = 0; b < 256; b++) {
      byte b2 = 8;
      int k = 8;
      int i = b;
      int j = 0;
      for (byte b1 = 0; b1 < 8; b1++) {
        j <<= 1;
        if ((i & 0x1) == 1) {
          if (b2 > b1)
            b2 = b1; 
          j |= 0x1;
          k = 7 - b1;
        } 
        i >>= 1;
      } 
      dvc_reversal[b] = j;
      dvc_right[b] = b2;
      dvc_left[b] = k;
    } 
  }

  final int add_bits(char input) {
    dvc_ib_acc |= input << dvc_ib_bcnt;
    dvc_ib_bcnt += 8;
    return 0;
  }

  /** Consume N bits from dvc_ib_acc and place them at bits 0, 1, .. N-1 of dvc_code */
  final boolean get_bits(int bits) {
    if (bits == 1) {
      dvc_code = dvc_ib_acc & 1;
      dvc_ib_acc >>= 1;
      dvc_ib_bcnt--;
      return false;
    }
    if (bits == 0) {
      return false;
    }
    
    int i = dvc_ib_acc & dvc_getmask[bits];
    dvc_ib_bcnt -= bits;
    dvc_ib_acc >>= bits;
    i = dvc_reversal[i];
    i >>= 8 - bits;
    dvc_code = i;
    
    return false;
  }

  boolean process_bits(char input) {
    boolean bool = true;
    
    byte exit = 0;

    add_bits(input);
  
    dvc_zero_count += dvc_right[input];
    if (dvc_zero_count > 30) {
      if (!debug_msgs || 
        dvc_decoder_state != 38 || fatal_count >= 40 || fatal_count > 0);
      dvc_next_state = 43;
      dvc_decoder_state = 43;
    } else if (input != 0) {
      dvc_zero_count = dvc_left[input];
    }

    dvc_new_bits = input;
    count_bytes++;

    while (!exit) {
      int bitsToRead = bits_to_read[dvc_decoder_state];
      
      if (bitsToRead > dvc_ib_bcnt) {
        exit = 0;        
        break;
      }
      
      boolean lruRes = get_bits(bitsToRead);
      dvc_counter_bits += bitsToRead;

      if (dvc_code == 0) {
        dvc_next_state = next_0[dvc_decoder_state];
      } else {
        dvc_next_state = next_1[dvc_decoder_state];
      }
      
      switch (dvc_decoder_state) {
        
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 32:
          if (dvc_cc_active == 1) {
            dvc_code = dvc_cc_usage[0];
          } else if (dvc_decoder_state == 4) {
            dvc_code = 0;
          } else if (dvc_decoder_state == 3) {
            dvc_code = 1;
          } else if (dvc_code != 0) {
            dvc_code++;
          } 
          dvc_color = cache_find(dvc_code);
          if (dvc_color == -1) {
            dvc_next_state = 38;
            break;
          } 
          
          dvc_last_color = this.color_remap_table[dvc_color];
          
          if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
            block[dvc_pixel_count] = dvc_last_color;
          } else {
            dvc_next_state = 38;
            break;
          } 
          
          dvc_pixel_count++;
          break;
        
        case 12:
          if (dvc_code == 7) {
            dvc_next_state = 14; break;
          }  if (dvc_code == 6) {
            dvc_next_state = 13;
            break;
          } 
          dvc_code += 2;
          for (byte b1 = 0; b1 < dvc_code; b1++) {
            if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
              block[dvc_pixel_count] = dvc_last_color;
            } else {
              dvc_next_state = 38;
              break;
            } 
            
            dvc_pixel_count++;
          } 
          break;
        
        case 13:
          dvc_code += 8;
        
        case 14:
          if (!debug_msgs || 
            dvc_decoder_state != 14 || dvc_code < 16);

          for (byte b1 = 0; b1 < dvc_code; b1++) {
            if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
              block[dvc_pixel_count] = dvc_last_color;
            } else {
              dvc_next_state = 38;
              break;
            } 
            
            dvc_pixel_count++;
          } 
          break;
        
        case 33:
          if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
            block[dvc_pixel_count] = dvc_last_color;
          } else {
            dvc_next_state = 38;
            break;
          } 
          
          dvc_pixel_count++;
          break;

        case 35:
          dvc_next_state = dvc_pixcode;
          break;
        
        case 9:
          dvc_red = dvc_code << this.bitsPerColor * 2;
          break;

        case 41:
          dvc_green = dvc_code << this.bitsPerColor;
          break;

        case 8:
          dvc_red = dvc_code << this.bitsPerColor * 2;
          dvc_green = dvc_code << this.bitsPerColor;
        
        case 42:
          dvc_blue = dvc_code;
          dvc_color = dvc_red | dvc_green | dvc_blue;
          lruRes = cache_lru(dvc_color);
          if (lruRes) {
            if (!debug_msgs || 
              count_bytes > 6L);
            
            dvc_next_state = 38;
            
            break;
          } 
          
          dvc_last_color = this.color_remap_table[dvc_color];
          
          if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
            block[dvc_pixel_count] = dvc_last_color;
          } else {
            dvc_next_state = 38;
            break;
          } 
          
          dvc_pixel_count++;
          break;
        case 17:
        case 26:
          dvc_newx = dvc_code;
          if (dvc_decoder_state == 17 && dvc_newx > dvc_size_x) {
            if (debug_msgs);

            dvc_newx = 0;
          } 
          break;
        
        case 39:
          dvc_newy = dvc_code;
          if (this.blockHeight == 16) {
            dvc_newy &= 0x7F;
          }
          
          dvc_lastx = dvc_newx;
          dvc_lasty = dvc_newy;
          
          if (dvc_lasty <= dvc_size_y || 
            debug_msgs);

          this.screen.repaint_it(1);
          break;
        
        case 20:
          dvc_code = dvc_lastx + dvc_code + 1;
          if (dvc_code <= dvc_size_x || 
            debug_msgs);
        
        case 21:
          dvc_lastx = dvc_code;
          if (this.blockHeight == 16) {
            dvc_lastx &= 0x7F;
          }
          if (dvc_lastx <= dvc_size_x || 
            debug_msgs);
          break;
        
        case 27:
          if (timeout_count == count_bytes - 1L) {
            dvc_next_state = 38;
          }

          if ((dvc_ib_bcnt % 8) != 0)
            get_bits(dvc_ib_bcnt % 8); 
          timeout_count = count_bytes;
          
          this.screen.repaint_it(1);
          break;

        
        case 24:
          if (cmd_p_count != 0)
            cmd_p_buff[cmd_p_count - 1] = cmd_last; 
          cmd_p_count++;
          
          cmd_last = dvc_code;
          break;
        
        case 46:
          if (dvc_code == 0) {

            switch (cmd_last) {
              case 1:
                dvc_next_state = 37;
                break;

              case 2:
                dvc_next_state = 44;
                break;

              
              case 3:
                if (cmd_p_count != 0) {
                  set_framerate(cmd_p_buff[0]); break;
                } 
                set_framerate(0);
                break;
              case 4:
                this.remconsObj.setPwrStatusPower(1);
                break;
              
              case 5:
                this.remconsObj.setPwrStatusPower(0);
                
                this.screen.clearScreen();
                dvc_newx = 50;
                dvc_code = 38; // FIXME: possible bug, they meant dvc_pixcode or dvc_next_state
                break;

              case 6:
                this.screen.clearScreen();
                
                if (!video_detected) {
                  this.screen.clearScreen();
                }
                set_status(2, getLocalString(12290));
                set_status(1, " ");
                
                set_status(3, " ");
                set_status(4, " ");
                this.post_complete = false;
                break;
              case 7:
                this.ts_type = cmd_p_buff[0];
                break;

              case 9:
                System.out.println("received keychg and cleared bits\n");
                if ((dvc_ib_bcnt % 8) != 0) {
                  get_bits(dvc_ib_bcnt % 8);
                }
                break;
              
              case 10:
                seize();
                break;
              
              case 11:
                System.out.println("Setting bpc to  " + cmd_p_buff[0]);
                setBitsPerColor(cmd_p_buff[0]);
                break;
              
              case 12:
                System.out.println("Setting encryption to  " + cmd_p_buff[0]);
                setVideoDecryption(cmd_p_buff[0]);
                break;
              
              case 13:
                System.out.println("Header received ");
                setBitsPerColor(cmd_p_buff[0]);
                
                setVideoDecryption(cmd_p_buff[1]);
                
                this.remconsObj.SetLicensed(cmd_p_buff[2]);
                this.remconsObj.SetFlags(cmd_p_buff[3]);
                break;
              case 16:
                sendAck();
                break;
              
              case 128:
                this.screen.invalidate();
                this.screen.repaint();
                break;
            } 
            
            cmd_p_count = 0;
          } 
          break;
        
        case 44:
          printchan = dvc_code;
          printstring = "";
          break;

        
        case 45:
          if (dvc_code != 0) {
            printstring += (char)dvc_code;
            
            break;
          } 
          
          switch (printchan) {
            case 1:
            case 2:
              set_status(2 + printchan, printstring);
              break;
            case 3:
              System.out.println(printstring);
              break;
            
            case 4:
              this.screen.show_text(printstring);
              break;
          } 
          
          dvc_next_state = 1;
          break;

        case 0:
          cache_reset();
          dvc_pixel_count = 0;
          dvc_lastx = 0;
          dvc_lasty = 0;
          dvc_red = 0;
          dvc_green = 0;
          dvc_blue = 0;
          fatal_count = 0;
          timeout_count = -1L;
          
          cmd_p_count = 0;
          break;
        
        case 38:
          if (fatal_count == 0) {
            debug_lastx = dvc_lastx;
            debug_lasty = dvc_lasty;
            debug_show_block = 1;
          } 
          if (fatal_count == 40)
          {
            refresh_screen();
          }
          if (fatal_count == 11680) {
            refresh_screen();
          }
          fatal_count++;
          if (fatal_count == 120000)
          {
            refresh_screen();
          }
          if (fatal_count == 12000000) {
            refresh_screen();
            fatal_count = 41;
          } 
          break;
        
        case 34:
          next_block(1);
          break;
        case 29:
          dvc_code += 2;
        
        case 30:
          next_block(dvc_code);
          break;
        
        case 40:
          dvc_size_x = dvc_newx;
          dvc_size_y = dvc_code;
          break;
        
        case 47:
          dvc_lastx = 0;
          dvc_lasty = 0;
          dvc_pixel_count = 0;
          cache_reset();
          this.scale_x = 1;
          this.scale_y = 1;
          this.screen_x = dvc_size_x * this.blockWidth;
          this.screen_y = dvc_size_y * 16 + dvc_code;

          if (this.screen_x == 0 || this.screen_y == 0) {
            video_detected = false;
          } else {
            video_detected = true;
          } 
          
          if (dvc_code > 0) {
            dvc_y_clipped = 256 - 16 * dvc_code;
          } else {
            dvc_y_clipped = 0;
          } 
          if (!video_detected) {
            this.screen.clearScreen();
            set_status(2, getLocalString(12290));
            set_status(1, " ");
            set_status(3, " ");
            set_status(4, " ");
            System.out.println("No video. image_source = " + this.screen.image_source);
            this.post_complete = false;
            
            break;
          } 
          
          this.screen.set_abs_dimensions(this.screen_x, this.screen_y);
          SetHalfHeight();
          this.mouse_sync.serverScreen(this.screen_x, this.screen_y);
          set_status(2, getLocalString(12291) + this.screen_x + "x" + this.screen_y);
          set_status(1, " ");
          break;

        case 43:
          if (dvc_next_state != dvc_decoder_state) {
            dvc_ib_bcnt = 0;
            dvc_ib_acc = 0;
            dvc_zero_count = 0;
            count_bytes = 0L;
          } 
          break;
        
        case 37:
          return 1;
      } 

      if (dvc_next_state == 2 && dvc_pixel_count == this.blockHeight * this.blockWidth) {
        next_block(1);
        cache_prune();
      } 

      if (dvc_decoder_state == dvc_next_state && dvc_decoder_state != 45 && dvc_decoder_state != 38 && dvc_decoder_state != 43) {
        System.out.println("Machine hung in state " + dvc_decoder_state);
        exit = 6;
        continue;
      } 
      dvc_decoder_state = dvc_next_state;
    } 

    return exit;
  }
  
  boolean process_dvc(char input) {
    if (dvc_reversal[255] == 0) {
      System.out.println("dvc initializing"); // FIXME: why is this initialized here?
      
      init_reversal();
      cache_reset();
      dvc_decoder_state = 0;
      dvc_next_state = 0;
      dvc_zero_count = 0;
      dvc_ib_acc = 0;
      dvc_ib_bcnt = 0;
      
      buildPixelTable(this.bitsPerColor);
      SetHalfHeight();
    } 
    
    boolean bool1 = false;
    if (!dvc_process_inhibit)
      bool1 = process_bits(input);
  
    boolean result;
    if (!bool1) {
      result = true;
    } else {
      System.out.println("Exit from DVC mode status =" + bool1);
      System.out.println("Current block at " + dvc_lastx + " " + dvc_lasty);
      System.out.println("Byte count " + count_bytes);
      result = true;
      
      dvc_decoder_state = 38;
      dvc_next_state = 38;

      fatal_count = 0;
      refresh_screen();
    }
    return result;
  }
  
  public void set_sig_colors(int[] paramArrayOfint) {}


  public void change_key() {
    this.RC4encrypter.update_key();
    super.change_key();
  }



  public void set_mouse_protocol(int paramInt) {
    this.mouse_protocol = paramInt;
  }

  private static final byte[] cursor_none = new byte[] { 0 };

  private static final int[] cursor_outline = new int[] { -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0,
          0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0,
          0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0,
          0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0,
          -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, -8355712, -8355712,
          -8355712, -8355712, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0,
          -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, -8355712, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, 0,
          -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712,
          0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712,
          0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0 };

  protected Cursor current_cursor;
  
  Cursor customCursor(Image paramImage, Point paramPoint, String paramString) {
    Cursor cursor = null;
    try {
      Class clazz = Toolkit.class;
      Method method = clazz.getMethod("createCustomCursor", new Class[] { Image.class, Point.class, String.class });
      
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (method != null) {
        cursor = (Cursor)method.invoke(toolkit, new Object[] { paramImage, paramPoint, paramString });
      }
    } catch (Exception exception) {
      
      System.out.println("This JVM cannot create custom cursors");
    } 
    return cursor;
  }
  
  Cursor createCursor(int paramInt) {
    MemoryImageSource memoryImageSource;
    Image image;
    int[] arrayOfInt;
    byte y;
    String str = System.getProperty("java.version", "0");

    
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    
    switch (paramInt) {
      case 0:
        return Cursor.getDefaultCursor();
      case 1:
        return Cursor.getPredefinedCursor(1);
      case 2:
        image = toolkit.createImage(cursor_none);
        break;
      
      case 3:
        arrayOfInt = new int[1024];
        arrayOfInt[33] = -8355712;
        arrayOfInt[32] = -8355712;
        arrayOfInt[1] = -8355712;
        arrayOfInt[0] = -8355712;
        memoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
        image = createImage(memoryImageSource);
        break;
      
      case 4:
        arrayOfInt = new int[1024];
        for (y = 0; y < 21; y++) {
          for (byte x = 0; x < 12; x++) {
            arrayOfInt[x + y * 32] = cursor_outline[x + y * 12];
          }
        } 
        memoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
        image = createImage(memoryImageSource);
        break;
      default:
        System.out.println("createCursor: unknown cursor " + paramInt);
        return Cursor.getDefaultCursor();
    } 
    
    Cursor cursor = null;
    if (str.compareTo("1.2") < 0) {
      System.out.println("This JVM cannot create custom cursors");
    } else {
      
      cursor = customCursor(image, new Point(), "rcCursor");
    } 
    
    return (cursor != null) ? cursor : Cursor.getDefaultCursor();
  }

  
  public void set_cursor(int paramInt) {
    this.current_cursor = createCursor(paramInt);
    setCursor(this.current_cursor);
  }

  
  private void SetHalfHeight() {
    if (this.screen_x > 1616) {
      if (this.remconsObj.halfHeightCapable) {
        if (8 != this.blockHeight) {
          System.out.println("Setting halfheight mode on supported system");
          this.blockHeight = 8;
          bits_to_read[21] = 8;
          bits_to_read[17] = 8;
          bits_to_read[39] = 8;
          bits_to_read[30] = 8;
        }
      
      }
      else if (!this.unsupportedVideoModeWarned) {
        
        new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8225), getLocalString(8226), false);
        this.unsupportedVideoModeWarned = true;
      
      }
    
    } else if (this.blockHeight != 16) {
      System.out.println("Setting non-halfheight mode");
      this.blockHeight = 16;
      bits_to_read[21] = 7;
      bits_to_read[17] = 7;
      bits_to_read[39] = 7;
      bits_to_read[30] = 7;
    } 
  }





  
  void buildPixelTable(int bits_per_color) {
    int num_colors = 1 << (bits_per_color * 3);

    switch (bits_per_color) {
      case 5:
        for (int i = 0; i < num_colors; i++) {
          this.color_remap_table[i] = (i & 0x1F) << 3;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0x3E0) << 6;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0x7C00) << 9;
        } 
        break;
      
      case 4:
        for (int i = 0; i < num_colors; i++) {
          this.color_remap_table[i] = (i & 0xF) << 4;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0xF0) << 8;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0xF00) << 12;
        }
        break;

      case 3:
        for (int i = 0; i < num_colors; i++) {
          this.color_remap_table[i] = (i & 0xF) << 5;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0xF0) << 11;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0xF00) << 15;
        } 
        break;
      
      case 2:
        for (int i = 0; i < num_colors; i++) {
          this.color_remap_table[i] = (i & 0xF) << 6;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0xF0) << 15;
          this.color_remap_table[i] = this.color_remap_table[i] | (i & 0xF00) << 18;
        } 
        break;
    } 
  }
  
  void setBitsPerColor(int paramInt) {
    this.bitsPerColor = 5 - (paramInt & 3);

    bits_to_read[8] = this.bitsPerColor;
    bits_to_read[9] = this.bitsPerColor;
    bits_to_read[41] = this.bitsPerColor;
    bits_to_read[42] = this.bitsPerColor;
    
    buildPixelTable(this.bitsPerColor);
  }

  void setVideoDecryption(int paramInt) {
    switch (paramInt) {
      case 0:
        this.dvc_encryption = false;
        this.cipher = 0;
        this.remconsObj.setPwrStatusEncLabel(getLocalString(12292));
        this.remconsObj.setPwrStatusEnc(0);
        System.out.println("Setting encryption -> None");
        return;
      case 1:
        this.dvc_encryption = true;
        this.remconsObj.setPwrStatusEncLabel(getLocalString(12293));
        this.remconsObj.setPwrStatusEnc(1);
        this.dvc_mode = true;
        this.cipher = 1;
        System.out.println("Setting encryption -> RC4 - 128 bit");
        return;
      case 2:
        this.dvc_encryption = true;
        this.remconsObj.setPwrStatusEncLabel(getLocalString(12294));
        this.remconsObj.setPwrStatusEnc(1);
        this.dvc_mode = true;
        this.cipher = 2;
        System.out.println("Setting encryption -> AES - 128 bit");
        return;
      case 3:
        this.dvc_encryption = true;
        this.remconsObj.setPwrStatusEncLabel(getLocalString(12295));
        this.remconsObj.setPwrStatusEnc(1);
        this.dvc_mode = true;
        this.cipher = 3;
        System.out.println("Setting encryption -> AES - 256 bit");
        return;
    } 
    this.dvc_encryption = false;
    this.remconsObj.setPwrStatusEncLabel(getLocalString(12292));
    this.remconsObj.setPwrStatusEnc(0);
    System.out.println("Unsupported encryption");
  }

  public byte mouseButtonState(int paramInt) {
    byte b = 0;
    switch (paramInt) {
      case 4:
        b = (byte)(b | 0x1);
        break;
      case 2:
        b = (byte)(b | 0x4);
        break;
      case 1:
        b = (byte)(b | 0x2);
        break;
    } 
    return b;
  }

  public byte getMouseButtonState(MouseEvent paramMouseEvent) {
    byte b = 0;
    if ((paramMouseEvent.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0)
      b = (byte)(b | 0x2); 
    if ((paramMouseEvent.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0)
      b = (byte)(b | 0x4); 
    if ((paramMouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
      b = (byte)(b | 0x1);
    
    return b;
  }
  
  public void sendMouse(MouseEvent paramMouseEvent) {
    Point point1 = new Point(0, 0);
    Point point2 = new Point(0, 0);
    
    point1 = getAbsMouseCoordinates(paramMouseEvent);
    char mx = (char)point1.x;
    char my = (char)point1.y;
    
    if ((paramMouseEvent.getModifiersEx() & 0x80) > 0) {
      this.mousePrevPosn.x = mx;
      this.mousePrevPosn.y = my;
    } else if (mx <= this.screen_x && my <= this.screen_y) {
      
      point2.x = mx - this.mousePrevPosn.x;
      this.mousePrevPosn.y -= my;

      
      this.mousePrevPosn.x = mx;
      this.mousePrevPosn.y = my;
      
      int i = point2.x;
      int j = point2.y;
      
      if (i < -127) {
        i = -127;
      }
      else if (i > 127) {
        i = 127;
      } 
      if (j < -127) {
        j = -127;
      }
      else if (j > 127) {
        j = 127;
      } 
      
      this.UI_dirty = true;
      
      if (this.screen_x > 0 && this.screen_y > 0) {
        mx = (char)(3000 * mx / this.screen_x);
        my = (char)(3000 * my / this.screen_y);
      } else {
        
        mx = (char)(3000 * mx / 1);
        my = (char)(3000 * my / 1);
      } 

      
      byte[] arrayOfByte = new byte[10];
      
      arrayOfByte[0] = 2;
      arrayOfByte[1] = 0;
      arrayOfByte[2] = (byte)(mx & 0xFF);
      arrayOfByte[3] = (byte)(mx >> 8);
      arrayOfByte[4] = (byte)(my & 0xFF);
      arrayOfByte[5] = (byte)(my >> 8);
      
      if (i < 0) {
        arrayOfByte[6] = (byte)(i & 0xFF);
      } else {
        
        arrayOfByte[6] = (byte)(i & 0xFF);
      } 
      
      if (j < 0) {
        arrayOfByte[7] = (byte)(j & 0xFF);
      } else {
        
        arrayOfByte[7] = (byte)(j & 0xFF);
      } 
      arrayOfByte[8] = getMouseButtonState(paramMouseEvent);
      arrayOfByte[9] = 0;



      
      transmitb(arrayOfByte, arrayOfByte.length);
    } 
  }

  private Point getAbsMouseCoordinates(MouseEvent paramMouseEvent) {
    Point point = new Point();
    point.y = paramMouseEvent.getY();
    point.x = paramMouseEvent.getX();
    return point;
  }

  public void sendMouseScroll(MouseWheelEvent paramMouseWheelEvent) {}

  
  private void sendAck() {
    byte[] arrayOfByte = new byte[2];
    
    arrayOfByte[0] = 12;
    arrayOfByte[1] = 0;
    
    String str = new String(arrayOfByte);
    transmit(str);
  }

  
  public void requestScreenFocus(MouseEvent paramMouseEvent) {
    requestFocus();
  }

  
  public void installKeyboardHook() {
    this.remconsObj.remconsInstallKeyboardHook();
  }

  
  public void unInstallKeyboardHook() {
    this.remconsObj.remconsUnInstallKeyboardHook();
  }
}
