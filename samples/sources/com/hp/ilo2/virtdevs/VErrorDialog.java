/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VErrorDialog
/*     */   extends JDialog
/*     */   implements ActionListener
/*     */ {
/*     */   JPanel mainPanel;
/*     */   JLabel txt;
/*     */   JButton ok;
/*     */   boolean disp;
/*     */   virtdevs virtdevsObj;
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  33 */     String str = "";
/*     */     try {
/*  35 */       str = this.virtdevsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*  37 */       System.out.println("VErrorDialog:getLocalString" + exception.getMessage());
/*     */     } 
/*  39 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public VErrorDialog(JFrame paramJFrame, String paramString) {
/*  44 */     super(paramJFrame, "Error", true);
/*  45 */     ui_init(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public VErrorDialog(JFrame paramJFrame, String paramString1, String paramString2) {
/*  50 */     super(paramJFrame, paramString1, true);
/*  51 */     ui_init(paramString2);
/*     */   }
/*     */ 
/*     */   
/*     */   public VErrorDialog(String paramString, boolean paramBoolean) {
/*  56 */     super(new JFrame(), "Error", paramBoolean);
/*  57 */     ui_init(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public VErrorDialog(JFrame paramJFrame, String paramString1, String paramString2, boolean paramBoolean) {
/*  62 */     super(paramJFrame, paramString1, paramBoolean);
/*  63 */     ui_init(paramString2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void ui_init(String paramString) {
/*  69 */     this.txt = new JLabel(paramString);
/*     */ 
/*     */     
/*  72 */     this.mainPanel = new JPanel();
/*  73 */     this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
/*  74 */     this.mainPanel.add(this.txt);
/*  75 */     this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
/*     */     
/*  77 */     JPanel jPanel = new JPanel();
/*  78 */     jPanel.setLayout(new FlowLayout(2));
/*  79 */     this.ok = new JButton("    OK    ");
/*  80 */     this.ok.addActionListener(this);
/*  81 */     jPanel.add(this.ok);
/*  82 */     getRootPane().setDefaultButton(this.ok);
/*     */     
/*  84 */     GridBagLayout gridBagLayout = new GridBagLayout();
/*  85 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*     */     
/*  87 */     setLayout(gridBagLayout);
/*     */     
/*  89 */     gridBagConstraints.fill = 0;
/*  90 */     gridBagConstraints.anchor = 10;
/*  91 */     gridBagConstraints.gridx = 0;
/*  92 */     gridBagConstraints.gridy = 0;
/*  93 */     add(this.mainPanel, gridBagConstraints);
/*     */     
/*  95 */     gridBagConstraints.fill = 0;
/*  96 */     gridBagConstraints.anchor = 13;
/*  97 */     gridBagConstraints.gridx = 0;
/*  98 */     gridBagConstraints.gridy = 1;
/*  99 */     add(jPanel, gridBagConstraints);
/*     */     
/* 101 */     setSize((this.mainPanel.getPreferredSize()).width + 40, (this.mainPanel.getPreferredSize()).height + 100);
/* 102 */     setResizable(false);
/* 103 */     setLocationRelativeTo(null);
/* 104 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 109 */     if (paramActionEvent.getSource() == this.ok) {
/* 110 */       this.disp = true;
/* 111 */       dispose();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBoolean() {
/* 117 */     return this.disp;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String paramString) {
/* 123 */     this.txt.repaint();
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/VErrorDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */