/*    */ package com.hp.ilo2.remcons;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.WindowEvent;
/*    */ import java.awt.event.WindowListener;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class aboutJircDialog
/*    */   extends JDialog
/*    */   implements ActionListener, WindowListener
/*    */ {
/*    */   JPanel mainPanel;
/*    */   JLabel txt1;
/*    */   JLabel txt2;
/*    */   JLabel txt3;
/*    */   JButton close;
/*    */   remcons remconsObj;
/*    */   
/*    */   public aboutJircDialog(remcons paramremcons) {
/* 32 */     super(paramremcons.ParentApp.dispFrame, "About", false);
/*    */     
/* 34 */     this.remconsObj = paramremcons;
/* 35 */     ui_init();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void ui_init() {
/* 41 */     this.txt1 = new JLabel("Java Integrated Remote Console");
/* 42 */     this.txt2 = new JLabel("Version 231");
/* 43 */     this.txt3 = new JLabel("Copyright 2009, 2016 Hewlett Packard Enterprise Development, LP");
/*    */     
/* 45 */     this.mainPanel = new JPanel();
/* 46 */     this.mainPanel.setLayout(new GridBagLayout());
/*    */     
/* 48 */     this.close = new JButton("Close");
/* 49 */     this.close.addActionListener(this);
/*    */     
/* 51 */     setBackground(Color.lightGray);
/*    */     
/* 53 */     GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
/*    */     
/* 55 */     gridBagConstraints1.fill = 2;
/* 56 */     gridBagConstraints1.anchor = 17;
/* 57 */     gridBagConstraints1.gridx = 0;
/* 58 */     gridBagConstraints1.gridy = 0;
/* 59 */     this.mainPanel.add(this.txt1, gridBagConstraints1);
/* 60 */     gridBagConstraints1.gridy = 1;
/* 61 */     this.mainPanel.add(this.txt2, gridBagConstraints1);
/* 62 */     gridBagConstraints1.gridy = 2;
/* 63 */     this.mainPanel.add(this.txt3, gridBagConstraints1);
/*    */     
/* 65 */     JPanel jPanel = new JPanel();
/* 66 */     jPanel.setLayout(new FlowLayout(2));
/* 67 */     jPanel.add(this.close);
/*    */     
/* 69 */     setLayout(new GridBagLayout());
/* 70 */     GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
/*    */     
/* 72 */     gridBagConstraints2.fill = 2;
/* 73 */     gridBagConstraints2.anchor = 17;
/* 74 */     gridBagConstraints2.gridx = 0;
/* 75 */     gridBagConstraints2.gridy = 0;
/* 76 */     add(this.mainPanel, gridBagConstraints2);
/*    */     
/* 78 */     gridBagConstraints2.gridx = 0;
/* 79 */     gridBagConstraints2.gridy = 1;
/* 80 */     add(jPanel, gridBagConstraints2);
/*    */     
/* 82 */     setSize((this.mainPanel.getPreferredSize()).width + 40, (this.mainPanel.getPreferredSize()).height + 100);
/* 83 */     addWindowListener(this);
/* 84 */     setResizable(false);
/* 85 */     setLocationRelativeTo(null);
/* 86 */     setVisible(true);
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 91 */     if (paramActionEvent.getSource() == this.close) {
/* 92 */       dispose();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 99 */     dispose();
/*    */   }
/*    */   
/*    */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowDeiconified(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowIconified(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*    */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/aboutJircDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */