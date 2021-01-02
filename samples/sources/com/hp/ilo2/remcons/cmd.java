/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
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
/*     */ public class cmd
/*     */   implements Runnable
/*     */ {
/*     */   protected Thread receiver;
/*     */   protected Socket s;
/*     */   protected DataInputStream in;
/*     */   protected DataOutputStream out;
/*  42 */   protected String login = "";
/*     */ 
/*     */   
/*  45 */   protected String host = "";
/*     */   
/*     */   public static final int TELNET_PORT = 23;
/*     */   
/*  49 */   protected int port = 23;
/*     */ 
/*     */   
/*  52 */   protected int connected = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   remcons cmdHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void transmit(String paramString) {
/*  65 */     System.out.println("in cmd::transmit");
/*  66 */     if (this.out == null) {
/*     */       return;
/*     */     }
/*  69 */     if (paramString.length() != 0) {
/*  70 */       byte[] arrayOfByte = new byte[paramString.length()];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  79 */       for (byte b = 0; b < paramString.length(); b++) {
/*  80 */         arrayOfByte[b] = (byte)paramString.charAt(b);
/*     */       }
/*     */       
/*     */       try {
/*  84 */         this.out.write(arrayOfByte, 0, arrayOfByte.length);
/*     */       } catch (IOException iOException) {
/*     */         
/*  87 */         System.out.println("telnet.transmit() IOException: " + iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  94 */     String str = "";
/*     */     try {
/*  96 */       str = this.cmdHandler.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*  98 */       System.out.println("VSeizeDialog:getLocalString" + exception.getMessage());
/*     */     } 
/* 100 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {
/*     */     try {
/* 108 */       this.out.write(paramArrayOfbyte, 0, paramInt);
/*     */     } catch (IOException iOException) {
/*     */       
/* 111 */       System.out.println("cmd.transmitb() IOException: " + iOException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendBool(boolean paramBoolean) {
/* 117 */     byte[] arrayOfByte = new byte[4];
/* 118 */     if (paramBoolean == true) {
/* 119 */       arrayOfByte[0] = 4;
/*     */     } else {
/*     */       
/* 122 */       arrayOfByte[0] = 3;
/*     */     } 
/* 124 */     arrayOfByte[1] = 0;
/* 125 */     arrayOfByte[2] = 0;
/* 126 */     arrayOfByte[3] = 0;
/* 127 */     transmitb(arrayOfByte, arrayOfByte.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 137 */     byte[] arrayOfByte1 = new byte[12];
/* 138 */     byte[] arrayOfByte2 = new byte[1];
/* 139 */     byte[] arrayOfByte3 = new byte[4];
/* 140 */     byte[] arrayOfByte4 = new byte[128];
/*     */ 
/*     */ 
/*     */     
/* 144 */     int i = 0;
/*     */     
/* 146 */     short s2 = 0;
/* 147 */     short s1 = 0;
/*     */     try {
/*     */       while (true) {
/*     */         String str1, str2, str3, str4, str5;
/*     */         boolean bool;
/* 152 */         byte b = 0; int j = b;
/* 153 */         while (b < 12) {
/* 154 */           j = this.in.read(arrayOfByte2, 0, 1);
/* 155 */           if (j == 1) {
/* 156 */             arrayOfByte1[b++] = arrayOfByte2[0];
/*     */           }
/*     */         } 
/* 159 */         byte b1 = arrayOfByte1[0];
/* 160 */         byte b2 = arrayOfByte1[4];
/* 161 */         s1 = (short)arrayOfByte1[8];
/* 162 */         s2 = (short)arrayOfByte1[10];
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
/* 174 */         switch (b1) {
/*     */           case 2:
/* 176 */             System.out.println("Received Post complete notification\n");
/* 177 */             this.cmdHandler.session.post_complete = true;
/* 178 */             this.cmdHandler.session.set_status(4, "");
/*     */             break;
/*     */           
/*     */           case 3:
/* 182 */             if (b2 != 1)
/* 183 */               System.out.println("Invalid size for cmd: " + b1 + " size:" + b2); 
/* 184 */             this.in.read(arrayOfByte2, 0, 1);
/* 185 */             this.cmdHandler.setPwrStatusPower(arrayOfByte2[0]);
/*     */             break;
/*     */           case 4:
/* 188 */             if (b2 != 1)
/* 189 */               System.out.println("Invalid size for cmd: " + b1 + " size:" + b2); 
/* 190 */             this.in.read(arrayOfByte2, 0, 1);
/* 191 */             this.cmdHandler.setPwrStatusHealth(arrayOfByte2[0]);
/*     */             break;
/*     */           case 5:
/* 194 */             if (!this.cmdHandler.session.post_complete) {
/* 195 */               StringBuffer stringBuffer = new StringBuffer(16);
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 200 */               j = this.in.read(arrayOfByte4, 0, 2);
/* 201 */               String str7 = Integer.toHexString(0xFF & arrayOfByte4[1]).toUpperCase();
/* 202 */               String str8 = Integer.toHexString(0xFF & arrayOfByte4[0]).toUpperCase();
/* 203 */               String str6 = stringBuffer.append(this.cmdHandler.getLocalString(12582)).append(str7).append(str8).toString();
/*     */               
/* 205 */               this.cmdHandler.session.set_status(4, str6);
/*     */             } 
/*     */             break;
/*     */           case 6:
/* 209 */             System.out.println("Seized command notification\n");
/*     */ 
/*     */             
/* 212 */             j = this.in.read(arrayOfByte4, 0, 128);
/* 213 */             str1 = "UNKNOWN";
/* 214 */             str2 = "UNKNOWN";
/* 215 */             System.out.println("Data rcvd for acquire " + arrayOfByte4 + "rd count " + j);
/* 216 */             if (j > 0) {
/* 217 */               String str = new String(arrayOfByte4);
/* 218 */               System.out.println("Pakcet " + str);
/* 219 */               str1 = str.substring(0, 63).trim();
/* 220 */               str2 = str.substring(64, 127).trim();
/* 221 */               if (str1.length() <= 0) {
/* 222 */                 str1 = "UNKNOWN";
/*     */               }
/* 224 */               if (str2.length() <= 0) {
/* 225 */                 str2 = "UNKNOWN";
/*     */               }
/*     */             } else {
/*     */               
/* 229 */               System.out.println("Invalid acquire info");
/*     */             } 
/* 231 */             i = this.cmdHandler.seize_dialog(str1, str2, s2);
/* 232 */             if (i == 0) {
/* 233 */               sendBool(true);
/* 234 */               this.cmdHandler.seize_confirmed();
/*     */               break;
/*     */             } 
/* 237 */             sendBool(false);
/*     */             break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case 7:
/* 248 */             this.in.read(arrayOfByte3, 0, 4);
/* 249 */             this.cmdHandler.ack(arrayOfByte3[0], arrayOfByte3[1], arrayOfByte3[2], arrayOfByte3[3]);
/*     */             break;
/*     */           
/*     */           case 8:
/* 253 */             System.out.println("Playback not supported now.\n");
/*     */             break;
/*     */           
/*     */           case 9:
/* 257 */             System.out.println("Share command notification\n");
/*     */ 
/*     */             
/* 260 */             j = this.in.read(arrayOfByte4, 0, 128);
/* 261 */             str3 = "UNKNOWN";
/* 262 */             str4 = "UNKNOWN";
/*     */ 
/*     */             
/* 265 */             if (j > 0) {
/* 266 */               String str = new String(arrayOfByte4);
/* 267 */               System.out.println("Pakcet " + str);
/* 268 */               str3 = str.substring(0, 63).trim();
/* 269 */               str4 = str.substring(64, 127).trim();
/* 270 */               if (str3.length() <= 0) {
/* 271 */                 str3 = "UNKNOWN";
/*     */               }
/* 273 */               if (str4.length() <= 0) {
/* 274 */                 str4 = "UNKNOWN";
/*     */               }
/*     */             } else {
/*     */               
/* 278 */               System.out.println("Invalid acquire info");
/*     */             } 
/* 280 */             sendBool(false);
/*     */             
/* 282 */             this.cmdHandler.shared(str4, str3);
/*     */             break;
/*     */           
/*     */           case 10:
/* 286 */             System.out.println("Firmware upgrade in progress notification\n");
/* 287 */             this.cmdHandler.firmwareUpgrade();
/*     */             break;
/*     */           case 11:
/* 290 */             System.out.println("Un authorized action performed\n");
/* 291 */             str5 = "";
/* 292 */             bool = false;
/* 293 */             switch (s2) {
/*     */               case 2:
/* 295 */                 str5 = getLocalString(8293);
/* 296 */                 bool = true;
/*     */                 break;
/*     */               case 3:
/* 299 */                 str5 = getLocalString(8294);
/*     */                 break;
/*     */               
/*     */               case 4:
/* 303 */                 str5 = getLocalString(8295);
/*     */                 break;
/*     */               default:
/* 306 */                 str5 = "{0x" + s2 + "}";
/*     */                 break;
/*     */             } 
/* 309 */             this.cmdHandler.unAuthorized(str5, bool);
/*     */             break;
/*     */           
/*     */           case 13:
/* 313 */             this.in.read(arrayOfByte2, 0, 1);
/* 314 */             System.out.println("VM notification from firmware\n");
/*     */             break;
/*     */ 
/*     */           
/*     */           case 14:
/* 319 */             System.out.println("Unlicensed notification from firmware\n");
/* 320 */             this.cmdHandler.UnlicensedShutdown();
/*     */             break;
/*     */           case 15:
/* 323 */             System.out.println("Reset notification from firmware\n");
/* 324 */             this.cmdHandler.resetShutdown();
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 332 */         b1 = 0;
/*     */       } 
/*     */     } catch (Exception exception) {
/*     */       
/* 336 */       System.out.println("CMD exception: " + exception.toString());
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
/*     */   public boolean connectCmd(remcons paramremcons, String paramString, int paramInt) {
/*     */     try {
/* 353 */       this.cmdHandler = paramremcons;
/*     */ 
/*     */ 
/*     */       
/* 357 */       byte[] arrayOfByte1 = new byte[32];
/* 358 */       byte[] arrayOfByte2 = new byte[2];
/*     */ 
/*     */       
/* 361 */       this.s = new Socket(paramString, paramInt);
/*     */       try {
/* 363 */         this.s.setSoLinger(true, 0);
/*     */       } catch (SocketException socketException) {
/*     */         
/* 366 */         System.out.println("connectCmd linger SocketException: " + socketException);
/*     */       } 
/*     */       
/* 369 */       this.in = new DataInputStream(this.s.getInputStream());
/* 370 */       this.out = new DataOutputStream(this.s.getOutputStream());
/*     */       
/* 372 */       byte b = this.in.readByte();
/* 373 */       if (b == 80) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 378 */         arrayOfByte2[0] = 2;
/* 379 */         arrayOfByte2[1] = 32;
/* 380 */         arrayOfByte1 = paramremcons.ParentApp.getParameter("RCINFO1").getBytes();
/*     */ 
/*     */         
/* 383 */         if (paramremcons.ParentApp.optional_features.indexOf("ENCRYPT_KEY") != -1) {
/*     */           
/* 385 */           for (byte b1 = 0; b1 < arrayOfByte1.length; b1++)
/*     */           {
/* 387 */             arrayOfByte1[b1] = (byte)(arrayOfByte1[b1] ^ (byte)paramremcons.ParentApp.enc_key.charAt(b1 % paramremcons.ParentApp.enc_key.length()));
/*     */           }
/* 389 */           if (paramremcons.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
/*     */             
/* 391 */             arrayOfByte2[1] = (byte)(arrayOfByte2[1] | 0x40);
/*     */           }
/*     */           else {
/*     */             
/* 395 */             arrayOfByte2[1] = (byte)(arrayOfByte2[1] | 0x80);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 400 */         byte[] arrayOfByte = new byte[arrayOfByte2.length + arrayOfByte1.length];
/* 401 */         System.arraycopy(arrayOfByte2, 0, arrayOfByte, 0, arrayOfByte2.length);
/* 402 */         System.arraycopy(arrayOfByte1, 0, arrayOfByte, arrayOfByte2.length, arrayOfByte1.length);
/*     */ 
/*     */         
/* 405 */         String str = new String(arrayOfByte);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 414 */         transmit(str);
/* 415 */         b = this.in.readByte();
/* 416 */         if (b == 82) {
/*     */           
/* 418 */           this.receiver = new Thread(this);
/* 419 */           this.receiver.setName("cmd_rcvr");
/* 420 */           this.receiver.start();
/*     */         }
/*     */         else {
/*     */           
/* 424 */           System.out.println("login failed. read data" + b);
/*     */         } 
/*     */       } else {
/*     */         
/* 428 */         System.out.println("Socket connection failure... ");
/*     */       }
/*     */     
/*     */     } catch (SocketException socketException) {
/*     */       
/* 433 */       System.out.println("telnet.connect() SocketException: " + socketException);
/* 434 */       this.s = null;
/* 435 */       this.in = null;
/* 436 */       this.out = null;
/* 437 */       this.receiver = null;
/* 438 */       this.connected = 0;
/*     */     } catch (UnknownHostException unknownHostException) {
/*     */       
/* 441 */       System.out.println("telnet.connect() UnknownHostException: " + unknownHostException);
/* 442 */       this.s = null;
/* 443 */       this.in = null;
/* 444 */       this.out = null;
/* 445 */       this.receiver = null;
/* 446 */       this.connected = 0;
/*     */     } catch (IOException iOException) {
/*     */       
/* 449 */       System.out.println("telnet.connect() IOException: " + iOException);
/* 450 */       this.s = null;
/* 451 */       this.in = null;
/* 452 */       this.out = null;
/* 453 */       this.receiver = null;
/* 454 */       this.connected = 0;
/*     */     } 
/*     */     
/* 457 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void disconnectCmd() {
/* 465 */     if (this.receiver != null && this.receiver.isAlive()) {
/* 466 */       this.receiver.stop();
/*     */     }
/* 468 */     this.receiver = null;
/*     */     
/* 470 */     if (this.s != null) {
/*     */       try {
/* 472 */         System.out.println("Closing socket");
/* 473 */         this.s.close();
/*     */       } catch (IOException iOException) {
/*     */         
/* 476 */         System.out.println("telnet.disconnect() IOException: " + iOException);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 481 */     if (this.cmdHandler != null) {
/* 482 */       this.cmdHandler.setPwrStatusHealth(3);
/* 483 */       this.cmdHandler.setPwrStatusPower(0);
/* 484 */       this.cmdHandler = null;
/*     */     } 
/* 486 */     this.s = null;
/* 487 */     this.in = null;
/* 488 */     this.out = null;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/cmd.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */