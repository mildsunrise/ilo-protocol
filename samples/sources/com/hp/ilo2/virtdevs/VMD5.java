/*     */ package com.hp.ilo2.virtdevs;
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
/*  36 */     init();
/*     */   }
/*     */ 
/*     */   
/*     */   private VMD5(VMD5 paramVMD5) {
/*  41 */     this();
/*  42 */     this.state = new int[paramVMD5.state.length];
/*     */     
/*  44 */     System.arraycopy(paramVMD5.state, 0, this.state, 0, paramVMD5.state.length);
/*  45 */     this.transformBuffer = new int[paramVMD5.transformBuffer.length];
/*     */     
/*  47 */     System.arraycopy(paramVMD5.transformBuffer, 0, this.transformBuffer, 0, paramVMD5.transformBuffer.length);
/*  48 */     this.buffer = new byte[paramVMD5.buffer.length];
/*     */     
/*  50 */     System.arraycopy(paramVMD5.buffer, 0, this.buffer, 0, paramVMD5.buffer.length);
/*  51 */     this.digestBits = new byte[paramVMD5.digestBits.length];
/*     */     
/*  53 */     System.arraycopy(paramVMD5.digestBits, 0, this.digestBits, 0, paramVMD5.digestBits.length);
/*  54 */     this.count = paramVMD5.count;
/*     */   }
/*     */ 
/*     */   
/*     */   private int F(int paramInt1, int paramInt2, int paramInt3) {
/*  59 */     return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
/*     */   }
/*     */ 
/*     */   
/*     */   private int G(int paramInt1, int paramInt2, int paramInt3) {
/*  64 */     return paramInt1 & paramInt3 | paramInt2 & (paramInt3 ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   
/*     */   private int H(int paramInt1, int paramInt2, int paramInt3) {
/*  69 */     return paramInt1 ^ paramInt2 ^ paramInt3;
/*     */   }
/*     */ 
/*     */   
/*     */   private int I(int paramInt1, int paramInt2, int paramInt3) {
/*  74 */     return paramInt2 ^ (paramInt1 | paramInt3 ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   
/*     */   private int rotateLeft(int paramInt1, int paramInt2) {
/*  79 */     return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   private int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/*  84 */     paramInt1 += F(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/*  85 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/*  86 */     paramInt1 += paramInt2;
/*  87 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   private int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/*  92 */     paramInt1 += G(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/*  93 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/*  94 */     paramInt1 += paramInt2;
/*  95 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   private int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 100 */     paramInt1 += H(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 101 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 102 */     paramInt1 += paramInt2;
/* 103 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   private int II(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
/* 108 */     paramInt1 += I(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 109 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 110 */     paramInt1 += paramInt2;
/* 111 */     return paramInt1;
/*     */   }
/*     */ 
/*     */   
/*     */   void transform(byte[] paramArrayOfbyte, int paramInt) {
/* 116 */     int[] arrayOfInt = this.transformBuffer;
/* 117 */     int i = this.state[0];
/* 118 */     int j = this.state[1];
/* 119 */     int k = this.state[2];
/* 120 */     int m = this.state[3];
/* 121 */     for (byte b = 0; b < 16; b++) {
/* 122 */       arrayOfInt[b] = paramArrayOfbyte[b * 4 + paramInt] & 0xFF;
/* 123 */       for (byte b1 = 1; b1 < 4; b1++)
/* 124 */         arrayOfInt[b] = arrayOfInt[b] + ((paramArrayOfbyte[b * 4 + b1 + paramInt] & 0xFF) << b1 * 8); 
/*     */     } 
/* 126 */     i = FF(i, j, k, m, arrayOfInt[0], 7, -680876936);
/* 127 */     m = FF(m, i, j, k, arrayOfInt[1], 12, -389564586);
/* 128 */     k = FF(k, m, i, j, arrayOfInt[2], 17, 606105819);
/* 129 */     j = FF(j, k, m, i, arrayOfInt[3], 22, -1044525330);
/* 130 */     i = FF(i, j, k, m, arrayOfInt[4], 7, -176418897);
/* 131 */     m = FF(m, i, j, k, arrayOfInt[5], 12, 1200080426);
/* 132 */     k = FF(k, m, i, j, arrayOfInt[6], 17, -1473231341);
/* 133 */     j = FF(j, k, m, i, arrayOfInt[7], 22, -45705983);
/* 134 */     i = FF(i, j, k, m, arrayOfInt[8], 7, 1770035416);
/* 135 */     m = FF(m, i, j, k, arrayOfInt[9], 12, -1958414417);
/* 136 */     k = FF(k, m, i, j, arrayOfInt[10], 17, -42063);
/* 137 */     j = FF(j, k, m, i, arrayOfInt[11], 22, -1990404162);
/* 138 */     i = FF(i, j, k, m, arrayOfInt[12], 7, 1804603682);
/* 139 */     m = FF(m, i, j, k, arrayOfInt[13], 12, -40341101);
/* 140 */     k = FF(k, m, i, j, arrayOfInt[14], 17, -1502002290);
/* 141 */     j = FF(j, k, m, i, arrayOfInt[15], 22, 1236535329);
/* 142 */     i = GG(i, j, k, m, arrayOfInt[1], 5, -165796510);
/* 143 */     m = GG(m, i, j, k, arrayOfInt[6], 9, -1069501632);
/* 144 */     k = GG(k, m, i, j, arrayOfInt[11], 14, 643717713);
/* 145 */     j = GG(j, k, m, i, arrayOfInt[0], 20, -373897302);
/* 146 */     i = GG(i, j, k, m, arrayOfInt[5], 5, -701558691);
/* 147 */     m = GG(m, i, j, k, arrayOfInt[10], 9, 38016083);
/* 148 */     k = GG(k, m, i, j, arrayOfInt[15], 14, -660478335);
/* 149 */     j = GG(j, k, m, i, arrayOfInt[4], 20, -405537848);
/* 150 */     i = GG(i, j, k, m, arrayOfInt[9], 5, 568446438);
/* 151 */     m = GG(m, i, j, k, arrayOfInt[14], 9, -1019803690);
/* 152 */     k = GG(k, m, i, j, arrayOfInt[3], 14, -187363961);
/* 153 */     j = GG(j, k, m, i, arrayOfInt[8], 20, 1163531501);
/* 154 */     i = GG(i, j, k, m, arrayOfInt[13], 5, -1444681467);
/* 155 */     m = GG(m, i, j, k, arrayOfInt[2], 9, -51403784);
/* 156 */     k = GG(k, m, i, j, arrayOfInt[7], 14, 1735328473);
/* 157 */     j = GG(j, k, m, i, arrayOfInt[12], 20, -1926607734);
/* 158 */     i = HH(i, j, k, m, arrayOfInt[5], 4, -378558);
/* 159 */     m = HH(m, i, j, k, arrayOfInt[8], 11, -2022574463);
/* 160 */     k = HH(k, m, i, j, arrayOfInt[11], 16, 1839030562);
/* 161 */     j = HH(j, k, m, i, arrayOfInt[14], 23, -35309556);
/* 162 */     i = HH(i, j, k, m, arrayOfInt[1], 4, -1530992060);
/* 163 */     m = HH(m, i, j, k, arrayOfInt[4], 11, 1272893353);
/* 164 */     k = HH(k, m, i, j, arrayOfInt[7], 16, -155497632);
/* 165 */     j = HH(j, k, m, i, arrayOfInt[10], 23, -1094730640);
/* 166 */     i = HH(i, j, k, m, arrayOfInt[13], 4, 681279174);
/* 167 */     m = HH(m, i, j, k, arrayOfInt[0], 11, -358537222);
/* 168 */     k = HH(k, m, i, j, arrayOfInt[3], 16, -722521979);
/* 169 */     j = HH(j, k, m, i, arrayOfInt[6], 23, 76029189);
/* 170 */     i = HH(i, j, k, m, arrayOfInt[9], 4, -640364487);
/* 171 */     m = HH(m, i, j, k, arrayOfInt[12], 11, -421815835);
/* 172 */     k = HH(k, m, i, j, arrayOfInt[15], 16, 530742520);
/* 173 */     j = HH(j, k, m, i, arrayOfInt[2], 23, -995338651);
/* 174 */     i = II(i, j, k, m, arrayOfInt[0], 6, -198630844);
/* 175 */     m = II(m, i, j, k, arrayOfInt[7], 10, 1126891415);
/* 176 */     k = II(k, m, i, j, arrayOfInt[14], 15, -1416354905);
/* 177 */     j = II(j, k, m, i, arrayOfInt[5], 21, -57434055);
/* 178 */     i = II(i, j, k, m, arrayOfInt[12], 6, 1700485571);
/* 179 */     m = II(m, i, j, k, arrayOfInt[3], 10, -1894986606);
/* 180 */     k = II(k, m, i, j, arrayOfInt[10], 15, -1051523);
/* 181 */     j = II(j, k, m, i, arrayOfInt[1], 21, -2054922799);
/* 182 */     i = II(i, j, k, m, arrayOfInt[8], 6, 1873313359);
/* 183 */     m = II(m, i, j, k, arrayOfInt[15], 10, -30611744);
/* 184 */     k = II(k, m, i, j, arrayOfInt[6], 15, -1560198380);
/* 185 */     j = II(j, k, m, i, arrayOfInt[13], 21, 1309151649);
/* 186 */     i = II(i, j, k, m, arrayOfInt[4], 6, -145523070);
/* 187 */     m = II(m, i, j, k, arrayOfInt[11], 10, -1120210379);
/* 188 */     k = II(k, m, i, j, arrayOfInt[2], 15, 718787259);
/* 189 */     j = II(j, k, m, i, arrayOfInt[9], 21, -343485551);
/* 190 */     this.state[0] = this.state[0] + i;
/* 191 */     this.state[1] = this.state[1] + j;
/* 192 */     this.state[2] = this.state[2] + k;
/* 193 */     this.state[3] = this.state[3] + m;
/*     */   }
/*     */ 
/*     */   
/*     */   public void init() {
/* 198 */     this.state = new int[4];
/* 199 */     this.transformBuffer = new int[16];
/* 200 */     this.buffer = new byte[64];
/* 201 */     this.digestBits = new byte[16];
/* 202 */     this.count = 0L;
/* 203 */     this.state[0] = 1732584193;
/* 204 */     this.state[1] = -271733879;
/* 205 */     this.state[2] = -1732584194;
/* 206 */     this.state[3] = 271733878;
/* 207 */     for (byte b = 0; b < this.digestBits.length; b++) {
/* 208 */       this.digestBits[b] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */   public void engineReset() {
/* 213 */     init();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void engineUpdate(byte paramByte) {
/* 218 */     int i = (int)(this.count >>> 3L & 0x3FL);
/* 219 */     this.count += 8L;
/* 220 */     this.buffer[i] = paramByte;
/* 221 */     if (i >= 63) {
/* 222 */       transform(this.buffer, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 227 */     int i = paramInt1;
/* 228 */     while (paramInt2 > 0) {
/* 229 */       int j = (int)(this.count >>> 3L & 0x3FL);
/* 230 */       if (j == 0 && paramInt2 > 64) {
/* 231 */         this.count += 512L;
/* 232 */         transform(paramArrayOfbyte, i);
/* 233 */         paramInt2 -= 64;
/* 234 */         i += 64; continue;
/*     */       } 
/* 236 */       this.count += 8L;
/* 237 */       this.buffer[j] = paramArrayOfbyte[i];
/* 238 */       if (j >= 63)
/* 239 */         transform(this.buffer, 0); 
/* 240 */       i++;
/* 241 */       paramInt2--;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void finish() {
/* 248 */     byte[] arrayOfByte1 = new byte[8];
/* 249 */     int i = 0;
/* 250 */     byte b = 0;
/* 251 */     int j = 0;
/* 252 */     for (i = 0; i < 8; i++)
/* 253 */       arrayOfByte1[i] = (byte)(int)(this.count >>> i * 8 & 0xFFL); 
/* 254 */     j = (int)(this.count >> 3L) & 0x3F;
/* 255 */     i = (j < 56) ? (56 - j) : (120 - j);
/* 256 */     byte[] arrayOfByte2 = new byte[i];
/* 257 */     arrayOfByte2[0] = Byte.MIN_VALUE;
/* 258 */     engineUpdate(arrayOfByte2, 0, arrayOfByte2.length);
/* 259 */     engineUpdate(arrayOfByte1, 0, arrayOfByte1.length);
/* 260 */     for (i = 0; i < 4; i++) {
/* 261 */       for (b = 0; b < 4; b++) {
/* 262 */         this.digestBits[i * 4 + b] = (byte)(this.state[i] >>> b * 8 & 0xFF);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] engineDigest() {
/* 270 */     finish();
/* 271 */     byte[] arrayOfByte = new byte[16];
/* 272 */     System.arraycopy(this.digestBits, 0, arrayOfByte, 0, 16);
/* 273 */     init();
/* 274 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 279 */     return new VMD5(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 287 */     engineReset();
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
/* 299 */     engineUpdate(paramByte);
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
/* 317 */     engineUpdate(paramArrayOfbyte, paramInt1, paramInt2);
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
/* 328 */     engineUpdate(paramArrayOfbyte, 0, paramArrayOfbyte.length);
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
/* 340 */     this.digestBits = engineDigest();
/* 341 */     return this.digestBits;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/VMD5.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */