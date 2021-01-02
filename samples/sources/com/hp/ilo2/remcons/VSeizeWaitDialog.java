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
/*     */ public class VSeizeWaitDialog
/*     */   extends JDialog
/*     */   implements ActionListener
/*     */ {
/*     */   public static final byte SELYES = 0;
/*     */   public static final byte SELNO = 2;
/*     */   JPanel mainPanel;
/*     */   JLabel txt;
/*     */   JButton seize;
/*     */   JButton cancel;
/*     */   boolean disp;
/*     */   byte userInput;
/*     */   remcons remconsObj;
/*     */   private Timer szWaitTimer;
/*  34 */   private int szWaitTimerTick = 1000;
/*     */   
/*     */   String susr;
/*     */   String saddr;
/*     */   int sflag;
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  41 */     String str = "";
/*     */     try {
/*  43 */       str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*  45 */       System.out.println("VSeizeWaitDialog:getLocalString" + exception.getMessage());
/*     */     } 
/*  47 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public VSeizeWaitDialog(remcons paramremcons, String paramString1, String paramString2, int paramInt) {
/*  52 */     super((null == paramremcons.ParentApp.dispFrame) ? new JFrame() : paramremcons.ParentApp.dispFrame, paramremcons.getLocalString(12562), true);
/*  53 */     this.remconsObj = paramremcons;
/*  54 */     this.susr = paramString1;
/*  55 */     this.saddr = paramString2;
/*  56 */     this.sflag = paramInt;
/*  57 */     ui_init(paramremcons.ParentApp.dispFrame);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ui_init(JFrame paramJFrame) {
/*  62 */     this.txt = new JLabel("<html>" + getLocalString(8264) + " " + this.susr + " " + getLocalString(8265) + " " + this.saddr + " " + getLocalString(8282) + "<br><br>" + getLocalString(8283) + this.sflag + getLocalString(8284) + "</html>");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     this.mainPanel = new JPanel();
/*  69 */     this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
/*  70 */     this.mainPanel.add(this.txt);
/*  71 */     this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
/*     */     
/*  73 */     this.seize = new JButton(getLocalString(8285));
/*  74 */     this.seize.addActionListener(this);
/*  75 */     this.cancel = new JButton(getLocalString(8286));
/*  76 */     this.cancel.addActionListener(this);
/*     */     
/*  78 */     GridBagLayout gridBagLayout = new GridBagLayout();
/*  79 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*     */     
/*  81 */     setLayout(gridBagLayout);
/*     */     
/*  83 */     gridBagConstraints.fill = 2;
/*  84 */     gridBagConstraints.anchor = 17;
/*  85 */     gridBagConstraints.gridx = 0;
/*  86 */     gridBagConstraints.gridy = 0;
/*  87 */     add(this.mainPanel, gridBagConstraints);
/*     */     
/*  89 */     JPanel jPanel = new JPanel();
/*  90 */     jPanel.setLayout(new FlowLayout(2));
/*  91 */     jPanel.add(this.cancel);
/*  92 */     jPanel.add(this.seize);
/*     */ 
/*     */     
/*  95 */     gridBagConstraints.fill = 0;
/*  96 */     gridBagConstraints.anchor = 13;
/*  97 */     gridBagConstraints.gridx = 0;
/*  98 */     gridBagConstraints.gridy = 1;
/*  99 */     gridBagConstraints.gridwidth = 1;
/* 100 */     add(jPanel, gridBagConstraints);
/*     */     
/* 102 */     this.szWaitTimer = new Timer(this.szWaitTimerTick, false, this.remconsObj);
/* 103 */     this.szWaitTimer.setListener(new szWaitTimerListener(this), this);
/* 104 */     this.szWaitTimer.start();
/* 105 */     System.out.println("seize wait timer started...");
/*     */     
/* 107 */     setSize((this.mainPanel.getPreferredSize()).width + 40, (this.mainPanel.getPreferredSize()).height + 100);
/* 108 */     setResizable(false);
/* 109 */     setLocationRelativeTo(null);
/* 110 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 115 */     if (paramActionEvent.getSource() == this.seize) {
/* 116 */       this.userInput = 0;
/* 117 */       dispose();
/* 118 */       stop_szWaitTimer();
/* 119 */       this.disp = true;
/* 120 */     } else if (paramActionEvent.getSource() == this.cancel) {
/* 121 */       this.userInput = 2;
/* 122 */       dispose();
/* 123 */       stop_szWaitTimer();
/* 124 */       this.disp = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean disposed() {
/* 131 */     return this.disp;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String paramString) {
/* 137 */     this.txt.repaint();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getUserInput() {
/* 142 */     return this.userInput;
/*     */   }
/*     */ 
/*     */   
/*     */   private void stop_szWaitTimer() {
/* 147 */     if (this.szWaitTimer != null) {
/* 148 */       this.szWaitTimer.stop();
/* 149 */       this.szWaitTimer = null;
/*     */     } 
/*     */   }
/*     */   class szWaitTimerListener implements TimerListener { szWaitTimerListener(VSeizeWaitDialog this$0) {
/* 153 */       this.this$0 = this$0;
/*     */     }
/*     */     private final VSeizeWaitDialog this$0;
/*     */     public synchronized void timeout(Object param1Object) {
/* 157 */       VSeizeWaitDialog vSeizeWaitDialog = (VSeizeWaitDialog)param1Object;
/* 158 */       vSeizeWaitDialog.sflag--;
/* 159 */       if (vSeizeWaitDialog.sflag > 0) {
/* 160 */         this.this$0.txt.setText("<html>" + this.this$0.getLocalString(8264) + " " + vSeizeWaitDialog.susr + " " + this.this$0.getLocalString(8265) + " " + vSeizeWaitDialog.saddr + " " + this.this$0.getLocalString(8282) + "<br><br>" + this.this$0.getLocalString(8283) + vSeizeWaitDialog.sflag + " " + this.this$0.getLocalString(8284) + "</html>");
/*     */       } else {
/*     */         
/* 163 */         ActionEvent actionEvent = new ActionEvent(vSeizeWaitDialog.seize, 1, "vobjyes");
/* 164 */         vSeizeWaitDialog.actionPerformed(actionEvent);
/*     */       } 
/*     */     } }
/*     */ 
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/VSeizeWaitDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */