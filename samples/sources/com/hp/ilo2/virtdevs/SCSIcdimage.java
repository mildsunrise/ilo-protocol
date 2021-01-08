package com.hp.ilo2.virtdevs;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SCSIcdimage
  extends SCSI
{
  int fdd_state = 0;
  int event_state = 0;
  
  long media_sz;
  virtdevs v;
  
  public void setWriteProt(boolean paramBoolean) {
    this.writeprot = paramBoolean;
  }

  
  public SCSIcdimage(Socket paramSocket, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, String paramString, int paramInt, virtdevs paramvirtdevs) throws IOException {
    super(paramSocket, paramInputStream, paramBufferedOutputStream, paramString, paramInt);
    int i = this.media.open(paramString, 0);
    D.println(1, "Media open returns " + i + " / " + this.media.size() + " bytes");
    this.v = paramvirtdevs;
  }

  
  public boolean process() throws IOException {
    boolean bool = true;
    D.println(1, "Device: " + this.selectedDevice + " (" + this.targetIsDevice + ")");
    read_command(this.req, 12);
    D.print(1, "SCSI Request: ");
    D.hexdump(1, this.req, 12);
    this.v.ParentApp.remconsObj.setvmAct(1);
    
    this.media_sz = this.media.size();
    if (this.media_sz == 0L) {
      this.reply.setmedia(0);
      this.fdd_state = 0;
      
      this.event_state = 4;
    }
    else {
      
      this.reply.setmedia(1);
      this.fdd_state++;
      if (this.fdd_state > 2) {
        this.fdd_state = 2;
      }
      if (this.event_state == 4)
        this.event_state = 0; 
      this.event_state++;
      if (this.event_state > 2) {
        this.event_state = 2;
      }
    } 
    switch (this.req[0] & 0xFF) {
        case SCSI.SCSI_PA_MEDIA_REMOVAL:
            client_pa_media_removal(this.req);
            return bool;
        case SCSI.SCSI_READ_CAPACITY:
            client_read_capacity();
            return bool;
        case SCSI.SCSI_SEND_DIAGNOSTIC:
            client_send_diagnostic();
            return bool;
        case SCSI.SCSI_TEST_UNIT_READY:
            client_test_unit_ready();
            return bool;
        case SCSI.SCSI_READ_10:
        case SCSI.SCSI_READ_12:
            client_read(this.req);
            return bool;
        case SCSI.SCSI_START_STOP_UNIT:
            bool = client_start_stop_unit(this.req);
            return bool;
        case SCSI.SCSI_READ_TOC:
            client_read_toc(this.req);
            return bool;
        case SCSI.SCSI_MODE_SENSE:
            client_mode_sense(this.req);
            return bool;
        case SCSI.SCSI_GET_EVENT_STATUS:
            client_get_event_status(this.req);
            return bool;
    }
    D.println(0, "Unknown request:cmd = " + Integer.toHexString(this.req[0]));
    this.reply.set(5, 36, 0, 0);
    this.reply.send(this.out);
    this.out.flush();
    return bool;
}


  
  void client_send_diagnostic() throws IOException {}


  
  void client_read(byte[] req) throws IOException {
    boolean bool = (req[0] == 168) ? true : false;
    
    long l = SCSI.mk_int32(req, 2) * 2048L;
    int i = bool ? SCSI.mk_int32(req, 6) : SCSI.mk_int16(req, 7);
    i *= 2048;
    
    D.println(3, "CDImage :Client read " + l + ", len=" + i);
    
    if (this.fdd_state == 0) {
      D.println(3, "media not present");
      this.reply.set(2, 58, 0, 0);
      i = 0;
    } else if (this.fdd_state == 1) {
      D.println(3, "media changed");
      this.reply.set(6, 40, 0, 0);
      i = 0;
      this.fdd_state = 2;
    } else if (l >= 0L && l < this.media_sz) {
      this.media.read(l, i, this.buffer);
      this.reply.set(0, 0, 0, i);
    } else {
      this.reply.set(5, 33, 0, 0);
      i = 0;
    }
    
    this.reply.send(this.out);
    if (i != 0)
      this.out.write(this.buffer, 0, i); 
    this.out.flush();
  }


  
  void client_pa_media_removal(byte[] req) throws IOException {
    if ((req[4] & 0x1) != 0) {
      D.println(3, "Media removal prevented");
    } else {
      D.println(3, "Media removal allowed");
    } 
    this.reply.set(0, 0, 0, 0);
    this.reply.send(this.out);
    this.out.flush();
  }










  
  boolean client_start_stop_unit(byte[] paramArrayOfbyte) throws IOException {
    this.reply.set(0, 0, 0, 0);
    this.reply.send(this.out);
    this.out.flush();
    
    if ((paramArrayOfbyte[4] & 0x3) == 2) {
      this.fdd_state = 0;
      
      this.event_state = 4;
      D.println(3, "Media eject");
      return false;
    } 
    return true;
  }


  
  void client_test_unit_ready() throws IOException {
    if (this.fdd_state == 0) {
      D.println(3, "media not present");
      this.reply.set(2, 58, 0, 0);
    } else if (this.fdd_state == 1) {
      D.println(3, "media changed");
      this.reply.set(6, 40, 0, 0);
      this.fdd_state = 2;
    } else {
      D.println(3, "device ready");
      this.reply.set(0, 0, 0, 0);
    } 
    this.reply.send(this.out);
    this.out.flush();
  }

  
  void client_read_capacity() throws IOException {
    byte[] arrayOfByte = { 0, 0, 0, 0, 0, 0, 0, 0 };

    
    this.reply.set(0, 0, 0, arrayOfByte.length);
    if (this.fdd_state == 0) {
      
      this.reply.set(2, 58, 0, 0);
    } else if (this.fdd_state == 1) {
      
      this.reply.set(6, 40, 0, 0);
    } else {
      int i = (int)(this.media.size() / 2048L - 1L);
      arrayOfByte[0] = (byte)(i >> 24 & 0xFF);
      arrayOfByte[1] = (byte)(i >> 16 & 0xFF);
      arrayOfByte[2] = (byte)(i >> 8 & 0xFF);
      arrayOfByte[3] = (byte)(i >> 0 & 0xFF);
      arrayOfByte[6] = 8;
    } 
    this.reply.send(this.out);
    if (this.fdd_state == 2)
      this.out.write(arrayOfByte, 0, arrayOfByte.length); 
    this.out.flush();
    D.print(3, "client_read_capacity: ");
    D.hexdump(3, arrayOfByte, 8);
  }

  
  void client_read_toc(byte[] paramArrayOfbyte) throws IOException {
    boolean bool = ((paramArrayOfbyte[1] & 0x2) != 0) ? true : false;
    int i = (paramArrayOfbyte[9] & 0xC0) >> 6;
    int j = (int)(this.media.size() / 2048L);
    double d = j / 75.0D + 2.0D;
    int k = (int)d / 60;
    int m = (int)d % 60;
    int n = (int)((d - (int)d) * 75.0D);
    int i1 = SCSI.mk_int16(paramArrayOfbyte, 7);
    
    for (byte b = 0; b < i1; b++) {
      this.buffer[b] = 0;
    }
    if (i == 0) {
      this.buffer[0] = 0;
      this.buffer[1] = 18;
      this.buffer[2] = 1;
      this.buffer[3] = 1;
      
      this.buffer[4] = 0;
      this.buffer[5] = 20;
      this.buffer[6] = 1;
      this.buffer[7] = 0;
      this.buffer[8] = 0;
      this.buffer[9] = 0;
      this.buffer[10] = bool ? 2 : 0;
      this.buffer[11] = 0;
      
      this.buffer[12] = 0;
      this.buffer[13] = 20;
      this.buffer[14] = -86;
      this.buffer[15] = 0;
      this.buffer[16] = 0;
      this.buffer[17] = bool ? (byte)k : (byte)(j >> 16 & 0xFF);
      this.buffer[18] = bool ? (byte)m : (byte)(j >> 8 & 0xFF);
      this.buffer[19] = bool ? (byte)n : (byte)(j & 0xFF);
    } 
    
    if (i == 1) {
      this.buffer[0] = 0;
      this.buffer[1] = 10;
      this.buffer[2] = 1;
      this.buffer[3] = 1;
      
      this.buffer[4] = 0;
      this.buffer[5] = 20;
      this.buffer[6] = 1;
      this.buffer[7] = 0;
      this.buffer[8] = 0;
      this.buffer[9] = 0;
      this.buffer[10] = bool ? 2 : 0;
      this.buffer[11] = 0;
    } 


    
    j = 412;
    if (i1 < j)
      j = i1; 
    D.hexdump(3, this.buffer, j);
    this.reply.set(0, 0, 0, j);
    this.reply.send(this.out);
    this.out.write(this.buffer, 0, j);
    this.out.flush();
  }

  
  void client_mode_sense(byte[] paramArrayOfbyte) throws IOException {
    this.buffer[0] = 0;
    this.buffer[1] = 8;
    this.buffer[2] = 1;
    this.buffer[3] = 0;
    this.buffer[4] = 0;
    this.buffer[5] = 0;
    this.buffer[6] = 0;
    this.buffer[7] = 0;
    this.reply.set(0, 0, 0, 8);
    D.hexdump(3, this.buffer, 8);
    this.reply.setmedia(this.buffer[2]);
    this.reply.send(this.out);
    this.out.write(this.buffer, 0, 8);
    this.out.flush();
  }

  
  void client_get_event_status(byte[] req) throws IOException {
    int i = SCSI.mk_int16(req, 7);
    for (byte b1 = 0; b1 < i; b1++) {
      this.buffer[b1] = 0;
    }
    
    if ((req[1] & 0x1) == 0) {
      this.reply.set(5, 36, 0, 0);
      this.reply.send(this.out);
      this.out.flush();
    }
    
    if ((req[4] & 0x10) != 0) {
      this.buffer[0] = 0;
      this.buffer[1] = 6;
      this.buffer[2] = 4;
      this.buffer[3] = 16;
      if (this.event_state == 0) {
        this.buffer[4] = 0;
        this.buffer[5] = 0;
      } else if (this.event_state == 1) {
        this.buffer[4] = 4;
        this.buffer[5] = 2;
        if (i > 4)
          this.event_state = 2; 
      } else if (this.event_state == 4) {
        this.buffer[4] = 3;
        this.buffer[5] = 0;
        if (i > 4)
          this.event_state = 0;
      } else {
        this.buffer[4] = 0;
        this.buffer[5] = 2;
      }
      
      D.hexdump(3, this.buffer, 8);
      this.reply.set(0, 0, 0, (i < 8) ? i : 8);
      this.reply.send(this.out);
      this.out.write(this.buffer, 0, (i < 8) ? i : 8);
      this.out.flush();
    } else {
      this.buffer[0] = 0;
      this.buffer[1] = 2;
      this.buffer[2] = (byte) 0x80;
      this.buffer[3] = 16;
      D.hexdump(3, this.buffer, 4);
      this.reply.set(0, 0, 0, (i < 4) ? i : 4);
      this.reply.send(this.out);
      this.out.write(this.buffer, 0, (i < 4) ? i : 4);
      this.out.flush();
    } 
  }
}


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/SCSIcdimage.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */