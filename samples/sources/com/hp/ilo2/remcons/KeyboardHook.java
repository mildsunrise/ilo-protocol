/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import com.hp.ilo2.virtdevs.virtdevs;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ public class KeyboardHook {
/*  36 */   private int[] winkey_to_hid_dll_en_US = new int[] { 0, 0, 0, 72, 0, 0, 0, 0, 42, 43, 0, 0, 0, 40, 0, 0, 0, 0, 0, 72, 57, 0, 0, 0, 0, 0, 0, 41, 0, 0, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 70, 73, 76, 0, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 0, 0, 0, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 227, 231, 101, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 0, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 225, 229, 224, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, 46, 54, 45, 55, 56, 53, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 47, 49, 48, 52, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255 };
/*     */ 
/*  56 */   private int[] winkey_to_hid_dll_ja_JP = new int[] { 0, 0, 0, 72, 0, 0, 0, 0, 42, 43, 0, 0, 0, 40, 0, 0, 0, 0, 0, 72, 57, 0, 0, 0, 0, 53, 0, 41, 138, 139, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 70, 73, 76, 0, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 0, 0, 0, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 227, 231, 101, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 0, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 225, 229, 224, 228, 226, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 52, 51, 54, 45, 55, 56, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 137, 50, 46, 0, 0, 0, 135, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 57, 0, 136, 53, 53, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255 };
/*     */ 
/*  76 */   private int[] linkey_to_hid_dll_en_US = new int[] { 0, 41, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 45, 46, 42, 43, 20, 26, 8, 21, 23, 28, 24, 12, 18, 19, 47, 48, 40, 224, 4, 22, 7, 9, 10, 11, 13, 14, 15, 51, 52, 53, 225, 49, 29, 27, 6, 25, 5, 17, 16, 54, 55, 56, 229, 85, 226, 44, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 83, 71, 95, 96, 97, 86, 92, 93, 94, 87, 89, 90, 91, 98, 99, 0, 0, 100, 68, 69, 135, 0, 0, 0, 0, 0, 0, 88, 228, 84, 70, 230, 100, 74, 82, 75, 80, 79, 77, 81, 78, 73, 76, 0, 104, 105, 0, 0, 0, 0, 0, 0, 0, 0, 0, 137, 227, 231, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */ 
/*  96 */   private int keyboardLayoutId = 0;
/*  97 */   private int[] keyMap = new int[256];
/*     */   
/*  99 */   public byte[] kcmd = new byte[10];
            public boolean kcmdValid = false;
/*     */   
/*     */   public native int InstallKeyboardHook();
/*     */   
/*     */   public void clearKeymap() {
/* 104 */     for (byte b = 0; b < 256; b++)
/* 105 */       this.keyMap[b] = 0; 
/*     */   }
/*     */   
/*     */   public native int UnInstallKeyboardHook();
/*     */   
/*     */   public void setKeyboardLayoutId(int paramInt) {
/* 111 */     this.keyboardLayoutId = paramInt;
/*     */   }
/*     */   public native int GetKeyData();
/*     */   
/*     */   public void HandleSpecialKey(int paramInt1, int paramInt2) {
/* 116 */     if (1041 == this.keyboardLayoutId) {
/* 118 */       if (this.keyMap[25] == 1 && this.keyMap[164] == 0) {
/* 119 */         this.keyMap[25] = 0;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public native int setLocalKbdLayout(int paramInt);
/*     */   
/*     */   public byte[] HandleHookKey(int keyCode, int keyCodeUpper, boolean pressed, boolean paramBoolean2) {
/* 127 */     int keyWasPressed = this.keyMap[keyCode];
/*     */ 
/* 130 */     Arrays.fill(this.kcmd, (byte)0);
/* 131 */     this.kcmd[0] = 1;
/* 132 */     this.kcmdValid = false;
/*     */     
/* 134 */     if (paramBoolean2) {
/* 135 */       System.out.println("HandleHookKey ctl-Alt-Del clearkeymap");
/* 136 */       clearKeymap();
/* 137 */       this.kcmdValid = true;
/*     */     } else {
/*     */       
/* 141 */       if (this.keyboardLayoutId == 1041 && (243 == keyCode || 244 == keyCode)) {
/*     */         
/* 143 */         keyCode = 243;
/* 144 */         keyWasPressed = this.keyMap[keyCode];
/* 145 */         if (pressed) {
/* 146 */           this.keyMap[keyCode] = 0;
/*     */         } else {
/* 148 */           this.keyMap[keyCode] = 1;
/*     */         }
/*     */       
/* 151 */       } else if (pressed) {
/* 152 */         this.keyMap[keyCode] = 1;
/*     */       } else {
/* 154 */         this.keyMap[keyCode] = 0;
/*     */       } 
/*     */ 
/*     */       
/* 158 */       if (keyWasPressed != this.keyMap[keyCode]) {
/* 159 */         this.kcmdValid = true;
/* 160 */         HandleSpecialKey(keyCode, pressed ? 1 : 0);
/* 161 */         for (byte i = 0, count = 0; i < 256; i++) {
/*     */           
/* 163 */           if (this.keyMap[i] != 0) {
/*     */             int code;
/* 165 */             if (-16711935 == this.keyboardLayoutId) {
/* 166 */               code = this.linkey_to_hid_dll_en_US[i];
/* 167 */             } else if (1041 == this.keyboardLayoutId) {
/* 168 */               code = this.winkey_to_hid_dll_ja_JP[i];
/*     */             } else {
/* 170 */               code = this.winkey_to_hid_dll_en_US[i];
/*     */             }
/*     */ 
/* 174 */             if (code != 0 && code != 255) {
/* 175 */               if ((code & 0xE0) == 224) {
/* 179 */                 code ^= 0xE0;
/* 180 */                 this.kcmd[2] = (byte)(this.kcmd[2] | (byte)(1 << code));
/*     */               } else {
/* 183 */                 this.kcmd[4 + count] = (byte)code;
/* 184 */                 count++;
/* 187 */                 if (count == 6)
/* 188 */                   count = 5;
/*     */               }
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 197 */     this.kcmd[0] = 1;
/*     */     
/* 199 */     return this.kcmd;
/*     */   }
/*     */   
/*     */   static {
/* 203 */     String dllName = "HpqKbHook-" + Integer.toHexString(virtdevs.UID) + ".dll";
/* 204 */     String fileSep = System.getProperty("file.separator");
/* 205 */     String tmpDir = System.getProperty("java.io.tmpdir");
/* 206 */     String osName = System.getProperty("os.name").toLowerCase();
/*     */     
/* 208 */     if (tmpDir == null) {
/* 209 */       tmpDir = osName.startsWith("windows") ? "C:\\TEMP" : "/tmp";
/*     */     }
/* 211 */     if (!tmpDir.endsWith(fileSep))
/* 212 */       tmpDir = tmpDir + fileSep; 
/* 213 */     tmpDir = tmpDir + dllName;
/*     */     
/*     */     try {
/* 216 */       System.out.println(" Loading " + tmpDir + "...");
/* 217 */       System.load(tmpDir);
/* 218 */       System.out.println(" Loaded..!");
/*     */     } catch (Exception exception) {
/* 220 */       System.out.println("Error loading library HpqKbHook.dll - " + exception);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/KeyboardHook.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */