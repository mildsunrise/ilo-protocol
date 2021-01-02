/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.MemoryImageSource;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class dvcwin
/*     */   extends JPanel
/*     */   implements Runnable
/*     */ {
/*  26 */   protected Image offscreen_image = null;
/*  27 */   protected Image first_image = null;
/*  28 */   protected Graphics offscreen_gc = null;
/*     */   
/*     */   protected MemoryImageSource image_source;
/*     */   
/*     */   protected int screen_x;
/*     */   protected int screen_y;
/*     */   protected int block_y;
/*     */   protected int block_x;
/*     */   protected ColorModel cm;
/*  37 */   protected Image clearScreenImage = null;
/*     */   
/*     */   protected Graphics clearScreenGc;
/*     */   
/*     */   public int[] pixel_buffer;
/*     */   
/*     */   public remcons remconsObj;
/*     */   
/*  45 */   protected Thread screen_updater = null;
/*     */   
/*     */   protected static final int REFRESH_RATE = 15;
/*     */   
/*  49 */   private int refresh_count = 0;
/*  50 */   private int need_to_refresh = 1;
/*  51 */   private int need_to_refresh_r = 1;
/*  52 */   private int need_to_refresh_w = 1;
/*     */   public boolean mirror = false;
/*  54 */   private int frametime = 0;
/*  55 */   private int paint_count = 0;
/*     */   
/*     */   protected boolean updater_running = false;
/*     */   
/*     */   private boolean abs_dimen_initialized = false;
/*     */   private boolean clear_screen = false;
/*     */   private boolean firstResize = true;
/*     */   
/*     */   public dvcwin(int paramInt1, int paramInt2, remcons paramremcons) {
/*  64 */     this.screen_x = paramInt1;
/*  65 */     this.screen_y = paramInt2;
/*     */     
/*  67 */     this.cm = new DirectColorModel(32, 16711680, 65280, 255, 0);
/*     */     
/*  69 */     set_framerate(0);
/*  70 */     this.remconsObj = paramremcons;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFocusTraversable() {
/*  78 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addNotify() {
/*  88 */     super.addNotify();
/*     */     
/*  90 */     if (this.offscreen_image == null) {
/*     */       
/*  92 */       if (this.screen_x == 0 && this.screen_y == 0) {
/*  93 */         this.screen_x = 1;
/*  94 */         this.screen_y = 1;
/*     */       } 
/*     */       
/*  97 */       this.offscreen_image = createImage(this.screen_x, this.screen_y);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean repaint_it(int paramInt) {
/* 115 */     boolean bool = false;
/* 116 */     if (paramInt == 1) {
/* 117 */       this.need_to_refresh_w++;
/*     */     }
/*     */     else {
/*     */       
/* 121 */       int i = this.need_to_refresh_w;
/* 122 */       if (this.need_to_refresh_r != i) {
/* 123 */         this.need_to_refresh_r = i;
/* 124 */         bool = true;
/*     */       } 
/*     */     } 
/* 127 */     return bool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void paintComponent(Graphics paramGraphics) {
/* 138 */     paintComponents(paramGraphics);
/* 139 */     if (paramGraphics == null) {
/* 140 */       System.out.println("dvcwin.paint() g is null");
/*     */       
/*     */       return;
/*     */     } 
/* 144 */     if (this.first_image != null) {
/* 145 */       paramGraphics.drawImage(this.first_image, 0, 0, this);
/*     */       return;
/*     */     } 
/* 148 */     if (this.clearScreenImage != null) {
/* 149 */       paramGraphics.drawImage(this.clearScreenImage, 0, 0, this);
/*     */       return;
/*     */     } 
/* 152 */     if (this.offscreen_image != null) {
/* 153 */       paramGraphics.drawImage(this.offscreen_image, 0, 0, this);
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(Graphics paramGraphics) {
/* 170 */     if (this.offscreen_image == null || null == this.offscreen_gc) {
/* 171 */       this.offscreen_image = createImage((getSize()).width, (getSize()).height);
/*     */       
/* 173 */       this.offscreen_gc = this.offscreen_image.getGraphics();
/*     */     } 
/*     */ 
/*     */     
/* 177 */     if (this.first_image != null) {
/* 178 */       if (null == this.offscreen_gc) {
/* 179 */         System.out.println("Message from offscreen_gc null detection");
/*     */       }
/* 181 */       this.offscreen_gc.drawImage(this.first_image, 0, 0, this);
/*     */     } 
/*     */     
/* 184 */     paramGraphics.drawImage(this.offscreen_image, 0, 0, this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void paste_array(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*     */     byte b2;
/* 199 */     if (8 == paramInt4) {
/* 200 */       b2 = 8;
/*     */     }
/* 202 */     else if (paramInt2 + 16 > this.screen_y) {
/* 203 */       b2 = this.screen_y - paramInt2;
/*     */     } else {
/*     */       
/* 206 */       b2 = 16;
/*     */     } 
/* 208 */     for (byte b1 = 0; b1 < b2; b1++) {
/*     */       try {
/* 210 */         System.arraycopy(paramArrayOfint, b1 * 16, this.pixel_buffer, (paramInt2 + b1) * this.screen_x + paramInt1, paramInt3);
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 215 */       catch (Exception exception) {
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 220 */     this.image_source.newPixels(paramInt1, paramInt2, paramInt3, 16, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set_abs_dimensions(int paramInt1, int paramInt2) {
/* 228 */     if (paramInt1 != this.screen_x || paramInt2 != this.screen_y || false == this.abs_dimen_initialized || this.clear_screen == true) {
/*     */       
/* 230 */       synchronized (this) {
/* 231 */         this.screen_x = paramInt1;
/* 232 */         this.screen_y = paramInt2;
/*     */       } 
/* 234 */       this.clear_screen = false;
/* 235 */       this.abs_dimen_initialized = true;
/*     */ 
/*     */       
/* 238 */       this.offscreen_image = null;
/*     */       
/* 240 */       this.pixel_buffer = new int[this.screen_x * this.screen_y];
/*     */ 
/*     */ 
/*     */       
/* 244 */       this.image_source = new MemoryImageSource(this.screen_x, this.screen_y, this.cm, this.pixel_buffer, 0, this.screen_x);
/* 245 */       if (this.image_source != null);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 250 */       this.image_source.setAnimated(true);
/* 251 */       this.image_source.setFullBufferUpdates(false);
/*     */       
/* 253 */       this.first_image = createImage(this.image_source);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 265 */       invalidate();
/* 266 */       validate();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 275 */       Container container = getParent();
/* 276 */       if (container != null) {
/* 277 */         while (container.getParent() != null) {
/* 278 */           container.invalidate();
/* 279 */           container = container.getParent();
/*     */         } 
/* 281 */         container.invalidate();
/* 282 */         container.validate();
/*     */       } 
/* 284 */       System.gc();
/* 285 */       if (this.firstResize) {
/* 286 */         this.firstResize = false;
/* 287 */         this.remconsObj.ParentApp.dispFrame.pack();
/* 288 */         this.remconsObj.ParentApp.dispFrame.setLocationRelativeTo(null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/*     */     Dimension dimension;
/* 300 */     synchronized (this) {
/* 301 */       dimension = new Dimension(this.screen_x, this.screen_y);
/*     */     } 
/* 303 */     return dimension;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension getMinimumSize() {
/* 312 */     return getPreferredSize();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void show_text(String paramString) {
/* 318 */     if (this.screen_updater == null) {
/* 319 */       System.out.println("Screen is no longer updating");
/*     */       
/*     */       return;
/*     */     } 
/* 323 */     System.out.println("dvcwin:show_text " + paramString);
/* 324 */     if (this.screen_x != 640 || this.screen_y != 100) {
/*     */       
/* 326 */       set_abs_dimensions(640, 100);
/* 327 */       this.image_source = null;
/* 328 */       this.first_image = null;
/* 329 */       this.offscreen_image = null;
/* 330 */       this.offscreen_image = createImage(this.screen_x, this.screen_y);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 338 */     if (this.offscreen_image != null) {
/* 339 */       Graphics graphics = this.offscreen_image.getGraphics();
/*     */ 
/*     */       
/* 342 */       new Color(0); graphics.setColor(Color.black);
/* 343 */       graphics.fillRect(0, 0, this.screen_x, this.screen_y);
/* 344 */       Font font = new Font("Courier", 0, 20);
/* 345 */       new Color(0); graphics.setColor(Color.white);
/* 346 */       graphics.setFont(font);
/* 347 */       graphics.drawString(paramString, 10, 20);
/* 348 */       graphics.drawImage(this.offscreen_image, 0, 0, this);
/*     */       
/* 350 */       graphics.dispose();
/* 351 */       System.gc();
/* 352 */       repaint();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set_framerate(int paramInt) {
/* 361 */     if (paramInt > 0) {
/* 362 */       this.frametime = 1000 / paramInt;
/*     */     } else {
/*     */       
/* 365 */       this.frametime = 66;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 381 */     while (this.updater_running) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 389 */         Thread.sleep(this.frametime);
/*     */       }
/* 391 */       catch (InterruptedException interruptedException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 396 */       if (repaint_it(0))
/*     */       {
/* 398 */         repaint();
/*     */       }
/*     */     } 
/* 401 */     System.out.println("Updater finished running");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void start_updates() {
/* 409 */     this.screen_updater = new Thread(this, "dvcwin");
/* 410 */     this.updater_running = true;
/* 411 */     this.screen_updater.start();
/*     */     
/* 413 */     System.out.println("..screen update thread started..");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void stop_updates() {
/* 421 */     System.out.println("dvcwin.stop_update");
/* 422 */     if (this.screen_updater != null && this.screen_updater.isAlive())
/*     */     {
/*     */ 
/*     */       
/* 426 */       this.updater_running = false;
/*     */     }
/* 428 */     this.screen_x = 0;
/* 429 */     this.screen_y = 0;
/* 430 */     this.screen_updater = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearScreen() {
/* 436 */     if (this.screen_updater == null) {
/* 437 */       System.out.println("Screen is no longer updating");
/*     */       return;
/*     */     } 
/* 440 */     this.clear_screen = true;
/*     */ 
/*     */     
/* 443 */     if (this.screen_x == 0 && this.screen_y == 0) {
/* 444 */       System.out.println("clearScreen() EXCEPTION Screen_x = 0 Screen_y = 0");
/* 445 */       this.screen_x = 1;
/* 446 */       this.screen_y = 1;
/*     */     } 
/*     */     
/* 449 */     set_abs_dimensions(this.screen_x, this.screen_y);
/* 450 */     this.offscreen_image = null;
/* 451 */     this.offscreen_image = createImage(this.screen_x, this.screen_y);
/*     */     
/* 453 */     Graphics graphics = this.offscreen_image.getGraphics();
/* 454 */     new Color(0); graphics.setColor(Color.black);
/* 455 */     graphics.fillRect(0, 0, this.screen_x, this.screen_y);
/* 456 */     graphics.drawImage(this.offscreen_image, 0, 0, this);
/* 457 */     graphics.dispose();
/* 458 */     System.gc();
/* 459 */     repaint();
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/dvcwin.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */