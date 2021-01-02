/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
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
/*     */ public class D
/*     */ {
/*     */   public static final int NONE = -1;
/*     */   public static final int FATAL = 0;
/*     */   public static final int INFORM = 1;
/*     */   public static final int WARNING = 2;
/*     */   public static final int VERBOSE = 3;
/*  28 */   public static int debug = 0;
/*     */   
/*     */   public static PrintStream out;
/*     */   
/*     */   public static void println(int paramInt, String paramString) {
/*  33 */     if (debug >= paramInt) {
/*  34 */       out.println(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void print(int paramInt, String paramString) {
/*  39 */     if (debug >= paramInt) {
/*  40 */       out.println(paramString);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String hex(byte paramByte, int paramInt) {
/*  45 */     return hex(paramByte & 0xFF, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String hex(short paramShort, int paramInt) {
/*  50 */     return hex(paramShort & 0xFFFF, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String hex(int paramInt1, int paramInt2) {
/*  55 */     String str = Integer.toHexString(paramInt1);
/*  56 */     while (str.length() < paramInt2) {
/*  57 */       str = "0" + str;
/*     */     }
/*  59 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String hex(long paramLong, int paramInt) {
/*  64 */     String str = Long.toHexString(paramLong);
/*  65 */     while (str.length() < paramInt) {
/*  66 */       str = "0" + str;
/*     */     }
/*  68 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void hexdump(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
/*  73 */     if (debug < paramInt1) {
/*     */       return;
/*     */     }
/*  76 */     if (paramInt2 == 0)
/*  77 */       paramInt2 = paramArrayOfbyte.length; 
/*  78 */     for (byte b = 0; b < paramInt2; b++) {
/*  79 */       if (b % 16 == 0)
/*  80 */         out.print("\n"); 
/*  81 */       out.print(hex(paramArrayOfbyte[b], 2) + " ");
/*     */     } 
/*  83 */     out.print("\n");
/*     */   }
/*     */   
/*     */   static {
/*  87 */     String str = virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.debugfile");
/*     */     try {
/*  89 */       if (str == null) {
/*  90 */         out = System.out;
/*     */       } else {
/*  92 */         out = new PrintStream(new FileOutputStream(str));
/*     */       } 
/*     */     } catch (Exception exception) {
/*  95 */       out = System.out;
/*  96 */       out.println("Exception trying to open debug trace\n" + exception);
/*     */     } 
/*  98 */     str = virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.debug");
/*  99 */     if (str != null)
/* 100 */       debug = Integer.valueOf(str).intValue(); 
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/D.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */