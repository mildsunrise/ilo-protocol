/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.Socket;
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
/*     */ public class SCSIFloppy
/*     */   extends SCSI
/*     */ {
/*  25 */   int fdd_state = 0;
/*     */   long media_sz;
/*  27 */   Date date = new Date();
/*  28 */   byte[] rcs_resp = new byte[] { 0, 0, 0, 16, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 11, 64, 0, 0, 2, 0 };
/*     */ 
/*     */   
/*     */   virtdevs v;
/*     */ 
/*     */   
/*     */   VErrorDialog dlg;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteProt(boolean paramBoolean) {
/*  39 */     this.writeprot = paramBoolean;
/*     */     
/*  41 */     if (this.fdd_state == 2) {
/*  42 */       this.fdd_state = 0;
/*     */     }
/*     */   }
/*     */   
/*     */   public SCSIFloppy(Socket paramSocket, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, String paramString, int paramInt, virtdevs paramvirtdevs) throws IOException {
/*  47 */     super(paramSocket, paramInputStream, paramBufferedOutputStream, paramString, paramInt);
/*  48 */     int i = this.media.open(paramString, paramInt);
/*  49 */     D.print(1, "open returns " + i);
/*  50 */     this.v = paramvirtdevs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean process() throws IOException {
/*  56 */     this.date.setTime(System.currentTimeMillis());
/*  57 */     D.println(1, "Date = " + this.date);
/*  58 */     D.println(1, "Device: " + this.selectedDevice + " (" + this.targetIsDevice + ")");
/*  59 */     read_command(this.req, 12);
/*  60 */     D.print(1, "SCSI Request: ");
/*  61 */     D.hexdump(1, this.req, 12);
/*  62 */     this.media_sz = this.media.size();
/*  63 */     this.v.ParentApp.remconsObj.setvmAct(1);
/*     */ 
/*     */ 
/*     */     
/*  67 */     if (this.media_sz < 0L || (this.media.dio != null && this.media.dio.filehandle == -1)) {
/*  68 */       D.println(1, "Disk change detected\n");
/*  69 */       this.media.close();
/*  70 */       this.media.open(this.selectedDevice, this.targetIsDevice);
/*  71 */       this.media_sz = this.media.size();
/*  72 */       this.fdd_state = 0;
/*     */     } 
/*  74 */     D.println(1, "retval=" + this.media_sz + " type=" + this.media.type() + " physdrive=" + ((this.media.dio != null) ? this.media.dio.PhysicalDevice : -1));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     if (this.media_sz == -6L) {
/*  82 */       new VErrorDialog(this.v.ParentApp.dispFrame, this.selectedDevice + " " + this.v.ParentApp.remconsObj.getLocalString(8288) + "\n\n" + this.v.ParentApp.remconsObj.getLocalString(8239));
/*     */       
/*  84 */       return false;
/*  85 */     }  if (this.media_sz <= 0L) {
/*  86 */       this.reply.setmedia(0);
/*  87 */       this.fdd_state = 0;
/*     */     }
/*     */     else {
/*     */       
/*  91 */       this.reply.setmedia(36);
/*  92 */       this.fdd_state++;
/*  93 */       if (this.fdd_state > 2) {
/*  94 */         this.fdd_state = 2;
/*     */       }
/*     */     } 
/*  97 */     if (!this.writeprot && this.media.wp()) {
/*  98 */       this.writeprot = true;
/*     */     }
/* 100 */     switch (this.req[0] & 0xFF)
/*     */     { case 4:
/* 102 */         client_format_unit(this.req);
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
/* 135 */         return true;case 30: client_pa_media_removal(this.req); return true;case 37: client_read_capacity(); return true;case 29: client_send_diagnostic(); return true;case 0: client_test_unit_ready(); return true;case 40: case 168: client_read(this.req); return true;case 42: case 46: case 170: client_write(this.req); return true;case 35: client_read_capacities(); return true;case 27: client_start_stop_unit(this.req); return true; }  D.println(0, "Unknown request:cmd = " + Integer.toHexString(this.req[0])); return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void client_read_capacities() throws IOException {
/* 141 */     if (this.fdd_state != 1) {
/* 142 */       this.reply.set(0, 0, 0, this.rcs_resp.length);
/*     */     } else {
/* 144 */       this.reply.set(6, 40, 0, this.rcs_resp.length);
/* 145 */       this.fdd_state = 2;
/*     */     } 
/* 147 */     if (this.media.type() == 0) {
/* 148 */       this.rcs_resp[11] = 0; this.rcs_resp[10] = 0; this.rcs_resp[7] = 0; this.rcs_resp[6] = 0; this.rcs_resp[5] = 0; this.rcs_resp[4] = 0;
/* 149 */     } else if (this.media.type() == 100) {
/* 150 */       long l = this.media.size() / 512L;
/* 151 */       this.rcs_resp[4] = (byte)(int)(l >> 24L & 0xFFL);
/* 152 */       this.rcs_resp[5] = (byte)(int)(l >> 16L & 0xFFL);
/* 153 */       this.rcs_resp[6] = (byte)(int)(l >> 8L & 0xFFL);
/* 154 */       this.rcs_resp[7] = (byte)(int)(l >> 0L & 0xFFL);
/* 155 */       this.rcs_resp[10] = 2;
/* 156 */       this.rcs_resp[11] = 0;
/*     */     } else {
/* 158 */       long l = this.media.size() / this.media.dio.BytesPerSec;
/* 159 */       this.rcs_resp[4] = (byte)(int)(l >> 24L & 0xFFL);
/* 160 */       this.rcs_resp[5] = (byte)(int)(l >> 16L & 0xFFL);
/* 161 */       this.rcs_resp[6] = (byte)(int)(l >> 8L & 0xFFL);
/* 162 */       this.rcs_resp[7] = (byte)(int)(l >> 0L & 0xFFL);
/* 163 */       this.rcs_resp[10] = (byte)(this.media.dio.BytesPerSec >> 8 & 0xFF);
/* 164 */       this.rcs_resp[11] = (byte)(this.media.dio.BytesPerSec & 0xFF);
/*     */     } 
/* 166 */     this.reply.setflags(this.writeprot);
/* 167 */     this.reply.send(this.out);
/* 168 */     this.out.write(this.rcs_resp, 0, this.rcs_resp.length);
/* 169 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   void client_send_diagnostic() throws IOException {
/* 174 */     this.fdd_state = 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void client_read(byte[] paramArrayOfbyte) throws IOException {
/* 181 */     boolean bool = (paramArrayOfbyte[0] == 168) ? true : false;
/*     */     
/* 183 */     long l = SCSI.mk_int32(paramArrayOfbyte, 2) * 512L;
/* 184 */     int i = bool ? SCSI.mk_int32(paramArrayOfbyte, 6) : SCSI.mk_int16(paramArrayOfbyte, 7);
/* 185 */     i *= 512;
/*     */     
/* 187 */     D.println(3, "FDIO.client_read:Client read " + l + ", len=" + i);
/*     */ 
/*     */     
/* 190 */     if (l >= 0L && l < this.media_sz) {
/*     */       try {
/* 192 */         this.media.read(l, i, this.buffer);
/* 193 */         this.reply.set(0, 0, 0, i);
/*     */       } catch (IOException iOException) {
/* 195 */         D.println(0, "Exception during read: " + iOException);
/*     */         
/* 197 */         this.reply.set(3, 16, 0, 0);
/* 198 */         i = 0;
/*     */       } 
/*     */     } else {
/*     */       
/* 202 */       this.reply.set(5, 33, 0, 0);
/* 203 */       i = 0;
/*     */     } 
/*     */     
/* 206 */     this.reply.setflags(this.writeprot);
/* 207 */     this.reply.send(this.out);
/* 208 */     if (i != 0)
/* 209 */       this.out.write(this.buffer, 0, i); 
/* 210 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void client_write(byte[] paramArrayOfbyte) throws IOException {
/* 217 */     boolean bool = (paramArrayOfbyte[0] == 170) ? true : false;
/*     */     
/* 219 */     long l = SCSI.mk_int32(paramArrayOfbyte, 2) * 512L;
/* 220 */     int i = bool ? SCSI.mk_int32(paramArrayOfbyte, 6) : SCSI.mk_int16(paramArrayOfbyte, 7);
/* 221 */     i *= 512;
/*     */     
/* 223 */     D.println(3, "FDIO.client_write:lba = " + l + ", length = " + i);
/* 224 */     read_complete(this.buffer, i);
/*     */     
/* 226 */     if (!this.writeprot) {
/*     */       
/* 228 */       if (l >= 0L && l < this.media_sz) {
/*     */         try {
/* 230 */           this.media.write(l, i, this.buffer);
/* 231 */           this.reply.set(0, 0, 0, 0);
/*     */         } catch (IOException iOException) {
/* 233 */           D.println(0, "Exception during write: " + iOException);
/*     */           
/* 235 */           this.reply.set(3, 16, 0, 0);
/*     */         } 
/*     */       } else {
/*     */         
/* 239 */         this.reply.set(5, 33, 0, 0);
/*     */       } 
/*     */     } else {
/*     */       
/* 243 */       this.reply.set(7, 39, 0, 0);
/*     */     } 
/* 245 */     this.reply.setflags(this.writeprot);
/* 246 */     this.reply.send(this.out);
/* 247 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void client_pa_media_removal(byte[] paramArrayOfbyte) throws IOException {
/* 253 */     if ((paramArrayOfbyte[4] & 0x1) != 0) {
/*     */ 
/*     */ 
/*     */       
/* 257 */       this.reply.set(5, 36, 0, 0);
/*     */     } else {
/* 259 */       this.reply.set(0, 0, 0, 0);
/*     */     } 
/* 261 */     this.reply.setflags(this.writeprot);
/* 262 */     this.reply.send(this.out);
/* 263 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void client_start_stop_unit(byte[] paramArrayOfbyte) throws IOException {
/* 271 */     if ((paramArrayOfbyte[4] & 0x2) != 0) {
/*     */ 
/*     */ 
/*     */       
/* 275 */       this.reply.set(5, 36, 0, 0);
/*     */     } else {
/* 277 */       this.reply.set(0, 0, 0, 0);
/*     */     } 
/* 279 */     this.reply.setflags(this.writeprot);
/* 280 */     this.reply.send(this.out);
/* 281 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   void client_test_unit_ready() throws IOException {
/* 286 */     if (this.fdd_state == 0) {
/* 287 */       D.println(3, "media not present");
/* 288 */       this.reply.set(2, 58, 0, 0);
/* 289 */     } else if (this.fdd_state == 1) {
/* 290 */       D.println(3, "media changed");
/* 291 */       this.reply.set(6, 40, 0, 0);
/* 292 */       this.fdd_state = 2;
/*     */     } else {
/* 294 */       D.println(3, "device ready");
/* 295 */       this.reply.set(0, 0, 0, 0);
/*     */     } 
/* 297 */     this.reply.setflags(this.writeprot);
/* 298 */     this.reply.send(this.out);
/* 299 */     this.out.flush();
/*     */   }
/*     */   
/*     */   void client_format_unit(byte[] paramArrayOfbyte) throws IOException {
/*     */     boolean bool;
/* 304 */     byte[] arrayOfByte = new byte[100];
/* 305 */     int i = SCSI.mk_int16(paramArrayOfbyte, 7);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 310 */     read_complete(arrayOfByte, i);
/* 311 */     D.print(3, "Format params: ");
/* 312 */     D.hexdump(3, arrayOfByte, i);
/* 313 */     int j = arrayOfByte[1] & 0x1;
/*     */     
/* 315 */     if (SCSI.mk_int32(arrayOfByte, 4) == 2880 && SCSI.mk_int24(arrayOfByte, 9) == 512) {
/* 316 */       bool = true;
/* 317 */     } else if (SCSI.mk_int32(arrayOfByte, 4) == 1440 && SCSI.mk_int24(arrayOfByte, 9) == 512) {
/* 318 */       bool = true;
/*     */     } else {
/* 320 */       bool = false;
/*     */     } 
/*     */     
/* 323 */     if (this.writeprot) {
/*     */       
/* 325 */       this.reply.set(7, 39, 0, 0);
/* 326 */     } else if (bool) {
/* 327 */       int k = paramArrayOfbyte[2] & 0xFF;
/*     */       
/* 329 */       this.media.format(bool, k, k, j, j);
/* 330 */       D.println(3, "format");
/* 331 */       this.reply.set(0, 0, 0, 0);
/*     */     } else {
/*     */       
/* 334 */       this.reply.set(5, 38, 0, 0);
/*     */     } 
/*     */     
/* 337 */     this.reply.setflags(this.writeprot);
/* 338 */     this.reply.send(this.out);
/* 339 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   void client_read_capacity() throws IOException {
/* 344 */     byte[] arrayOfByte = { 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */     
/* 347 */     this.reply.set(0, 0, 0, arrayOfByte.length);
/* 348 */     if (this.fdd_state == 0) {
/*     */       
/* 350 */       this.reply.set(2, 58, 0, 0);
/* 351 */     } else if (this.fdd_state == 1) {
/*     */       
/* 353 */       this.reply.set(6, 40, 0, 0);
/*     */     }
/* 355 */     else if (this.media.type() != 0) {
/*     */       
/* 357 */       if (this.media.type() == 100) {
/* 358 */         long l = this.media.size() / 512L - 1L;
/* 359 */         arrayOfByte[0] = (byte)(int)(l >> 24L & 0xFFL);
/* 360 */         arrayOfByte[1] = (byte)(int)(l >> 16L & 0xFFL);
/* 361 */         arrayOfByte[2] = (byte)(int)(l >> 8L & 0xFFL);
/* 362 */         arrayOfByte[3] = (byte)(int)(l >> 0L & 0xFFL);
/* 363 */         arrayOfByte[6] = 2;
/*     */       } else {
/* 365 */         long l = this.media.size() / this.media.dio.BytesPerSec - 1L;
/* 366 */         arrayOfByte[0] = (byte)(int)(l >> 24L & 0xFFL);
/* 367 */         arrayOfByte[1] = (byte)(int)(l >> 16L & 0xFFL);
/* 368 */         arrayOfByte[2] = (byte)(int)(l >> 8L & 0xFFL);
/* 369 */         arrayOfByte[3] = (byte)(int)(l >> 0L & 0xFFL);
/* 370 */         arrayOfByte[6] = (byte)(this.media.dio.BytesPerSec >> 8 & 0xFF);
/* 371 */         arrayOfByte[7] = (byte)(this.media.dio.BytesPerSec & 0xFF);
/*     */       } 
/*     */     } 
/* 374 */     this.reply.setflags(this.writeprot);
/* 375 */     this.reply.send(this.out);
/* 376 */     if (this.fdd_state == 2)
/* 377 */       this.out.write(arrayOfByte, 0, arrayOfByte.length); 
/* 378 */     this.out.flush();
/* 379 */     D.print(3, "FDIO.client_read_capacity: ");
/* 380 */     D.hexdump(3, arrayOfByte, 8);
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/SCSIFloppy.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */