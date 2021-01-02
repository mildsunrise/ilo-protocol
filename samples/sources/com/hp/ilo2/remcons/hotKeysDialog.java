/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.util.Hashtable;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ 
/*     */ public class hotKeysDialog
/*     */   extends JDialog
/*     */   implements ActionListener, WindowListener
/*     */ {
/*     */   JButton close;
/*     */   String jsonString;
/*  27 */   String[] ctrl = new String[] { "Ctrl-T:", "Ctrl-U:", "Ctrl-V:", "Ctrl-W:", "Ctrl-X:", "Ctrl-Y:" };
/*     */   
/*  29 */   Hashtable map = new Hashtable(this)
/*     */     {
/*     */       private final hotKeysDialog this$0;
/*     */     };
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
/*     */   public hotKeysDialog(remcons paramremcons) {
/* 121 */     super(paramremcons.ParentApp.dispFrame, "Programmed Hot Keys", false);
/* 122 */     ui_init(paramremcons);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ui_init(remcons paramremcons) {
/* 127 */     setLayout(new GridBagLayout());
/* 128 */     GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
/*     */     
/* 130 */     this.close = new JButton("Close");
/* 131 */     this.close.addActionListener(this);
/*     */     
/* 133 */     JPanel jPanel1 = new JPanel(new GridLayout(6, 6));
/* 134 */     GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
/* 135 */     gridBagConstraints2.fill = 2;
/*     */     
/*     */     try {
/* 138 */       this.jsonString = paramremcons.ParentApp.jsonObj.getJSONRequest("hot_keys");
/* 139 */       this.jsonString = this.jsonString.trim();
/* 140 */       this.jsonString = this.jsonString.substring(1, this.jsonString.length() - 1);
/* 141 */       Pattern pattern = Pattern.compile("-?\\d+");
/* 142 */       Matcher matcher = pattern.matcher(this.jsonString);
/* 143 */       for (byte b = 0; b < 6; b++) {
/* 144 */         JLabel jLabel = new JLabel("        " + this.ctrl[b] + "        ");
/* 145 */         gridBagConstraints2.gridx = b;
/* 146 */         gridBagConstraints2.gridy = 0;
/* 147 */         gridBagConstraints2.fill = 2;
/* 148 */         jPanel1.add(jLabel, gridBagConstraints2);
/* 149 */         for (byte b1 = 1; b1 < 6 && matcher.find(); b1++) {
/* 150 */           JLabel jLabel1 = new JLabel((String)this.map.get(matcher.group()));
/* 151 */           gridBagConstraints2.gridx = b;
/* 152 */           gridBagConstraints2.gridy = b1;
/* 153 */           gridBagConstraints2.fill = 2;
/* 154 */           jPanel1.add(jLabel1, gridBagConstraints2);
/*     */         } 
/*     */       } 
/*     */     } catch (Exception exception) {
/*     */       
/* 159 */       System.out.println("Error Parsing the JSON Requets");
/* 160 */       exception.printStackTrace();
/* 161 */       dispose();
/*     */       
/*     */       return;
/*     */     } 
/* 165 */     jPanel1.setBorder(BorderFactory.createEtchedBorder(0));
/* 166 */     gridBagConstraints1.gridx = 0;
/* 167 */     gridBagConstraints1.gridy = 0;
/* 168 */     add(jPanel1, gridBagConstraints1);
/*     */     
/* 170 */     JPanel jPanel2 = new JPanel();
/* 171 */     jPanel2.setLayout(new FlowLayout(2));
/* 172 */     jPanel2.add(this.close);
/* 173 */     gridBagConstraints1.fill = 2;
/* 174 */     gridBagConstraints1.anchor = 13;
/* 175 */     gridBagConstraints1.gridx = 0;
/* 176 */     gridBagConstraints1.gridy = 1;
/* 177 */     add(jPanel2, gridBagConstraints1);
/*     */     
/* 179 */     pack();
/* 180 */     setSize((getPreferredSize()).width + 20, (getPreferredSize()).height + 10);
/* 181 */     setResizable(false);
/*     */ 
/*     */     
/* 184 */     setLocationRelativeTo(null);
/* 185 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 190 */     if (paramActionEvent.getSource() == this.close) {
/* 191 */       dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 197 */     dispose();
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


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/hotKeysDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */