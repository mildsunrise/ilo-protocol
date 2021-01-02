/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.util.Date;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class Timer
/*     */   implements Runnable
/*     */ {
/*     */   private static final int STATE_INIT = 0;
/*     */   private static final int STATE_RUNNING = 1;
/*     */   private static final int STATE_PAUSED = 2;
/*     */   private static final int STATE_STOPPED = 3;
/*  28 */   private int state = 0;
/*     */   
/*     */   private static final int POLL_PERIOD = 50;
/*     */   
/*     */   private int timeout_count;
/*     */   private int timeout_max;
/*     */   private boolean one_shot;
/*     */   private long start_time_millis;
/*     */   private long stop_time_millis;
/*  37 */   private Date date = new Date();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TimerListener callback;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object callback_info;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object mutex;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timer(int paramInt, boolean paramBoolean, Object paramObject) {
/*  65 */     this.timeout_max = paramInt;
/*  66 */     this.one_shot = paramBoolean;
/*  67 */     this.mutex = paramObject;
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
/*     */   public void setListener(TimerListener paramTimerListener, Object paramObject) {
/*  80 */     synchronized (this.mutex) {
/*  81 */       this.callback = paramTimerListener;
/*  82 */       this.callback_info = paramObject;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  92 */     synchronized (this.mutex) {
/*  93 */       switch (this.state) {
/*     */         case 0:
/*  95 */           this.state = 1;
/*  96 */           this.timeout_count = 0;
/*  97 */           (new Thread(this)).start();
/*     */           break;
/*     */         
/*     */         case 1:
/* 101 */           this.timeout_count = 0;
/*     */           break;
/*     */         
/*     */         case 2:
/* 105 */           this.timeout_count = 0;
/* 106 */           this.state = 1;
/*     */           break;
/*     */         
/*     */         case 3:
/* 110 */           this.timeout_count = 0;
/* 111 */           this.state = 1;
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 123 */     synchronized (this.mutex) {
/* 124 */       if (this.state != 0) {
/* 125 */         this.state = 3;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pause() {
/* 135 */     synchronized (this.mutex) {
/* 136 */       if (this.state == 1) {
/* 137 */         this.state = 2;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cont() {
/* 149 */     synchronized (this.mutex) {
/* 150 */       if (this.state == 2) {
/* 151 */         this.state = 1;
/*     */       }
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
/*     */   public void run() {
/*     */     do {
/* 166 */       this.date = new Date();
/* 167 */       this.start_time_millis = this.date.getTime();
/*     */       try {
/* 169 */         Thread.sleep(50L);
/* 170 */       } catch (InterruptedException interruptedException) {}
/*     */       
/* 172 */       this.date = new Date();
/* 173 */       this.stop_time_millis = this.date.getTime();
/* 174 */     } while (process_state());
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
/*     */   private boolean process_state() {
/* 189 */     boolean bool = true;
/*     */     
/* 191 */     synchronized (this.mutex) {
/* 192 */       switch (this.state) {
/*     */ 
/*     */ 
/*     */         
/*     */         case 1:
/* 197 */           if (this.stop_time_millis > this.start_time_millis) {
/* 198 */             this.timeout_count = (int)(this.timeout_count + this.stop_time_millis - this.start_time_millis);
/*     */           }
/*     */           else {
/*     */             
/* 202 */             this.timeout_count += 50;
/* 203 */           }  if (this.timeout_count >= this.timeout_max) {
/*     */             
/* 205 */             if (this.callback != null) {
/* 206 */               this.callback.timeout(this.callback_info);
/*     */             }
/* 208 */             if (this.one_shot) {
/* 209 */               this.state = 0;
/* 210 */               bool = false; break;
/*     */             } 
/* 212 */             this.timeout_count = 0;
/*     */           } 
/*     */           break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 3:
/* 221 */           this.state = 0;
/* 222 */           bool = false;
/*     */           break;
/*     */       } 
/*     */     } 
/* 226 */     return bool;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/Timer.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */