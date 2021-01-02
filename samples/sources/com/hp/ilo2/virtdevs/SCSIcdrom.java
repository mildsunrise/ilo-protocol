/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.Socket;
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
/*     */ public class SCSIcdrom
/*     */   extends SCSI
/*     */ {
/*     */   public static final int SCSI_IOCTL_DATA_OUT = 0;
/*     */   public static final int SCSI_IOCTL_DATA_IN = 1;
/*     */   public static final int SCSI_IOCTL_DATA_UNSPECIFIED = 2;
/*     */   public static final int CONST = 0;
/*     */   static final int WRITE = 0;
/*     */   static final int READ = 16777216;
/*     */   static final int NONE = 33554432;
/*     */   public static final int BLKS = 8388608;
/*     */   static final int B32 = 262144;
/*     */   static final int B24 = 196608;
/*     */   static final int B16 = 131072;
/*     */   static final int B08 = 65536;
/*  38 */   static final int[] commands = new int[] { 30, 33554432, 37, 16777224, 29, 33554432, 0, 33554432, 40, 25296903, 168, 25427974, 27, 33554432, 190, 25362438, 185, 16777216, 68, 16777224, 66, 16908295, 67, 16908295, 78, 33554432, 189, 16908296, 90, 16908295, 74, 16908295 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   byte[] sense = new byte[3];
/*     */   
/*     */   int retrycount;
/*     */   VErrorDialog dlg;
/*     */   boolean do_split_reads = false;
/*     */   virtdevs v;
/*     */   
/*     */   void media_err(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
/*  52 */     String str = "The CDROM drive reports a media error:\nCommand: " + D.hex(paramArrayOfbyte1[0], 2) + " " + D.hex(paramArrayOfbyte1[1], 2) + " " + D.hex(paramArrayOfbyte1[2], 2) + " " + D.hex(paramArrayOfbyte1[3], 2) + " " + D.hex(paramArrayOfbyte1[4], 2) + " " + D.hex(paramArrayOfbyte1[5], 2) + " " + D.hex(paramArrayOfbyte1[6], 2) + " " + D.hex(paramArrayOfbyte1[7], 2) + " " + D.hex(paramArrayOfbyte1[8], 2) + " " + D.hex(paramArrayOfbyte1[9], 2) + " " + D.hex(paramArrayOfbyte1[10], 2) + " " + D.hex(paramArrayOfbyte1[11], 2) + "\n" + "Sense Code: " + D.hex(paramArrayOfbyte2[0], 2) + "/" + D.hex(paramArrayOfbyte2[1], 2) + "/" + D.hex(paramArrayOfbyte2[2], 2) + "\n\n";
/*     */ 
/*     */ 
/*     */     
/*  56 */     this.dlg = new VErrorDialog(str, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public SCSIcdrom(Socket paramSocket, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, String paramString, int paramInt, virtdevs paramvirtdevs) throws IOException {
/*  61 */     super(paramSocket, paramInputStream, paramBufferedOutputStream, paramString, paramInt);
/*     */ 
/*     */     
/*  64 */     D.println(1, "Media opening " + paramString + "(" + (paramInt | 0x2) + ")");
/*  65 */     int i = this.media.open(paramString, paramInt);
/*  66 */     D.println(1, "Media open returns " + i);
/*  67 */     this.retrycount = Integer.valueOf(virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.retrycount", "10")).intValue();
/*  68 */     this.v = paramvirtdevs;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  73 */     this.req[0] = 30;
/*  74 */     this.req[11] = 0; this.req[10] = 0; this.req[9] = 0; this.req[8] = 0; this.req[7] = 0; this.req[7] = 0; this.req[5] = 0; this.req[4] = 0; this.req[3] = 0; this.req[2] = 0; this.req[1] = 0;
/*     */     
/*  76 */     this.media.scsi(this.req, 2, 0, this.buffer, null);
/*  77 */     super.close();
/*     */   }
/*     */ 
/*     */   
/*     */   int scsi_length(int paramInt, byte[] paramArrayOfbyte) {
/*  82 */     int i = 0;
/*  83 */     paramInt++;
/*  84 */     switch (commands[paramInt] & 0x7F0000) {
/*     */       case 0:
/*  86 */         i = commands[paramInt] & 0xFFFF;
/*     */         break;
/*     */       case 262144:
/*  89 */         i = SCSI.mk_int32(paramArrayOfbyte, commands[paramInt] & 0xFFFF);
/*     */         break;
/*     */       case 196608:
/*  92 */         i = SCSI.mk_int24(paramArrayOfbyte, commands[paramInt] & 0xFFFF);
/*     */         break;
/*     */       case 131072:
/*  95 */         i = SCSI.mk_int16(paramArrayOfbyte, commands[paramInt] & 0xFFFF);
/*     */         break;
/*     */       case 65536:
/*  98 */         i = paramArrayOfbyte[commands[paramInt] & 0xFFFF] & 0xFF;
/*     */         break;
/*     */       default:
/* 101 */         D.println(0, "Unknown Size!"); break;
/*     */     } 
/* 103 */     if ((commands[paramInt] & 0x800000) == 8388608)
/* 104 */       i *= 2048; 
/* 105 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   void start_stop_unit() {
/* 110 */     byte[] arrayOfByte1 = { 27, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
/* 111 */     byte[] arrayOfByte2 = new byte[3];
/*     */ 
/*     */     
/* 114 */     int i = this.media.scsi(arrayOfByte1, 2, 0, this.buffer, arrayOfByte2);
/* 115 */     D.println(3, "Start/Stop unit = " + i + " " + arrayOfByte2[0] + "/" + arrayOfByte2[1] + "/" + arrayOfByte2[2]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean within_75(byte[] paramArrayOfbyte) {
/* 121 */     byte[] arrayOfByte1 = new byte[8];
/* 122 */     byte[] arrayOfByte2 = { 37, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 123 */     boolean bool = (paramArrayOfbyte[0] == 168) ? true : false;
/* 124 */     int i = SCSI.mk_int32(paramArrayOfbyte, 2);
/* 125 */     int j = bool ? SCSI.mk_int32(paramArrayOfbyte, 6) : SCSI.mk_int16(paramArrayOfbyte, 7);
/*     */ 
/*     */     
/* 128 */     this.media.scsi(arrayOfByte2, 1, 8, arrayOfByte1, null);
/*     */     
/* 130 */     int k = SCSI.mk_int32(arrayOfByte1, 0);
/* 131 */     if (i > k - 75 || i + j > k - 75)
/* 132 */       return true; 
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   int split_read() {
/* 138 */     boolean bool1 = (this.req[0] == 168) ? true : false;
/* 139 */     int i = SCSI.mk_int32(this.req, 2);
/* 140 */     int j = bool1 ? SCSI.mk_int32(this.req, 6) : SCSI.mk_int16(this.req, 7);
/* 141 */     byte b = (j > 32) ? 32 : j;
/*     */     
/* 143 */     boolean bool2 = true;
/*     */ 
/*     */ 
/*     */     
/* 147 */     this.req[2] = (byte)(i >> 24);
/* 148 */     this.req[3] = (byte)(i >> 16);
/* 149 */     this.req[4] = (byte)(i >> 8);
/* 150 */     this.req[5] = (byte)i;
/* 151 */     if (bool1) {
/* 152 */       this.req[6] = (byte)(b >> 24);
/* 153 */       this.req[7] = (byte)(b >> 16);
/* 154 */       this.req[8] = (byte)(b >> 8);
/* 155 */       this.req[9] = (byte)b;
/*     */     } else {
/* 157 */       this.req[7] = (byte)(b >> 8);
/* 158 */       this.req[8] = (byte)b;
/*     */     } 
/* 160 */     int k = this.media.scsi(this.req, bool2, b * 2048, this.buffer, this.sense);
/* 161 */     if (k < 0) {
/* 162 */       return k;
/*     */     }
/* 164 */     j -= b;
/* 165 */     if (j <= 0) {
/* 166 */       return k;
/*     */     }
/*     */ 
/*     */     
/* 170 */     i += b;
/* 171 */     this.req[2] = (byte)(i >> 24);
/* 172 */     this.req[3] = (byte)(i >> 16);
/* 173 */     this.req[4] = (byte)(i >> 8);
/* 174 */     this.req[5] = (byte)i;
/* 175 */     if (bool1) {
/* 176 */       this.req[6] = (byte)(j >> 24);
/* 177 */       this.req[7] = (byte)(j >> 16);
/* 178 */       this.req[8] = (byte)(j >> 8);
/* 179 */       this.req[9] = (byte)j;
/*     */     } else {
/* 181 */       this.req[7] = (byte)(j >> 8);
/* 182 */       this.req[8] = (byte)j;
/*     */     } 
/* 184 */     int m = this.media.scsi(this.req, bool2, j * 2048, this.buffer, this.sense, 65536);
/* 185 */     if (m < 0) {
/* 186 */       return m;
/*     */     }
/* 188 */     return k + m;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean process() throws IOException {
/* 194 */     int j = 0, k = 0;
/*     */ 
/*     */     
/* 197 */     read_command(this.req, 12);
/* 198 */     D.println(1, "SCSI Request:");
/* 199 */     D.hexdump(1, this.req, 12);
/* 200 */     this.v.ParentApp.remconsObj.setvmAct(1);
/*     */     
/* 202 */     if (this.media.dio.filehandle == -1) {
/* 203 */       int m = this.media.open(this.selectedDevice, this.targetIsDevice);
/* 204 */       if (m < 0) {
/* 205 */         new VErrorDialog("Could not open CDROM (" + this.media.dio.sysError(-m) + ")", false);
/* 206 */         throw new IOException("Couldn't open cdrom " + m);
/*     */       } 
/*     */     } 
/*     */     int i;
/* 210 */     for (i = 0; i < commands.length && 
/* 211 */       this.req[0] != (byte)commands[i]; i += 2);
/*     */ 
/*     */     
/* 214 */     if (i != commands.length) {
/* 215 */       int m; j = scsi_length(i, this.req);
/* 216 */       k = commands[i + 1] >> 24;
/* 217 */       i = this.req[0] & 0xFF;
/*     */ 
/*     */       
/* 220 */       if (k == 0) {
/* 221 */         read_complete(this.buffer, j);
/*     */       }
/* 223 */       D.println(1, "SCSI dir=" + k + " len=" + j);
/* 224 */       byte b = 0;
/*     */       do {
/* 226 */         long l1 = System.currentTimeMillis();
/* 227 */         if ((i == 40 || i == 168) && this.do_split_reads) {
/* 228 */           m = split_read();
/*     */         } else {
/* 230 */           m = this.media.scsi(this.req, k, j, this.buffer, this.sense);
/*     */         } 
/* 232 */         long l2 = System.currentTimeMillis();
/*     */         
/* 234 */         D.println(1, "ret=" + m + " sense=" + D.hex(this.sense[0], 2) + " " + D.hex(this.sense[1], 2) + " " + D.hex(this.sense[2], 2) + " Time=" + (l2 - l1));
/*     */         
/* 236 */         if (i == 90) {
/* 237 */           D.println(1, "media type: " + D.hex(this.buffer[3], 2));
/* 238 */           this.reply.setmedia(this.buffer[3]);
/*     */         } 
/* 240 */         if (i == 67) {
/* 241 */           D.hexdump(3, this.buffer, j);
/*     */         }
/*     */         
/* 244 */         if (i == 27)
/*     */         {
/*     */           
/* 247 */           m = 0;
/*     */         }
/*     */ 
/*     */         
/* 251 */         if (i == 40 || i == 168) {
/* 252 */           if (this.sense[1] == 41) {
/*     */             
/* 254 */             m = -1;
/* 255 */           } else if (m < 0 && within_75(this.req)) {
/*     */ 
/*     */             
/* 258 */             this.sense[0] = 5;
/* 259 */             this.sense[1] = 33;
/* 260 */             this.sense[2] = 0;
/* 261 */             m = 0;
/* 262 */           } else if (m < 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 268 */             this.do_split_reads = true;
/*     */           } 
/*     */         }
/*     */         
/* 272 */         if (this.sense[0] != 3 && this.sense[0] != 4)
/*     */           continue; 
/* 274 */         media_err(this.req, this.sense);
/* 275 */         m = -1;
/*     */       
/*     */       }
/* 278 */       while (m < 0 && b++ < this.retrycount);
/*     */       
/* 280 */       j = m;
/* 281 */       if (j < 0 || j > 131072) {
/* 282 */         D.println(0, "AIEE! len out of bounds: " + j + ", cmd: " + D.hex(i, 2) + "\n");
/* 283 */         j = 0;
/* 284 */         this.reply.set(5, 32, 0, 0);
/*     */       } else {
/* 286 */         this.reply.set(this.sense[0], this.sense[1], this.sense[2], j);
/*     */       } 
/*     */     } else {
/*     */       
/* 290 */       D.println(0, "AIEE! Unhandled command" + D.hex(this.req[0], 2) + "\n");
/* 291 */       this.reply.set(5, 32, 0, 0);
/* 292 */       j = 0;
/*     */     } 
/* 294 */     this.reply.send(this.out);
/*     */ 
/*     */     
/* 297 */     if (j != 0)
/* 298 */       this.out.write(this.buffer, 0, j); 
/* 299 */     this.out.flush();
/*     */     
/* 301 */     return true;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/SCSIcdrom.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */