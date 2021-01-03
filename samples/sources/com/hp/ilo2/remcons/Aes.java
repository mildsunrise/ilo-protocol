package com.hp.ilo2.remcons;

public class Aes {
    public static final int Bits128 = 0;
    public static final int Bits192 = 1;
    public static final int Bits256 = 2;
    private int Nb;
    private int Nk;
    private int Nr;
    private byte[] key;
    private byte[][] Sbox;
    private byte[][] iSbox;
    private byte[][] w;
    private byte[][] Rcon;
    private byte[][] State;
    byte[] ofb_iv;
    int ofb_num;

    public void Cipher(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        if (paramArrayOfbyte1.length < 16)
            System.out.println("Alert- InputSize:" + paramArrayOfbyte1.length + " is less than standard size:16");
        if (paramArrayOfbyte2.length < 16)
            System.out.println("Alert- OutputSize:" + paramArrayOfbyte2.length + " is less than standard size:16");
        this.State = new byte[4][this.Nb];
        for (byte b1 = 0; b1 < 4 * this.Nb; b1++)
            this.State[b1 % 4][b1 / 4] = paramArrayOfbyte1[b1];
        AddRoundKey(0);
        for (byte b2 = 1; b2 <= this.Nr - 1; b2++) {
            SubBytes();
            ShiftRows();
            MixColumns();
            AddRoundKey(b2);
        }
        SubBytes();
        ShiftRows();
        AddRoundKey(this.Nr);
        for (byte b3 = 0; b3 < 4 * this.Nb; b3++)
            paramArrayOfbyte2[b3] = this.State[b3 % 4][b3 / 4];
    }

    public void InvCipher(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        this.State = new byte[4][this.Nb];
        for (byte b1 = 0; b1 < 4 * this.Nb; b1++)
            this.State[b1 % 4][b1 / 4] = paramArrayOfbyte1[b1];
        AddRoundKey(this.Nr);
        for (int i = this.Nr - 1; i >= 1; i--) {
            InvShiftRows();
            InvSubBytes();
            AddRoundKey(i);
            InvMixColumns();
        }
        InvShiftRows();
        InvSubBytes();
        AddRoundKey(0);
        for (byte b2 = 0; b2 < 4 * this.Nb; b2++)
            paramArrayOfbyte2[b2] = this.State[b2 % 4][b2 / 4];
    }

    private void SetNbNkNr(int keyBits) {
        this.Nb = 4;
        if (keyBits == 0) {
            this.Nk = 4;
            this.Nr = 10;
        } else if (keyBits == 1) {
            this.Nk = 6;
            this.Nr = 12;
        } else if (keyBits == 2) {
            this.Nk = 8;
            this.Nr = 14;
        } else {
            System.out.println("Alert: Invalid keysize Specified for SetNbNkNr");
            System.out.println("Pls use constants from Aes.KeySize");
        }
    }

    private void BuildSbox() {
        byte[][] arrayOfByte = { { 99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118 },
                { -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64 },
                { -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21 },
                { 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, Byte.MIN_VALUE, -30, -21, 39, -78, 117 },
                { 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124 },
                { 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49 },
                { -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, Byte.MAX_VALUE, 80, 60, -97, -88 },
                { 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46 },
                { -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115 },
                { 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37 },
                { -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121 },
                { -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8 },
                { -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118 },
                { 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98 },
                { -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33 },
                { -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22 } };
        this.Sbox = arrayOfByte;
    }

    private void BuildInvSbox() {
        byte[][] arrayOfByte = { { 82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5 },
                { 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53 },
                { 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78 },
                { 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37 },
                { 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110 },
                { 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124 },
                { -112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6 },
                { -48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107 },
                { 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115 },
                { -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110 },
                { 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27 },
                { -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12 },
                { 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, Byte.MIN_VALUE, -20, 95 },
                { 96, 81, Byte.MAX_VALUE, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17 },
                { -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97 },
                { 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125 } };
        this.iSbox = arrayOfByte;
    }

    public Aes(int keyBits, byte[] key) {
        this.ofb_iv = new byte[16];
        SetNbNkNr(keyBits);
        this.key = new byte[this.Nk * 4];
        if (key.length < this.key.length)
            System.out.println("Alert: KeyBytes size is less than specified KeySize");
        System.arraycopy(key, 0, this.key, 0, this.key.length);
        BuildSbox();
        BuildInvSbox();
        BuildRcon();
        KeyExpansion();
    }

    private void BuildRcon() {
        byte[][] arrayOfByte = { { 0, 0, 0, 0 }, { 1, 0, 0, 0 }, { 2, 0, 0, 0 }, { 4, 0, 0, 0 }, { 8, 0, 0, 0 },
                { 16, 0, 0, 0 }, { 32, 0, 0, 0 }, { 64, 0, 0, 0 }, { Byte.MIN_VALUE, 0, 0, 0 }, { 27, 0, 0, 0 },
                { 54, 0, 0, 0 } };
        this.Rcon = arrayOfByte;
    }

    private void AddRoundKey(int paramInt) {
        for (byte b = 0; b < 4; b++) {
            for (byte b1 = 0; b1 < 4; b1++)
                this.State[b][b1] = (byte) (this.State[b][b1] & 0xFF ^ this.w[paramInt * 4 + b1][b] & 0xFF);
        }
    }

    private void SubBytes() {
        for (byte b = 0; b < 4; b++) {
            for (byte b1 = 0; b1 < 4; b1++)
                this.State[b][b1] = this.Sbox[(byte) (this.State[b][b1] >> 4 & 0xF)][this.State[b][b1] & 0xF];
        }
    }

    private void InvSubBytes() {
        for (byte b = 0; b < 4; b++) {
            for (byte b1 = 0; b1 < 4; b1++)
                this.State[b][b1] = this.iSbox[(byte) (this.State[b][b1] >> 4 & 0xF)][this.State[b][b1] & 0xF];
        }
    }

    private void ShiftRows() {
        byte[][] arrayOfByte = new byte[4][4];
        for (byte b1 = 0; b1 < 4; b1++) {
            for (byte b = 0; b < 4; b++)
                arrayOfByte[b1][b] = this.State[b1][b];
        }
        for (byte b2 = 1; b2 < 4; b2++) {
            for (byte b = 0; b < 4; b++)
                this.State[b2][b] = arrayOfByte[b2][(b + b2) % this.Nb];
        }
    }

    private void InitOfbIv() {
        this.ofb_num = 0;
        for (byte b = 0; b < this.ofb_iv.length; b++)
            this.ofb_iv[b] = 0;
    }

    private void InvShiftRows() {
        byte[][] arrayOfByte = new byte[4][4];
        for (byte b1 = 0; b1 < 4; b1++) {
            for (byte b = 0; b < 4; b++)
                arrayOfByte[b1][b] = this.State[b1][b];
        }
        for (byte b2 = 1; b2 < 4; b2++) {
            for (byte b = 0; b < 4; b++)
                this.State[b2][(b + b2) % this.Nb] = arrayOfByte[b2][b];
        }
    }

    private void MixColumns() {
        byte[][] arrayOfByte = new byte[4][4];
        for (byte b1 = 0; b1 < 4; b1++) {
            for (byte b = 0; b < 4; b++)
                arrayOfByte[b1][b] = this.State[b1][b];
        }
        for (byte b2 = 0; b2 < 4; b2++) {
            this.State[0][b2] = (byte) (gfmultby02(arrayOfByte[0][b2]) & 0xFF ^ gfmultby03(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby01(arrayOfByte[2][b2]) & 0xFF ^ gfmultby01(arrayOfByte[3][b2]) & 0xFF);
            this.State[1][b2] = (byte) (gfmultby01(arrayOfByte[0][b2]) & 0xFF ^ gfmultby02(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby03(arrayOfByte[2][b2]) & 0xFF ^ gfmultby01(arrayOfByte[3][b2]) & 0xFF);
            this.State[2][b2] = (byte) (gfmultby01(arrayOfByte[0][b2]) & 0xFF ^ gfmultby01(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby02(arrayOfByte[2][b2]) & 0xFF ^ gfmultby03(arrayOfByte[3][b2]) & 0xFF);
            this.State[3][b2] = (byte) (gfmultby03(arrayOfByte[0][b2]) & 0xFF ^ gfmultby01(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby01(arrayOfByte[2][b2]) & 0xFF ^ gfmultby02(arrayOfByte[3][b2]) & 0xFF);
        }
    }

    private byte[] SubWord(byte[] paramArrayOfbyte) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = this.Sbox[(byte) (paramArrayOfbyte[0] >> 4 & 0xF)][paramArrayOfbyte[0] & 0xF];
        arrayOfByte[1] = this.Sbox[(byte) (paramArrayOfbyte[1] >> 4 & 0xF)][paramArrayOfbyte[1] & 0xF];
        arrayOfByte[2] = this.Sbox[(byte) (paramArrayOfbyte[2] >> 4 & 0xF)][paramArrayOfbyte[2] & 0xF];
        arrayOfByte[3] = this.Sbox[(byte) (paramArrayOfbyte[3] >> 4 & 0xF)][paramArrayOfbyte[3] & 0xF];
        return arrayOfByte;
    }

    private void InvMixColumns() {
        byte[][] arrayOfByte = new byte[4][4];
        for (byte b1 = 0; b1 < 4; b1++) {
            for (byte b = 0; b < 4; b++)
                arrayOfByte[b1][b] = this.State[b1][b];
        }
        for (byte b2 = 0; b2 < 4; b2++) {
            this.State[0][b2] = (byte) (gfmultby0e(arrayOfByte[0][b2]) & 0xFF ^ gfmultby0b(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby0d(arrayOfByte[2][b2]) & 0xFF ^ gfmultby09(arrayOfByte[3][b2]) & 0xFF);
            this.State[1][b2] = (byte) (gfmultby09(arrayOfByte[0][b2]) & 0xFF ^ gfmultby0e(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby0b(arrayOfByte[2][b2]) & 0xFF ^ gfmultby0d(arrayOfByte[3][b2]) & 0xFF);
            this.State[2][b2] = (byte) (gfmultby0d(arrayOfByte[0][b2]) & 0xFF ^ gfmultby09(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby0e(arrayOfByte[2][b2]) & 0xFF ^ gfmultby0b(arrayOfByte[3][b2]) & 0xFF);
            this.State[3][b2] = (byte) (gfmultby0b(arrayOfByte[0][b2]) & 0xFF ^ gfmultby0d(arrayOfByte[1][b2]) & 0xFF
                    ^ gfmultby09(arrayOfByte[2][b2]) & 0xFF ^ gfmultby0e(arrayOfByte[3][b2]) & 0xFF);
        }
    }

    private static byte gfmultby01(byte paramByte) {
        return paramByte;
    }

    private static byte gfmultby02(byte paramByte) {
        if ((paramByte & 0xFF) < 128)
            return (byte) (paramByte << 1 & 0xFF);
        return (byte) (paramByte << 1 & 0xFF ^ 0x1B);
    }

    private static byte gfmultby03(byte paramByte) {
        return (byte) (gfmultby02(paramByte) & 0xFF ^ paramByte & 0xFF);
    }

    private static byte gfmultby09(byte paramByte) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(paramByte))) & 0xFF ^ paramByte & 0xFF);
    }

    private static byte gfmultby0b(byte paramByte) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(paramByte))) & 0xFF ^ gfmultby02(paramByte) & 0xFF
                ^ paramByte & 0xFF);
    }

    private byte[] RotWord(byte[] paramArrayOfbyte) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = paramArrayOfbyte[1];
        arrayOfByte[1] = paramArrayOfbyte[2];
        arrayOfByte[2] = paramArrayOfbyte[3];
        arrayOfByte[3] = paramArrayOfbyte[0];
        return arrayOfByte;
    }

    private static byte gfmultby0d(byte paramByte) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(paramByte))) & 0xFF ^ gfmultby02(gfmultby02(paramByte)) & 0xFF
                ^ paramByte & 0xFF);
    }

    private static byte gfmultby0e(byte paramByte) {
        return (byte) (gfmultby02(gfmultby02(gfmultby02(paramByte))) & 0xFF ^ gfmultby02(gfmultby02(paramByte)) & 0xFF
                ^ gfmultby02(paramByte) & 0xFF);
    }

    private void KeyExpansion() {
        this.w = new byte[this.Nb * (this.Nr + 1)][4];
        for (byte b = 0; b < this.Nk; b++) {
            this.w[b][0] = this.key[4 * b];
            this.w[b][1] = this.key[4 * b + 1];
            this.w[b][2] = this.key[4 * b + 2];
            this.w[b][3] = this.key[4 * b + 3];
        }
        byte[] arrayOfByte = new byte[4];
        for (int i = this.Nk; i < this.Nb * (this.Nr + 1); i++) {
            arrayOfByte[0] = this.w[i - 1][0];
            arrayOfByte[1] = this.w[i - 1][1];
            arrayOfByte[2] = this.w[i - 1][2];
            arrayOfByte[3] = this.w[i - 1][3];
            if (i % this.Nk == 0) {
                arrayOfByte = SubWord(RotWord(arrayOfByte));
                arrayOfByte[0] = (byte) (arrayOfByte[0] & 0xFF ^ this.Rcon[i / this.Nk][0] & 0xFF);
                arrayOfByte[1] = (byte) (arrayOfByte[1] & 0xFF ^ this.Rcon[i / this.Nk][1] & 0xFF);
                arrayOfByte[2] = (byte) (arrayOfByte[2] & 0xFF ^ this.Rcon[i / this.Nk][2] & 0xFF);
                arrayOfByte[3] = (byte) (arrayOfByte[3] & 0xFF ^ this.Rcon[i / this.Nk][3] & 0xFF);
            } else if (this.Nk > 6 && i % this.Nk == 4) {
                arrayOfByte = SubWord(arrayOfByte);
            }
            this.w[i][0] = (byte) (this.w[i - this.Nk][0] & 0xFF ^ arrayOfByte[0] & 0xFF);
            this.w[i][1] = (byte) (this.w[i - this.Nk][1] & 0xFF ^ arrayOfByte[1] & 0xFF);
            this.w[i][2] = (byte) (this.w[i - this.Nk][2] & 0xFF ^ arrayOfByte[2] & 0xFF);
            this.w[i][3] = (byte) (this.w[i - this.Nk][3] & 0xFF ^ arrayOfByte[3] & 0xFF);
        }
        InitOfbIv();
    }

    public void Dump() {
        System.out.println("Nb = " + this.Nb + " Nk = " + this.Nk + " Nr = " + this.Nr);
        System.out.println("\nThe key is \n" + DumpKey());
        System.out.println("\nThe Sbox is \n" + DumpTwoByTwo(this.Sbox));
        System.out.println("\nThe w array is \n" + DumpTwoByTwo(this.w));
        System.out.println("\nThe State array is \n" + DumpTwoByTwo(this.State));
    }

    public String DumpKey() {
        String str1 = "", str2 = "";
        for (byte b = 0; b < this.key.length; b++) {
            str2 = Integer.toHexString(this.key[b] & 0xFF);
            if (str2.length() == 1) {
                str1 = str1 + "0";
            }
            str1 = str1 + str2 + " ";
        }
        return str1;
    }

    public String DumpTwoByTwo(byte[][] paramArrayOfbyte) {
        String str1 = "", str2 = "";
        for (byte b = 0; b < paramArrayOfbyte.length; b++) {
            str1 = str1 + "[" + b + "]" + " ";
            for (byte b1 = 0; b1 < (paramArrayOfbyte[b]).length; b1++) {
                str2 = Integer.toHexString(paramArrayOfbyte[b][b1] & 0xFF);
                if (str2.length() == 1) {
                    str1 = str1 + "0";
                }
                str1 = str1 + str2 + " ";
            }
            str1 = str1 + "\n";
        }
        return str1;
    }

    public byte randomValue() {
        if (this.ofb_num == 0) {
            Cipher(this.ofb_iv, this.ofb_iv);
        }

        byte b = this.ofb_iv[this.ofb_num];
        this.ofb_num = this.ofb_num + 1 & 0xF;
        return b;
    }
}

/*
 * Location:
 * /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/
 * Aes.class Java compiler version: 1 (45.3) JD-Core Version: 1.1.3
 */