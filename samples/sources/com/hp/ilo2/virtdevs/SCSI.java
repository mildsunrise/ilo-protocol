/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
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
/*     */ public abstract class SCSI
/*     */ {
/*     */   public static final int SCSI_FORMAT_UNIT = 4;
/*     */   public static final int SCSI_INQUIRY = 18;
/*     */   public static final int SCSI_MODE_SELECT_6 = 21;
/*     */   public static final int SCSI_MODE_SELECT = 85;
/*     */   public static final int SCSI_MODE_SENSE_6 = 26;
/*     */   public static final int SCSI_MODE_SENSE = 90;
/*     */   public static final int SCSI_PA_MEDIA_REMOVAL = 30;
/*     */   public static final int SCSI_READ_10 = 40;
/*     */   public static final int SCSI_READ_12 = 168;
/*     */   public static final int SCSI_READ_CAPACITY = 37;
/*     */   public static final int SCSI_READ_CAPACITIES = 35;
/*     */   public static final int SCSI_REQUEST_SENSE = 3;
/*     */   public static final int SCSI_REZERO_UNIT = 1;
/*     */   public static final int SCSI_SEEK = 43;
/*     */   public static final int SCSI_SEND_DIAGNOSTIC = 29;
/*     */   public static final int SCSI_START_STOP_UNIT = 27;
/*     */   public static final int SCSI_TEST_UNIT_READY = 0;
/*     */   public static final int SCSI_VERIFY = 47;
/*     */   public static final int SCSI_WRITE_10 = 42;
/*     */   public static final int SCSI_WRITE_12 = 170;
/*     */   public static final int SCSI_WRITE_VERIFY = 46;
/*     */   public static final int SCSI_READ_CD = 190;
/*     */   public static final int SCSI_READ_CD_MSF = 185;
/*     */   public static final int SCSI_READ_HEADER = 68;
/*     */   public static final int SCSI_READ_SUBCHANNEL = 66;
/*     */   public static final int SCSI_READ_TOC = 67;
/*     */   public static final int SCSI_STOP_PLAY_SCAN = 78;
/*     */   public static final int SCSI_MECHANISM_STATUS = 189;
/*     */   public static final int SCSI_GET_EVENT_STATUS = 74;
/*  53 */   MediaAccess media = new MediaAccess();
/*  54 */   ReplyHeader reply = new ReplyHeader();
/*     */   
/*     */   String selectedDevice;
/*     */   
/*     */   protected InputStream in;
/*     */   
/*     */   protected BufferedOutputStream out;
/*     */   
/*     */   protected Socket sock;
/*     */   
/*     */   boolean writeprot = false;
/*     */   
/*     */   boolean please_exit = false;
/*  67 */   int targetIsDevice = 0;
/*  68 */   byte[] buffer = new byte[131072];
/*  69 */   byte[] req = new byte[12];
/*     */ 
/*     */   
/*     */   public void setWriteProt(boolean paramBoolean) {
/*  73 */     this.writeprot = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWriteProt() {
/*  78 */     D.println(3, "media.wp = " + this.media.wp());
/*  79 */     return this.media.wp();
/*     */   }
/*     */ 
/*     */   
/*     */   public SCSI(Socket paramSocket, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, String paramString, int paramInt) {
/*  84 */     this.sock = paramSocket;
/*  85 */     this.in = paramInputStream;
/*  86 */     this.out = paramBufferedOutputStream;
/*  87 */     this.selectedDevice = paramString;
/*  88 */     this.targetIsDevice = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  93 */     this.media.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static int mk_int32(byte[] paramArrayOfbyte, int paramInt) {
/*  98 */     byte b1 = paramArrayOfbyte[paramInt + 0];
/*  99 */     byte b2 = paramArrayOfbyte[paramInt + 1];
/* 100 */     byte b3 = paramArrayOfbyte[paramInt + 2];
/* 101 */     byte b4 = paramArrayOfbyte[paramInt + 3];
/*     */     
/* 103 */     return (b1 & 0xFF) << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | b4 & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int mk_int24(byte[] paramArrayOfbyte, int paramInt) {
/* 109 */     byte b1 = paramArrayOfbyte[paramInt + 0];
/* 110 */     byte b2 = paramArrayOfbyte[paramInt + 1];
/* 111 */     byte b3 = paramArrayOfbyte[paramInt + 2];
/*     */     
/* 113 */     return (b1 & 0xFF) << 16 | (b2 & 0xFF) << 8 | b3 & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int mk_int16(byte[] paramArrayOfbyte, int paramInt) {
/* 119 */     byte b1 = paramArrayOfbyte[paramInt + 0];
/* 120 */     byte b2 = paramArrayOfbyte[paramInt + 1];
/* 121 */     return (b1 & 0xFF) << 8 | b2 & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int read_complete(byte[] paramArrayOfbyte, int paramInt) throws IOException {
/* 129 */     int i = 0;
/* 130 */     int j = 0;
/* 131 */     while (paramInt > 0) {
/*     */ 
/*     */       
/*     */       try {
/* 135 */         this.sock.setSoTimeout(1000);
/* 136 */         j = this.in.read(paramArrayOfbyte, i, paramInt);
/*     */       
/*     */       }
/* 139 */       catch (InterruptedIOException interruptedIOException) {
/*     */         continue;
/* 141 */       }  if (j < 0)
/*     */         break; 
/* 143 */       paramInt -= j;
/* 144 */       i += j;
/*     */     } 
/* 146 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int read_command(byte[] paramArrayOfbyte, int paramInt) throws IOException {
/* 151 */     int i = 0;
/*     */     while (true) {
/*     */       try {
/* 154 */         this.sock.setSoTimeout(1000);
/* 155 */         i = this.in.read(paramArrayOfbyte, 0, paramInt);
/*     */       } catch (InterruptedIOException interruptedIOException) {
/* 157 */         this.reply.keepalive(true);
/* 158 */         D.println(3, "Sending keepalive");
/* 159 */         this.reply.send(this.out);
/* 160 */         this.out.flush();
/* 161 */         this.reply.keepalive(false);
/* 162 */         if (this.please_exit)
/*     */           break; 
/*     */         continue;
/*     */       } 
/* 166 */       if ((paramArrayOfbyte[0] & 0xFF) == 254) {
/* 167 */         this.reply.sendsynch(this.out, paramArrayOfbyte);
/* 168 */         this.out.flush();
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 173 */     if (this.please_exit)
/* 174 */       throw new IOException("Asked to exit"); 
/* 175 */     if (i < 0)
/* 176 */       throw new IOException("Socket Closed"); 
/* 177 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public void send_disconnect() {
/*     */     try {
/* 183 */       this.reply.disconnect(true);
/*     */       
/* 185 */       this.reply.send(this.out);
/* 186 */       this.out.flush();
/* 187 */       this.reply.disconnect(false);
/*     */     } catch (Exception exception) {
/* 189 */       D.println(1, "Exception in send_disconnect" + exception);
/* 190 */       exception.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract boolean process() throws IOException;
/*     */   
/*     */   public void change_disk() {
/* 198 */     this.please_exit = true;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/SCSI.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */