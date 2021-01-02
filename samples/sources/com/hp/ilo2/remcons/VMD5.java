/*     */ package com.hp.ilo2.remcons;
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
/*     */ public final class VMD5
/*     */   implements Cloneable
/*     */ {
/*     */   private byte[] digestBits;
/*     */   private String algorithm;
/*     */   private int[] state;
/*     */   private long count;
/*     */   private byte[] buffer;
/*     */   private int[] transformBuffer;
/*     */   private static final int S11 = 7;
/*     */   private static final int S12 = 12;
/*     */   private static final int S13 = 17;
/*     */   private static final int S14 = 22;
/*     */   private static final int S21 = 5;
/*     */   private static final int S22 = 9;
/*     */   private static final int S23 = 14;
/*     */   private static final int S24 = 20;
/*     */   private static final int S31 = 4;
/*     */   private static final int S32 = 11;
/*     */   private static final int S33 = 16;
/*     */   private static final int S34 = 23;
/*     */   private static final int S41 = 6;
/*     */   private static final int S42 = 10;
/*     */   private static final int S43 = 15;
/*     */   private static final int S44 = 21;
/*     */   
/*     */   public VMD5() {
/*  49 */     init();
/*     */   }
/*     */ 
/*     */   
/*     */   private VMD5(VMD5 paramVMD5) {
/*  54 */     this();
/*  55 */     this.state = new int[paramVMD5.state.length];
/*     */     
/*  57 */     System.arraycopy(paramVMD5.state, 0, this.state, 0, paramVMD5.state.length);
/*  58 */     this.transformBuffer = new int[paramVMD5.transformBuffer.length];
/*     */     
/*  60 */     System.arraycopy(paramVMD5.transformBuffer, 0, this.transformBuffer, 0, paramVMD5.transformBuffer.length);
/*  61 */     this.buffer = new byte[paramVMD5.buffer.length];
/*     */     
/*  63 */     System.arraycopy(paramVMD5.buffer, 0, this.buffer, 0, paramVMD5.buffer.length);
/*  64 */     this.digestBits = new byte[paramVMD5.digestBits.length];
/*     */     
/*  66 */     System.arraycopy(paramVMD5.digestBits, 0, this.digestBits, 0, paramVMD5.digestBits.length);
/*  67 */     this.count = paramVMD5.count;
/*     */   }
/*     */ 
/*     */   
/*     */   private int F(int paramInt1, int paramInt2, int paramInt3) {
/*  72 */     return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
/*     */   }
/*     */ 
/*     */   
/*     */   private int G(int paramInt1, int paramInt2, int paramInt3) {
/*  77 */     return paramInt1 & paramInt3 | paramInt2 & (paramInt3 ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   
/*     */   private int H(int paramInt1, int paramInt2, int paramInt3) {
/*  82 */     return paramInt1 ^ paramInt2 ^ paramInt3;
/*     */   }
/*     */ 
/*     */   
/*     */   private int I(int paramInt1, int paramInt2, int paramInt3) {
/*  87 */     return paramInt2 ^ (paramInt1 | paramInt3 ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   
/*     */   private int rotateLeft(int paramInt1, int paramInt2) {
/*  92 */     return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   private int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/*  97 */     paramInt1 += F(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/*  98 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/*  99 */     paramInt1 += paramInt2;
/* 100 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   private int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 105 */     paramInt1 += G(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 106 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 107 */     paramInt1 += paramInt2;
/* 108 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   private int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 113 */     paramInt1 += H(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 114 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 115 */     paramInt1 += paramInt2;
/* 116 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   private int II(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 121 */     paramInt1 += I(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 122 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 123 */     paramInt1 += paramInt2;
/* 124 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   void transform(byte[] paramArrayOfbyte, int paramInt) {
/* 129 */     int[] arrayOfInt = this.transformBuffer;
/* 130 */     int i = this.state[0];
/* 131 */     int j = this.state[1];
/* 132 */     int k = this.state[2];
/* 133 */     int m = this.state[3];
/* 134 */     for (byte b = 0; b < 16; b++) {
/* 135 */       arrayOfInt[b] = paramArrayOfbyte[b * 4 + paramInt] & 0xFF;
/* 136 */       for (byte b1 = 1; b1 < 4; b1++)
/* 137 */         arrayOfInt[b] = arrayOfInt[b] + ((paramArrayOfbyte[b * 4 + b1 + paramInt] & 0xFF) << b1 * 8); 
/*     */     } 
/* 139 */     i = FF(i, j, k, m, arrayOfInt[0], 7, -680876936);
/* 140 */     m = FF(m, i, j, k, arrayOfInt[1], 12, -389564586);
/* 141 */     k = FF(k, m, i, j, arrayOfInt[2], 17, 606105819);
/* 142 */     j = FF(j, k, m, i, arrayOfInt[3], 22, -1044525330);
/* 143 */     i = FF(i, j, k, m, arrayOfInt[4], 7, -176418897);
/* 144 */     m = FF(m, i, j, k, arrayOfInt[5], 12, 1200080426);
/* 145 */     k = FF(k, m, i, j, arrayOfInt[6], 17, -1473231341);
/* 146 */     j = FF(j, k, m, i, arrayOfInt[7], 22, -45705983);
/* 147 */     i = FF(i, j, k, m, arrayOfInt[8], 7, 1770035416);
/* 148 */     m = FF(m, i, j, k, arrayOfInt[9], 12, -1958414417);
/* 149 */     k = FF(k, m, i, j, arrayOfInt[10], 17, -42063);
/* 150 */     j = FF(j, k, m, i, arrayOfInt[11], 22, -1990404162);
/* 151 */     i = FF(i, j, k, m, arrayOfInt[12], 7, 1804603682);
/* 152 */     m = FF(m, i, j, k, arrayOfInt[13], 12, -40341101);
/* 153 */     k = FF(k, m, i, j, arrayOfInt[14], 17, -1502002290);
/* 154 */     j = FF(j, k, m, i, arrayOfInt[15], 22, 1236535329);
/* 155 */     i = GG(i, j, k, m, arrayOfInt[1], 5, -165796510);
/* 156 */     m = GG(m, i, j, k, arrayOfInt[6], 9, -1069501632);
/* 157 */     k = GG(k, m, i, j, arrayOfInt[11], 14, 643717713);
/* 158 */     j = GG(j, k, m, i, arrayOfInt[0], 20, -373897302);
/* 159 */     i = GG(i, j, k, m, arrayOfInt[5], 5, -701558691);
/* 160 */     m = GG(m, i, j, k, arrayOfInt[10], 9, 38016083);
/* 161 */     k = GG(k, m, i, j, arrayOfInt[15], 14, -660478335);
/* 162 */     j = GG(j, k, m, i, arrayOfInt[4], 20, -405537848);
/* 163 */     i = GG(i, j, k, m, arrayOfInt[9], 5, 568446438);
/* 164 */     m = GG(m, i, j, k, arrayOfInt[14], 9, -1019803690);
/* 165 */     k = GG(k, m, i, j, arrayOfInt[3], 14, -187363961);
/* 166 */     j = GG(j, k, m, i, arrayOfInt[8], 20, 1163531501);
/* 167 */     i = GG(i, j, k, m, arrayOfInt[13], 5, -1444681467);
/* 168 */     m = GG(m, i, j, k, arrayOfInt[2], 9, -51403784);
/* 169 */     k = GG(k, m, i, j, arrayOfInt[7], 14, 1735328473);
/* 170 */     j = GG(j, k, m, i, arrayOfInt[12], 20, -1926607734);
/* 171 */     i = HH(i, j, k, m, arrayOfInt[5], 4, -378558);
/* 172 */     m = HH(m, i, j, k, arrayOfInt[8], 11, -2022574463);
/* 173 */     k = HH(k, m, i, j, arrayOfInt[11], 16, 1839030562);
/* 174 */     j = HH(j, k, m, i, arrayOfInt[14], 23, -35309556);
/* 175 */     i = HH(i, j, k, m, arrayOfInt[1], 4, -1530992060);
/* 176 */     m = HH(m, i, j, k, arrayOfInt[4], 11, 1272893353);
/* 177 */     k = HH(k, m, i, j, arrayOfInt[7], 16, -155497632);
/* 178 */     j = HH(j, k, m, i, arrayOfInt[10], 23, -1094730640);
/* 179 */     i = HH(i, j, k, m, arrayOfInt[13], 4, 681279174);
/* 180 */     m = HH(m, i, j, k, arrayOfInt[0], 11, -358537222);
/* 181 */     k = HH(k, m, i, j, arrayOfInt[3], 16, -722521979);
/* 182 */     j = HH(j, k, m, i, arrayOfInt[6], 23, 76029189);
/* 183 */     i = HH(i, j, k, m, arrayOfInt[9], 4, -640364487);
/* 184 */     m = HH(m, i, j, k, arrayOfInt[12], 11, -421815835);
/* 185 */     k = HH(k, m, i, j, arrayOfInt[15], 16, 530742520);
/* 186 */     j = HH(j, k, m, i, arrayOfInt[2], 23, -995338651);
/* 187 */     i = II(i, j, k, m, arrayOfInt[0], 6, -198630844);
/* 188 */     m = II(m, i, j, k, arrayOfInt[7], 10, 1126891415);
/* 189 */     k = II(k, m, i, j, arrayOfInt[14], 15, -1416354905);
/* 190 */     j = II(j, k, m, i, arrayOfInt[5], 21, -57434055);
/* 191 */     i = II(i, j, k, m, arrayOfInt[12], 6, 1700485571);
/* 192 */     m = II(m, i, j, k, arrayOfInt[3], 10, -1894986606);
/* 193 */     k = II(k, m, i, j, arrayOfInt[10], 15, -1051523);
/* 194 */     j = II(j, k, m, i, arrayOfInt[1], 21, -2054922799);
/* 195 */     i = II(i, j, k, m, arrayOfInt[8], 6, 1873313359);
/* 196 */     m = II(m, i, j, k, arrayOfInt[15], 10, -30611744);
/* 197 */     k = II(k, m, i, j, arrayOfInt[6], 15, -1560198380);
/* 198 */     j = II(j, k, m, i, arrayOfInt[13], 21, 1309151649);
/* 199 */     i = II(i, j, k, m, arrayOfInt[4], 6, -145523070);
/* 200 */     m = II(m, i, j, k, arrayOfInt[11], 10, -1120210379);
/* 201 */     k = II(k, m, i, j, arrayOfInt[2], 15, 718787259);
/* 202 */     j = II(j, k, m, i, arrayOfInt[9], 21, -343485551);
/* 203 */     this.state[0] = this.state[0] + i;
/* 204 */     this.state[1] = this.state[1] + j;
/* 205 */     this.state[2] = this.state[2] + k;
/* 206 */     this.state[3] = this.state[3] + m;
/*     */   }
/*     */ 
/*     */   
/*     */   public void init() {
/* 211 */     this.state = new int[4];
/* 212 */     this.transformBuffer = new int[16];
/* 213 */     this.buffer = new byte[64];
/* 214 */     this.digestBits = new byte[16];
/* 215 */     this.count = 0L;
/* 216 */     this.state[0] = 1732584193;
/* 217 */     this.state[1] = -271733879;
/* 218 */     this.state[2] = -1732584194;
/* 219 */     this.state[3] = 271733878;
/* 220 */     for (byte b = 0; b < this.digestBits.length; b++) {
/* 221 */       this.digestBits[b] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */   public void engineReset() {
/* 226 */     init();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void engineUpdate(byte paramByte) {
/* 231 */     int i = (int)(this.count >>> 3L & 0x3FL);
/* 232 */     this.count += 8L;
/* 233 */     this.buffer[i] = paramByte;
/* 234 */     if (i >= 63) {
/* 235 */       transform(this.buffer, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 240 */     int i = paramInt1;
/* 241 */     while (paramInt2 > 0) {
/* 242 */       int j = (int)(this.count >>> 3L & 0x3FL);
/* 243 */       if (j == 0 && paramInt2 > 64) {
/* 244 */         this.count += 512L;
/* 245 */         transform(paramArrayOfbyte, i);
/* 246 */         paramInt2 -= 64;
/* 247 */         i += 64; continue;
/*     */       } 
/* 249 */       this.count += 8L;
/* 250 */       this.buffer[j] = paramArrayOfbyte[i];
/* 251 */       if (j >= 63)
/* 252 */         transform(this.buffer, 0); 
/* 253 */       i++;
/* 254 */       paramInt2--;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void finish() {
/* 261 */     byte[] arrayOfByte1 = new byte[8];
/* 262 */     int i = 0;
/* 263 */     byte b = 0;
/* 264 */     int j = 0;
/* 265 */     for (i = 0; i < 8; i++)
/* 266 */       arrayOfByte1[i] = (byte)(int)(this.count >>> i * 8 & 0xFFL); 
/* 267 */     j = (int)(this.count >> 3L) & 0x3F;
/* 268 */     i = (j < 56) ? (56 - j) : (120 - j);
/* 269 */     byte[] arrayOfByte2 = new byte[i];
/* 270 */     arrayOfByte2[0] = Byte.MIN_VALUE;
/* 271 */     engineUpdate(arrayOfByte2, 0, arrayOfByte2.length);
/* 272 */     engineUpdate(arrayOfByte1, 0, arrayOfByte1.length);
/* 273 */     for (i = 0; i < 4; i++) {
/* 274 */       for (b = 0; b < 4; b++) {
/* 275 */         this.digestBits[i * 4 + b] = (byte)(this.state[i] >>> b * 8 & 0xFF);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] engineDigest() {
/* 283 */     finish();
/* 284 */     byte[] arrayOfByte = new byte[16];
/* 285 */     System.arraycopy(this.digestBits, 0, arrayOfByte, 0, 16);
/* 286 */     init();
/* 287 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 292 */     return new VMD5(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 300 */     engineReset();
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
/*     */   public void update(byte paramByte) {
/* 312 */     engineUpdate(paramByte);
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
/*     */   public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 330 */     engineUpdate(paramArrayOfbyte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(byte[] paramArrayOfbyte) {
/* 341 */     engineUpdate(paramArrayOfbyte, 0, paramArrayOfbyte.length);
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
/*     */   public byte[] digest() {
/* 353 */     this.digestBits = engineDigest();
/* 354 */     return this.digestBits;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/VMD5.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */