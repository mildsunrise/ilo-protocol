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
/*     */ public class SCSIcdimage
/*     */   extends SCSI
/*     */ {
/*  20 */   int fdd_state = 0;
/*  21 */   int event_state = 0;
/*     */   
/*     */   long media_sz;
/*     */   virtdevs v;
/*     */   
/*     */   public void setWriteProt(boolean paramBoolean) {
/*  27 */     this.writeprot = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public SCSIcdimage(Socket paramSocket, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, String paramString, int paramInt, virtdevs paramvirtdevs) throws IOException {
/*  32 */     super(paramSocket, paramInputStream, paramBufferedOutputStream, paramString, paramInt);
/*  33 */     int i = this.media.open(paramString, 0);
/*  34 */     D.println(1, "Media open returns " + i + " / " + this.media.size() + " bytes");
/*  35 */     this.v = paramvirtdevs;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean process() throws IOException {
/*  40 */     boolean bool = true;
/*  41 */     D.println(1, "Device: " + this.selectedDevice + " (" + this.targetIsDevice + ")");
/*  42 */     read_command(this.req, 12);
/*  43 */     D.print(1, "SCSI Request: ");
/*  44 */     D.hexdump(1, this.req, 12);
/*  45 */     this.v.ParentApp.remconsObj.setvmAct(1);
/*     */     
/*  47 */     this.media_sz = this.media.size();
/*  48 */     if (this.media_sz == 0L) {
/*  49 */       this.reply.setmedia(0);
/*  50 */       this.fdd_state = 0;
/*     */       
/*  52 */       this.event_state = 4;
/*     */     }
/*     */     else {
/*     */       
/*  56 */       this.reply.setmedia(1);
/*  57 */       this.fdd_state++;
/*  58 */       if (this.fdd_state > 2) {
/*  59 */         this.fdd_state = 2;
/*     */       }
/*  61 */       if (this.event_state == 4)
/*  62 */         this.event_state = 0; 
/*  63 */       this.event_state++;
/*  64 */       if (this.event_state > 2) {
/*  65 */         this.event_state = 2;
/*     */       }
/*     */     } 
/*  68 */     switch (this.req[0] & 0xFF)
/*     */     { case 30:
/*  70 */         client_pa_media_removal(this.req);
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
/* 104 */         return bool;case 37: client_read_capacity(); return bool;case 29: client_send_diagnostic(); return bool;case 0: client_test_unit_ready(); return bool;case 40: case 168: client_read(this.req); return bool;case 27: bool = client_start_stop_unit(this.req); return bool;case 67: client_read_toc(this.req); return bool;case 90: client_mode_sense(this.req); return bool;case 74: client_get_event_status(this.req); return bool; }  D.println(0, "Unknown request:cmd = " + Integer.toHexString(this.req[0])); this.reply.set(5, 36, 0, 0); this.reply.send(this.out); this.out.flush(); return bool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void client_send_diagnostic() throws IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   void client_read(byte[] paramArrayOfbyte) throws IOException {
/* 115 */     boolean bool = (paramArrayOfbyte[0] == 168) ? true : false;
/*     */     
/* 117 */     long l = SCSI.mk_int32(paramArrayOfbyte, 2) * 2048L;
/* 118 */     int i = bool ? SCSI.mk_int32(paramArrayOfbyte, 6) : SCSI.mk_int16(paramArrayOfbyte, 7);
/* 119 */     i *= 2048;
/*     */     
/* 121 */     D.println(3, "CDImage :Client read " + l + ", len=" + i);
/*     */     
/* 123 */     if (this.fdd_state == 0) {
/* 124 */       D.println(3, "media not present");
/* 125 */       this.reply.set(2, 58, 0, 0);
/* 126 */       i = 0;
/* 127 */     } else if (this.fdd_state == 1) {
/* 128 */       D.println(3, "media changed");
/* 129 */       this.reply.set(6, 40, 0, 0);
/* 130 */       i = 0;
/* 131 */       this.fdd_state = 2;
/*     */     
/*     */     }
/* 134 */     else if (l >= 0L && l < this.media_sz) {
/* 135 */       this.media.read(l, i, this.buffer);
/* 136 */       this.reply.set(0, 0, 0, i);
/*     */     } else {
/*     */       
/* 139 */       this.reply.set(5, 33, 0, 0);
/* 140 */       i = 0;
/*     */     } 
/*     */ 
/*     */     
/* 144 */     this.reply.send(this.out);
/* 145 */     if (i != 0)
/* 146 */       this.out.write(this.buffer, 0, i); 
/* 147 */     this.out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void client_pa_media_removal(byte[] paramArrayOfbyte) throws IOException {
/* 153 */     if ((paramArrayOfbyte[4] & 0x1) != 0) {
/*     */ 
/*     */ 
/*     */       
/* 157 */       D.println(3, "Media removal prevented");
/*     */     } else {
/* 159 */       D.println(3, "Media removal allowed");
/*     */     } 
/* 161 */     this.reply.set(0, 0, 0, 0);
/* 162 */     this.reply.send(this.out);
/* 163 */     this.out.flush();
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
/*     */   boolean client_start_stop_unit(byte[] paramArrayOfbyte) throws IOException {
/* 177 */     this.reply.set(0, 0, 0, 0);
/* 178 */     this.reply.send(this.out);
/* 179 */     this.out.flush();
/*     */     
/* 181 */     if ((paramArrayOfbyte[4] & 0x3) == 2) {
/* 182 */       this.fdd_state = 0;
/*     */       
/* 184 */       this.event_state = 4;
/* 185 */       D.println(3, "Media eject");
/* 186 */       return false;
/*     */     } 
/* 188 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void client_test_unit_ready() throws IOException {
/* 194 */     if (this.fdd_state == 0) {
/* 195 */       D.println(3, "media not present");
/* 196 */       this.reply.set(2, 58, 0, 0);
/* 197 */     } else if (this.fdd_state == 1) {
/* 198 */       D.println(3, "media changed");
/* 199 */       this.reply.set(6, 40, 0, 0);
/* 200 */       this.fdd_state = 2;
/*     */     } else {
/* 202 */       D.println(3, "device ready");
/* 203 */       this.reply.set(0, 0, 0, 0);
/*     */     } 
/* 205 */     this.reply.send(this.out);
/* 206 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   void client_read_capacity() throws IOException {
/* 211 */     byte[] arrayOfByte = { 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */     
/* 214 */     this.reply.set(0, 0, 0, arrayOfByte.length);
/* 215 */     if (this.fdd_state == 0) {
/*     */       
/* 217 */       this.reply.set(2, 58, 0, 0);
/* 218 */     } else if (this.fdd_state == 1) {
/*     */       
/* 220 */       this.reply.set(6, 40, 0, 0);
/*     */     } else {
/* 222 */       int i = (int)(this.media.size() / 2048L - 1L);
/* 223 */       arrayOfByte[0] = (byte)(i >> 24 & 0xFF);
/* 224 */       arrayOfByte[1] = (byte)(i >> 16 & 0xFF);
/* 225 */       arrayOfByte[2] = (byte)(i >> 8 & 0xFF);
/* 226 */       arrayOfByte[3] = (byte)(i >> 0 & 0xFF);
/* 227 */       arrayOfByte[6] = 8;
/*     */     } 
/* 229 */     this.reply.send(this.out);
/* 230 */     if (this.fdd_state == 2)
/* 231 */       this.out.write(arrayOfByte, 0, arrayOfByte.length); 
/* 232 */     this.out.flush();
/* 233 */     D.print(3, "client_read_capacity: ");
/* 234 */     D.hexdump(3, arrayOfByte, 8);
/*     */   }
/*     */ 
/*     */   
/*     */   void client_read_toc(byte[] paramArrayOfbyte) throws IOException {
/* 239 */     boolean bool = ((paramArrayOfbyte[1] & 0x2) != 0) ? true : false;
/* 240 */     int i = (paramArrayOfbyte[9] & 0xC0) >> 6;
/* 241 */     int j = (int)(this.media.size() / 2048L);
/* 242 */     double d = j / 75.0D + 2.0D;
/* 243 */     int k = (int)d / 60;
/* 244 */     int m = (int)d % 60;
/* 245 */     int n = (int)((d - (int)d) * 75.0D);
/* 246 */     int i1 = SCSI.mk_int16(paramArrayOfbyte, 7);
/*     */     
/* 248 */     for (byte b = 0; b < i1; b++) {
/* 249 */       this.buffer[b] = 0;
/*     */     }
/* 251 */     if (i == 0) {
/* 252 */       this.buffer[0] = 0;
/* 253 */       this.buffer[1] = 18;
/* 254 */       this.buffer[2] = 1;
/* 255 */       this.buffer[3] = 1;
/*     */       
/* 257 */       this.buffer[4] = 0;
/* 258 */       this.buffer[5] = 20;
/* 259 */       this.buffer[6] = 1;
/* 260 */       this.buffer[7] = 0;
/* 261 */       this.buffer[8] = 0;
/* 262 */       this.buffer[9] = 0;
/* 263 */       this.buffer[10] = bool ? 2 : 0;
/* 264 */       this.buffer[11] = 0;
/*     */       
/* 266 */       this.buffer[12] = 0;
/* 267 */       this.buffer[13] = 20;
/* 268 */       this.buffer[14] = -86;
/* 269 */       this.buffer[15] = 0;
/* 270 */       this.buffer[16] = 0;
/* 271 */       this.buffer[17] = bool ? (byte)k : (byte)(j >> 16 & 0xFF);
/* 272 */       this.buffer[18] = bool ? (byte)m : (byte)(j >> 8 & 0xFF);
/* 273 */       this.buffer[19] = bool ? (byte)n : (byte)(j & 0xFF);
/*     */     } 
/*     */     
/* 276 */     if (i == 1) {
/* 277 */       this.buffer[0] = 0;
/* 278 */       this.buffer[1] = 10;
/* 279 */       this.buffer[2] = 1;
/* 280 */       this.buffer[3] = 1;
/*     */       
/* 282 */       this.buffer[4] = 0;
/* 283 */       this.buffer[5] = 20;
/* 284 */       this.buffer[6] = 1;
/* 285 */       this.buffer[7] = 0;
/* 286 */       this.buffer[8] = 0;
/* 287 */       this.buffer[9] = 0;
/* 288 */       this.buffer[10] = bool ? 2 : 0;
/* 289 */       this.buffer[11] = 0;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 294 */     j = 412;
/* 295 */     if (i1 < j)
/* 296 */       j = i1; 
/* 297 */     D.hexdump(3, this.buffer, j);
/* 298 */     this.reply.set(0, 0, 0, j);
/* 299 */     this.reply.send(this.out);
/* 300 */     this.out.write(this.buffer, 0, j);
/* 301 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   void client_mode_sense(byte[] paramArrayOfbyte) throws IOException {
/* 306 */     this.buffer[0] = 0;
/* 307 */     this.buffer[1] = 8;
/* 308 */     this.buffer[2] = 1;
/* 309 */     this.buffer[3] = 0;
/* 310 */     this.buffer[4] = 0;
/* 311 */     this.buffer[5] = 0;
/* 312 */     this.buffer[6] = 0;
/* 313 */     this.buffer[7] = 0;
/* 314 */     this.reply.set(0, 0, 0, 8);
/* 315 */     D.hexdump(3, this.buffer, 8);
/* 316 */     this.reply.setmedia(this.buffer[2]);
/* 317 */     this.reply.send(this.out);
/* 318 */     this.out.write(this.buffer, 0, 8);
/* 319 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   void client_get_event_status(byte[] paramArrayOfbyte) throws IOException {
/* 324 */     byte b = paramArrayOfbyte[4];
/* 325 */     int i = SCSI.mk_int16(paramArrayOfbyte, 7);
/* 326 */     for (byte b1 = 0; b1 < i; b1++) {
/* 327 */       this.buffer[b1] = 0;
/*     */     }
/*     */ 
/*     */     
/* 331 */     if ((paramArrayOfbyte[1] & 0x1) == 0) {
/* 332 */       this.reply.set(5, 36, 0, 0);
/* 333 */       this.reply.send(this.out);
/* 334 */       this.out.flush();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 344 */     if ((b & 0x10) != 0) {
/*     */ 
/*     */ 
/*     */       
/* 348 */       this.buffer[0] = 0;
/* 349 */       this.buffer[1] = 6;
/*     */       
/* 351 */       this.buffer[2] = 4;
/*     */       
/* 353 */       this.buffer[3] = 16;
/* 354 */       if (this.event_state == 0) {
/*     */         
/* 356 */         this.buffer[4] = 0;
/* 357 */         this.buffer[5] = 0;
/* 358 */       } else if (this.event_state == 1) {
/*     */         
/* 360 */         this.buffer[4] = 4;
/* 361 */         this.buffer[5] = 2;
/*     */ 
/*     */         
/* 364 */         if (i > 4)
/* 365 */           this.event_state = 2; 
/* 366 */       } else if (this.event_state == 4) {
/*     */         
/* 368 */         this.buffer[4] = 3;
/* 369 */         this.buffer[5] = 0;
/*     */ 
/*     */         
/* 372 */         if (i > 4) {
/* 373 */           this.event_state = 0;
/*     */         }
/*     */       } else {
/* 376 */         this.buffer[4] = 0;
/* 377 */         this.buffer[5] = 2;
/*     */       } 
/*     */ 
/*     */       
/* 381 */       D.hexdump(3, this.buffer, 8);
/* 382 */       this.reply.set(0, 0, 0, (i < 8) ? i : 8);
/* 383 */       this.reply.send(this.out);
/* 384 */       this.out.write(this.buffer, 0, (i < 8) ? i : 8);
/* 385 */       this.out.flush();
/*     */     } else {
/*     */       
/* 388 */       this.buffer[0] = 0;
/* 389 */       this.buffer[1] = 2;
/*     */ 
/*     */       
/* 392 */       this.buffer[2] = Byte.MIN_VALUE;
/*     */       
/* 394 */       this.buffer[3] = 16;
/* 395 */       D.hexdump(3, this.buffer, 4);
/* 396 */       this.reply.set(0, 0, 0, (i < 4) ? i : 4);
/* 397 */       this.reply.send(this.out);
/* 398 */       this.out.write(this.buffer, 0, (i < 4) ? i : 4);
/* 399 */       this.out.flush();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/SCSIcdimage.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */