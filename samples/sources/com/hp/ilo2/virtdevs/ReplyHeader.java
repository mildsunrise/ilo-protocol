/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ public class ReplyHeader
/*     */ {
/*     */   public static final int magic = 195936478;
/*     */   public static final int WP = 1;
/*     */   public static final int KEEPALIVE = 2;
/*     */   public static final int DISCONNECT = 4;
/*     */   int flags;
/*     */   byte sense_key;
/*     */   byte asc;
/*     */   byte ascq;
/*     */   byte media;
/*     */   int length;
/*  27 */   byte[] data = new byte[16];
/*     */ 
/*     */   
/*     */   void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  31 */     this.sense_key = (byte)paramInt1;
/*  32 */     this.asc = (byte)paramInt2;
/*  33 */     this.ascq = (byte)paramInt3;
/*  34 */     this.length = paramInt4;
/*     */   }
/*     */ 
/*     */   
/*     */   void setmedia(int paramInt) {
/*  39 */     this.media = (byte)paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   void setflags(boolean paramBoolean) {
/*  44 */     if (paramBoolean) {
/*  45 */       this.flags |= 0x1;
/*     */     } else {
/*  47 */       this.flags &= 0xFFFFFFFE;
/*     */     } 
/*     */   }
/*     */   
/*     */   void keepalive(boolean paramBoolean) {
/*  52 */     if (paramBoolean) {
/*  53 */       this.flags |= 0x2;
/*     */     } else {
/*  55 */       this.flags &= 0xFFFFFFFD;
/*     */     } 
/*     */   }
/*     */   
/*     */   void disconnect(boolean paramBoolean) {
/*  60 */     if (paramBoolean) {
/*  61 */       this.flags |= 0x4;
/*     */     } else {
/*  63 */       this.flags &= 0xFFFFFFFB;
/*     */     } 
/*     */   }
/*     */   
/*     */   void send(OutputStream paramOutputStream) throws IOException {
/*  68 */     this.data[0] = -34;
/*  69 */     this.data[1] = -64;
/*  70 */     this.data[2] = -83;
/*  71 */     this.data[3] = 11;
/*  72 */     this.data[4] = (byte)(this.flags & 0xFF);
/*  73 */     this.data[5] = (byte)(this.flags >> 8 & 0xFF);
/*  74 */     this.data[6] = (byte)(this.flags >> 16 & 0xFF);
/*  75 */     this.data[7] = (byte)(this.flags >> 24 & 0xFF);
/*  76 */     this.data[8] = this.media;
/*  77 */     this.data[9] = this.sense_key;
/*  78 */     this.data[10] = this.asc;
/*  79 */     this.data[11] = this.ascq;
/*  80 */     this.data[12] = (byte)(this.length & 0xFF);
/*  81 */     this.data[13] = (byte)(this.length >> 8 & 0xFF);
/*  82 */     this.data[14] = (byte)(this.length >> 16 & 0xFF);
/*  83 */     this.data[15] = (byte)(this.length >> 24 & 0xFF);
/*  84 */     paramOutputStream.write(this.data, 0, 16);
/*     */   }
/*     */ 
/*     */   
/*     */   void sendsynch(OutputStream paramOutputStream, byte[] paramArrayOfbyte) throws IOException {
/*  89 */     this.data[0] = -34;
/*  90 */     this.data[1] = -64;
/*  91 */     this.data[2] = -83;
/*  92 */     this.data[3] = 11;
/*  93 */     this.data[4] = (byte)(this.flags & 0xFF);
/*  94 */     this.data[5] = (byte)(this.flags >> 8 & 0xFF);
/*  95 */     this.data[6] = (byte)(this.flags >> 16 & 0xFF);
/*  96 */     this.data[7] = (byte)(this.flags >> 24 & 0xFF);
/*  97 */     this.data[8] = paramArrayOfbyte[4];
/*  98 */     this.data[9] = paramArrayOfbyte[5];
/*  99 */     this.data[10] = paramArrayOfbyte[6];
/* 100 */     this.data[11] = paramArrayOfbyte[7];
/* 101 */     this.data[12] = paramArrayOfbyte[8];
/* 102 */     this.data[13] = paramArrayOfbyte[9];
/* 103 */     this.data[14] = paramArrayOfbyte[10];
/* 104 */     this.data[15] = paramArrayOfbyte[11];
/* 105 */     paramOutputStream.write(this.data, 0, 16);
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/ReplyHeader.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */