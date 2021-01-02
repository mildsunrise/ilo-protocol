/*     */ package com.hp.ilo2.remcons;
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
/*     */ 
/*     */ public class VSeizeDialog
/*     */   extends JDialog
/*     */   implements ActionListener
/*     */ {
/*     */   public static final byte SELCANCEL = 0;
/*     */   public static final byte SELSEIZE = 2;
/*     */   JPanel mainPanel;
/*     */   JLabel txt;
/*     */   JButton seize;
/*     */   JButton cancel;
/*     */   boolean disp;
/*     */   byte userInput;
/*     */   remcons remconsObj;
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  38 */     String str = "";
/*     */     try {
/*  40 */       str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*  42 */       System.out.println("VSeizeDialog:getLocalString" + exception.getMessage());
/*     */     } 
/*  44 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public VSeizeDialog(remcons paramremcons) {
/*  49 */     super((null == paramremcons.ParentApp.dispFrame) ? new JFrame() : paramremcons.ParentApp.dispFrame, paramremcons.getLocalString(12562), true);
/*  50 */     this.remconsObj = paramremcons;
/*  51 */     ui_init(paramremcons.ParentApp.dispFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ui_init(JFrame paramJFrame) {
/*  56 */     this.txt = new JLabel(getLocalString(12563));
/*     */ 
/*     */     
/*  59 */     this.mainPanel = new JPanel();
/*  60 */     this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
/*  61 */     this.mainPanel.add(this.txt);
/*  62 */     this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
/*     */     
/*  64 */     this.seize = new JButton(getLocalString(12564));
/*  65 */     this.seize.addActionListener(this);
/*     */ 
/*     */ 
/*     */     
/*  69 */     this.cancel = new JButton(getLocalString(12565));
/*  70 */     this.cancel.addActionListener(this);
/*     */     
/*  72 */     GridBagLayout gridBagLayout = new GridBagLayout();
/*  73 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*     */     
/*  75 */     setLayout(gridBagLayout);
/*     */     
/*  77 */     gridBagConstraints.fill = 2;
/*  78 */     gridBagConstraints.anchor = 17;
/*  79 */     gridBagConstraints.gridx = 0;
/*  80 */     gridBagConstraints.gridy = 0;
/*  81 */     add(this.mainPanel, gridBagConstraints);
/*     */     
/*  83 */     JPanel jPanel = new JPanel();
/*  84 */     jPanel.setLayout(new FlowLayout(2));
/*  85 */     jPanel.add(this.cancel);
/*  86 */     jPanel.add(this.seize);
/*     */ 
/*     */     
/*  89 */     gridBagConstraints.fill = 0;
/*  90 */     gridBagConstraints.anchor = 13;
/*  91 */     gridBagConstraints.gridx = 0;
/*  92 */     gridBagConstraints.gridy = 1;
/*  93 */     gridBagConstraints.gridwidth = 1;
/*  94 */     add(jPanel, gridBagConstraints);
/*     */     
/*  96 */     setSize((this.mainPanel.getPreferredSize()).width + 40, (this.mainPanel.getPreferredSize()).height + 100);
/*  97 */     setResizable(false);
/*  98 */     setLocationRelativeTo(null);
/*  99 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 104 */     if (paramActionEvent.getSource() == this.seize) {
/* 105 */       this.userInput = 2;
/* 106 */       dispose();
/* 107 */       this.disp = true;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 113 */     else if (paramActionEvent.getSource() == this.cancel) {
/* 114 */       this.userInput = 0;
/* 115 */       dispose();
/* 116 */       this.disp = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean disposed() {
/* 123 */     return this.disp;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String paramString) {
/* 129 */     this.txt.repaint();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getUserInput() {
/* 134 */     return this.userInput;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/VSeizeDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */