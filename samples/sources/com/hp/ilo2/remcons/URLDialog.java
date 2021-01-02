/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextArea;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URLDialog
/*     */   extends JDialog
/*     */   implements ActionListener, WindowListener
/*     */ {
/*     */   JPanel mainPanel;
/*     */   JLabel txt1;
/*     */   JLabel txt2;
/*     */   JTextArea txt3;
/*     */   JScrollPane scroller;
/*     */   JButton ok;
/*     */   JButton cancel;
/*     */   String url;
/*     */   boolean rc;
/*     */   remcons remconsObj;
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  37 */     String str = "";
/*     */     try {
/*  39 */       str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*  41 */       System.out.println("VSeizeDialog:getLocalString" + exception.getMessage());
/*     */     } 
/*  43 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public URLDialog(remcons paramremcons) {
/*  48 */     super((null == paramremcons.ParentApp.dispFrame) ? new JFrame() : paramremcons.ParentApp.dispFrame, paramremcons.getLocalString(8290), true);
/*  49 */     this.remconsObj = paramremcons;
/*  50 */     ui_init();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ui_init() {
/*  55 */     this.txt1 = new JLabel(getLocalString(8291) + "\n\n\n");
/*  56 */     this.txt3 = new JTextArea(1, 40);
/*  57 */     this.txt3.setEditable(true);
/*     */     
/*  59 */     this.mainPanel = new JPanel();
/*     */     
/*  61 */     this.mainPanel.setLayout(new GridBagLayout());
/*     */ 
/*     */     
/*  64 */     this.ok = new JButton(getLocalString(12577));
/*  65 */     this.ok.addActionListener(this);
/*     */     
/*  67 */     this.cancel = new JButton(getLocalString(12565));
/*  68 */     this.cancel.addActionListener(this);
/*     */     
/*  70 */     setBackground(Color.lightGray);
/*     */     
/*  72 */     GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
/*     */     
/*  74 */     gridBagConstraints1.fill = 2;
/*  75 */     gridBagConstraints1.anchor = 17;
/*  76 */     gridBagConstraints1.gridx = 0;
/*  77 */     gridBagConstraints1.gridy = 0;
/*  78 */     this.mainPanel.add(this.txt1, gridBagConstraints1);
/*     */     
/*  80 */     this.scroller = new JScrollPane(this.txt3, 21, 31);
/*     */     
/*  82 */     gridBagConstraints1.gridx = 0;
/*  83 */     gridBagConstraints1.gridy = 1;
/*  84 */     this.mainPanel.add(this.scroller, gridBagConstraints1);
/*     */     
/*  86 */     this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
/*     */     
/*  88 */     JPanel jPanel = new JPanel();
/*  89 */     jPanel.setLayout(new FlowLayout(2));
/*  90 */     jPanel.add(this.cancel);
/*  91 */     jPanel.add(this.ok);
/*     */     
/*  93 */     setLayout(new GridBagLayout());
/*  94 */     GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
/*     */     
/*  96 */     gridBagConstraints2.fill = 2;
/*  97 */     gridBagConstraints2.anchor = 17;
/*  98 */     gridBagConstraints2.gridx = 0;
/*  99 */     gridBagConstraints2.gridy = 0;
/* 100 */     add(this.mainPanel, gridBagConstraints2);
/*     */     
/* 102 */     gridBagConstraints2.gridx = 0;
/* 103 */     gridBagConstraints2.gridy = 1;
/* 104 */     add(jPanel, gridBagConstraints2);
/*     */     
/* 106 */     setSize((this.mainPanel.getPreferredSize()).width + 40, (this.mainPanel.getPreferredSize()).height + 100);
/* 107 */     addWindowListener(this);
/* 108 */     setResizable(false);
/* 109 */     setLocationRelativeTo(null);
/* 110 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 115 */     if (paramActionEvent.getSource() == this.ok) {
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
/* 128 */       this.url = this.txt3.getText();
/* 129 */       dispose();
/* 130 */     } else if (paramActionEvent.getSource() == this.cancel) {
/* 131 */       this.url = "userhitcancel";
/* 132 */       dispose();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUserInput() {
/* 139 */     return this.url;
/*     */   }
/*     */ 
/*     */   
/*     */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 144 */     this.url = "userhitclose";
/* 145 */     dispose();
/* 146 */     this.rc = false;
/*     */   }
/*     */   
/*     */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeiconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowIconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/URLDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */