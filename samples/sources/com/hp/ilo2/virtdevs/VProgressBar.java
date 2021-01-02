/*     */ package com.hp.ilo2.virtdevs;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
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
/*     */ public class VProgressBar
/*     */   extends Canvas
/*     */ {
/*     */   private int progressWidth;
/*     */   private int progressHeight;
/*     */   private float percentage;
/*     */   private Image offscreenImg;
/*     */   private Graphics offscreenG;
/*  32 */   private Color progressColor = Color.red;
/*     */   
/*  34 */   private Color progressBackground = Color.white;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VProgressBar(int paramInt1, int paramInt2) {
/*  42 */     Font font = new Font("Dialog", 0, 15);
/*  43 */     setFont(font);
/*     */     
/*  45 */     this.progressWidth = paramInt1;
/*  46 */     this.progressHeight = paramInt2;
/*  47 */     setSize(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VProgressBar(int paramInt1, int paramInt2, Color paramColor1, Color paramColor2, Color paramColor3) {
/*  56 */     Font font = new Font("Dialog", 0, 12);
/*  57 */     setFont(font);
/*     */     
/*  59 */     this.progressWidth = paramInt1;
/*  60 */     this.progressHeight = paramInt2;
/*  61 */     this.progressColor = paramColor2;
/*  62 */     this.progressBackground = paramColor3;
/*  63 */     setSize(paramInt1, paramInt2);
/*     */     
/*  65 */     setBackground(paramColor1);
/*     */   }
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
/*     */   public void updateBar(float paramFloat) {
/*  78 */     this.percentage = paramFloat;
/*  79 */     repaint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCanvasColor(Color paramColor) {
/*  87 */     setBackground(paramColor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProgressColor(Color paramColor) {
/*  95 */     this.progressColor = paramColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBackGroundColor(Color paramColor) {
/* 103 */     this.progressBackground = paramColor;
/*     */   }
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
/*     */   public void paint(Graphics paramGraphics) {
/* 119 */     int i = 0;
/* 120 */     int j = 0;
/* 121 */     byte b = 4;
/*     */     
/* 123 */     if (this.offscreenImg == null) {
/* 124 */       this.offscreenImg = createImage(this.progressWidth - b, this.progressHeight - b);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 131 */     this.offscreenG = this.offscreenImg.getGraphics();
/*     */ 
/*     */     
/* 134 */     i = this.offscreenImg.getWidth(this);
/*     */     
/* 136 */     j = this.offscreenImg.getHeight(this);
/*     */ 
/*     */     
/* 139 */     this.offscreenG.setColor(this.progressBackground);
/* 140 */     this.offscreenG.fillRect(0, 0, i, j);
/*     */ 
/*     */     
/* 143 */     this.offscreenG.setColor(this.progressColor);
/* 144 */     this.offscreenG.fillRect(0, 0, (int)(i * this.percentage), j);
/*     */     
/* 146 */     this.offscreenG.drawString(Integer.toString((int)(this.percentage * 100.0F)) + "%", i / 2 - 8, j / 2 + 5);
/*     */ 
/*     */ 
/*     */     
/* 150 */     this.offscreenG.clipRect(0, 0, (int)(i * this.percentage), j);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 157 */     this.offscreenG.setColor(this.progressBackground);
/* 158 */     this.offscreenG.drawString(Integer.toString((int)(this.percentage * 100.0F)) + "%", i / 2 - 8, j / 2 + 5);
/*     */ 
/*     */ 
/*     */     
/* 162 */     paramGraphics.setColor(this.progressBackground);
/*     */     
/* 164 */     paramGraphics.draw3DRect((getSize()).width / 2 - this.progressWidth / 2, 0, this.progressWidth - 1, this.progressHeight - 1, false);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     paramGraphics.drawImage(this.offscreenImg, b / 2, b / 2, this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(Graphics paramGraphics) {
/* 178 */     paint(paramGraphics);
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/virtdevs/VProgressBar.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */