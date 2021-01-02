/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OkCancelDialog
/*     */   extends JDialog
/*     */   implements ActionListener, WindowListener
/*     */ {
/*     */   JPanel mainPanel;
/*     */   JLabel txt;
/*     */   JButton ok;
/*     */   JButton cancel;
/*     */   boolean rc;
/*     */   remcons cmdHandler;
/*     */   
/*     */   public OkCancelDialog(remcons paramremcons, JFrame paramJFrame, String paramString) {
/*  32 */     super(paramJFrame, "Notice!", true);
/*  33 */     this.cmdHandler = paramremcons;
/*  34 */     ui_init(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public OkCancelDialog(remcons paramremcons, String paramString, boolean paramBoolean) {
/*  39 */     super(new JFrame(), "Notice!", paramBoolean);
/*  40 */     this.cmdHandler = paramremcons;
/*  41 */     ui_init(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  46 */     String str = "";
/*     */     try {
/*  48 */       str = this.cmdHandler.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*  50 */       System.out.println("OkCancelDialog:getLocalString" + exception.getMessage());
/*     */     } 
/*  52 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ui_init(String paramString) {
/*  57 */     this.txt = new JLabel(paramString);
/*     */ 
/*     */     
/*  60 */     this.mainPanel = new JPanel();
/*  61 */     this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
/*  62 */     this.mainPanel.add(this.txt);
/*  63 */     this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
/*     */     
/*  65 */     this.ok = new JButton("    " + getLocalString(12576) + "    ");
/*  66 */     this.ok.addActionListener(this);
/*     */     
/*  68 */     this.cancel = new JButton(getLocalString(12555));
/*  69 */     this.cancel.addActionListener(this);
/*     */     
/*  71 */     GridBagLayout gridBagLayout = new GridBagLayout();
/*  72 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*     */     
/*  74 */     setLayout(gridBagLayout);
/*     */     
/*  76 */     gridBagConstraints.fill = 2;
/*  77 */     gridBagConstraints.anchor = 17;
/*  78 */     gridBagConstraints.gridx = 0;
/*  79 */     gridBagConstraints.gridy = 0;
/*  80 */     add(this.mainPanel, gridBagConstraints);
/*     */     
/*  82 */     JPanel jPanel = new JPanel();
/*  83 */     jPanel.setLayout(new FlowLayout(2));
/*  84 */     jPanel.add(this.ok);
/*  85 */     jPanel.add(this.cancel);
/*     */     
/*  87 */     gridBagConstraints.fill = 0;
/*  88 */     gridBagConstraints.anchor = 13;
/*  89 */     gridBagConstraints.gridx = 0;
/*  90 */     gridBagConstraints.gridy = 1;
/*  91 */     gridBagConstraints.gridwidth = 1;
/*     */     
/*  93 */     add(jPanel, gridBagConstraints);
/*  94 */     addWindowListener(this);
/*     */     
/*  96 */     setSize((this.mainPanel.getPreferredSize()).width + 40, (this.txt.getPreferredSize()).height + 100);
/*  97 */     setResizable(false);
/*  98 */     setLocationRelativeTo(null);
/*  99 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 104 */     if (paramActionEvent.getSource() == this.ok) {
/* 105 */       dispose();
/* 106 */       this.rc = true;
/*     */     }
/* 108 */     else if (paramActionEvent.getSource() == this.cancel) {
/* 109 */       dispose();
/* 110 */       this.rc = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean result() {
/* 116 */     return this.rc;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String paramString) {
/* 122 */     this.txt.repaint();
/*     */   }
/*     */ 
/*     */   
/*     */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 127 */     dispose();
/* 128 */     this.rc = false;
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


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/OkCancelDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */