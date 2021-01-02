/*    */ package com.hp.ilo2.virtdevs;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DirectIO
/*    */ {
/*    */   public int media_type;
/*    */   public int StartCylinder;
/*    */   public int EndCylinder;
/*    */   public int StartHead;
/*    */   public int EndHead;
/*    */   public int Cylinders;
/*    */   public int TracksPerCyl;
/*    */   public int SecPerTrack;
/*    */   public int BytesPerSec;
/*    */   public int media_size;
/* 32 */   public int filehandle = -1;
/* 33 */   public int aux_handle = -1;
/*    */   
/*    */   public long bufferaddr;
/*    */   public int wp;
/*    */   public int misc0;
/*    */   public int PhysicalDevice;
/*    */   
/*    */   public native int open(String paramString);
/*    */   
/*    */   public native int close();
/*    */   
/*    */   public native int read(long paramLong, int paramInt, byte[] paramArrayOfbyte);
/*    */   
/*    */   public native int write(long paramLong, int paramInt, byte[] paramArrayOfbyte);
/*    */   
/*    */   public native long size();
/*    */   
/*    */   public native int format();
/*    */   
/*    */   public native String[] devices();
/*    */   
/*    */   public native int devtype(String paramString);
/*    */   
/*    */   public native int scsi(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, int paramInt3);
/*    */   
/*    */   public native String sysError(int paramInt);
/*    */   
/*    */   protected void finalize() {
/* 61 */     if (this.filehandle != -1) {
/* 62 */       close();
/*    */     }
/*    */   }
/*    */   
/* 66 */   public static int keydrive = 1;
/*    */   static {
/* 68 */     String str1 = "cpqma-" + Integer.toHexString(virtdevs.UID) + MediaAccess.dllext;
/* 69 */     String str2 = System.getProperty("file.separator");
/* 70 */     String str3 = System.getProperty("java.io.tmpdir");
/* 71 */     String str4 = System.getProperty("os.name").toLowerCase();
/*    */     
/* 73 */     if (str3 == null) {
/* 74 */       str3 = str4.startsWith("windows") ? "C:\\TEMP" : "/tmp";
/*    */     }
/*    */     
/* 77 */     if (!str3.endsWith(str2)) {
/* 78 */       str3 = str3 + str2;
/*    */     }
/* 80 */     str3 = str3 + str1;
/*    */ 
/*    */     
/* 83 */     str1 = virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.dll");
/* 84 */     String str5 = virtdevs.prop.getProperty("com.hp.ilo2.virtdevs.keydrive", "true");
/* 85 */     keydrive = Boolean.valueOf(str5).booleanValue() ? 1 : 0;
/*    */     
/* 87 */     if (str1 != null)
/* 88 */       str3 = str1; 
/* 89 */     System.out.println("Loading " + str3);
/* 90 */     System.load(str3);
/*    */   }
/*    */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/DirectIO.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */