/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import javax.swing.Timer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Connection
/*     */   implements Runnable, ActionListener
/*     */ {
/*     */   public static final int FLOPPY = 1;
/*     */   public static final int CDROM = 2;
/*     */   public static final int USBKEY = 3;
/*     */   Socket s;
/*     */   InputStream in;
/*     */   BufferedOutputStream out;
/*     */   String host;
/*     */   int port;
/*     */   int device;
/*     */   String target;
/*     */   int targetIsDevice;
/*     */   SCSI scsi;
/*     */   boolean writeprot = false;
/*     */   virtdevs v;
/*     */   byte[] pre;
/*     */   byte[] key;
/*     */   boolean changing_disks;
/*     */   VMD5 digest;
/*     */   
/*     */   public Connection(String host, int port, int device, String target, int paramInt3, byte[] pre, byte[] key, virtdevs app) throws IOException {
/*  42 */     this.host = host;
/*  43 */     this.port = port;
/*  44 */     this.device = device;
/*  45 */     this.target = target;
/*  46 */     this.pre = pre;
/*  47 */     this.key = key;
/*  48 */     this.v = app;
/*     */     
/*  50 */     MediaAccess mediaAccess = new MediaAccess();
/*  51 */     int i = mediaAccess.devtype(target);
/*  52 */     if (i == 2 || i == 5) {
/*  53 */       this.targetIsDevice = 1;
/*  54 */       D.println(0, "Got CD or removable connection\n");
/*     */     } else {
/*  56 */       this.targetIsDevice = 0;
/*  57 */       D.println(0, "Got NO CD or removable connection\n");
/*     */     } 
/*     */     
/*  60 */     int j = mediaAccess.open(target, this.targetIsDevice);
/*  61 */     long l = mediaAccess.size();
/*  62 */     mediaAccess.close();
/*     */     
/*  64 */     if (this.device == 1 && l > 2949120L) {
/*  65 */       this.device = 3;
/*     */     }
/*  67 */     this.digest = new VMD5();
/*     */   }
/*     */ 
/*     */   
/*     */   public int connect() throws UnknownHostException, IOException {
/*  72 */     byte[] buf = { 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */ 
/*     */     
/*  76 */     this.s = new Socket(this.host, this.port);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     this.s.setTcpNoDelay(true);
/*  84 */     this.in = this.s.getInputStream();
/*  85 */     this.out = new BufferedOutputStream(this.s.getOutputStream());
/*     */ 
/*     */     
/*  88 */     this.digest.reset();
/*  89 */     this.digest.update(this.pre);
/*  90 */     this.digest.update(this.key);
/*     */     
/*  92 */     System.arraycopy(this.key, 0, buf, 2, this.key.length);
/*     */     
/*  94 */     buf[1] = (byte)this.device;
/*  95 */     if (this.targetIsDevice == 0) {
/*  99 */       buf[1] = (byte)(buf[1] | Byte.MIN_VALUE);
/*     */     }
/* 101 */     this.out.write(buf);
/* 102 */     this.out.flush();
/*     */     
/* 104 */     this.in.read(buf, 0, 4);
/* 105 */     D.println(3, "Hello response0: " + D.hex(buf[0], 2));
/* 106 */     D.println(3, "Hello response1: " + D.hex(buf[1], 2));
/*     */ 
/*     */ 
/*     */     
/* 110 */     if (buf[0] == 32 && buf[1] == 0) {
/* 115 */       D.println(1, "Connected.  Protocol version = " + (buf[3] & 0xFF) + "." + (buf[2] & 0xFF));
/*     */     } else {
/* 117 */       D.println(0, "Unexpected Hello Response!");
/* 118 */       this.s.close();
/* 119 */       this.s = null;
/* 120 */       this.in = null;
/* 121 */       this.out = null;
/* 122 */       return buf[0];
/*     */     } 
/* 124 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 129 */     if (this.scsi != null) {
/*     */       try {
/* 131 */         this.scsi.send_disconnect();
/* 132 */         Timer timer = new Timer(2000, this);
/* 133 */         timer.setRepeats(false);
/* 134 */         timer.start();
/* 135 */         this.scsi.change_disk();
/* 136 */         timer.stop();
/*     */       }
/*     */       catch (Exception exception) {
/*     */         
/* 140 */         this.scsi.change_disk();
/*     */       } 
/*     */     } else {
/* 143 */       internal_close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/*     */     try {
/* 150 */       internal_close();
/* 151 */     } catch (Exception exception) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void internal_close() throws IOException {
/* 157 */     if (this.s != null)
/* 158 */       this.s.close(); 
/* 159 */     this.s = null;
/* 160 */     this.in = null;
/* 161 */     this.out = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWriteProt(boolean paramBoolean) {
/* 166 */     this.writeprot = paramBoolean;
/* 167 */     if (this.scsi != null) {
/* 168 */       this.scsi.setWriteProt(this.writeprot);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void change_disk(String paramString) throws IOException {
/*     */     boolean bool;
/* 175 */     MediaAccess mediaAccess = new MediaAccess();
/* 176 */     int i = mediaAccess.devtype(paramString);
/* 177 */     if (i == 2 || i == 5) {
/* 178 */       bool = true;
/*     */     } else {
/* 180 */       bool = false;
/*     */     } 
/* 182 */     if (!bool) {
/* 183 */       int j = mediaAccess.open(paramString, 0);
/* 184 */       mediaAccess.close();
/*     */     } 
/* 186 */     this.target = paramString;
/* 187 */     this.targetIsDevice = bool;
/* 188 */     this.changing_disks = true;
/* 189 */     this.scsi.change_disk();
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/* 194 */     System.out.println("Message before invoking  connection run method");
/*     */     while (true) {
/* 196 */       this.changing_disks = false;
/*     */       try {
/* 198 */         if (this.device == 1 || this.device == 3) {
/* 199 */           this.scsi = new SCSIFloppy(this.s, this.in, this.out, this.target, this.targetIsDevice, this.v);
/* 200 */         } else if (this.device == 2) {
/* 201 */           if (this.targetIsDevice == 1) {
/* 202 */             this.scsi = new SCSIcdrom(this.s, this.in, this.out, this.target, 1, this.v);
/*     */           } else {
/* 204 */             this.scsi = new SCSIcdimage(this.s, this.in, this.out, this.target, 0, this.v);
/*     */           } 
/*     */         } else {
/* 207 */           D.println(0, "Unsupported virtual device " + this.device);
/*     */           return;
/*     */         } 
/*     */       } catch (Exception exception) {
/* 211 */         D.println(0, "Exception while opening " + this.target + "(" + exception + ")");
/*     */       } 
/*     */       
/* 214 */       this.scsi.setWriteProt(this.writeprot); try {
/*     */         do {
/*     */         
/* 217 */         } while (this.scsi.process());
/*     */         
/* 219 */         System.out.println("Connection can not be stablished");
/*     */       }
/*     */       catch (IOException iOException) {
/*     */         
/* 223 */         D.println(1, "Exception in Connection::run() " + iOException);
/* 224 */         iOException.printStackTrace();
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 229 */       D.println(3, "Closing scsi and socket");
/*     */       try {
/* 231 */         this.scsi.close();
/* 232 */         if (!this.changing_disks)
/* 233 */           internal_close(); 
/*     */       } catch (IOException iOException) {
/* 235 */         D.println(0, "Exception closing connection " + iOException);
/*     */       } 
/* 237 */       this.scsi = null;
/* 238 */       if (!this.changing_disks) {
/* 239 */         if (this.device == 1 || this.device == 3) {
/* 240 */           System.out.println("Message before invoking fdDisconnect");
/* 241 */           this.v.fdDisconnect();
/* 242 */         } else if (this.device == 2) {
/* 243 */           System.out.println("Message before invoking cdDisconnect");
/* 244 */           this.v.cdDisconnect();
/*     */         } 
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/Connection.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */