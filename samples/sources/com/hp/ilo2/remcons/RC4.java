/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RC4
/*     */ {
/*     */   byte[] keyData;
/*     */   byte[] key;
/*     */   byte[] pre;
/*     */   byte[] sBox;
/*     */   byte[] keyBox;
/*     */   int i;
/*     */   int j;
/*     */   
/*     */   public RC4(byte[] paramArrayOfbyte) {
/*  36 */     this.keyData = new byte[16];
/*  37 */     this.key = new byte[16];
/*  38 */     this.pre = new byte[16];
/*  39 */     this.sBox = new byte[256];
/*  40 */     this.keyBox = new byte[256];
/*     */     
/*  42 */     System.arraycopy(paramArrayOfbyte, 0, this.keyData, 0, this.keyData.length);
/*     */ 
/*     */     
/*  45 */     Init();
/*     */   }
/*     */ 
/*     */   
/*     */   public void Init() {
/*  50 */     this.i = 0;
/*  51 */     this.j = 0;
/*     */     
/*  53 */     Arrays.fill(this.key, (byte)0);
/*  54 */     System.arraycopy(this.keyData, 0, this.pre, 0, this.pre.length);
/*  55 */     Arrays.fill(this.sBox, (byte)0);
/*  56 */     Arrays.fill(this.keyBox, (byte)0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  63 */     update_key();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update_key() {
/*  70 */     System.arraycopy(this.pre, 0, this.key, 0, this.key.length);
/*     */     
/*     */     byte b;
/*     */     
/*  74 */     for (b = 0; b < 'Ā'; b++) {
/*  75 */       this.sBox[b] = (byte)(b & 0xFF);
/*  76 */       this.keyBox[b] = this.key[b % 16];
/*     */     } 
/*     */ 
/*     */     
/*  80 */     int i = 0;
/*  81 */     for (b = 0; b < 'Ā'; b++) {
/*  82 */       i = (i & 0xFF) + (this.sBox[b] & 0xFF) + (this.keyBox[b] & 0xFF) & 0xFF;
/*  83 */       byte b1 = this.sBox[b];
/*  84 */       this.sBox[b] = this.sBox[i];
/*  85 */       this.sBox[i] = b1;
/*     */     } 
/*     */     
/*  88 */     this.i = 0;
/*  89 */     this.j = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int randomValue() {
/*  94 */     this.i = (this.i & 0xFF) + 1 & 0xFF;
/*  95 */     this.j = (this.j & 0xFF) + (this.sBox[this.i] & 0xFF) & 0xFF;
/*  96 */     byte b = this.sBox[this.i];
/*  97 */     this.sBox[this.i] = this.sBox[this.j];
/*  98 */     this.sBox[this.j] = b;
/*  99 */     int i = (this.sBox[this.i] & 0xFF) + (this.sBox[this.j] & 0xFF) & 0xFF;
/* 100 */     return this.sBox[i];
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/RC4.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */