/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
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
/*     */ public class MediaAccess
/*     */ {
/*     */   public static final int Unknown = 0;
/*     */   public static final int F5_1Pt2_512 = 1;
/*     */   public static final int F3_1Pt44_512 = 2;
/*     */   public static final int F3_2Pt88_512 = 3;
/*     */   public static final int F3_20Pt88_512 = 4;
/*     */   public static final int F3_720_512 = 5;
/*     */   public static final int F5_360_512 = 6;
/*     */   public static final int F5_320_512 = 7;
/*     */   public static final int F5_320_1024 = 8;
/*     */   public static final int F5_180_512 = 9;
/*     */   public static final int F5_160_512 = 10;
/*     */   public static final int RemovableMedia = 11;
/*     */   public static final int FixedMedia = 12;
/*     */   public static final int F3_120M_512 = 13;
/*     */   public static final int ImageFile = 100;
/*     */   public static final int NoRootDir = 1;
/*     */   public static final int Removable = 2;
/*     */   public static final int Fixed = 3;
/*     */   public static final int Remote = 4;
/*     */   public static final int CDROM = 5;
/*     */   public static final int Ramdisk = 6;
/*  42 */   public static String dllext = "";
/*  43 */   static int dio_setup = -1;
/*     */   
/*     */   DirectIO dio;
/*     */   File file;
/*     */   RandomAccessFile raf;
/*     */   boolean dev = false;
/*     */   boolean readonly = false;
/*  50 */   int zero_offset = 0;
/*     */ 
/*     */   
/*     */   public int open(String filename, int flags) throws IOException {
/*  54 */     this.dev = ((flags & 0x1) == 1);
/*  55 */     boolean bool = ((flags & 0x2) == 2) ? true : false;
/*     */ 
/*     */     
/*  58 */     this.zero_offset = 0;
/*  59 */     if (this.dev) {
/*  60 */       if (dio_setup != 0)
/*  61 */         throw new IOException("DirectIO not possible (" + dio_setup + ")"); 
/*  62 */       if (this.dio == null)
/*  63 */         this.dio = new DirectIO(); 
/*  64 */       return this.dio.open(filename);
/*     */     } 
/*  66 */     this.readonly = false;
/*  67 */     this.file = new File(filename);
/*  68 */     if (!this.file.exists() && !bool)
/*  69 */       throw new IOException("File " + filename + " does not exist"); 
/*  70 */     if (this.file.isDirectory()) {
/*  71 */       throw new IOException("File " + filename + " is a directory");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  80 */       this.raf = new RandomAccessFile(filename, "rw");
/*     */     } catch (IOException iOException) {
/*     */       
/*  83 */       if (!bool) {
/*  84 */         this.raf = new RandomAccessFile(filename, "r");
/*  85 */         this.readonly = true;
/*     */       } else {
/*  87 */         throw iOException;
/*     */       } 
/*     */     } 
/*     */     
/*  91 */     byte[] arrayOfByte = new byte[512];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     read(0L, 512, arrayOfByte);
/* 100 */     if (arrayOfByte[0] == 67 && arrayOfByte[1] == 80 && arrayOfByte[2] == 81 && arrayOfByte[3] == 82 && arrayOfByte[4] == 70 && arrayOfByte[5] == 66 && arrayOfByte[6] == 76 && arrayOfByte[7] == 79)
/*     */     {
/* 102 */       this.zero_offset = arrayOfByte[14] | arrayOfByte[15] << 8;
/*     */     }
/* 104 */     arrayOfByte = null;
/*     */     
/* 106 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int close() throws IOException {
/* 111 */     if (this.dev) {
/* 112 */       return this.dio.close();
/*     */     }
/* 114 */     this.raf.close();
/*     */     
/* 116 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(long paramLong, int paramInt, byte[] paramArrayOfbyte) throws IOException {
/* 122 */     paramLong += this.zero_offset;
/* 123 */     if (this.dev) {
/* 124 */       int i = this.dio.read(paramLong, paramInt, paramArrayOfbyte);
/* 125 */       if (i != 0)
/* 126 */         throw new IOException("DirectIO read error (" + this.dio.sysError(-i) + ")"); 
/*     */     } else {
/* 128 */       this.raf.seek(paramLong);
/* 129 */       this.raf.read(paramArrayOfbyte, 0, paramInt);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(long paramLong, int paramInt, byte[] paramArrayOfbyte) throws IOException {
/* 136 */     paramLong += this.zero_offset;
/* 137 */     if (this.dev) {
/* 138 */       int i = this.dio.write(paramLong, paramInt, paramArrayOfbyte);
/* 139 */       if (i != 0)
/* 140 */         throw new IOException("DirectIO write error (" + this.dio.sysError(-i) + ")"); 
/*     */     } else {
/* 142 */       this.raf.seek(paramLong);
/* 143 */       this.raf.write(paramArrayOfbyte, 0, paramInt);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() throws IOException {
/*     */     long l;
/* 150 */     if (this.dev) {
/* 151 */       l = this.dio.size();
/*     */     } else {
/* 153 */       l = this.raf.length() - this.zero_offset;
/*     */     } 
/* 155 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public int format(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) throws IOException {
/* 160 */     if (this.dev) {
/* 161 */       this.dio.media_type = paramInt1;
/* 162 */       this.dio.StartCylinder = paramInt2;
/* 163 */       this.dio.EndCylinder = paramInt3;
/* 164 */       this.dio.StartHead = paramInt4;
/* 165 */       this.dio.EndHead = paramInt5;
/* 166 */       return this.dio.format();
/*     */     } 
/*     */     
/* 169 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] devices() {
/* 174 */     if (dio_setup != 0)
/* 175 */       return null; 
/* 176 */     if (this.dio == null)
/* 177 */       this.dio = new DirectIO(); 
/* 178 */     return this.dio.devices();
/*     */   }
/*     */ 
/*     */   
/*     */   public int devtype(String target) {
/* 183 */     if (dio_setup != 0)
/* 184 */       return 0; 
/* 185 */     if (this.dio == null)
/* 186 */       this.dio = new DirectIO(); 
/* 187 */     return this.dio.devtype(target);
/*     */   }
/*     */ 
/*     */   
/*     */   public int scsi(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
/* 192 */     return scsi(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramArrayOfbyte3, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int scsi(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt3) {
/*     */     byte b;
/* 198 */     if (this.dev) {
/* 199 */       b = this.dio.scsi(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramArrayOfbyte3, paramInt3);
/*     */     } else {
/* 201 */       b = -1;
/*     */     } 
/* 203 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean wp() {
/*     */     boolean bool;
/* 210 */     if (this.dev) {
/* 211 */       bool = (this.dio.wp == 1);
/*     */     } else {
/* 213 */       bool = this.readonly;
/*     */     } 
/* 215 */     return bool;
/*     */   }
/*     */ 
/*     */   
/*     */   public int type() {
/* 220 */     if (this.dev && this.dio != null)
/* 221 */       return this.dio.media_type; 
/* 222 */     if (this.raf != null) {
/* 223 */       return 100;
/*     */     }
/* 225 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int dllExtract(String paramString1, String paramString2) {
/* 230 */     ClassLoader classLoader = getClass().getClassLoader();
/*     */ 
/*     */     
/* 233 */     byte[] arrayOfByte = new byte[4096];
/*     */     
/* 235 */     D.println(1, "dllExtract trying " + paramString1);
/* 236 */     if (classLoader.getResource(paramString1) == null) {
/* 237 */       return -1;
/*     */     }
/* 239 */     D.println(1, "Extracting " + classLoader.getResource(paramString1).toExternalForm() + " to " + paramString2);
/*     */     
/*     */     try {
/* 242 */       InputStream inputStream = classLoader.getResourceAsStream(paramString1);
/* 243 */       FileOutputStream fileOutputStream = new FileOutputStream(paramString2);
/*     */       int i;
/* 245 */       while ((i = inputStream.read(arrayOfByte, 0, 4096)) != -1) {
/* 246 */         fileOutputStream.write(arrayOfByte, 0, i);
/*     */       }
/* 248 */       inputStream.close();
/* 249 */       fileOutputStream.close();
/*     */     } catch (IOException iOException) {
/* 251 */       D.println(0, "dllExtract: " + iOException);
/* 252 */       return -2;
/*     */     } 
/*     */     
/* 255 */     return 0;
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
/*     */   public int setup_DirectIO() {
/* 291 */     int i = 0;
/* 292 */     String str1 = System.getProperty("file.separator");
/* 293 */     String str2 = System.getProperty("java.io.tmpdir");
/* 294 */     String str3 = System.getProperty("os.name").toLowerCase();
/* 295 */     String str4 = System.getProperty("java.vm.name");
/* 296 */     String str5 = "unknown";
/*     */     
/* 298 */     if (str2 == null) {
/* 299 */       str2 = str3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
/*     */     }
/*     */     
/* 302 */     if (str3.startsWith("windows")) {
/* 303 */       if (str4.indexOf("64") != -1) {
/* 304 */         System.out.println("virt: Detected win 64bit jvm");
/* 305 */         str5 = "x86-win64";
/*     */       } else {
/* 307 */         System.out.println("virt: Detected win 32bit jvm");
/* 308 */         str5 = "x86-win32";
/*     */       } 
/* 310 */       dllext = ".dll";
/* 311 */     } else if (str3.startsWith("linux")) {
/* 312 */       if (str4.indexOf("64") != -1) {
/* 313 */         System.out.println("virt: Detected 64bit linux jvm");
/* 314 */         str5 = "x86-linux-64";
/*     */       } else {
/* 316 */         System.out.println("virt: Detected 32bit linux jvm");
/* 317 */         str5 = "x86-linux-32";
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 322 */     File file1 = new File(str2);
/* 323 */     if (!file1.exists()) {
/* 324 */       file1.mkdir();
/*     */     }
/*     */     
/* 327 */     if (!str2.endsWith(str1)) {
/* 328 */       str2 = str2 + str1;
/*     */     }
/* 330 */     str2 = str2 + "cpqma-" + Integer.toHexString(virtdevs.UID) + dllext;
/*     */     
/* 332 */     System.out.println("Checking for " + str2);
/* 333 */     File file2 = new File(str2);
/* 334 */     if (file2.exists()) {
/* 335 */       System.out.println("DLL present");
/* 336 */       dio_setup = 0;
/* 337 */       return 0;
/*     */     } 
/* 339 */     System.out.println("DLL not present");
/*     */     
/* 341 */     i = dllExtract("com/hp/ilo2/virtdevs/cpqma-" + str5, str2);
/* 342 */     dio_setup = i;
/* 343 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void cleanup(virtdevs paramvirtdevs) {
/* 348 */     String str1 = System.getProperty("file.separator");
/* 349 */     String str2 = System.getProperty("java.io.tmpdir");
/* 350 */     String str3 = System.getProperty("os.name").toLowerCase();
/* 351 */     if (str2 == null) {
/* 352 */       str2 = str3.startsWith("windows") ? "C:\\TEMP" : "/tmp";
/*     */     }
/* 354 */     File file = new File(str2);
/* 355 */     String[] arrayOfString = file.list();
/* 356 */     String str4 = "";
/*     */     
/* 358 */     if (!str2.endsWith(str1)) {
/* 359 */       str2 = str2 + str1;
/*     */     }
/* 361 */     for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
/* 362 */       if (arrayOfString[b1].startsWith("cpqma-") && arrayOfString[b1].endsWith(dllext)) {
/* 363 */         File file1 = new File(str2 + arrayOfString[b1]);
/* 364 */         file1.delete();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 370 */     for (byte b2 = 0; b2 < arrayOfString.length; b2++) {
/* 371 */       if (arrayOfString[b2].startsWith("HpqKbHook-") && arrayOfString[b2].endsWith(dllext)) {
/* 372 */         File file1 = new File(str2 + arrayOfString[b2]);
/* 373 */         file1.delete();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 379 */     for (byte b3 = 0; b3 < arrayOfString.length; b3++) {
/* 380 */       if (arrayOfString[b3].startsWith("jirc_strings") && arrayOfString[b3].endsWith("xml")) {
/* 381 */         File file1 = new File(str2 + arrayOfString[b3]);
/* 382 */         file1.delete();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/MediaAccess.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */