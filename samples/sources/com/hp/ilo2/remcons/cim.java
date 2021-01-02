/*      */ package com.hp.ilo2.remcons;
/*      */ 
/*      */ import com.hp.ilo2.virtdevs.VErrorDialog;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Image;
/*      */ import java.awt.Point;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Method;
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
/*      */ public class cim
/*      */   extends telnet
/*      */   implements MouseSyncListener
/*      */ {
/*      */   private static final int CMD_MOUSE_MOVE = 208;
/*      */   private static final int CMD_BUTTON_PRESS = 209;
/*      */   private static final int CMD_BUTTON_RELEASE = 210;
/*      */   private static final int CMD_BUTTON_CLICK = 211;
/*      */   private static final int CMD_BYTE = 212;
/*      */   private static final int CMD_SET_MODE = 213;
/*      */   private static final char MOUSE_USBABS = '\001';
/*      */   private static final char MOUSE_USBREL = '\002';
/*      */   static final int CMD_ENCRYPT = 192;
/*      */   public static final int MOUSE_BUTTON_LEFT = 4;
/*      */   public static final int MOUSE_BUTTON_CENTER = 2;
/*      */   public static final int MOUSE_BUTTON_RIGHT = 1;
/*   56 */   private char prev_char = ' ';
/*      */ 
/*      */   
/*      */   private boolean disable_kbd = false;
/*      */ 
/*      */   
/*      */   private boolean altlock = false;
/*      */ 
/*      */   
/*      */   private static final int block_width = 16;
/*      */ 
/*      */   
/*      */   private static final int block_height = 16;
/*      */ 
/*      */   
/*   71 */   public int[] color_remap_table = new int[32768];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   77 */   private int scale_x = 1;
/*   78 */   private int scale_y = 1;
/*      */ 
/*      */   
/*   81 */   private int screen_x = 1;
/*   82 */   private int screen_y = 1;
/*   83 */   private int mouse_protocol = 0;
/*      */   
/*   85 */   protected MouseSync mouse_sync = new MouseSync(this);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean UI_dirty = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean sending_encrypt_command = false;
/*      */ 
/*      */ 
/*      */   
/*   98 */   public byte[] encrypt_key = new byte[16];
/*      */   private RC4 RC4encrypter;
/*      */   private Aes Aes128encrypter;
/*      */   private Aes Aes256encrypter;
/*  102 */   private int key_index = 0;
/*      */ 
/*      */   
/*  105 */   private int bitsPerColor = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  110 */   public Point mousePrevPosn = new Point(0, 0);
/*      */   
/*  112 */   private byte mouseBtnState = 0;
/*      */   
/*      */   private static final int RESET = 0;
/*      */   
/*      */   private static final int START = 1;
/*      */   
/*      */   private static final int PIXELS = 2;
/*      */   
/*      */   private static final int PIXLRU1 = 3;
/*      */   
/*      */   private static final int PIXLRU0 = 4;
/*      */   
/*      */   private static final int PIXCODE1 = 5;
/*      */   
/*      */   private static final int PIXCODE2 = 6;
/*      */   private static final int PIXCODE3 = 7;
/*      */   private static final int PIXGREY = 8;
/*      */   private static final int PIXRGBR = 9;
/*      */   private static final int PIXRPT = 10;
/*      */   private static final int PIXRPT1 = 11;
/*      */   private static final int PIXRPTSTD1 = 12;
/*      */   private static final int PIXRPTSTD2 = 13;
/*      */   private static final int PIXRPTNSTD = 14;
/*      */   private static final int CMD = 15;
/*      */   private static final int CMD0 = 16;
/*      */   private static final int MOVEXY0 = 17;
/*      */   private static final int EXTCMD = 18;
/*      */   private static final int CMDX = 19;
/*      */   private static final int MOVESHORTX = 20;
/*      */   private static final int MOVELONGX = 21;
/*      */   private static final int BLKRPT = 22;
/*      */   private static final int EXTCMD1 = 23;
/*      */   private static final int FIRMWARE = 24;
/*      */   private static final int EXTCMD2 = 25;
/*      */   private static final int MODE0 = 26;
/*      */   private static final int TIMEOUT = 27;
/*      */   private static final int BLKRPT1 = 28;
/*      */   private static final int BLKRPTSTD = 29;
/*      */   private static final int BLKRPTNSTD = 30;
/*      */   private static final int PIXFAN = 31;
/*      */   private static final int PIXCODE4 = 32;
/*      */   private static final int PIXDUP = 33;
/*      */   private static final int BLKDUP = 34;
/*      */   private static final int PIXCODE = 35;
/*      */   private static final int PIXSPEC = 36;
/*      */   private static final int EXIT = 37;
/*      */   private static final int LATCHED = 38;
/*      */   private static final int MOVEXY1 = 39;
/*      */   private static final int MODE1 = 40;
/*      */   private static final int PIXRGBG = 41;
/*      */   private static final int PIXRGBB = 42;
/*      */   private static final int HUNT = 43;
/*      */   private static final int PRINT0 = 44;
/*      */   private static final int PRINT1 = 45;
/*      */   private static final int CORP = 46;
/*      */   private static final int MODE2 = 47;
/*      */   private static final int SIZE_OF_ALL = 48;
/*  169 */   private static int[] bits_to_read = new int[] { 0, 1, 1, 1, 1, 1, 2, 3, 5, 5, 1, 1, 3, 3, 8, 1, 1, 7, 1, 1, 3, 7, 1, 1, 8, 1, 7, 0, 1, 3, 7, 1, 4, 0, 0, 0, 1, 0, 1, 7, 7, 5, 5, 1, 8, 8, 1, 4 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  179 */   private static int[] next_0 = new int[] { 1, 2, 31, 2, 2, 10, 10, 10, 10, 41, 2, 33, 2, 2, 2, 16, 19, 39, 22, 20, 1, 1, 34, 25, 46, 26, 40, 1, 29, 1, 1, 36, 10, 2, 1, 35, 8, 37, 38, 1, 47, 42, 10, 43, 45, 45, 1, 1 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  189 */   private static int[] next_1 = new int[] { 1, 15, 3, 11, 11, 10, 10, 10, 10, 41, 11, 12, 2, 2, 2, 17, 18, 39, 23, 21, 1, 1, 28, 24, 46, 27, 40, 1, 30, 1, 1, 35, 10, 2, 1, 35, 9, 37, 38, 1, 47, 42, 10, 0, 45, 45, 24, 1 };
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
/*  200 */   private static int dvc_cc_active = 0;
/*  201 */   private static int[] dvc_cc_color = new int[17];
/*  202 */   private static int[] dvc_cc_usage = new int[17];
/*  203 */   private static int[] dvc_cc_block = new int[17];
/*      */ 
/*      */   
/*  206 */   private static int[] dvc_lru_lengths = new int[] { 0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 };
/*      */   
/*  208 */   private static int[] dvc_getmask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255 };
/*  209 */   private static int[] dvc_reversal = new int[256];
/*  210 */   private static int[] dvc_left = new int[256];
/*  211 */   private static int[] dvc_right = new int[256];
/*      */   
/*      */   private static int dvc_pixel_count;
/*      */   
/*      */   private static int dvc_size_x;
/*      */   
/*      */   private static int dvc_size_y;
/*      */   private static int dvc_y_clipped;
/*      */   private static int dvc_lastx;
/*  220 */   private static int dvc_ib_acc = 0; private static int dvc_lasty; private static int dvc_newx; private static int dvc_newy; private static int dvc_color;
/*      */   private static int dvc_last_color;
/*  222 */   private static int dvc_ib_bcnt = 0;
/*      */   
/*  224 */   private static int dvc_zero_count = 0;
/*      */ 
/*      */   
/*  227 */   private static int dvc_decoder_state = 0;
/*      */   
/*  229 */   private static int dvc_next_state = 0;
/*      */   
/*  231 */   private static int dvc_pixcode = 38;
/*      */   
/*  233 */   private static int dvc_code = 0;
/*  234 */   private static int[] block = new int[256];
/*      */   private static int dvc_red;
/*      */   private static int dvc_green;
/*      */   private static int dvc_blue;
/*      */   private static int fatal_count;
/*  239 */   private static int printchan = 0;
/*  240 */   private static String printstring = "";
/*  241 */   private static long count_bytes = 0L;
/*  242 */   private static int[] cmd_p_buff = new int[256];
/*  243 */   private static int cmd_p_count = 0;
/*  244 */   private static int cmd_last = 0;
/*      */   
/*  246 */   private static int framerate = 30;
/*      */   
/*      */   private static boolean debug_msgs = false;
/*      */   
/*  250 */   private static char last_bits = Character.MIN_VALUE;
/*  251 */   private static char last_bits2 = Character.MIN_VALUE;
/*  252 */   private static char last_bits3 = Character.MIN_VALUE;
/*  253 */   private static char last_bits4 = Character.MIN_VALUE;
/*  254 */   private static char last_bits5 = Character.MIN_VALUE;
/*  255 */   private static char last_bits6 = Character.MIN_VALUE;
/*  256 */   private static char last_bits7 = Character.MIN_VALUE;
/*  257 */   private static int last_len = 0;
/*  258 */   private static int last_len1 = 0;
/*  259 */   private static int last_len2 = 0;
/*  260 */   private static int last_len3 = 0;
/*  261 */   private static int last_len4 = 0;
/*  262 */   private static int last_len5 = 0;
/*  263 */   private static int last_len6 = 0;
/*  264 */   private static int last_len7 = 0;
/*  265 */   private static int last_len8 = 0;
/*  266 */   private static int last_len9 = 0;
/*  267 */   private static int last_len10 = 0;
/*  268 */   private static int last_len11 = 0;
/*  269 */   private static int last_len12 = 0;
/*  270 */   private static int last_len13 = 0;
/*  271 */   private static int last_len14 = 0;
/*  272 */   private static int last_len15 = 0;
/*  273 */   private static int last_len16 = 0;
/*  274 */   private static int last_len17 = 0;
/*  275 */   private static int last_len18 = 0;
/*  276 */   private static int last_len19 = 0;
/*  277 */   private static int last_len20 = 0;
/*  278 */   private static int last_len21 = 0;
/*  279 */   private static char dvc_new_bits = Character.MIN_VALUE;
/*  280 */   private static int debug_lastx = 0;
/*  281 */   private static int debug_lasty = 0;
/*  282 */   private static int debug_show_block = 0;
/*  283 */   private static long timeout_count = 0L;
/*  284 */   private static long dvc_counter_block = 0L;
/*  285 */   private static long dvc_counter_bits = 0L;
/*      */   private static boolean show_bitsblk_count = false;
/*  287 */   private static long show_slices = 0L;
/*      */ 
/*      */   
/*      */   private static boolean dvc_process_inhibit = false;
/*      */   
/*      */   private static boolean video_detected = true;
/*      */   
/*      */   private boolean ignore_next_key = false;
/*      */   
/*  296 */   private int blockHeight = 16;
/*  297 */   private int blockWidth = 16; private boolean unsupportedVideoModeWarned = false;
/*      */   private static final int B = -16777216;
/*      */   private static final int W = -8355712;
/*      */   
/*      */   public String getLocalString(int paramInt) {
/*  302 */     String str = "";
/*      */     try {
/*  304 */       str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*      */     } catch (Exception exception) {
/*      */       
/*  307 */       System.out.println("cim:getLocalString" + exception.getMessage());
/*      */     } 
/*  309 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public cim(remcons paramremcons) {
/*  317 */     super(paramremcons);
/*  318 */     dvc_reversal[255] = 0;
/*  319 */     this.current_cursor = Cursor.getDefaultCursor();
/*  320 */     this.screen.addMouseListener(this.mouse_sync);
/*  321 */     this.screen.addMouseMotionListener(this.mouse_sync);
/*  322 */     this.screen.addMouseWheelListener(this.mouse_sync);
/*  323 */     this.mouse_sync.setListener(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setup_encryption(byte[] paramArrayOfbyte, int paramInt) {
/*  332 */     System.arraycopy(paramArrayOfbyte, 0, this.encrypt_key, 0, 16);
/*      */     
/*  334 */     this.RC4encrypter = new RC4(paramArrayOfbyte);
/*  335 */     this.Aes128encrypter = new Aes(0, paramArrayOfbyte);
/*  336 */     this.Aes256encrypter = new Aes(0, paramArrayOfbyte);
/*  337 */     this.key_index = paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void reinit_vars() {
/*  345 */     super.reinit_vars();
/*      */     
/*  347 */     dvc_code = 0;
/*  348 */     dvc_ib_acc = 0;
/*  349 */     dvc_ib_bcnt = 0;
/*  350 */     dvc_counter_bits = 0L;
/*      */     
/*  352 */     this.prev_char = ' ';
/*  353 */     this.disable_kbd = false;
/*  354 */     this.altlock = false;
/*      */     
/*  356 */     dvc_reversal[255] = 0;
/*      */     
/*  358 */     this.scale_x = 1;
/*  359 */     this.scale_y = 1;
/*      */     
/*  361 */     this.mouse_sync.restart();
/*      */     
/*  363 */     dvc_process_inhibit = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void enable_debug() {
/*  371 */     debug_msgs = true;
/*  372 */     super.enable_debug();
/*  373 */     this.mouse_sync.enableDebug();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disable_debug() {
/*  381 */     debug_msgs = false;
/*  382 */     super.disable_debug();
/*  383 */     this.mouse_sync.disableDebug();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sync_start() {
/*  391 */     this.mouse_sync.sync();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverMove(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  400 */     if (paramInt1 < -128) {
/*  401 */       paramInt1 = -128;
/*      */     }
/*  403 */     else if (paramInt1 > 127) {
/*  404 */       paramInt1 = 127;
/*      */     } 
/*  406 */     if (paramInt2 < -128) {
/*  407 */       paramInt2 = -128;
/*      */     }
/*  409 */     else if (paramInt2 > 127) {
/*  410 */       paramInt2 = 127;
/*      */     } 
/*  412 */     this.UI_dirty = true;
/*      */     
/*  414 */     if (this.screen_x > 0 && this.screen_y > 0) {
/*  415 */       paramInt3 = 3000 * paramInt3 / this.screen_x;
/*  416 */       paramInt4 = 3000 * paramInt4 / this.screen_y;
/*      */     } else {
/*      */       
/*  419 */       paramInt3 = 3000 * paramInt3 / 1;
/*  420 */       paramInt4 = 3000 * paramInt4 / 1;
/*      */     } 
/*      */ 
/*      */     
/*  424 */     byte[] arrayOfByte = new byte[10];
/*      */     
/*  426 */     arrayOfByte[0] = 2;
/*  427 */     arrayOfByte[1] = 0;
/*  428 */     arrayOfByte[2] = (byte)(paramInt3 & 0xFF);
/*  429 */     arrayOfByte[3] = (byte)(paramInt3 >> 8);
/*  430 */     arrayOfByte[4] = (byte)(paramInt4 & 0xFF);
/*  431 */     arrayOfByte[5] = (byte)(paramInt4 >> 8);
/*  432 */     arrayOfByte[6] = 0;
/*  433 */     arrayOfByte[7] = 0;
/*      */ 
/*      */     
/*  436 */     arrayOfByte[8] = this.mouseBtnState;
/*  437 */     arrayOfByte[9] = 0;
/*      */     
/*  439 */     String str = new String(arrayOfByte);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  444 */     transmit(str);
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
/*      */   public void mouse_mode_change(boolean paramBoolean) {
/*  463 */     boolean bool = paramBoolean ? true : true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseEntered(MouseEvent paramMouseEvent) {
/*  471 */     this.UI_dirty = true;
/*      */     
/*  473 */     super.mouseEntered(paramMouseEvent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverPress(int paramInt) {
/*  481 */     this.UI_dirty = true;
/*  482 */     send_mouse_press(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverRelease(int paramInt) {
/*  491 */     this.UI_dirty = true;
/*  492 */     send_mouse_release(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverClick(int paramInt1, int paramInt2) {
/*  501 */     this.UI_dirty = true;
/*  502 */     send_mouse_click(paramInt1, paramInt2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  508 */     this.mouseBtnState = mouseButtonState(paramInt1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseExited(MouseEvent paramMouseEvent) {
/*  516 */     super.mouseExited(paramMouseEvent);
/*  517 */     setCursor(Cursor.getDefaultCursor());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disable_keyboard() {
/*  525 */     this.disable_kbd = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void enable_keyboard() {
/*  533 */     this.disable_kbd = false;
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
/*      */   
/*      */   public void disable_altlock() {
/*  567 */     this.altlock = false;
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
/*      */   public void enable_altlock() {
/*  579 */     this.altlock = true;
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
/*      */   public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, remcons paramremcons) {
/*  597 */     char[] arrayOfChar = { 'ÿ', 'À' };
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
/*  609 */     super.connect(paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramremcons);
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
/*      */   public synchronized void transmit(String paramString) {
/*  623 */     if (this.out == null || paramString == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  628 */     if (paramString.length() != 0) {
/*  629 */       byte[] arrayOfByte = new byte[paramString.length()];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  638 */       for (byte b = 0; b < paramString.length(); b++) {
/*  639 */         arrayOfByte[b] = (byte)paramString.charAt(b);
/*      */ 
/*      */         
/*  642 */         if (this.dvc_encryption) {
/*  643 */           char c; switch (this.cipher) {
/*      */             case 1:
/*  645 */               c = (char)(this.RC4encrypter.randomValue() & 0xFF);
/*  646 */               arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */               break;
/*      */             case 2:
/*  649 */               c = (char)(this.Aes128encrypter.randomValue() & 0xFF);
/*  650 */               arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */               break;
/*      */             case 3:
/*  653 */               c = (char)(this.Aes256encrypter.randomValue() & 0xFF);
/*  654 */               arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */               break;
/*      */             default:
/*  657 */               c = Character.MIN_VALUE;
/*  658 */               System.out.println("Unknown encryption"); break;
/*      */           } 
/*  660 */           arrayOfByte[b] = (byte)(arrayOfByte[b] & 0xFF);
/*      */         } 
/*      */       } 
/*      */       try {
/*  664 */         this.out.write(arrayOfByte, 0, arrayOfByte.length);
/*      */       } catch (IOException iOException) {
/*      */         
/*  667 */         System.out.println("telnet.transmit() IOException: " + iOException);
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
/*      */   public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {
/*  679 */     byte[] arrayOfByte = new byte[paramInt];
/*  680 */     System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramInt);
/*      */     
/*  682 */     for (byte b = 0; b < paramInt; b++) {
/*      */ 
/*      */       
/*  685 */       if (this.dvc_encryption) {
/*  686 */         char c; switch (this.cipher) {
/*      */           case 1:
/*  688 */             c = (char)(this.RC4encrypter.randomValue() & 0xFF);
/*  689 */             arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */             break;
/*      */           case 2:
/*  692 */             c = (char)(this.Aes128encrypter.randomValue() & 0xFF);
/*  693 */             arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */             break;
/*      */           case 3:
/*  696 */             c = (char)(this.Aes256encrypter.randomValue() & 0xFF);
/*  697 */             arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */             break;
/*      */           default:
/*  700 */             c = Character.MIN_VALUE;
/*  701 */             System.out.println("Unknown encryption"); break;
/*      */         } 
/*  703 */         arrayOfByte[b] = (byte)(arrayOfByte[b] & 0xFF);
/*      */       } 
/*      */     } 
/*      */     
/*      */     try {
/*  708 */       if (null != this.out) {
/*  709 */         this.out.write(arrayOfByte, 0, paramInt);
/*      */       }
/*      */     } catch (IOException iOException) {
/*      */       
/*  713 */       System.out.println("telnet.transmitb() IOException: " + iOException);
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
/*      */   protected String translate_key(KeyEvent paramKeyEvent) {
/*  726 */     String str = "";
/*  727 */     char c = paramKeyEvent.getKeyChar();
/*  728 */     byte b = 0;
/*  729 */     boolean bool = true;
/*      */ 
/*      */ 
/*      */     
/*  733 */     if (this.disable_kbd) {
/*  734 */       return "";
/*      */     }
/*      */     
/*  737 */     if (this.ignore_next_key) {
/*  738 */       this.ignore_next_key = false;
/*  739 */       return "";
/*      */     } 
/*      */     
/*  742 */     this.UI_dirty = true;
/*  743 */     if (paramKeyEvent.isShiftDown()) {
/*  744 */       b = 1;
/*      */     }
/*  746 */     else if (paramKeyEvent.isControlDown()) {
/*  747 */       b = 2;
/*      */     }
/*  749 */     else if (this.altlock || paramKeyEvent.isAltDown()) {
/*  750 */       b = 3;
/*  751 */       if (paramKeyEvent.isAltDown()) {
/*  752 */         paramKeyEvent.consume();
/*      */       }
/*      */     } 
/*      */     
/*  756 */     switch (c) {
/*      */       
/*      */       case '\033':
/*  759 */         bool = false;
/*      */         break;
/*      */ 
/*      */       
/*      */       case '\n':
/*      */       case '\r':
/*  765 */         switch (b) {
/*      */           case 0:
/*  767 */             str = "\r";
/*      */             break;
/*      */           
/*      */           case 1:
/*  771 */             str = "\033[3\r";
/*      */             break;
/*      */           
/*      */           case 2:
/*  775 */             str = "\n";
/*      */             break;
/*      */           
/*      */           case 3:
/*  779 */             str = "\033[1\r";
/*      */             break;
/*      */         } 
/*  782 */         bool = false;
/*      */         break;
/*      */ 
/*      */       
/*      */       case '\b':
/*  787 */         switch (b) {
/*      */           case 0:
/*  789 */             str = "\b";
/*      */             break;
/*      */           
/*      */           case 1:
/*  793 */             str = "\033[3\b";
/*      */             break;
/*      */           
/*      */           case 2:
/*  797 */             str = "";
/*      */             break;
/*      */           
/*      */           case 3:
/*  801 */             str = "\033[1\b";
/*      */             break;
/*      */         } 
/*  804 */         bool = false;
/*      */         break;
/*      */       
/*      */       default:
/*  808 */         str = super.translate_key(paramKeyEvent);
/*      */         break;
/*      */     } 
/*      */     
/*  812 */     if (bool == true && str.length() != 0 && b == 3) {
/*  813 */       str = "\033[1" + str;
/*      */     }
/*  815 */     return str;
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
/*      */   protected String translate_special_key(KeyEvent paramKeyEvent) {
/*  829 */     String str = "";
/*  830 */     boolean bool = true;
/*  831 */     byte b = 0;
/*      */     
/*  833 */     if (this.disable_kbd) {
/*  834 */       return "";
/*      */     }
/*      */ 
/*      */     
/*  838 */     this.UI_dirty = true;
/*  839 */     if (paramKeyEvent.isShiftDown()) {
/*  840 */       b = 1;
/*      */     }
/*  842 */     else if (paramKeyEvent.isControlDown()) {
/*  843 */       b = 2;
/*      */     }
/*  845 */     else if (this.altlock || paramKeyEvent.isAltDown()) {
/*  846 */       b = 3;
/*      */     } 
/*      */     
/*  849 */     switch (paramKeyEvent.getKeyCode()) {
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
/*      */       case 27:
/*  881 */         str = "\033";
/*      */         break;
/*      */       
/*      */       case 9:
/*  885 */         paramKeyEvent.consume();
/*  886 */         str = "\t";
/*      */         break;
/*      */       
/*      */       case 127:
/*  890 */         if (paramKeyEvent.isControlDown() && (this.altlock || paramKeyEvent.isAltDown())) {
/*      */           
/*  892 */           send_ctrl_alt_del();
/*  893 */           return "";
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  898 */         if (System.getProperty("java.version", "0").compareTo("1.4.2") < 0) {
/*  899 */           str = "";
/*      */         }
/*      */         break;
/*      */       
/*      */       case 36:
/*  904 */         str = "\033[H";
/*      */         break;
/*      */       
/*      */       case 35:
/*  908 */         str = "\033[F";
/*      */         break;
/*      */       
/*      */       case 33:
/*  912 */         str = "\033[I";
/*      */         break;
/*      */       
/*      */       case 34:
/*  916 */         str = "\033[G";
/*      */         break;
/*      */       
/*      */       case 155:
/*  920 */         str = "\033[L";
/*      */         break;
/*      */       
/*      */       case 38:
/*  924 */         str = "\033[A";
/*      */         break;
/*      */       
/*      */       case 40:
/*  928 */         str = "\033[B";
/*      */         break;
/*      */       
/*      */       case 37:
/*  932 */         str = "\033[D";
/*      */         break;
/*      */       
/*      */       case 39:
/*  936 */         str = "\033[C";
/*      */         break;
/*      */       
/*      */       case 112:
/*  940 */         switch (b) {
/*      */           case 0:
/*  942 */             str = "\033[M";
/*      */             break;
/*      */           
/*      */           case 1:
/*  946 */             str = "\033[Y";
/*      */             break;
/*      */           
/*      */           case 2:
/*  950 */             str = "\033[k";
/*      */             break;
/*      */           
/*      */           case 3:
/*  954 */             str = "\033[w";
/*      */             break;
/*      */         } 
/*  957 */         paramKeyEvent.consume();
/*  958 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 113:
/*  962 */         switch (b) {
/*      */           case 0:
/*  964 */             str = "\033[N";
/*      */             break;
/*      */           
/*      */           case 1:
/*  968 */             str = "\033[Z";
/*      */             break;
/*      */           
/*      */           case 2:
/*  972 */             str = "\033[l";
/*      */             break;
/*      */           
/*      */           case 3:
/*  976 */             str = "\033[x";
/*      */             break;
/*      */         } 
/*  979 */         paramKeyEvent.consume();
/*  980 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 114:
/*  984 */         switch (b) {
/*      */           case 0:
/*  986 */             str = "\033[O";
/*      */             break;
/*      */           
/*      */           case 1:
/*  990 */             str = "\033[a";
/*      */             break;
/*      */           
/*      */           case 2:
/*  994 */             str = "\033[m";
/*      */             break;
/*      */           
/*      */           case 3:
/*  998 */             str = "\033[y";
/*      */             break;
/*      */         } 
/* 1001 */         paramKeyEvent.consume();
/* 1002 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 115:
/* 1006 */         switch (b) {
/*      */           case 0:
/* 1008 */             str = "\033[P";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1012 */             str = "\033[b";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1016 */             str = "\033[n";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1020 */             str = "\033[z";
/*      */             break;
/*      */         } 
/* 1023 */         paramKeyEvent.consume();
/* 1024 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 116:
/* 1028 */         switch (b) {
/*      */           case 0:
/* 1030 */             str = "\033[Q";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1034 */             str = "\033[c";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1038 */             str = "\033[o";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1042 */             str = "\033[@";
/*      */             break;
/*      */         } 
/* 1045 */         paramKeyEvent.consume();
/* 1046 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 117:
/* 1050 */         switch (b) {
/*      */           case 0:
/* 1052 */             str = "\033[R";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1056 */             str = "\033[d";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1060 */             str = "\033[p";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1064 */             str = "\033[[";
/*      */             break;
/*      */         } 
/* 1067 */         paramKeyEvent.consume();
/* 1068 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 118:
/* 1072 */         switch (b) {
/*      */           case 0:
/* 1074 */             str = "\033[S";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1078 */             str = "\033[e";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1082 */             str = "\033[q";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1086 */             str = "\033[\\";
/*      */             break;
/*      */         } 
/* 1089 */         paramKeyEvent.consume();
/* 1090 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 119:
/* 1094 */         switch (b) {
/*      */           case 0:
/* 1096 */             str = "\033[T";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1100 */             str = "\033[f";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1104 */             str = "\033[r";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1108 */             str = "\033[]";
/*      */             break;
/*      */         } 
/* 1111 */         paramKeyEvent.consume();
/* 1112 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 120:
/* 1116 */         switch (b) {
/*      */           case 0:
/* 1118 */             str = "\033[U";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1122 */             str = "\033[g";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1126 */             str = "\033[s";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1130 */             str = "\033[^";
/*      */             break;
/*      */         } 
/* 1133 */         paramKeyEvent.consume();
/* 1134 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 121:
/* 1138 */         switch (b) {
/*      */           case 0:
/* 1140 */             str = "\033[V";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1144 */             str = "\033[h";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1148 */             str = "\033[t";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1152 */             str = "\033[_";
/*      */             break;
/*      */         } 
/* 1155 */         paramKeyEvent.consume();
/* 1156 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 122:
/* 1160 */         switch (b) {
/*      */           case 0:
/* 1162 */             str = "\033[W";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1166 */             str = "\033[i";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1170 */             str = "\033[u";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1174 */             str = "\033[`";
/*      */             break;
/*      */         } 
/* 1177 */         paramKeyEvent.consume();
/* 1178 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 123:
/* 1182 */         switch (b) {
/*      */           case 0:
/* 1184 */             str = "\033[X";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1188 */             str = "\033[j";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1192 */             str = "\033[v";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1196 */             str = "\033['";
/*      */             break;
/*      */         } 
/* 1199 */         paramKeyEvent.consume();
/* 1200 */         bool = false;
/*      */         break;
/*      */       
/*      */       default:
/* 1204 */         bool = false;
/* 1205 */         str = super.translate_special_key(paramKeyEvent);
/*      */         break;
/*      */     } 
/*      */     
/* 1209 */     if (str.length() != 0 && 
/* 1210 */       bool == true) {
/* 1211 */       switch (b) {
/*      */         case 1:
/* 1213 */           str = "\033[3" + str;
/*      */           break;
/*      */         
/*      */         case 2:
/* 1217 */           str = "\033[2" + str;
/*      */           break;
/*      */         
/*      */         case 3:
/* 1221 */           str = "\033[1" + str;
/*      */           break;
/*      */       } 
/*      */ 
/*      */     
/*      */     }
/* 1227 */     return str;
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
/*      */   protected String translate_special_key_release(KeyEvent paramKeyEvent) {
/* 1251 */     String str = "";
/* 1252 */     int i = 0;
/*      */ 
/*      */     
/* 1255 */     if (paramKeyEvent.isShiftDown()) {
/* 1256 */       i = 1;
/*      */     }
/*      */     
/* 1259 */     if (this.altlock || paramKeyEvent.isAltDown()) {
/* 1260 */       i += true;
/*      */     }
/*      */     
/* 1263 */     if (paramKeyEvent.isControlDown()) {
/* 1264 */       i += true;
/*      */     }
/*      */     
/* 1267 */     switch (paramKeyEvent.getKeyCode()) {
/*      */       case 243:
/*      */       case 244:
/*      */       case 263:
/* 1271 */         i += 128;
/*      */         break;
/*      */       case 29:
/* 1274 */         i += 136;
/*      */         break;
/*      */       case 28:
/*      */       case 256:
/*      */       case 257:
/* 1279 */         i += 144;
/*      */         break;
/*      */       case 241:
/*      */       case 242:
/*      */       case 245:
/* 1284 */         i += 152;
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1296 */     if (i > 127) {
/* 1297 */       str = "" + (char)i;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 1303 */       str = "";
/*      */     } 
/*      */     
/* 1306 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_ctrl_alt_del() {
/* 1317 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1319 */     arrayOfByte[2] = 5;
/* 1320 */     arrayOfByte[4] = 76;
/* 1321 */     String str1 = new String(arrayOfByte);
/* 1322 */     transmit(str1);
/*      */     
/*      */     try {
/* 1325 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1328 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1331 */     arrayOfByte[4] = 0;
/* 1332 */     String str2 = new String(arrayOfByte);
/* 1333 */     transmit(str2);
/*      */     
/*      */     try {
/* 1336 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1339 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1342 */     arrayOfByte[2] = 0;
/* 1343 */     String str3 = new String(arrayOfByte);
/* 1344 */     transmit(str3);
/*      */     
/* 1346 */     requestFocus();
/*      */   }
/*      */ 
/*      */   
/*      */   public void send_num_lock() {
/* 1351 */     System.out.println("sending num lock");
/* 1352 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1354 */     arrayOfByte[4] = 83;
/* 1355 */     String str1 = new String(arrayOfByte);
/* 1356 */     transmit(str1);
/*      */     
/*      */     try {
/* 1359 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1362 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1365 */     arrayOfByte[4] = 0;
/* 1366 */     String str2 = new String(arrayOfByte);
/* 1367 */     transmit(str2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void send_caps_lock() {
/* 1372 */     System.out.println("sending caps lock");
/* 1373 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1375 */     arrayOfByte[4] = 57;
/* 1376 */     String str1 = new String(arrayOfByte);
/* 1377 */     transmit(str1);
/*      */     
/*      */     try {
/* 1380 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1383 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1386 */     arrayOfByte[4] = 0;
/* 1387 */     String str2 = new String(arrayOfByte);
/* 1388 */     transmit(str2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_ctrl_alt_back() {
/* 1397 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1399 */     arrayOfByte[2] = 5;
/* 1400 */     arrayOfByte[4] = 42;
/* 1401 */     String str1 = new String(arrayOfByte);
/* 1402 */     transmit(str1);
/*      */     
/*      */     try {
/* 1405 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1408 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1411 */     arrayOfByte[4] = 0;
/* 1412 */     String str2 = new String(arrayOfByte);
/* 1413 */     transmit(str2);
/*      */     
/*      */     try {
/* 1416 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1419 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1422 */     arrayOfByte[2] = 0;
/* 1423 */     String str3 = new String(arrayOfByte);
/* 1424 */     transmit(str3);
/*      */     
/* 1426 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_ctrl_alt_fn(int paramInt) {
/* 1435 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 1436 */     byte b = 0;
/*      */ 
/*      */ 
/*      */     
/* 1440 */     switch (paramInt + 1) {
/*      */       case 1:
/* 1442 */         b = 58;
/*      */         break;
/*      */       case 2:
/* 1445 */         b = 59;
/*      */         break;
/*      */       case 3:
/* 1448 */         b = 60;
/*      */         break;
/*      */       case 4:
/* 1451 */         b = 61;
/*      */         break;
/*      */       case 5:
/* 1454 */         b = 62;
/*      */         break;
/*      */       case 6:
/* 1457 */         b = 63;
/*      */         break;
/*      */       case 7:
/* 1460 */         b = 64;
/*      */         break;
/*      */       case 8:
/* 1463 */         b = 65;
/*      */         break;
/*      */       case 9:
/* 1466 */         b = 66;
/*      */         break;
/*      */       case 10:
/* 1469 */         b = 67;
/*      */         break;
/*      */       case 11:
/* 1472 */         b = 68;
/*      */         break;
/*      */       case 12:
/* 1475 */         b = 69;
/*      */         break;
/*      */       default:
/* 1478 */         b = 64;
/*      */         break;
/*      */     } 
/*      */     
/* 1482 */     arrayOfByte[2] = 5;
/* 1483 */     arrayOfByte[4] = b;
/*      */     
/* 1485 */     String str1 = new String(arrayOfByte);
/* 1486 */     transmit(str1);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1492 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1495 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1498 */     arrayOfByte[4] = 0;
/* 1499 */     String str2 = new String(arrayOfByte);
/* 1500 */     transmit(str2);
/*      */     
/*      */     try {
/* 1503 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1506 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1509 */     arrayOfByte[2] = 0;
/* 1510 */     String str3 = new String(arrayOfByte);
/* 1511 */     transmit(str3);
/*      */     
/* 1513 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_alt_fn(int paramInt) {
/* 1522 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 1523 */     byte b = 0;
/*      */ 
/*      */ 
/*      */     
/* 1527 */     switch (paramInt + 1) {
/*      */       case 1:
/* 1529 */         b = 58;
/*      */         break;
/*      */       case 2:
/* 1532 */         b = 59;
/*      */         break;
/*      */       case 3:
/* 1535 */         b = 60;
/*      */         break;
/*      */       case 4:
/* 1538 */         b = 61;
/*      */         break;
/*      */       case 5:
/* 1541 */         b = 62;
/*      */         break;
/*      */       case 6:
/* 1544 */         b = 63;
/*      */         break;
/*      */       case 7:
/* 1547 */         b = 64;
/*      */         break;
/*      */       case 8:
/* 1550 */         b = 65;
/*      */         break;
/*      */       case 9:
/* 1553 */         b = 66;
/*      */         break;
/*      */       case 10:
/* 1556 */         b = 67;
/*      */         break;
/*      */       case 11:
/* 1559 */         b = 68;
/*      */         break;
/*      */       case 12:
/* 1562 */         b = 69;
/*      */         break;
/*      */       default:
/* 1565 */         b = 64;
/*      */         break;
/*      */     } 
/*      */     
/* 1569 */     arrayOfByte[2] = 4;
/* 1570 */     arrayOfByte[4] = b;
/*      */     
/* 1572 */     String str1 = new String(arrayOfByte);
/* 1573 */     transmit(str1);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1579 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1582 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1585 */     arrayOfByte[4] = 0;
/* 1586 */     String str2 = new String(arrayOfByte);
/* 1587 */     transmit(str2);
/*      */     
/*      */     try {
/* 1590 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1593 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1596 */     arrayOfByte[2] = 0;
/* 1597 */     String str3 = new String(arrayOfByte);
/* 1598 */     transmit(str3);
/*      */     
/* 1600 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendMomPress() {
/* 1607 */     this.post_complete = false;
/* 1608 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1610 */     arrayOfByte[0] = 0;
/* 1611 */     arrayOfByte[1] = 0;
/* 1612 */     arrayOfByte[2] = 0;
/* 1613 */     arrayOfByte[3] = 0;
/*      */     
/* 1615 */     String str = new String(arrayOfByte);
/* 1616 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendPressHold() {
/* 1624 */     this.post_complete = false;
/* 1625 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1627 */     arrayOfByte[0] = 0;
/* 1628 */     arrayOfByte[1] = 0;
/* 1629 */     arrayOfByte[2] = 1;
/* 1630 */     arrayOfByte[3] = 0;
/*      */     
/* 1632 */     String str = new String(arrayOfByte);
/* 1633 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendPowerCycle() {
/* 1641 */     this.post_complete = false;
/* 1642 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1644 */     arrayOfByte[0] = 0;
/* 1645 */     arrayOfByte[1] = 0;
/* 1646 */     arrayOfByte[2] = 2;
/* 1647 */     arrayOfByte[3] = 0;
/*      */     
/* 1649 */     String str = new String(arrayOfByte);
/* 1650 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendSystemReset() {
/* 1658 */     this.post_complete = false;
/* 1659 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1661 */     arrayOfByte[0] = 0;
/* 1662 */     arrayOfByte[1] = 0;
/* 1663 */     arrayOfByte[2] = 3;
/* 1664 */     arrayOfByte[3] = 0;
/*      */     
/* 1666 */     String str = new String(arrayOfByte);
/* 1667 */     transmit(str);
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
/*      */   public void send_mouse_press(int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_mouse_release(int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_mouse_click(int paramInt1, int paramInt2) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_mouse_byte(int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void refresh_screen() {
/* 1715 */     byte[] arrayOfByte = new byte[2];
/*      */     
/* 1717 */     arrayOfByte[0] = 5;
/* 1718 */     arrayOfByte[1] = 0;
/* 1719 */     String str = new String(arrayOfByte);
/*      */     
/* 1721 */     transmit(str);
/*      */     
/* 1723 */     requestFocus();
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
/*      */   public void send_keep_alive_msg() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String byteToHex(byte paramByte) {
/* 1745 */     StringBuffer stringBuffer = new StringBuffer();
/* 1746 */     stringBuffer.append(toHexChar(paramByte >>> 4 & 0xF));
/* 1747 */     stringBuffer.append(toHexChar(paramByte & 0xF));
/* 1748 */     return stringBuffer.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static String intToHex(int paramInt) {
/* 1754 */     byte b = (byte)paramInt;
/* 1755 */     return byteToHex(b);
/*      */   }
/*      */ 
/*      */   
/*      */   public static String intToHex4(int paramInt) {
/* 1760 */     StringBuffer stringBuffer = new StringBuffer();
/* 1761 */     stringBuffer.append(byteToHex((byte)(paramInt / 256)));
/* 1762 */     stringBuffer.append(byteToHex((byte)(paramInt & 0xFF)));
/* 1763 */     return stringBuffer.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static String charToHex(char paramChar) {
/* 1769 */     byte b = (byte)paramChar;
/* 1770 */     return byteToHex(b);
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
/*      */   public static char toHexChar(int paramInt) {
/* 1782 */     if (0 <= paramInt && paramInt <= 9) {
/* 1783 */       return (char)(48 + paramInt);
/*      */     }
/*      */     
/* 1786 */     return (char)(65 + paramInt - 10);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void set_framerate(int paramInt) {
/* 1792 */     framerate = paramInt;
/* 1793 */     this.screen.set_framerate(paramInt);
/* 1794 */     set_status(3, "" + framerate);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void show_error(String paramString) {
/* 1835 */     System.out.println("dvc:" + paramString + ": state " + dvc_decoder_state + " code " + dvc_code);
/* 1836 */     System.out.println("dvc:error at byte count " + count_bytes);
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
/*      */   final void cache_reset() {
/* 1863 */     dvc_cc_active = 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int cache_lru(int paramInt) {
/* 1874 */     int j = dvc_cc_active;
/* 1875 */     int i = 0;
/* 1876 */     boolean bool = false;
/*      */     
/*      */     byte b;
/*      */     
/* 1880 */     for (b = 0; b < j; b++) {
/* 1881 */       if (paramInt == dvc_cc_color[b]) {
/*      */ 
/*      */         
/* 1884 */         i = b;
/*      */         
/* 1886 */         bool = true;
/*      */         break;
/*      */       } 
/* 1889 */       if (dvc_cc_usage[b] == j - 1) {
/* 1890 */         i = b;
/*      */       }
/*      */     } 
/*      */     
/* 1894 */     int k = dvc_cc_usage[i];
/*      */     
/* 1896 */     if (!bool) {
/*      */       
/* 1898 */       if (j < 17) {
/*      */         
/* 1900 */         i = j;
/*      */         
/* 1902 */         k = j;
/*      */         
/* 1904 */         dvc_cc_active = ++j;
/*      */         
/* 1906 */         if (dvc_cc_active < 2) {
/*      */ 
/*      */ 
/*      */           
/* 1910 */           dvc_pixcode = 38;
/*      */         }
/* 1912 */         else if (dvc_cc_active == 2) {
/* 1913 */           dvc_pixcode = 4;
/* 1914 */         } else if (dvc_cc_active == 3) {
/* 1915 */           dvc_pixcode = 5;
/* 1916 */         } else if (dvc_cc_active < 6) {
/* 1917 */           dvc_pixcode = 6;
/* 1918 */         } else if (dvc_cc_active < 10) {
/* 1919 */           dvc_pixcode = 7;
/*      */         } else {
/* 1921 */           dvc_pixcode = 32;
/* 1922 */         }  next_1[31] = dvc_pixcode;
/*      */       } 
/*      */ 
/*      */       
/* 1926 */       dvc_cc_color[i] = paramInt;
/*      */     } 
/*      */     
/* 1929 */     dvc_cc_block[i] = 1;
/*      */ 
/*      */     
/* 1932 */     for (b = 0; b < j; b++) {
/* 1933 */       if (dvc_cc_usage[b] < k) {
/* 1934 */         dvc_cc_usage[b] = dvc_cc_usage[b] + 1;
/*      */       }
/*      */     } 
/* 1937 */     dvc_cc_usage[i] = 0;
/* 1938 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int cache_find(int paramInt) {
/* 1948 */     int i = dvc_cc_active;
/*      */     
/* 1950 */     for (byte b = 0; b < i; b++) {
/* 1951 */       if (paramInt == dvc_cc_usage[b]) {
/*      */         
/* 1953 */         int j = dvc_cc_color[b];
/* 1954 */         byte b1 = b;
/*      */         
/* 1956 */         for (b = 0; b < i; b++) {
/* 1957 */           if (dvc_cc_usage[b] < paramInt) {
/* 1958 */             dvc_cc_usage[b] = dvc_cc_usage[b] + 1;
/*      */           }
/*      */         } 
/* 1961 */         dvc_cc_usage[b1] = 0;
/* 1962 */         dvc_cc_block[b1] = 1;
/* 1963 */         return j;
/*      */       } 
/*      */     } 
/* 1966 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void cache_prune() {
/* 1975 */     int i = dvc_cc_active;
/*      */ 
/*      */     
/* 1978 */     for (byte b = 0; b < i; ) {
/* 1979 */       int j = dvc_cc_block[b];
/* 1980 */       if (j == 0) {
/*      */         
/* 1982 */         i--;
/* 1983 */         dvc_cc_block[b] = dvc_cc_block[i];
/* 1984 */         dvc_cc_color[b] = dvc_cc_color[i];
/* 1985 */         dvc_cc_usage[b] = dvc_cc_usage[i];
/*      */         continue;
/*      */       } 
/* 1988 */       dvc_cc_block[b] = dvc_cc_block[b] - 1;
/* 1989 */       b++;
/*      */     } 
/*      */     
/* 1992 */     dvc_cc_active = i;
/* 1993 */     if (dvc_cc_active < 2) {
/*      */       
/* 1995 */       dvc_pixcode = 38;
/*      */     }
/* 1997 */     else if (dvc_cc_active == 2) {
/* 1998 */       dvc_pixcode = 4;
/* 1999 */     } else if (dvc_cc_active == 3) {
/* 2000 */       dvc_pixcode = 5;
/* 2001 */     } else if (dvc_cc_active < 6) {
/* 2002 */       dvc_pixcode = 6;
/* 2003 */     } else if (dvc_cc_active < 10) {
/* 2004 */       dvc_pixcode = 7;
/*      */     } else {
/* 2006 */       dvc_pixcode = 32;
/* 2007 */     }  next_1[31] = dvc_pixcode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void next_block(int paramInt) {
/* 2016 */     boolean bool = true;
/* 2017 */     if (!video_detected) {
/* 2018 */       bool = false;
/*      */     }
/*      */     
/* 2021 */     if (dvc_pixel_count != 0)
/*      */     {
/* 2023 */       if (dvc_y_clipped > 0 && dvc_lasty == dvc_size_y) {
/*      */ 
/*      */         
/* 2026 */         int m = this.color_remap_table[0];
/* 2027 */         for (int k = dvc_y_clipped; k < 256; k++) {
/* 2028 */           block[k] = m;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/* 2033 */     dvc_pixel_count = 0;
/* 2034 */     dvc_next_state = 1;
/*      */     
/* 2036 */     int i = dvc_lastx * this.blockWidth;
/* 2037 */     int j = dvc_lasty * this.blockHeight;
/* 2038 */     while (paramInt != 0) {
/* 2039 */       if (bool) {
/* 2040 */         this.screen.paste_array(block, i, j, 16, this.blockHeight);
/*      */       }
/*      */       
/* 2043 */       dvc_lastx++;
/* 2044 */       i += 16;
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
/* 2061 */       if (dvc_lastx >= dvc_size_x)
/*      */         break; 
/* 2063 */       paramInt--;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void init_reversal() {
/* 2074 */     for (byte b = 0; b < 'Ā'; b++) {
/* 2075 */       byte b2 = 8;
/* 2076 */       int k = 8;
/* 2077 */       int i = b;
/* 2078 */       int j = 0;
/* 2079 */       for (byte b1 = 0; b1 < 8; b1++) {
/* 2080 */         j <<= 1;
/* 2081 */         if ((i & 0x1) == 1) {
/* 2082 */           if (b2 > b1)
/* 2083 */             b2 = b1; 
/* 2084 */           j |= 0x1;
/* 2085 */           k = 7 - b1;
/*      */         } 
/* 2087 */         i >>= 1;
/*      */       } 
/* 2089 */       dvc_reversal[b] = j;
/* 2090 */       dvc_right[b] = b2;
/* 2091 */       dvc_left[b] = k;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int add_bits(char paramChar) {
/* 2099 */     dvc_zero_count += dvc_right[paramChar];
/*      */ 
/*      */     
/* 2102 */     char c = paramChar;
/* 2103 */     dvc_ib_acc |= c << dvc_ib_bcnt;
/*      */     
/* 2105 */     dvc_ib_bcnt += 8;
/*      */     
/* 2107 */     if (dvc_zero_count > 30) {
/*      */ 
/*      */       
/* 2110 */       if (!debug_msgs || 
/* 2111 */         dvc_decoder_state != 38 || fatal_count >= 40 || fatal_count > 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2119 */       dvc_next_state = 43;
/* 2120 */       dvc_decoder_state = 43;
/* 2121 */       return 4;
/*      */     } 
/*      */     
/* 2124 */     if (paramChar != '\000') {
/* 2125 */       dvc_zero_count = dvc_left[paramChar];
/*      */     }
/* 2127 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int get_bits(int paramInt) {
/* 2136 */     if (paramInt == 1) {
/* 2137 */       dvc_code = dvc_ib_acc & 0x1;
/* 2138 */       dvc_ib_acc >>= 1;
/* 2139 */       dvc_ib_bcnt--;
/* 2140 */       return 0;
/*      */     } 
/*      */ 
/*      */     
/* 2144 */     if (paramInt == 0) {
/* 2145 */       return 0;
/*      */     }
/*      */     
/* 2148 */     int i = dvc_ib_acc & dvc_getmask[paramInt];
/*      */ 
/*      */     
/* 2151 */     dvc_ib_bcnt -= paramInt;
/*      */ 
/*      */     
/* 2154 */     dvc_ib_acc >>= paramInt;
/*      */ 
/*      */     
/* 2157 */     i = dvc_reversal[i];
/*      */ 
/*      */     
/* 2160 */     i >>= 8 - paramInt;
/*      */     
/* 2162 */     dvc_code = i;
/*      */ 
/*      */     
/* 2165 */     return 0;
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
/*      */   int process_bits(char paramChar) {
/* 2179 */     boolean bool = true;
/*      */     
/* 2181 */     byte b = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2186 */     add_bits(paramChar);
/* 2187 */     dvc_new_bits = paramChar;
/* 2188 */     count_bytes++;
/* 2189 */     int i = 0;
/*      */ 
/*      */     
/* 2192 */     while (!b) {
/* 2193 */       byte b1; i = bits_to_read[dvc_decoder_state];
/*      */       
/* 2195 */       if (i > dvc_ib_bcnt) {
/*      */         
/* 2197 */         b = 0;
/*      */ 
/*      */         
/*      */         break;
/*      */       } 
/*      */ 
/*      */       
/* 2204 */       int j = get_bits(i);
/* 2205 */       dvc_counter_bits += i;
/*      */ 
/*      */       
/* 2208 */       if (dvc_code == 0) {
/* 2209 */         dvc_next_state = next_0[dvc_decoder_state];
/*      */       } else {
/* 2211 */         dvc_next_state = next_1[dvc_decoder_state];
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2216 */       switch (dvc_decoder_state) {
/*      */ 
/*      */         
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 32:
/* 2225 */           if (dvc_cc_active == 1) {
/* 2226 */             dvc_code = dvc_cc_usage[0];
/*      */           }
/* 2228 */           else if (dvc_decoder_state == 4) {
/* 2229 */             dvc_code = 0;
/* 2230 */           } else if (dvc_decoder_state == 3) {
/* 2231 */             dvc_code = 1;
/* 2232 */           } else if (dvc_code != 0) {
/* 2233 */             dvc_code++;
/*      */           } 
/* 2235 */           dvc_color = cache_find(dvc_code);
/* 2236 */           if (dvc_color == -1) {
/*      */ 
/*      */ 
/*      */             
/* 2240 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2245 */           dvc_last_color = this.color_remap_table[dvc_color];
/*      */           
/* 2247 */           if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2248 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else {
/*      */             
/* 2252 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2257 */           dvc_pixel_count++;
/*      */           break;
/*      */         
/*      */         case 12:
/* 2261 */           if (dvc_code == 7) {
/* 2262 */             dvc_next_state = 14; break;
/* 2263 */           }  if (dvc_code == 6) {
/* 2264 */             dvc_next_state = 13;
/*      */             break;
/*      */           } 
/* 2267 */           dvc_code += 2;
/* 2268 */           for (b1 = 0; b1 < dvc_code; b1++) {
/*      */             
/* 2270 */             if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2271 */               block[dvc_pixel_count] = dvc_last_color;
/*      */             }
/*      */             else {
/*      */               
/* 2275 */               dvc_next_state = 38;
/*      */ 
/*      */               
/*      */               break;
/*      */             } 
/*      */             
/* 2281 */             dvc_pixel_count++;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 13:
/* 2286 */           dvc_code += 8;
/*      */         
/*      */         case 14:
/* 2289 */           if (!debug_msgs || 
/* 2290 */             dvc_decoder_state != 14 || dvc_code < 16);
/*      */ 
/*      */ 
/*      */           
/* 2294 */           for (b1 = 0; b1 < dvc_code; b1++) {
/*      */             
/* 2296 */             if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2297 */               block[dvc_pixel_count] = dvc_last_color;
/*      */             }
/*      */             else {
/*      */               
/* 2301 */               dvc_next_state = 38;
/*      */ 
/*      */               
/*      */               break;
/*      */             } 
/*      */             
/* 2307 */             dvc_pixel_count++;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 33:
/* 2312 */           if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2313 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else {
/*      */             
/* 2317 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2322 */           dvc_pixel_count++;
/*      */           break;
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
/*      */         case 35:
/* 2336 */           dvc_next_state = dvc_pixcode;
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 9:
/* 2342 */           dvc_red = dvc_code << this.bitsPerColor * 2;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 41:
/* 2352 */           dvc_green = dvc_code << this.bitsPerColor;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 8:
/* 2361 */           dvc_red = dvc_code << this.bitsPerColor * 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2369 */           dvc_green = dvc_code << this.bitsPerColor;
/*      */ 
/*      */ 
/*      */         
/*      */         case 42:
/* 2374 */           dvc_blue = dvc_code;
/* 2375 */           dvc_color = dvc_red | dvc_green | dvc_blue;
/* 2376 */           j = cache_lru(dvc_color);
/* 2377 */           if (j != 0) {
/* 2378 */             if (!debug_msgs || 
/* 2379 */               count_bytes > 6L);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2388 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2393 */           dvc_last_color = this.color_remap_table[dvc_color];
/*      */           
/* 2395 */           if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2396 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else {
/*      */             
/* 2400 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2405 */           dvc_pixel_count++;
/*      */           break;
/*      */         case 17:
/*      */         case 26:
/* 2409 */           dvc_newx = dvc_code;
/* 2410 */           if (dvc_decoder_state == 17 && dvc_newx > dvc_size_x) {
/* 2411 */             if (debug_msgs);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2416 */             dvc_newx = 0;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 39:
/* 2421 */           dvc_newy = dvc_code;
/* 2422 */           if (this.blockHeight == 16) {
/* 2423 */             dvc_newy &= 0x7F;
/*      */           }
/*      */           
/* 2426 */           dvc_lastx = dvc_newx;
/* 2427 */           dvc_lasty = dvc_newy;
/*      */           
/* 2429 */           if (dvc_lasty <= dvc_size_y || 
/* 2430 */             debug_msgs);
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
/* 2442 */           this.screen.repaint_it(1);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 20:
/* 2447 */           dvc_code = dvc_lastx + dvc_code + 1;
/* 2448 */           if (dvc_code <= dvc_size_x || 
/* 2449 */             debug_msgs);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 21:
/* 2457 */           dvc_lastx = dvc_code;
/* 2458 */           if (this.blockHeight == 16) {
/* 2459 */             dvc_lastx &= 0x7F;
/*      */           }
/* 2461 */           if (dvc_lastx <= dvc_size_x || 
/* 2462 */             debug_msgs);
/*      */           break;
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
/*      */         case 27:
/* 2476 */           if (timeout_count == count_bytes - 1L)
/*      */           {
/*      */             
/* 2479 */             dvc_next_state = 38;
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2489 */           if ((dvc_ib_bcnt & 0x7) != 0)
/* 2490 */             get_bits(dvc_ib_bcnt & 0x7); 
/* 2491 */           timeout_count = count_bytes;
/*      */           
/* 2493 */           this.screen.repaint_it(1);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 24:
/* 2498 */           if (cmd_p_count != 0)
/* 2499 */             cmd_p_buff[cmd_p_count - 1] = cmd_last; 
/* 2500 */           cmd_p_count++;
/*      */           
/* 2502 */           cmd_last = dvc_code;
/*      */           break;
/*      */         
/*      */         case 46:
/* 2506 */           if (dvc_code == 0) {
/*      */ 
/*      */             
/* 2509 */             switch (cmd_last) {
/*      */               case 1:
/* 2511 */                 dvc_next_state = 37;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 2:
/* 2516 */                 dvc_next_state = 44;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 3:
/* 2521 */                 if (cmd_p_count != 0) {
/* 2522 */                   set_framerate(cmd_p_buff[0]); break;
/*      */                 } 
/* 2524 */                 set_framerate(0);
/*      */                 break;
/*      */               case 4:
/* 2527 */                 this.remconsObj.setPwrStatusPower(1);
/*      */                 break;
/*      */               
/*      */               case 5:
/* 2531 */                 this.remconsObj.setPwrStatusPower(0);
/*      */                 
/* 2533 */                 this.screen.clearScreen();
/* 2534 */                 dvc_newx = 50;
/* 2535 */                 dvc_code = 38;
/*      */                 break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               case 6:
/* 2545 */                 this.screen.clearScreen();
/*      */ 
/*      */ 
/*      */                 
/* 2549 */                 if (!video_detected) {
/* 2550 */                   this.screen.clearScreen();
/*      */                 }
/* 2552 */                 set_status(2, getLocalString(12290));
/* 2553 */                 set_status(1, " ");
/*      */                 
/* 2555 */                 set_status(3, " ");
/* 2556 */                 set_status(4, " ");
/* 2557 */                 this.post_complete = false;
/*      */                 break;
/*      */               case 7:
/* 2560 */                 this.ts_type = cmd_p_buff[0];
/*      */                 break;
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
/*      */               case 9:
/* 2586 */                 System.out.println("received keychg and cleared bits\n");
/* 2587 */                 if ((dvc_ib_bcnt & 0x7) != 0) {
/* 2588 */                   get_bits(dvc_ib_bcnt & 0x7);
/*      */                 }
/*      */                 break;
/*      */               
/*      */               case 10:
/* 2593 */                 seize();
/*      */                 break;
/*      */ 
/*      */ 
/*      */               
/*      */               case 11:
/* 2599 */                 System.out.println("Setting bpc to  " + cmd_p_buff[0]);
/* 2600 */                 setBitsPerColor(cmd_p_buff[0]);
/*      */                 break;
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               case 12:
/* 2607 */                 System.out.println("Setting encryption to  " + cmd_p_buff[0]);
/* 2608 */                 setVideoDecryption(cmd_p_buff[0]);
/*      */                 break;
/*      */ 
/*      */ 
/*      */               
/*      */               case 13:
/* 2614 */                 System.out.println("Header received ");
/* 2615 */                 setBitsPerColor(cmd_p_buff[0]);
/*      */ 
/*      */                 
/* 2618 */                 setVideoDecryption(cmd_p_buff[1]);
/*      */                 
/* 2620 */                 this.remconsObj.SetLicensed(cmd_p_buff[2]);
/* 2621 */                 this.remconsObj.SetFlags(cmd_p_buff[3]);
/*      */                 break;
/*      */               case 16:
/* 2624 */                 sendAck();
/*      */                 break;
/*      */               
/*      */               case 128:
/* 2628 */                 this.screen.invalidate();
/* 2629 */                 this.screen.repaint();
/*      */                 break;
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2636 */             cmd_p_count = 0;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 44:
/* 2646 */           printchan = dvc_code;
/* 2647 */           printstring = "";
/*      */           break;
/*      */ 
/*      */         
/*      */         case 45:
/* 2652 */           if (dvc_code != 0) {
/* 2653 */             printstring += (char)dvc_code;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2658 */           switch (printchan) {
/*      */             case 1:
/*      */             case 2:
/* 2661 */               set_status(2 + printchan, printstring);
/*      */               break;
/*      */             case 3:
/* 2664 */               System.out.println(printstring);
/*      */               break;
/*      */             
/*      */             case 4:
/* 2668 */               this.screen.show_text(printstring);
/*      */               break;
/*      */           } 
/*      */           
/* 2672 */           dvc_next_state = 1;
/*      */           break;
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
/*      */         case 0:
/* 2688 */           cache_reset();
/* 2689 */           dvc_pixel_count = 0;
/* 2690 */           dvc_lastx = 0;
/* 2691 */           dvc_lasty = 0;
/* 2692 */           dvc_red = 0;
/* 2693 */           dvc_green = 0;
/* 2694 */           dvc_blue = 0;
/* 2695 */           fatal_count = 0;
/* 2696 */           timeout_count = -1L;
/*      */           
/* 2698 */           cmd_p_count = 0;
/*      */           break;
/*      */         
/*      */         case 38:
/* 2702 */           if (fatal_count == 0) {
/*      */             
/* 2704 */             debug_lastx = dvc_lastx;
/* 2705 */             debug_lasty = dvc_lasty;
/* 2706 */             debug_show_block = 1;
/*      */           } 
/* 2708 */           if (fatal_count == 40)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2714 */             refresh_screen();
/*      */           }
/* 2716 */           if (fatal_count == 11680) {
/* 2717 */             refresh_screen();
/*      */           }
/* 2719 */           fatal_count++;
/* 2720 */           if (fatal_count == 120000)
/*      */           {
/* 2722 */             refresh_screen();
/*      */           }
/* 2724 */           if (fatal_count == 12000000) {
/*      */             
/* 2726 */             refresh_screen();
/* 2727 */             fatal_count = 41;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 34:
/* 2732 */           next_block(1);
/*      */           break;
/*      */         case 29:
/* 2735 */           dvc_code += 2;
/*      */         
/*      */         case 30:
/* 2738 */           next_block(dvc_code);
/*      */           break;
/*      */         
/*      */         case 40:
/* 2742 */           dvc_size_x = dvc_newx;
/* 2743 */           dvc_size_y = dvc_code;
/*      */           break;
/*      */ 
/*      */         
/*      */         case 47:
/* 2748 */           dvc_lastx = 0;
/* 2749 */           dvc_lasty = 0;
/* 2750 */           dvc_pixel_count = 0;
/* 2751 */           cache_reset();
/* 2752 */           this.scale_x = 1;
/* 2753 */           this.scale_y = 1;
/* 2754 */           this.screen_x = dvc_size_x * this.blockWidth;
/* 2755 */           this.screen_y = dvc_size_y * 16 + dvc_code;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2763 */           if (this.screen_x == 0 || this.screen_y == 0) {
/* 2764 */             video_detected = false;
/*      */           } else {
/* 2766 */             video_detected = true;
/*      */           } 
/*      */           
/* 2769 */           if (dvc_code > 0) {
/* 2770 */             dvc_y_clipped = 256 - 16 * dvc_code;
/*      */           } else {
/* 2772 */             dvc_y_clipped = 0;
/*      */           } 
/* 2774 */           if (!video_detected) {
/* 2775 */             this.screen.clearScreen();
/* 2776 */             set_status(2, getLocalString(12290));
/* 2777 */             set_status(1, " ");
/* 2778 */             set_status(3, " ");
/* 2779 */             set_status(4, " ");
/* 2780 */             System.out.println("No video. image_source = " + this.screen.image_source);
/* 2781 */             this.post_complete = false;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2786 */           this.screen.set_abs_dimensions(this.screen_x, this.screen_y);
/* 2787 */           SetHalfHeight();
/* 2788 */           this.mouse_sync.serverScreen(this.screen_x, this.screen_y);
/* 2789 */           set_status(2, getLocalString(12291) + this.screen_x + "x" + this.screen_y);
/* 2790 */           set_status(1, " ");
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 43:
/* 2798 */           if (dvc_next_state != dvc_decoder_state) {
/* 2799 */             dvc_ib_bcnt = 0;
/* 2800 */             dvc_ib_acc = 0;
/* 2801 */             dvc_zero_count = 0;
/* 2802 */             count_bytes = 0L;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 37:
/* 2807 */           return 1;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2814 */       if (dvc_next_state == 2 && dvc_pixel_count == this.blockHeight * this.blockWidth) {
/* 2815 */         next_block(1);
/* 2816 */         cache_prune();
/*      */       } 
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
/* 2845 */       if (dvc_decoder_state == dvc_next_state && dvc_decoder_state != 45 && dvc_decoder_state != 38 && dvc_decoder_state != 43) {
/* 2846 */         System.out.println("Machine hung in state " + dvc_decoder_state);
/* 2847 */         b = 6;
/*      */         continue;
/*      */       } 
/* 2850 */       dvc_decoder_state = dvc_next_state;
/*      */     } 
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
/* 2862 */     return b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean process_dvc(char paramChar) {
/*      */     boolean bool1;
/*      */     boolean bool2;
/* 2872 */     if (dvc_reversal[255] == 0) {
/*      */       
/* 2874 */       System.out.println("dvc initializing");
/*      */ 
/*      */       
/* 2877 */       init_reversal();
/* 2878 */       cache_reset();
/* 2879 */       dvc_decoder_state = 0;
/* 2880 */       dvc_next_state = 0;
/* 2881 */       dvc_zero_count = 0;
/* 2882 */       dvc_ib_acc = 0;
/* 2883 */       dvc_ib_bcnt = 0;
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
/* 2897 */       buildPixelTable(this.bitsPerColor);
/* 2898 */       SetHalfHeight();
/*      */     } 
/*      */     
/* 2901 */     if (!dvc_process_inhibit) {
/* 2902 */       bool1 = process_bits(paramChar);
/*      */     } else {
/* 2904 */       bool1 = false;
/*      */     } 
/* 2906 */     if (!bool1) {
/* 2907 */       bool2 = true;
/*      */     } else {
/* 2909 */       System.out.println("Exit from DVC mode status =" + bool1);
/* 2910 */       System.out.println("Current block at " + dvc_lastx + " " + dvc_lasty);
/* 2911 */       System.out.println("Byte count " + count_bytes);
/* 2912 */       bool2 = true;
/*      */       
/* 2914 */       dvc_decoder_state = 38;
/* 2915 */       dvc_next_state = 38;
/*      */ 
/*      */ 
/*      */       
/* 2919 */       fatal_count = 0;
/* 2920 */       refresh_screen();
/*      */     } 
/* 2922 */     return bool2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void set_sig_colors(int[] paramArrayOfint) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void change_key() {
/* 2935 */     this.RC4encrypter.update_key();
/* 2936 */     super.change_key();
/*      */   }
/*      */ 
/*      */   
/*      */   public void set_mouse_protocol(int paramInt) {
/* 2941 */     this.mouse_protocol = paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2947 */   private static final byte[] cursor_none = new byte[] { 0 };
/*      */ 
/*      */   
/* 2950 */   private static final int[] cursor_outline = new int[] { -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, -8355712, -8355712, -8355712, -8355712, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, -8355712, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0 };
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
/*      */   protected Cursor current_cursor;
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
/*      */   Cursor customCursor(Image paramImage, Point paramPoint, String paramString) {
/* 2976 */     Cursor cursor = null;
/*      */     try {
/* 2978 */       Class clazz = Toolkit.class;
/* 2979 */       Method method = clazz.getMethod("createCustomCursor", new Class[] { Image.class, Point.class, String.class });
/*      */       
/* 2981 */       Toolkit toolkit = Toolkit.getDefaultToolkit();
/* 2982 */       if (method != null) {
/* 2983 */         cursor = (Cursor)method.invoke(toolkit, new Object[] { paramImage, paramPoint, paramString });
/*      */       }
/*      */     } catch (Exception exception) {
/*      */       
/* 2987 */       System.out.println("This JVM cannot create custom cursors");
/*      */     } 
/* 2989 */     return cursor;
/*      */   }
/*      */   
/*      */   Cursor createCursor(int paramInt) {
/*      */     MemoryImageSource memoryImageSource;
/*      */     Image image;
/*      */     int[] arrayOfInt;
/*      */     byte b;
/* 2997 */     String str = System.getProperty("java.version", "0");
/*      */ 
/*      */     
/* 3000 */     Toolkit toolkit = Toolkit.getDefaultToolkit();
/*      */     
/* 3002 */     switch (paramInt) {
/*      */       case 0:
/* 3004 */         return Cursor.getDefaultCursor();
/*      */       case 1:
/* 3006 */         return Cursor.getPredefinedCursor(1);
/*      */       case 2:
/* 3008 */         image = toolkit.createImage(cursor_none);
/*      */         break;
/*      */       
/*      */       case 3:
/* 3012 */         arrayOfInt = new int[1024];
/* 3013 */         arrayOfInt[33] = -8355712; arrayOfInt[32] = -8355712; arrayOfInt[1] = -8355712; arrayOfInt[0] = -8355712;
/* 3014 */         memoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
/* 3015 */         image = createImage(memoryImageSource);
/*      */         break;
/*      */       
/*      */       case 4:
/* 3019 */         arrayOfInt = new int[1024];
/* 3020 */         for (b = 0; b < 21; b++) {
/* 3021 */           for (byte b1 = 0; b1 < 12; b1++) {
/* 3022 */             arrayOfInt[b1 + b * 32] = cursor_outline[b1 + b * 12];
/*      */           }
/*      */         } 
/* 3025 */         memoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
/* 3026 */         image = createImage(memoryImageSource);
/*      */         break;
/*      */       default:
/* 3029 */         System.out.println("createCursor: unknown cursor " + paramInt);
/* 3030 */         return Cursor.getDefaultCursor();
/*      */     } 
/*      */     
/* 3033 */     Cursor cursor = null;
/* 3034 */     if (str.compareTo("1.2") < 0) {
/* 3035 */       System.out.println("This JVM cannot create custom cursors");
/*      */     } else {
/*      */       
/* 3038 */       cursor = customCursor(image, new Point(), "rcCursor");
/*      */     } 
/*      */     
/* 3041 */     return (cursor != null) ? cursor : Cursor.getDefaultCursor();
/*      */   }
/*      */ 
/*      */   
/*      */   public void set_cursor(int paramInt) {
/* 3046 */     this.current_cursor = createCursor(paramInt);
/* 3047 */     setCursor(this.current_cursor);
/*      */   }
/*      */ 
/*      */   
/*      */   private void SetHalfHeight() {
/* 3052 */     if (this.screen_x > 1616) {
/* 3053 */       if (this.remconsObj.halfHeightCapable) {
/* 3054 */         if (8 != this.blockHeight) {
/* 3055 */           System.out.println("Setting halfheight mode on supported system");
/* 3056 */           this.blockHeight = 8;
/* 3057 */           bits_to_read[21] = 8;
/* 3058 */           bits_to_read[17] = 8;
/* 3059 */           bits_to_read[39] = 8;
/* 3060 */           bits_to_read[30] = 8;
/*      */         }
/*      */       
/*      */       }
/* 3064 */       else if (!this.unsupportedVideoModeWarned) {
/*      */         
/* 3066 */         new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8225), getLocalString(8226), false);
/* 3067 */         this.unsupportedVideoModeWarned = true;
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 3072 */     else if (16 != this.blockHeight) {
/* 3073 */       System.out.println("Setting non-halfheight mode");
/* 3074 */       this.blockHeight = 16;
/* 3075 */       bits_to_read[21] = 7;
/* 3076 */       bits_to_read[17] = 7;
/* 3077 */       bits_to_read[39] = 7;
/* 3078 */       bits_to_read[30] = 7;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void buildPixelTable(int paramInt) {
/*      */     byte b;
/* 3089 */     int i = 1 << paramInt * 3;
/*      */ 
/*      */     
/* 3092 */     switch (paramInt) {
/*      */       
/*      */       case 5:
/* 3095 */         for (b = 0; b < i; b++) {
/* 3096 */           this.color_remap_table[b] = (b & 0x1F) << 3;
/* 3097 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0x3E0) << 6;
/* 3098 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0x7C00) << 9;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 4:
/* 3103 */         for (b = 0; b < i; b++) {
/* 3104 */           this.color_remap_table[b] = (b & 0xF) << 4;
/* 3105 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF0) << 8;
/* 3106 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF00) << 12;
/*      */         } 
/*      */         break;
/*      */       case 3:
/* 3110 */         for (b = 0; b < i; b++) {
/* 3111 */           this.color_remap_table[b] = (b & 0xF) << 5;
/* 3112 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF0) << 11;
/* 3113 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF00) << 15;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 2:
/* 3118 */         for (b = 0; b < i; b++) {
/* 3119 */           this.color_remap_table[b] = (b & 0xF) << 6;
/* 3120 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF0) << 15;
/* 3121 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF00) << 18;
/*      */         } 
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setBitsPerColor(int paramInt) {
/* 3131 */     this.bitsPerColor = 5 - (paramInt & 0x3);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3136 */     bits_to_read[8] = this.bitsPerColor;
/* 3137 */     bits_to_read[9] = this.bitsPerColor;
/* 3138 */     bits_to_read[41] = this.bitsPerColor;
/* 3139 */     bits_to_read[42] = this.bitsPerColor;
/*      */     
/* 3141 */     buildPixelTable(this.bitsPerColor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setVideoDecryption(int paramInt) {
/* 3148 */     switch (paramInt) {
/*      */       case 0:
/* 3150 */         this.dvc_encryption = false;
/* 3151 */         this.cipher = 0;
/* 3152 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12292));
/* 3153 */         this.remconsObj.setPwrStatusEnc(0);
/* 3154 */         System.out.println("Setting encryption -> None");
/*      */         return;
/*      */       case 1:
/* 3157 */         this.dvc_encryption = true;
/* 3158 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12293));
/* 3159 */         this.remconsObj.setPwrStatusEnc(1);
/* 3160 */         this.dvc_mode = true;
/* 3161 */         this.cipher = 1;
/* 3162 */         System.out.println("Setting encryption -> RC4 - 128 bit");
/*      */         return;
/*      */       case 2:
/* 3165 */         this.dvc_encryption = true;
/* 3166 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12294));
/* 3167 */         this.remconsObj.setPwrStatusEnc(1);
/* 3168 */         this.dvc_mode = true;
/* 3169 */         this.cipher = 2;
/* 3170 */         System.out.println("Setting encryption -> AES - 128 bit");
/*      */         return;
/*      */       case 3:
/* 3173 */         this.dvc_encryption = true;
/* 3174 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12295));
/* 3175 */         this.remconsObj.setPwrStatusEnc(1);
/* 3176 */         this.dvc_mode = true;
/* 3177 */         this.cipher = 3;
/* 3178 */         System.out.println("Setting encryption -> AES - 256 bit");
/*      */         return;
/*      */     } 
/* 3181 */     this.dvc_encryption = false;
/* 3182 */     this.remconsObj.setPwrStatusEncLabel(getLocalString(12292));
/* 3183 */     this.remconsObj.setPwrStatusEnc(0);
/* 3184 */     System.out.println("Unsupported encryption");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte mouseButtonState(int paramInt) {
/* 3190 */     byte b = 0;
/* 3191 */     switch (paramInt) {
/*      */       case 4:
/* 3193 */         b = (byte)(b | 0x1);
/*      */         break;
/*      */       case 2:
/* 3196 */         b = (byte)(b | 0x4);
/*      */         break;
/*      */       case 1:
/* 3199 */         b = (byte)(b | 0x2);
/*      */         break;
/*      */     } 
/* 3202 */     return b;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getMouseButtonState(MouseEvent paramMouseEvent) {
/* 3207 */     byte b = 0;
/* 3208 */     if ((paramMouseEvent.getModifiersEx() & 0x1000) != 0)
/* 3209 */       b = (byte)(b | 0x2); 
/* 3210 */     if ((paramMouseEvent.getModifiersEx() & 0x800) != 0)
/* 3211 */       b = (byte)(b | 0x4); 
/* 3212 */     if ((paramMouseEvent.getModifiersEx() & 0x400) != 0) {
/* 3213 */       b = (byte)(b | 0x1);
/*      */     }
/*      */     
/* 3216 */     return b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendMouse(MouseEvent paramMouseEvent) {
/* 3224 */     Point point1 = new Point(0, 0);
/* 3225 */     Point point2 = new Point(0, 0);
/*      */     
/* 3227 */     point1 = getAbsMouseCoordinates(paramMouseEvent);
/* 3228 */     char c1 = (char)point1.x;
/* 3229 */     char c2 = (char)point1.y;
/*      */     
/* 3231 */     if ((paramMouseEvent.getModifiersEx() & 0x80) > 0) {
/*      */ 
/*      */ 
/*      */       
/* 3235 */       this.mousePrevPosn.x = c1;
/* 3236 */       this.mousePrevPosn.y = c2;
/*      */     }
/* 3238 */     else if (c1 <= this.screen_x && c2 <= this.screen_y) {
/*      */       
/* 3240 */       point2.x = c1 - this.mousePrevPosn.x;
/* 3241 */       this.mousePrevPosn.y -= c2;
/*      */ 
/*      */       
/* 3244 */       this.mousePrevPosn.x = c1;
/* 3245 */       this.mousePrevPosn.y = c2;
/*      */       
/* 3247 */       int i = point2.x;
/* 3248 */       int j = point2.y;
/*      */       
/* 3250 */       if (i < -127) {
/* 3251 */         i = -127;
/*      */       }
/* 3253 */       else if (i > 127) {
/* 3254 */         i = 127;
/*      */       } 
/* 3256 */       if (j < -127) {
/* 3257 */         j = -127;
/*      */       }
/* 3259 */       else if (j > 127) {
/* 3260 */         j = 127;
/*      */       } 
/*      */       
/* 3263 */       this.UI_dirty = true;
/*      */       
/* 3265 */       if (this.screen_x > 0 && this.screen_y > 0) {
/* 3266 */         c1 = (char)(3000 * c1 / this.screen_x);
/* 3267 */         c2 = (char)(3000 * c2 / this.screen_y);
/*      */       } else {
/*      */         
/* 3270 */         c1 = (char)(3000 * c1 / 1);
/* 3271 */         c2 = (char)(3000 * c2 / 1);
/*      */       } 
/*      */ 
/*      */       
/* 3275 */       byte[] arrayOfByte = new byte[10];
/*      */       
/* 3277 */       arrayOfByte[0] = 2;
/* 3278 */       arrayOfByte[1] = 0;
/* 3279 */       arrayOfByte[2] = (byte)(c1 & 0xFF);
/* 3280 */       arrayOfByte[3] = (byte)(c1 >> 8);
/* 3281 */       arrayOfByte[4] = (byte)(c2 & 0xFF);
/* 3282 */       arrayOfByte[5] = (byte)(c2 >> 8);
/*      */       
/* 3284 */       if (i < 0) {
/* 3285 */         arrayOfByte[6] = (byte)(i & 0xFF);
/*      */       } else {
/*      */         
/* 3288 */         arrayOfByte[6] = (byte)(i & 0xFF);
/*      */       } 
/*      */       
/* 3291 */       if (j < 0) {
/* 3292 */         arrayOfByte[7] = (byte)(j & 0xFF);
/*      */       } else {
/*      */         
/* 3295 */         arrayOfByte[7] = (byte)(j & 0xFF);
/*      */       } 
/* 3297 */       arrayOfByte[8] = getMouseButtonState(paramMouseEvent);
/* 3298 */       arrayOfByte[9] = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3303 */       transmitb(arrayOfByte, arrayOfByte.length);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Point getAbsMouseCoordinates(MouseEvent paramMouseEvent) {
/* 3314 */     Point point = new Point();
/*      */     
/* 3316 */     point.y = paramMouseEvent.getY();
/*      */     
/* 3318 */     point.x = paramMouseEvent.getX();
/* 3319 */     return point;
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
/*      */   public void sendMouseScroll(MouseWheelEvent paramMouseWheelEvent) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendAck() {
/* 3342 */     byte[] arrayOfByte = new byte[2];
/*      */     
/* 3344 */     arrayOfByte[0] = 12;
/* 3345 */     arrayOfByte[1] = 0;
/*      */     
/* 3347 */     String str = new String(arrayOfByte);
/* 3348 */     transmit(str);
/*      */   }
/*      */ 
/*      */   
/*      */   public void requestScreenFocus(MouseEvent paramMouseEvent) {
/* 3353 */     requestFocus();
/*      */   }
/*      */ 
/*      */   
/*      */   public void installKeyboardHook() {
/* 3358 */     this.remconsObj.remconsInstallKeyboardHook();
/*      */   }
/*      */ 
/*      */   
/*      */   public void unInstallKeyboardHook() {
/* 3363 */     this.remconsObj.remconsUnInstallKeyboardHook();
/*      */   }
/*      */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/cim.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */