/*     */ package com.hp.ilo2.virtdevs;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.CheckboxGroup;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.IOException;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ 
/*     */ public class CreateImage extends JDialog implements ActionListener, WindowListener, ItemListener, Runnable {
/*     */   Choice fdDrive;
/*     */   JTextField ImgFile;
/*     */   JTextField DriveFile;
/*     */   JButton browse;
/*     */   JButton create;
/*     */   JButton cancel;
/*     */   JButton dimg;
/*     */   JButton dbrowse;
/*  35 */   int retrycount = 10; VProgressBar progress; boolean canceled = false; boolean diskimage = true; boolean iscdrom = false; JFrame frame; String[] dev; int[] devt; boolean defaultRemovable = false;
/*     */   private JLabel statLabel;
/*     */   JPanel p;
/*     */   CheckboxGroup drvGroup;
/*     */   Checkbox drvSel;
/*     */   Checkbox drvPath;
/*  41 */   int drvCboxChecked = 0;
/*     */   
/*  43 */   int targetIsDevice = 1;
/*  44 */   int targetIsCdrom = 0;
/*     */   
/*     */   virtdevs virtdevsObj;
/*     */ 
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  50 */     String str = "";
/*     */     try {
/*  52 */       str = this.virtdevsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*  54 */       System.out.println("CreateImage:getLocalString" + exception.getMessage());
/*     */     } 
/*  56 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public CreateImage(virtdevs paramvirtdevs) {
/*  61 */     super(paramvirtdevs.parent, paramvirtdevs.getLocalString(12544));
/*  62 */     boolean bool1 = true;
/*  63 */     boolean bool2 = false;
/*     */     
/*  65 */     this.virtdevsObj = paramvirtdevs;
/*  66 */     this.frame = paramvirtdevs.parent;
/*     */     
/*  68 */     this.virtdevsObj.ParentApp.vdMenuItemCrImage.setEnabled(false);
/*     */     
/*  70 */     setSize(400, 330);
/*  71 */     setResizable(false);
/*  72 */     setModal(false);
/*  73 */     addWindowListener(this);
/*  74 */     setLayout(new GridLayout());
/*     */     
/*  76 */     GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
/*  77 */     gridBagConstraints1.fill = 2;
/*  78 */     setLayout(new GridBagLayout());
/*     */ 
/*     */     
/*  81 */     this.dimg = new JButton(getLocalString(12551));
/*  82 */     this.dimg.addActionListener(this);
/*     */     
/*  84 */     JPanel jPanel1 = new JPanel();
/*  85 */     jPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), getLocalString(12577)));
/*     */     
/*  87 */     jPanel1.add(this.dimg);
/*     */     
/*  89 */     gridBagConstraints1.gridx = 0;
/*  90 */     gridBagConstraints1.gridy = 0;
/*  91 */     add(jPanel1, gridBagConstraints1);
/*     */ 
/*     */     
/*  94 */     this.drvGroup = new CheckboxGroup();
/*  95 */     this.drvSel = new Checkbox(getLocalString(12547), this.drvGroup, true);
/*  96 */     this.drvSel.addItemListener(this);
/*  97 */     this.drvPath = new Checkbox(getLocalString(12548), this.drvGroup, false);
/*  98 */     this.drvPath.addItemListener(this);
/*     */     
/* 100 */     this.DriveFile = new JTextField();
/* 101 */     this.DriveFile.addActionListener(this);
/*     */     
/* 103 */     this.dbrowse = new JButton(getLocalString(12553));
/* 104 */     this.dbrowse.setEnabled(false);
/* 105 */     this.dbrowse.addActionListener(this);
/*     */     
/* 107 */     this.fdDrive = new Choice();
/*     */     
/* 109 */     MediaAccess mediaAccess = new MediaAccess();
/* 110 */     this.dev = mediaAccess.devices();
/* 111 */     this.devt = new int[this.dev.length];
/* 112 */     for (byte b = 0; b < this.dev.length; b++) {
/*     */       
/* 114 */       this.devt[b] = mediaAccess.devtype(this.dev[b]);
/*     */       
/* 116 */       if (this.devt[b] == 2) {
/* 117 */         this.fdDrive.add(this.dev[b]);
/* 118 */         bool1 = false;
/* 119 */         this.defaultRemovable = true;
/*     */       } 
/* 121 */       if (this.devt[b] == 5) {
/* 122 */         this.fdDrive.add(this.dev[b]);
/* 123 */         if (b == 0) {
/* 124 */           this.iscdrom = true;
/* 125 */         } else if (!this.defaultRemovable) {
/* 126 */           this.iscdrom = true;
/* 127 */           bool2 = true;
/*     */         } 
/* 129 */         bool1 = false;
/*     */       } 
/*     */     } 
/* 132 */     if (bool1)
/* 133 */       this.fdDrive.add(getLocalString(12550)); 
/* 134 */     mediaAccess = null;
/* 135 */     this.fdDrive.addItemListener(this);
/*     */     
/* 137 */     JPanel jPanel2 = new JPanel(new GridBagLayout());
/* 138 */     jPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), getLocalString(12578)));
/*     */     
/* 140 */     GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
/*     */     
/* 142 */     gridBagConstraints2.gridx = 0;
/* 143 */     gridBagConstraints2.gridy = 0;
/* 144 */     jPanel2.add(this.drvSel, gridBagConstraints2);
/* 145 */     gridBagConstraints2.gridx = 1;
/* 146 */     gridBagConstraints2.gridy = 0;
/* 147 */     jPanel2.add(this.fdDrive, gridBagConstraints2);
/* 148 */     gridBagConstraints2.gridx = 0;
/* 149 */     gridBagConstraints2.gridy = 1;
/* 150 */     jPanel2.add(this.drvPath, gridBagConstraints2);
/* 151 */     gridBagConstraints2.gridx = 2;
/* 152 */     gridBagConstraints2.gridy = 1;
/* 153 */     gridBagConstraints2.weighty = 1.0D;
/* 154 */     gridBagConstraints2.anchor = 19;
/* 155 */     jPanel2.add(this.dbrowse, gridBagConstraints2);
/* 156 */     gridBagConstraints2.ipadx = 187;
/* 157 */     gridBagConstraints2.gridx = 1;
/* 158 */     gridBagConstraints2.gridy = 1;
/* 159 */     jPanel2.add(this.DriveFile, gridBagConstraints2);
/*     */     
/* 161 */     gridBagConstraints1.gridx = 0;
/* 162 */     gridBagConstraints1.gridy = 1;
/* 163 */     add(jPanel2, gridBagConstraints1);
/*     */ 
/*     */     
/* 166 */     this.ImgFile = new JTextField();
/* 167 */     this.ImgFile.setSize(250, 30);
/* 168 */     this.ImgFile.addActionListener(this);
/*     */     
/* 170 */     this.browse = new JButton(getLocalString(12553));
/* 171 */     this.browse.addActionListener(this);
/*     */     
/* 173 */     JPanel jPanel3 = new JPanel(new GridBagLayout());
/* 174 */     jPanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), getLocalString(12579)));
/*     */     
/* 176 */     GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
/*     */     
/* 178 */     gridBagConstraints3.gridx = 1;
/* 179 */     gridBagConstraints3.gridy = 0;
/* 180 */     jPanel3.add(this.browse, gridBagConstraints3);
/* 181 */     gridBagConstraints3.ipadx = 270;
/* 182 */     gridBagConstraints3.gridx = 0;
/* 183 */     gridBagConstraints3.gridy = 0;
/* 184 */     jPanel3.add(this.ImgFile, gridBagConstraints3);
/*     */     
/* 186 */     gridBagConstraints1.gridx = 0;
/* 187 */     gridBagConstraints1.gridy = 2;
/* 188 */     add(jPanel3, gridBagConstraints1);
/*     */ 
/*     */     
/* 191 */     this.progress = new VProgressBar(350, 25, Color.lightGray, Color.blue, Color.white);
/*     */     
/* 193 */     JPanel jPanel4 = new JPanel(new GridBagLayout());
/* 194 */     jPanel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), getLocalString(12580)));
/*     */     
/* 196 */     GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
/*     */     
/* 198 */     this.statLabel = new JLabel(" ");
/* 199 */     this.statLabel.setFont(new Font("Arial", 1, 12));
/*     */     
/* 201 */     gridBagConstraints4.gridx = 0;
/* 202 */     gridBagConstraints4.gridy = 0;
/* 203 */     jPanel4.add(this.statLabel, gridBagConstraints4);
/* 204 */     gridBagConstraints4.gridx = 0;
/* 205 */     gridBagConstraints4.gridy = 1;
/* 206 */     jPanel4.add(this.progress, gridBagConstraints4);
/*     */ 
/*     */     
/* 209 */     gridBagConstraints1.gridx = 0;
/* 210 */     gridBagConstraints1.gridy = 3;
/* 211 */     add(jPanel4, gridBagConstraints1);
/*     */ 
/*     */     
/* 214 */     this.create = new JButton(getLocalString(12554));
/* 215 */     this.create.setEnabled(false);
/* 216 */     this.create.addActionListener(this);
/*     */     
/* 218 */     this.cancel = new JButton(getLocalString(12555));
/* 219 */     this.cancel.addActionListener(this);
/*     */     
/* 221 */     this.p = new JPanel();
/* 222 */     this.p.setLayout(new FlowLayout(2));
/* 223 */     this.p.add(this.create);
/* 224 */     this.p.add(this.cancel);
/*     */     
/* 226 */     gridBagConstraints1.gridx = 0;
/* 227 */     gridBagConstraints1.gridy = 4;
/* 228 */     add(this.p, gridBagConstraints1);
/*     */     
/* 230 */     if (bool2) {
/* 231 */       this.dimg.setLabel(getLocalString(12551));
/* 232 */       this.diskimage = true;
/* 233 */       this.dimg.setEnabled(false);
/*     */     } else {
/* 235 */       this.dimg.setEnabled(true);
/*     */     } 
/* 237 */     this.dimg.repaint();
/*     */     
/* 239 */     setLocationRelativeTo(null);
/* 240 */     setVisible(true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void add(Component paramComponent, GridBagConstraints paramGridBagConstraints, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/* 246 */     paramGridBagConstraints.gridx = paramInt1;
/* 247 */     paramGridBagConstraints.gridy = paramInt2;
/* 248 */     paramGridBagConstraints.gridwidth = paramInt3;
/* 249 */     paramGridBagConstraints.gridheight = paramInt4;
/* 250 */     add(paramComponent, paramGridBagConstraints);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 255 */     Object object = paramActionEvent.getSource();
/* 256 */     if (object == this.browse) {
/* 257 */       this.statLabel.setText(" ");
/* 258 */       this.progress.updateBar(0.0F);
/* 259 */       VFileDialog vFileDialog = new VFileDialog(getLocalString(12556), null);
/* 260 */       String str = vFileDialog.getString();
/* 261 */       if (str != null) {
/* 262 */         this.ImgFile.setText(str);
/*     */         
/* 264 */         if ((0 == this.drvCboxChecked && !this.fdDrive.getSelectedItem().equals(getLocalString(12550))) || (1 == this.drvCboxChecked && !this.DriveFile.getText().equals(""))) {
/* 265 */           this.create.setEnabled(true);
/*     */         } else {
/* 267 */           this.create.setEnabled(false);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 272 */     if (object == this.dbrowse) {
/* 273 */       this.statLabel.setText(" ");
/* 274 */       this.progress.updateBar(0.0F);
/* 275 */       VFileDialog vFileDialog = new VFileDialog(getLocalString(12557), null);
/* 276 */       String str = vFileDialog.getString();
/* 277 */       if (str != null) {
/* 278 */         this.DriveFile.setText(str);
/*     */         
/* 280 */         if (!this.ImgFile.getText().equals("")) {
/* 281 */           this.create.setEnabled(true);
/*     */         } else {
/* 283 */           this.create.setEnabled(false);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 288 */     if (object == this.create) {
/* 289 */       this.create.setEnabled(false);
/* 290 */       this.browse.setEnabled(false);
/* 291 */       if (0 == this.drvCboxChecked) {
/* 292 */         this.fdDrive.setEnabled(false);
/*     */       } else {
/* 294 */         this.DriveFile.setEnabled(false);
/* 295 */         this.dbrowse.setEnabled(false);
/*     */       } 
/* 297 */       this.ImgFile.setEnabled(false);
/* 298 */       this.dimg.setEnabled(false);
/* 299 */       if (this.diskimage) {
/* 300 */         this.statLabel.setText(getLocalString(12558));
/*     */       } else {
/* 302 */         this.statLabel.setText(getLocalString(12559));
/* 303 */       }  Thread thread = new Thread(this);
/* 304 */       thread.start();
/*     */     } 
/* 306 */     if (object == this.dimg) {
/* 307 */       this.statLabel.setText(" ");
/* 308 */       this.progress.updateBar(0.0F);
/* 309 */       this.diskimage = !this.diskimage;
/* 310 */       if (this.diskimage) {
/* 311 */         this.dimg.setLabel(getLocalString(12551));
/*     */       } else {
/* 313 */         this.dimg.setLabel(getLocalString(12552));
/*     */       } 
/* 315 */       this.dimg.repaint();
/*     */     } 
/* 317 */     if (object == this.cancel) {
/* 318 */       this.statLabel.setText(" ");
/* 319 */       this.progress.updateBar(0.0F);
/* 320 */       this.virtdevsObj.ParentApp.vdMenuItemCrImage.setEnabled(true);
/* 321 */       this.canceled = true;
/* 322 */       dispose();
/*     */     } 
/*     */     
/* 325 */     if (object == this.ImgFile) {
/* 326 */       this.statLabel.setText(" ");
/* 327 */       this.progress.updateBar(0.0F);
/* 328 */       if (!this.ImgFile.getText().equals("") && ((0 == this.drvCboxChecked && !this.fdDrive.getSelectedItem().equals(getLocalString(12550))) || (1 == this.drvCboxChecked && !this.DriveFile.getText().equals(""))))
/*     */       
/* 330 */       { this.create.setEnabled(true); }
/*     */       else
/* 332 */       { this.create.setEnabled(false); } 
/* 333 */     } else if (object == this.DriveFile) {
/* 334 */       this.statLabel.setText(" ");
/* 335 */       this.progress.updateBar(0.0F);
/* 336 */       if (!this.ImgFile.getText().equals("") && !this.DriveFile.getText().equals("")) {
/* 337 */         this.create.setEnabled(true);
/*     */       } else {
/* 339 */         this.create.setEnabled(false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void itemStateChanged(ItemEvent paramItemEvent) {
/* 345 */     Object object = paramItemEvent.getSource();
/*     */     
/* 347 */     if (object == this.fdDrive) {
/* 348 */       this.statLabel.setText(" ");
/* 349 */       this.progress.updateBar(0.0F);
/* 350 */       String str = this.fdDrive.getSelectedItem(); byte b;
/* 351 */       for (b = 0; b < this.dev.length && !str.equals(this.dev[b]); b++);
/*     */       
/* 353 */       if (b < this.dev.length) {
/* 354 */         this.iscdrom = (this.devt[b] == 5);
/*     */       } else {
/* 356 */         this.iscdrom = false;
/* 357 */         this.create.setEnabled(false);
/*     */       } 
/* 359 */       if (this.iscdrom) {
/* 360 */         this.dimg.setLabel(getLocalString(12551));
/* 361 */         this.diskimage = true;
/* 362 */         this.dimg.setEnabled(false);
/*     */       } else {
/* 364 */         this.dimg.setEnabled(true);
/*     */       } 
/* 366 */       this.dimg.repaint();
/*     */       
/* 368 */       if (!this.ImgFile.getText().equals("") && !this.fdDrive.getSelectedItem().equals(getLocalString(12550))) {
/* 369 */         this.create.setEnabled(true);
/*     */       } else {
/* 371 */         this.create.setEnabled(false);
/*     */       } 
/*     */     } 
/* 374 */     if (paramItemEvent.getSource() == this.drvSel) {
/* 375 */       this.DriveFile.setEditable(false);
/* 376 */       this.dbrowse.setEnabled(false);
/* 377 */       this.fdDrive.setEnabled(true);
/* 378 */       this.drvCboxChecked = 0;
/*     */       
/* 380 */       this.statLabel.setText(" ");
/* 381 */       this.progress.updateBar(0.0F);
/* 382 */       String str = this.fdDrive.getSelectedItem(); byte b;
/* 383 */       for (b = 0; b < this.dev.length && !str.equals(this.dev[b]); b++);
/*     */       
/* 385 */       if (b < this.dev.length) {
/* 386 */         this.iscdrom = (this.devt[b] == 5);
/*     */       } else {
/* 388 */         this.iscdrom = false;
/*     */       } 
/* 390 */       if (this.iscdrom) {
/* 391 */         this.dimg.setLabel(getLocalString(12551));
/* 392 */         this.diskimage = true;
/* 393 */         this.dimg.setEnabled(false);
/*     */       } else {
/* 395 */         this.dimg.setEnabled(true);
/*     */       } 
/* 397 */       this.dimg.repaint();
/*     */       
/* 399 */       if (!this.fdDrive.getSelectedItem().equals(getLocalString(12550)) && !this.ImgFile.getText().equals("")) {
/* 400 */         this.create.setEnabled(true);
/*     */       } else {
/* 402 */         this.create.setEnabled(false);
/*     */       } 
/* 404 */     } else if (paramItemEvent.getSource() == this.drvPath) {
/* 405 */       this.DriveFile.setEditable(true);
/* 406 */       this.dbrowse.setEnabled(true);
/* 407 */       this.fdDrive.setEnabled(false);
/* 408 */       this.drvCboxChecked = 1;
/*     */       
/* 410 */       this.dimg.setLabel(getLocalString(12551));
/* 411 */       this.diskimage = true;
/* 412 */       this.dimg.setEnabled(false);
/* 413 */       this.dimg.repaint();
/*     */       
/* 415 */       if (!this.DriveFile.getText().equals("") && !this.ImgFile.getText().equals("")) {
/* 416 */         this.create.setEnabled(true);
/*     */       } else {
/* 418 */         this.create.setEnabled(false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int cdrom_testunitready(MediaAccess paramMediaAccess) {
/* 425 */     byte[] arrayOfByte1 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 426 */     byte[] arrayOfByte2 = new byte[8];
/* 427 */     byte[] arrayOfByte3 = new byte[3];
/*     */     
/* 429 */     int i = paramMediaAccess.scsi(arrayOfByte1, 1, 8, arrayOfByte2, arrayOfByte3);
/* 430 */     if (i >= 0)
/* 431 */       i = SCSI.mk_int32(arrayOfByte2, 0) * SCSI.mk_int32(arrayOfByte2, 4); 
/* 432 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int cdrom_startstopunit(MediaAccess paramMediaAccess) {
/* 437 */     byte[] arrayOfByte1 = { 27, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
/* 438 */     byte[] arrayOfByte2 = new byte[8];
/* 439 */     byte[] arrayOfByte3 = new byte[3];
/*     */     
/* 441 */     int i = paramMediaAccess.scsi(arrayOfByte1, 1, 8, arrayOfByte2, arrayOfByte3);
/*     */     
/* 443 */     if (i >= 0)
/* 444 */       i = SCSI.mk_int32(arrayOfByte2, 0) * SCSI.mk_int32(arrayOfByte2, 4); 
/* 445 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public long cdrom_size(MediaAccess paramMediaAccess) {
/* 450 */     byte[] arrayOfByte1 = { 37, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 451 */     byte[] arrayOfByte2 = new byte[8];
/* 452 */     byte[] arrayOfByte3 = new byte[3];
/*     */     
/* 454 */     long l = paramMediaAccess.scsi(arrayOfByte1, 1, 8, arrayOfByte2, arrayOfByte3);
/* 455 */     if (l >= 0L)
/* 456 */       l = SCSI.mk_int32(arrayOfByte2, 0) * SCSI.mk_int32(arrayOfByte2, 4); 
/* 457 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public void cdrom_read(MediaAccess paramMediaAccess, long paramLong, int paramInt, byte[] paramArrayOfbyte) throws IOException {
/* 462 */     byte[] arrayOfByte1 = new byte[12];
/* 463 */     byte[] arrayOfByte2 = new byte[3];
/*     */ 
/*     */     
/* 466 */     int i = (int)(paramLong / 2048L);
/* 467 */     arrayOfByte1[0] = 40;
/* 468 */     arrayOfByte1[1] = 0;
/* 469 */     arrayOfByte1[2] = (byte)(i >> 24 & 0xFF);
/* 470 */     arrayOfByte1[3] = (byte)(i >> 16 & 0xFF);
/* 471 */     arrayOfByte1[4] = (byte)(i >> 8 & 0xFF);
/* 472 */     arrayOfByte1[5] = (byte)(i >> 0 & 0xFF);
/* 473 */     arrayOfByte1[6] = 0;
/* 474 */     arrayOfByte1[7] = (byte)(paramInt / 2048 >> 8 & 0xFF);
/* 475 */     arrayOfByte1[8] = (byte)(paramInt / 2048 >> 0 & 0xFF);
/* 476 */     arrayOfByte1[9] = 0;
/* 477 */     arrayOfByte1[10] = 0;
/* 478 */     arrayOfByte1[11] = 0;
/*     */     
/* 480 */     int j = paramMediaAccess.scsi(arrayOfByte1, 1, paramInt, paramArrayOfbyte, arrayOfByte2);
/* 481 */     if (j == -1)
/* 482 */       throw new IOException("Error reading CD-ROM."); 
/* 483 */     if (arrayOfByte2[0] != 0)
/* 484 */       throw new IOException("Error reading CD-ROM.  Sense data (" + D.hex(arrayOfByte2[0], 1) + "/" + D.hex(arrayOfByte2[1], 2) + "/" + D.hex(arrayOfByte2[2], 2) + ")"); 
/*     */   }
/*     */   
/*     */   public void cdrom_read_retry(MediaAccess paramMediaAccess, long paramLong, int paramInt, byte[] paramArrayOfbyte) throws IOException {
/*     */     int i;
/* 489 */     byte[] arrayOfByte1 = new byte[12];
/* 490 */     byte[] arrayOfByte2 = new byte[3];
/* 491 */     byte[] arrayOfByte3 = new byte[12];
/*     */ 
/*     */     
/* 494 */     boolean bool1 = false, bool2 = false;
/* 495 */     byte b = 0;
/*     */ 
/*     */     
/* 498 */     int j = (int)(paramLong / 2048L);
/* 499 */     arrayOfByte1[0] = 40;
/* 500 */     arrayOfByte1[1] = 0;
/* 501 */     arrayOfByte1[2] = (byte)(j >> 24 & 0xFF);
/* 502 */     arrayOfByte1[3] = (byte)(j >> 16 & 0xFF);
/* 503 */     arrayOfByte1[4] = (byte)(j >> 8 & 0xFF);
/* 504 */     arrayOfByte1[5] = (byte)(j >> 0 & 0xFF);
/* 505 */     arrayOfByte1[6] = 0;
/* 506 */     arrayOfByte1[7] = (byte)(paramInt / 2048 >> 8 & 0xFF);
/* 507 */     arrayOfByte1[8] = (byte)(paramInt / 2048 >> 0 & 0xFF);
/* 508 */     arrayOfByte1[9] = 0;
/* 509 */     arrayOfByte1[10] = 0;
/* 510 */     arrayOfByte1[11] = 0;
/*     */     
/*     */     do {
/* 513 */       long l1 = System.currentTimeMillis();
/* 514 */       i = paramMediaAccess.scsi(arrayOfByte1, 1, paramInt, paramArrayOfbyte, arrayOfByte2);
/* 515 */       long l2 = System.currentTimeMillis();
/*     */       
/* 517 */       if (i < 0) {
/* 518 */         cdrom_testunitready(paramMediaAccess);
/* 519 */         cdrom_startstopunit(paramMediaAccess);
/* 520 */         i = -1;
/*     */       } 
/*     */       
/* 523 */       if (arrayOfByte2[1] == 41) {
/* 524 */         i = -1;
/*     */       }
/* 526 */       if (arrayOfByte2[0] != 3 && arrayOfByte2[0] != 4)
/*     */         continue; 
/* 528 */       if (arrayOfByte2[1] == 2 && arrayOfByte2[2] == 0) {
/* 529 */         arrayOfByte3[0] = 43;
/* 530 */         arrayOfByte3[1] = 0;
/* 531 */         arrayOfByte3[2] = arrayOfByte1[2];
/* 532 */         arrayOfByte3[3] = arrayOfByte1[3];
/* 533 */         arrayOfByte3[4] = arrayOfByte1[4];
/* 534 */         arrayOfByte3[5] = arrayOfByte1[5];
/* 535 */         arrayOfByte3[6] = 0;
/* 536 */         arrayOfByte3[7] = 0;
/* 537 */         arrayOfByte3[8] = 0;
/* 538 */         arrayOfByte3[9] = 0;
/* 539 */         arrayOfByte3[10] = 0;
/* 540 */         arrayOfByte3[11] = 0;
/*     */         
/* 542 */         i = paramMediaAccess.scsi(arrayOfByte3, 1, paramInt, paramArrayOfbyte, arrayOfByte2);
/* 543 */         cdrom_testunitready(paramMediaAccess);
/*     */ 
/*     */       
/*     */       }
/* 547 */       else if (arrayOfByte2[1] == 17) {
/* 548 */         cdrom_testunitready(paramMediaAccess);
/* 549 */         cdrom_startstopunit(paramMediaAccess);
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 554 */         cdrom_testunitready(paramMediaAccess);
/*     */       } 
/*     */       
/* 557 */       i = -1;
/*     */     }
/* 559 */     while (i < 0 && b++ < this.retrycount);
/*     */     
/* 561 */     if (b >= this.retrycount) {
/* 562 */       D.println(0, "RETRIES FAILED ! ");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 569 */     int i = 0;
/* 570 */     long l2 = 0L;
/* 571 */     String str = this.ImgFile.getText();
/*     */     
/* 573 */     boolean bool1 = false;
/*     */     
/* 575 */     if (str.equals("")) {
/* 576 */       this.browse.setEnabled(true);
/* 577 */       if (0 == this.drvCboxChecked) {
/* 578 */         this.fdDrive.setEnabled(true);
/*     */       } else {
/* 580 */         this.DriveFile.setEnabled(true);
/* 581 */         this.dbrowse.setEnabled(true);
/*     */       } 
/* 583 */       this.ImgFile.setEnabled(true);
/* 584 */       this.DriveFile.setEnabled(true);
/* 585 */       this.dimg.setEnabled(true);
/*     */       
/*     */       return;
/*     */     } 
/* 589 */     MediaAccess mediaAccess1 = new MediaAccess();
/* 590 */     MediaAccess mediaAccess2 = new MediaAccess();
/*     */     
/* 592 */     System.out.println("Message from CreateImage");
/*     */     try {
/* 594 */       if (0 == this.drvCboxChecked && this.iscdrom) {
/* 595 */         int j = mediaAccess1.open(this.fdDrive.getSelectedItem(), 1);
/* 596 */         if (j < 0) {
/* 597 */           bool1 = true;
/* 598 */           new VErrorDialog(getLocalString(8247) + " (" + mediaAccess1.dio.sysError(-j) + ")", false);
/* 599 */           throw new IOException("Couldn't open cdrom " + j);
/*     */         } 
/* 601 */         cdrom_testunitready(mediaAccess1);
/* 602 */         l2 = cdrom_size(mediaAccess1);
/* 603 */         i = 65536;
/*     */       } else {
/* 605 */         if (0 == this.drvCboxChecked) {
/* 606 */           int j = mediaAccess1.open(this.fdDrive.getSelectedItem(), 1);
/* 607 */           this.targetIsDevice = 1;
/* 608 */           this.targetIsCdrom = 0;
/* 609 */           System.out.println("CrtDev " + this.fdDrive.getSelectedItem() + " " + j + " " + this.targetIsDevice);
/*     */         } else {
/* 611 */           int k = mediaAccess1.devtype(this.DriveFile.getText());
/* 612 */           if (k == 5) {
/* 613 */             this.targetIsDevice = 1;
/* 614 */             this.targetIsCdrom = 1;
/* 615 */           } else if (k == 2) {
/* 616 */             this.targetIsDevice = 1;
/* 617 */             this.targetIsCdrom = 0;
/*     */           } else {
/* 619 */             this.targetIsDevice = 0;
/* 620 */             this.targetIsCdrom = 0;
/*     */           } 
/* 622 */           int j = mediaAccess1.open(this.DriveFile.getText(), this.targetIsDevice);
/* 623 */           System.out.println("CrtFile " + this.DriveFile.getText() + " " + j + " " + this.targetIsDevice);
/*     */         } 
/*     */         
/* 626 */         if (1 == this.targetIsDevice) {
/* 627 */           if (1 == this.targetIsCdrom) {
/* 628 */             cdrom_testunitready(mediaAccess1);
/* 629 */             l2 = cdrom_size(mediaAccess1);
/* 630 */             i = 65536;
/*     */           } else {
/* 632 */             l2 = mediaAccess1.size();
/* 633 */             i = mediaAccess1.dio.BytesPerSec * mediaAccess1.dio.SecPerTrack;
/*     */           } 
/* 635 */           System.out.println("CrtDev actual Dev size" + l2 + " " + i);
/*     */         } else {
/* 637 */           l2 = mediaAccess1.size();
/* 638 */           i = (int)(l2 / 512L);
/* 639 */           System.out.println("CrtFile static Dev size" + l2 + " " + i);
/*     */         } 
/*     */       } 
/*     */     } catch (IOException iOException) {
/* 643 */       System.out.println("Exception opening media access");
/*     */     } 
/*     */     
/* 646 */     if (!this.diskimage && mediaAccess1.wp()) {
/* 647 */       new VErrorDialog(this.frame, getLocalString(8248) + " " + this.fdDrive.getSelectedItem() + getLocalString(8249));
/*     */       
/* 649 */       bool1 = true;
/* 650 */       this.create.setEnabled(true);
/* 651 */       this.browse.setEnabled(true);
/* 652 */       if (0 == this.drvCboxChecked) {
/* 653 */         this.fdDrive.setEnabled(true);
/*     */       } else {
/* 655 */         this.DriveFile.setEnabled(true);
/* 656 */         this.dbrowse.setEnabled(true);
/*     */       } 
/* 658 */       this.ImgFile.setEnabled(true);
/* 659 */       this.DriveFile.setEnabled(true);
/* 660 */       this.dimg.setEnabled(true);
/*     */       try {
/* 662 */         mediaAccess1.close();
/* 663 */       } catch (IOException iOException) {}
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 668 */     setCursor(Cursor.getPredefinedCursor(3));
/*     */     
/* 670 */     long l1 = l2;
/* 671 */     if (i == 0 || l1 == 0L) {
/* 672 */       String str1 = getLocalString(8250) + " " + getLocalString(8241);
/* 673 */       new VErrorDialog(this.frame, str1);
/* 674 */       bool1 = true;
/* 675 */       i = 0;
/* 676 */       l1 = 0L;
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/* 681 */         mediaAccess2.open(str, this.diskimage ? 2 : 0);
/*     */       } catch (IOException iOException) {
/* 683 */         new VErrorDialog(this.frame, getLocalString(8251) + str + ".");
/*     */       } 
/*     */     } 
/*     */     
/* 687 */     long l3 = 0L;
/* 688 */     byte[] arrayOfByte = new byte[i];
/* 689 */     boolean bool2 = false;
/*     */     try {
/* 691 */       while (l1 > 0L && !this.canceled) {
/* 692 */         int j = (i < l1) ? i : (int)l1;
/* 693 */         if (this.diskimage) {
/* 694 */           if (this.iscdrom) {
/* 695 */             cdrom_read_retry(mediaAccess1, l3, j, arrayOfByte);
/*     */           } else {
/* 697 */             mediaAccess1.read(l3, j, arrayOfByte);
/*     */           } 
/* 699 */           mediaAccess2.write(l3, j, arrayOfByte);
/*     */         } else {
/* 701 */           mediaAccess2.read(l3, j, arrayOfByte);
/* 702 */           mediaAccess1.write(l3, j, arrayOfByte);
/*     */         } 
/* 704 */         l3 += j;
/* 705 */         l1 -= j;
/*     */ 
/*     */ 
/*     */         
/* 709 */         if (!this.diskimage && ((float)l3 / (float)l2) >= 0.95D) {
/* 710 */           this.progress.updateBar(0.95F); continue;
/*     */         } 
/* 712 */         this.progress.updateBar((float)l3 / (float)l2);
/*     */       } 
/*     */     } catch (IOException iOException) {
/*     */       
/* 716 */       bool1 = true;
/* 717 */       new VErrorDialog(this.frame, getLocalString(8252) + (this.diskimage ? getLocalString(8253) : getLocalString(8254)) + getLocalString(8255) + " (" + iOException + ")");
/*     */     } 
/*     */ 
/*     */     
/* 721 */     setCursor(Cursor.getPredefinedCursor(0));
/*     */     
/* 723 */     if (!bool1) {
/*     */       try {
/* 725 */         mediaAccess1.close();
/* 726 */         mediaAccess2.close();
/*     */       } catch (IOException iOException) {
/* 728 */         D.println(0, "Closing: " + iOException);
/*     */       } 
/*     */       
/* 731 */       this.progress.updateBar((float)l3 / (float)l2);
/*     */       
/* 733 */       if (this.diskimage) {
/* 734 */         this.statLabel.setText(getLocalString(12560));
/*     */       } else {
/* 736 */         this.statLabel.setText(getLocalString(12561));
/*     */       } 
/* 738 */       this.p.remove(this.create);
/* 739 */       this.cancel.setLabel(getLocalString(12566));
/*     */     } else {
/*     */       
/* 742 */       this.statLabel.setText(" ");
/*     */     } 
/*     */     
/* 745 */     this.create.setEnabled(true);
/* 746 */     this.browse.setEnabled(true);
/* 747 */     if (0 == this.drvCboxChecked) {
/* 748 */       this.fdDrive.setEnabled(true);
/*     */     } else {
/* 750 */       this.DriveFile.setEnabled(true);
/* 751 */       this.dbrowse.setEnabled(true);
/*     */     } 
/* 753 */     this.ImgFile.setEnabled(true);
/* 754 */     this.DriveFile.setEnabled(true);
/* 755 */     if (this.iscdrom) {
/* 756 */       this.dimg.setEnabled(false);
/*     */     } else {
/* 758 */       this.dimg.setEnabled(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 764 */     this.virtdevsObj.ParentApp.vdMenuItemCrImage.setEnabled(true);
/* 765 */     this.canceled = true;
/* 766 */     dispose();
/*     */   }
/*     */   
/*     */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeiconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowIconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/CreateImage.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */