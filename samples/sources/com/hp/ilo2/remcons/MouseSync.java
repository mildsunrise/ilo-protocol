/*      */ package com.hp.ilo2.remcons;
/*      */ 
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.MouseWheelListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MouseSync
/*      */   implements MouseListener, MouseMotionListener, MouseWheelListener, TimerListener
/*      */ {
/*      */   private static final int CMD_START = 0;
/*      */   private static final int CMD_STOP = 1;
/*      */   private static final int CMD_SYNC = 2;
/*      */   private static final int CMD_SERVER_MOVE = 3;
/*      */   private static final int CMD_SERVER_SCREEN = 4;
/*      */   private static final int CMD_SERVER_DISABLE = 5;
/*      */   private static final int CMD_TIMEOUT = 6;
/*      */   private static final int CMD_CLICK = 7;
/*      */   private static final int CMD_ENTER = 8;
/*      */   private static final int CMD_EXIT = 9;
/*      */   private static final int CMD_PRESS = 10;
/*      */   private static final int CMD_RELEASE = 11;
/*      */   private static final int CMD_DRAG = 12;
/*      */   private static final int CMD_MOVE = 13;
/*      */   private static final int CMD_ALIGN = 14;
/*      */   private static final int STATE_INIT = 0;
/*      */   private static final int STATE_SYNC = 1;
/*      */   private static final int STATE_ENABLE = 2;
/*      */   private static final int STATE_DISABLE = 3;
/*      */   private int state;
/*      */   private MouseSyncListener listener;
/*      */   private int server_w;
/*      */   private int server_h;
/*      */   private int server_x;
/*      */   private int server_y;
/*      */   private int client_x;
/*      */   private int client_y;
/*      */   private int client_dx;
/*      */   private int client_dy;
/*      */   private int[] send_dx;
/*      */   private int[] send_dy;
/*      */   private int[] recv_dx;
/*      */   private int[] recv_dy;
/*      */   private int send_dx_index;
/*      */   private int send_dy_index;
/*      */   private static final int SYNC_SUCCESS_COUNT = 2;
/*      */   private static final int SYNC_FAIL_COUNT = 4;
/*      */   private int send_dx_count;
/*      */   private int send_dy_count;
/*      */   private int send_dx_success;
/*      */   private int send_dy_success;
/*      */   private boolean sync_successful;
/*      */   private static final int TIMEOUT_DELAY = 5;
/*      */   private static final int TIMEOUT_MOVE = 200;
/*      */   private static final int TIMEOUT_SYNC = 2000;
/*      */   private Timer timer;
/*      */   public static final int MOUSE_BUTTON_LEFT = 4;
/*      */   public static final int MOUSE_BUTTON_CENTER = 2;
/*      */   public static final int MOUSE_BUTTON_RIGHT = 1;
/*      */   private int pressed_button;
/*      */   private boolean dragging;
/*      */   private Object mutex;
/*      */   private boolean debug_msg = false;
/*      */   
/*      */   public MouseSync(Object paramObject) {
/*  115 */     this.mutex = paramObject;
/*  116 */     this.state = 0;
/*  117 */     state_machine(0, null, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setListener(MouseSyncListener paramMouseSyncListener) {
/*  128 */     this.listener = paramMouseSyncListener;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void enableDebug() {
/*  136 */     this.debug_msg = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disableDebug() {
/*  144 */     this.debug_msg = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void restart() {
/*  152 */     go_state(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void align() {
/*  160 */     state_machine(14, null, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sync() {
/*  168 */     state_machine(2, null, 0, 0);
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
/*      */   public void serverMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  181 */     state_machine(3, null, paramInt1, paramInt2);
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
/*      */   public void serverScreen(int paramInt1, int paramInt2) {
/*  194 */     state_machine(4, null, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverDisabled() {
/*  202 */     state_machine(5, null, 0, 0);
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
/*      */   public void timeout(Object paramObject) {
/*  214 */     state_machine(6, null, 0, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mouseClicked(MouseEvent paramMouseEvent) {
/*  223 */     this.listener.requestScreenFocus(paramMouseEvent);
/*      */     
/*  225 */     this.listener.sendMouse(paramMouseEvent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mouseEntered(MouseEvent paramMouseEvent) {
/*  235 */     this.listener.installKeyboardHook();
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
/*      */   public void mouseExited(MouseEvent paramMouseEvent) {
/*  248 */     this.listener.unInstallKeyboardHook();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mousePressed(MouseEvent paramMouseEvent) {
/*  257 */     this.listener.sendMouse(paramMouseEvent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mouseReleased(MouseEvent paramMouseEvent) {
/*  266 */     this.listener.sendMouse(paramMouseEvent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mouseDragged(MouseEvent paramMouseEvent) {
/*  275 */     this.listener.sendMouse(paramMouseEvent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mouseMoved(MouseEvent paramMouseEvent) {
/*  284 */     this.listener.sendMouse(paramMouseEvent);
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
/*      */   private void move_delay() {
/*      */     try {
/*  299 */       Thread.sleep(5L);
/*  300 */     } catch (InterruptedException interruptedException) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sync_default() {
/*  310 */     int[] arrayOfInt = { 1, 4, 6, 8, 12, 16, 32, 64 };
/*      */     
/*  312 */     this.send_dx = new int[arrayOfInt.length];
/*  313 */     this.send_dy = new int[arrayOfInt.length];
/*  314 */     this.recv_dx = new int[arrayOfInt.length];
/*  315 */     this.recv_dy = new int[arrayOfInt.length];
/*      */     
/*  317 */     for (byte b = 0; b < arrayOfInt.length; b++) {
/*  318 */       this.send_dx[b] = arrayOfInt[b];
/*  319 */       this.send_dy[b] = arrayOfInt[b];
/*  320 */       this.recv_dx[b] = arrayOfInt[b];
/*  321 */       this.recv_dy[b] = arrayOfInt[b];
/*      */     } 
/*      */     
/*  324 */     this.send_dx_index = 0;
/*  325 */     this.send_dy_index = 0;
/*      */     
/*  327 */     this.send_dx_count = 0;
/*  328 */     this.send_dy_count = 0;
/*  329 */     this.send_dx_success = 0;
/*  330 */     this.send_dy_success = 0;
/*  331 */     this.sync_successful = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sync_continue() {
/*  339 */     byte b1 = 1;
/*  340 */     byte b2 = 1;
/*  341 */     int i = 0;
/*  342 */     int j = 0;
/*      */ 
/*      */     
/*  345 */     if (this.server_x > this.server_w / 2) {
/*  346 */       b1 = -1;
/*      */     }
/*  348 */     if (this.server_y < this.server_h / 2) {
/*  349 */       b2 = -1;
/*      */     }
/*      */     
/*  352 */     if (this.send_dx_index >= 0) {
/*  353 */       i = b1 * this.send_dx[this.send_dx_index];
/*      */     }
/*  355 */     if (this.send_dy_index >= 0) {
/*  356 */       j = b2 * this.send_dy[this.send_dy_index];
/*      */     }
/*  358 */     this.listener.serverMove(i, j, this.client_x, this.client_y);
/*  359 */     this.timer.start();
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
/*      */   private void sync_update(int paramInt1, int paramInt2) {
/*  376 */     this.timer.pause();
/*      */ 
/*      */     
/*  379 */     int i = paramInt1 - this.server_x;
/*  380 */     int j = this.server_y - paramInt2;
/*      */     
/*  382 */     this.server_x = paramInt1;
/*  383 */     this.server_y = paramInt2;
/*      */     
/*  385 */     if (i < 0) {
/*  386 */       i = -i;
/*      */     }
/*  388 */     if (j < 0) {
/*  389 */       j = -j;
/*      */     }
/*      */ 
/*      */     
/*  393 */     if (this.send_dx_index >= 0) {
/*  394 */       if (this.recv_dx[this.send_dx_index] == i)
/*      */       {
/*  396 */         this.send_dx_success++;
/*      */       }
/*  398 */       this.recv_dx[this.send_dx_index] = i;
/*  399 */       this.send_dx_count++;
/*  400 */       if (this.send_dx_success >= 2) {
/*      */         
/*  402 */         this.send_dx_index--;
/*  403 */         this.send_dx_success = 0;
/*  404 */         this.send_dx_count = 0;
/*  405 */       } else if (this.send_dx_count >= 4) {
/*      */         
/*  407 */         if (this.debug_msg) {
/*  408 */           System.out.println("no x sync:" + this.send_dx[this.send_dx_index]);
/*      */         }
/*  410 */         go_state(2);
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/*  416 */     if (this.send_dy_index >= 0) {
/*  417 */       if (this.recv_dy[this.send_dy_index] == j)
/*      */       {
/*  419 */         this.send_dy_success++;
/*      */       }
/*  421 */       this.recv_dy[this.send_dy_index] = j;
/*  422 */       this.send_dy_count++;
/*  423 */       if (this.send_dy_success >= 2) {
/*      */         
/*  425 */         this.send_dy_index--;
/*  426 */         this.send_dy_success = 0;
/*  427 */         this.send_dy_count = 0;
/*  428 */       } else if (this.send_dy_count >= 4) {
/*      */         
/*  430 */         if (this.debug_msg) {
/*  431 */           System.out.println("no y sync:" + this.send_dy[this.send_dy_index]);
/*      */         }
/*  433 */         go_state(2);
/*      */         return;
/*      */       } 
/*      */     } 
/*  437 */     if (this.send_dx_index < 0 && this.send_dy_index < 0) {
/*      */       
/*  439 */       for (int k = this.send_dx.length - 1; k >= 0; k--) {
/*  440 */         if (this.recv_dx[k] == 0 || this.recv_dy[k] == 0) {
/*      */           
/*  442 */           if (this.debug_msg);
/*      */ 
/*      */           
/*  445 */           go_state(2);
/*      */           return;
/*      */         } 
/*  448 */         if (k != 0 && (
/*  449 */           this.recv_dx[k] < this.recv_dx[k - 1] || this.recv_dy[k] < this.recv_dy[k - 1])) {
/*      */           
/*  451 */           if (this.debug_msg);
/*      */ 
/*      */           
/*  454 */           go_state(2);
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */       
/*  460 */       this.sync_successful = true;
/*  461 */       this.send_dx_index = 0;
/*  462 */       this.send_dy_index = 0;
/*  463 */       go_state(2);
/*      */     } else {
/*      */       
/*  466 */       sync_continue();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void init_vars() {
/*  475 */     this.server_w = 640;
/*  476 */     this.server_h = 480;
/*  477 */     this.server_x = 0;
/*  478 */     this.server_y = 0;
/*  479 */     this.client_x = 0;
/*  480 */     this.client_y = 0;
/*  481 */     this.client_dx = 0;
/*  482 */     this.client_dy = 0;
/*  483 */     this.pressed_button = 0;
/*  484 */     this.dragging = false;
/*      */     
/*  486 */     sync_default();
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
/*      */   private void move_server(boolean paramBoolean1, boolean paramBoolean2) {
/*      */     byte b1, b2;
/*  507 */     int k = 0;
/*  508 */     int m = 0;
/*  509 */     int n = 0;
/*  510 */     int i1 = 0;
/*      */     
/*  512 */     this.timer.pause();
/*      */     
/*  514 */     int i = this.client_dx;
/*  515 */     int j = this.client_dy;
/*      */     
/*  517 */     if (i >= 0) {
/*  518 */       b1 = 1;
/*      */     } else {
/*  520 */       b1 = -1;
/*  521 */       i = -i;
/*      */     } 
/*  523 */     if (j >= 0) {
/*  524 */       b2 = 1;
/*      */     } else {
/*  526 */       b2 = -1;
/*  527 */       j = -j;
/*      */     } 
/*      */     
/*      */     do {
/*  531 */       if (i != 0) {
/*  532 */         for (int i2 = this.send_dx.length - 1; i2 >= this.send_dx_index; i2--) {
/*  533 */           if (this.recv_dx[i2] <= i) {
/*  534 */             k = b1 * this.send_dx[i2];
/*  535 */             n += this.recv_dx[i2];
/*  536 */             i -= this.recv_dx[i2];
/*      */             break;
/*      */           } 
/*      */         } 
/*  540 */         if (i2 < this.send_dx_index) {
/*      */           
/*  542 */           k = 0;
/*  543 */           n += i;
/*  544 */           i = 0;
/*      */         } 
/*      */       } else {
/*  547 */         k = 0;
/*      */       } 
/*      */ 
/*      */       
/*  551 */       if (j != 0) {
/*  552 */         for (int i2 = this.send_dy.length - 1; i2 >= this.send_dy_index; i2--) {
/*  553 */           if (this.recv_dy[i2] <= j) {
/*  554 */             m = b2 * this.send_dy[i2];
/*  555 */             i1 += this.recv_dy[i2];
/*  556 */             j -= this.recv_dy[i2];
/*      */             break;
/*      */           } 
/*      */         } 
/*  560 */         if (i2 < this.send_dy_index) {
/*      */           
/*  562 */           m = 0;
/*  563 */           i1 += j;
/*  564 */           j = 0;
/*      */         } 
/*      */       } else {
/*  567 */         m = 0;
/*      */       } 
/*      */ 
/*      */       
/*  571 */       if (k == 0 && m == 0)
/*  572 */         continue;  this.listener.serverMove(k, m, this.client_x, this.client_y);
/*      */ 
/*      */     
/*      */     }
/*  576 */     while (paramBoolean1 && (i != 0 || j != 0));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  582 */     this.client_dx -= b1 * n;
/*  583 */     this.client_dy -= b2 * i1;
/*      */     
/*  585 */     if (!paramBoolean2) {
/*      */ 
/*      */       
/*  588 */       this.server_x += b1 * n;
/*  589 */       this.server_y -= b2 * i1;
/*  590 */       if (this.debug_msg);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  596 */     if (this.client_dx != 0 || this.client_dy != 0) {
/*  597 */       this.timer.start();
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
/*      */   private void go_state(int paramInt) {
/*  609 */     synchronized (this.mutex) {
/*  610 */       state_machine(1, null, 0, 0);
/*  611 */       this.state = paramInt;
/*  612 */       state_machine(0, null, 0, 0);
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
/*      */ 
/*      */   
/*      */   private void state_machine(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3) {
/*  631 */     synchronized (this.mutex) {
/*  632 */       switch (this.state) {
/*      */         case 0:
/*  634 */           state_init(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*      */           break;
/*      */         
/*      */         case 1:
/*  638 */           state_sync(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*      */           break;
/*      */         
/*      */         case 2:
/*  642 */           state_enable(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*      */           break;
/*      */         
/*      */         case 3:
/*  646 */           state_disable(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*      */           break;
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
/*      */ 
/*      */   
/*      */   private void state_init(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3) {
/*  667 */     switch (paramInt1) {
/*      */       case 0:
/*  669 */         init_vars();
/*  670 */         go_state(3);
/*      */         break;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void state_sync(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3) {
/*  710 */     switch (paramInt1) {
/*      */       case 0:
/*  712 */         this.timer = new Timer(2000, false, this.mutex);
/*  713 */         this.timer.setListener(this, null);
/*  714 */         sync_default();
/*  715 */         this.send_dx_index = this.send_dx.length - 1;
/*  716 */         this.send_dy_index = this.send_dy.length - 1;
/*  717 */         sync_continue();
/*      */         break;
/*      */       
/*      */       case 1:
/*  721 */         this.timer.stop();
/*  722 */         this.timer = null;
/*  723 */         if (!this.sync_successful) {
/*  724 */           if (this.debug_msg) {
/*  725 */             System.out.println("fail");
/*      */           }
/*  727 */           sync_default();
/*      */         }
/*  729 */         else if (this.debug_msg) {
/*      */         
/*      */         } 
/*      */         
/*  733 */         if (this.debug_msg) {
/*      */           byte b;
/*  735 */           for (b = 0; b < this.send_dx.length; b++);
/*      */ 
/*      */           
/*  738 */           for (b = 0; b < this.send_dx.length; b++);
/*      */         } 
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 2:
/*  746 */         go_state(1);
/*      */         break;
/*      */       
/*      */       case 3:
/*  750 */         if (paramInt2 > 2000 || paramInt3 > 2000) {
/*      */           
/*  752 */           go_state(3); break;
/*      */         } 
/*  754 */         sync_update(paramInt2, paramInt3);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 4:
/*  759 */         this.server_w = paramInt2;
/*  760 */         this.server_h = paramInt3;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 5:
/*  765 */         go_state(3);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 6:
/*  770 */         go_state(2);
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case 8:
/*      */       case 9:
/*      */       case 12:
/*      */       case 13:
/*  779 */         this.client_x = paramMouseEvent.getX();
/*  780 */         this.client_y = paramMouseEvent.getY();
/*      */         break;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void state_enable(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3) {
/*  813 */     switch (paramInt1) {
/*      */       case 0:
/*  815 */         if (this.debug_msg);
/*      */ 
/*      */         
/*  818 */         this.timer = new Timer(200, false, this.mutex);
/*  819 */         this.timer.setListener(this, null);
/*      */         break;
/*      */       
/*      */       case 1:
/*  823 */         this.timer.stop();
/*  824 */         this.timer = null;
/*      */         break;
/*      */       
/*      */       case 2:
/*  828 */         go_state(1);
/*      */         break;
/*      */       
/*      */       case 3:
/*  832 */         if (this.debug_msg);
/*      */ 
/*      */         
/*  835 */         if (paramInt2 > 2000 || paramInt3 > 2000) {
/*      */           
/*  837 */           go_state(3); break;
/*      */         } 
/*  839 */         this.server_x = paramInt2;
/*  840 */         this.server_y = paramInt3;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 4:
/*  845 */         this.server_w = paramInt2;
/*  846 */         this.server_h = paramInt3;
/*      */         break;
/*      */       
/*      */       case 5:
/*  850 */         go_state(3);
/*      */         break;
/*      */       
/*      */       case 14:
/*  854 */         this.client_dx = this.client_x - this.server_x;
/*  855 */         this.client_dy = this.server_y - this.client_y;
/*  856 */         move_server(true, true);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 6:
/*  861 */         move_server(true, true);
/*      */         break;
/*      */       
/*      */       case 8:
/*      */       case 9:
/*  866 */         this.client_x = paramMouseEvent.getX();
/*  867 */         this.client_y = paramMouseEvent.getY();
/*  868 */         if (this.client_x < 0) {
/*  869 */           this.client_x = 0;
/*      */         }
/*  871 */         if (this.client_x > this.server_w) {
/*  872 */           this.client_x = this.server_w;
/*      */         }
/*  874 */         if (this.client_y < 0) {
/*  875 */           this.client_y = 0;
/*      */         }
/*  877 */         if (this.client_y > this.server_h) {
/*  878 */           this.client_y = this.server_h;
/*      */         }
/*  880 */         if (this.debug_msg);
/*      */ 
/*      */         
/*  883 */         if (this.pressed_button != 1 && (paramMouseEvent.getModifiers() & 0x2) == 0)
/*      */         {
/*  885 */           align();
/*      */         }
/*      */         break;
/*      */       
/*      */       case 12:
/*  890 */         if (this.pressed_button != 1) {
/*      */           
/*  892 */           if (this.pressed_button > 0) {
/*      */             
/*  894 */             this.pressed_button = -this.pressed_button;
/*  895 */             this.listener.serverPress(this.pressed_button);
/*      */           } 
/*  897 */           this.client_dx += paramMouseEvent.getX() - this.client_x;
/*  898 */           this.client_dy += this.client_y - paramMouseEvent.getY();
/*  899 */           move_server(false, true);
/*      */         } 
/*  901 */         this.client_x = paramMouseEvent.getX();
/*  902 */         this.client_y = paramMouseEvent.getY();
/*  903 */         if (this.debug_msg);
/*      */ 
/*      */         
/*  906 */         this.dragging = true;
/*      */         break;
/*      */       
/*      */       case 13:
/*  910 */         if ((paramMouseEvent.getModifiers() & 0x2) == 0) {
/*      */           
/*  912 */           this.client_dx += paramMouseEvent.getX() - this.client_x;
/*  913 */           this.client_dy += this.client_y - paramMouseEvent.getY();
/*  914 */           move_server(false, true);
/*      */         } 
/*  916 */         this.client_x = paramMouseEvent.getX();
/*  917 */         this.client_y = paramMouseEvent.getY();
/*  918 */         if (this.debug_msg);
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case 10:
/*  924 */         if (this.pressed_button == 0) {
/*  925 */           if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
/*  926 */             this.pressed_button = 1;
/*  927 */           } else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/*  928 */             this.pressed_button = 2;
/*      */           } else {
/*  930 */             this.pressed_button = 4;
/*      */           } 
/*  932 */           this.dragging = false;
/*      */         } 
/*      */         break;
/*      */ 
/*      */       
/*      */       case 11:
/*  938 */         if (this.pressed_button == -4) {
/*  939 */           this.listener.serverRelease(4);
/*  940 */         } else if (this.pressed_button == -2) {
/*  941 */           this.listener.serverRelease(2);
/*  942 */         } else if (this.pressed_button == -1) {
/*  943 */           this.listener.serverRelease(1);
/*      */         } 
/*  945 */         this.pressed_button = 0;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 7:
/*  950 */         if (!this.dragging) {
/*  951 */           if ((paramMouseEvent.getModifiers() & 0x10) != 0) {
/*  952 */             this.listener.serverClick(4, 1); break;
/*  953 */           }  if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/*  954 */             this.listener.serverClick(2, 1); break;
/*  955 */           }  if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
/*  956 */             this.listener.serverClick(1, 1);
/*      */           }
/*      */         } 
/*      */         break;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void state_disable(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3) {
/*  988 */     switch (paramInt1) {
/*      */       case 0:
/*  990 */         if (this.debug_msg);
/*      */ 
/*      */         
/*  993 */         this.timer = new Timer(200, false, this.mutex);
/*  994 */         this.timer.setListener(this, null);
/*      */         break;
/*      */       
/*      */       case 1:
/*  998 */         this.timer.stop();
/*  999 */         this.timer = null;
/*      */         break;
/*      */       
/*      */       case 2:
/* 1003 */         sync_default();
/*      */         break;
/*      */       
/*      */       case 3:
/* 1007 */         if (this.debug_msg);
/*      */ 
/*      */         
/* 1010 */         if (paramInt2 < 2000 && paramInt3 < 2000) {
/*      */           
/* 1012 */           this.server_x = paramInt2;
/* 1013 */           this.server_y = paramInt3;
/* 1014 */           go_state(2);
/*      */         } 
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case 4:
/* 1021 */         this.server_w = paramInt2;
/* 1022 */         this.server_h = paramInt3;
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 14:
/* 1029 */         this.client_dx = this.client_x - this.server_x;
/* 1030 */         this.client_dy = this.server_y - this.client_y;
/* 1031 */         move_server(true, false);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 6:
/* 1036 */         move_server(true, false);
/*      */         break;
/*      */       
/*      */       case 8:
/*      */       case 9:
/* 1041 */         this.client_x = paramMouseEvent.getX();
/* 1042 */         this.client_y = paramMouseEvent.getY();
/* 1043 */         if (this.client_x < 0) {
/* 1044 */           this.client_x = 0;
/*      */         }
/* 1046 */         if (this.client_x > this.server_w) {
/* 1047 */           this.client_x = this.server_w;
/*      */         }
/* 1049 */         if (this.client_y < 0) {
/* 1050 */           this.client_y = 0;
/*      */         }
/* 1052 */         if (this.client_y > this.server_h) {
/* 1053 */           this.client_y = this.server_h;
/*      */         }
/* 1055 */         if (this.debug_msg);
/*      */ 
/*      */         
/* 1058 */         if (this.pressed_button != 1 && (paramMouseEvent.getModifiers() & 0x2) == 0)
/*      */         {
/* 1060 */           align();
/*      */         }
/*      */         break;
/*      */       
/*      */       case 12:
/* 1065 */         if (this.pressed_button != 1) {
/*      */           
/* 1067 */           if (this.pressed_button > 0) {
/*      */             
/* 1069 */             this.pressed_button = -this.pressed_button;
/* 1070 */             this.listener.serverPress(this.pressed_button);
/*      */           } 
/* 1072 */           this.client_dx += paramMouseEvent.getX() - this.client_x;
/* 1073 */           this.client_dy += this.client_y - paramMouseEvent.getY();
/* 1074 */           move_server(false, false);
/*      */         } else {
/*      */           
/* 1077 */           this.server_x = paramMouseEvent.getX();
/* 1078 */           this.server_y = paramMouseEvent.getY();
/*      */         } 
/* 1080 */         this.client_x = paramMouseEvent.getX();
/* 1081 */         this.client_y = paramMouseEvent.getY();
/* 1082 */         if (this.debug_msg);
/*      */ 
/*      */         
/* 1085 */         this.dragging = true;
/*      */         break;
/*      */       
/*      */       case 13:
/* 1089 */         if ((paramMouseEvent.getModifiers() & 0x2) == 0) {
/*      */           
/* 1091 */           this.client_dx += paramMouseEvent.getX() - this.client_x;
/* 1092 */           this.client_dy += this.client_y - paramMouseEvent.getY();
/* 1093 */           move_server(false, false);
/*      */         } else {
/*      */           
/* 1096 */           this.server_x = paramMouseEvent.getX();
/* 1097 */           this.server_y = paramMouseEvent.getY();
/*      */         } 
/* 1099 */         this.client_x = paramMouseEvent.getX();
/* 1100 */         this.client_y = paramMouseEvent.getY();
/* 1101 */         if (this.debug_msg);
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       case 10:
/* 1107 */         if (this.pressed_button == 0) {
/* 1108 */           if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
/* 1109 */             this.pressed_button = 1;
/* 1110 */           } else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/* 1111 */             this.pressed_button = 2;
/*      */           } else {
/* 1113 */             this.pressed_button = 4;
/*      */           } 
/* 1115 */           this.dragging = false;
/*      */         } 
/*      */         break;
/*      */ 
/*      */       
/*      */       case 11:
/* 1121 */         if (this.pressed_button == -4) {
/* 1122 */           this.listener.serverRelease(4);
/* 1123 */         } else if (this.pressed_button == -2) {
/* 1124 */           this.listener.serverRelease(2);
/* 1125 */         } else if (this.pressed_button == -1) {
/* 1126 */           this.listener.serverRelease(1);
/*      */         } 
/* 1128 */         this.pressed_button = 0;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 7:
/* 1133 */         if (!this.dragging) {
/* 1134 */           if ((paramMouseEvent.getModifiers() & 0x10) != 0) {
/* 1135 */             this.listener.serverClick(4, 1); break;
/* 1136 */           }  if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/* 1137 */             this.listener.serverClick(2, 1); break;
/* 1138 */           }  if ((paramMouseEvent.getModifiers() & 0x4) != 0)
/* 1139 */             this.listener.serverClick(1, 1); 
/*      */         } 
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent) {}
/*      */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/MouseSync.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */