/*    */ package com.hp.ilo2.virtdevs;
/*    */ 
/*    */ import java.awt.FileDialog;
/*    */ import java.awt.Frame;
/*    */ import java.awt.event.WindowEvent;
/*    */ import java.awt.event.WindowListener;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VFileDialog
/*    */   extends Frame
/*    */   implements WindowListener
/*    */ {
/*    */   FileDialog fd;
/*    */   
/*    */   public VFileDialog(String paramString1, String paramString2) {
/*    */     try {
/* 24 */       addWindowListener(this);
/* 25 */       this.fd = new FileDialog(new Frame(), paramString1);
/* 26 */       if (paramString2 != null) {
/* 27 */         this.fd.setFile(paramString2);
/*    */       }
/* 29 */       this.fd.setVisible(true);
/* 30 */       this.fd.setFocusable(true);
/*    */     } catch (Exception exception) {
/* 32 */       System.out.println("Un able to open virtual drive select");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getString() {
/* 39 */     String str = null;
/* 40 */     if (this.fd.getDirectory() != null && this.fd.getFile() != null) {
/* 41 */       str = this.fd.getDirectory() + this.fd.getFile();
/*    */     }
/* 43 */     return str;
/*    */   }
/*    */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 46 */     setVisible(false);
/*    */   }
/*    */   
/*    */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowDeiconified(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowIconified(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*    */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/VFileDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */