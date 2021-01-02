/*    */ package com.hp.ilo2.remcons;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Container;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.LayoutManager;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.awt.event.MouseListener;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ToolTip
/*    */   extends JPanel
/*    */   implements MouseListener
/*    */ {
/*    */   protected String tip;
/*    */   protected Component owner;
/*    */   private Container mainContainer;
/*    */   private LayoutManager mainLayout;
/*    */   private boolean shown;
/* 29 */   private final int VERTICAL_OFFSET = 10;
/* 30 */   private final int HORIZONTAL_ENLARGE = 10;
/*    */ 
/*    */   
/*    */   public ToolTip(String paramString, Component paramComponent) {
/* 34 */     this.tip = paramString;
/* 35 */     this.owner = paramComponent;
/* 36 */     paramComponent.addMouseListener(this);
/* 37 */     setBackground(new Color(255, 255, 220));
/*    */   }
/*    */ 
/*    */   
/*    */   public void paint(Graphics paramGraphics) {
/* 42 */     paramGraphics.drawRect(0, 0, (getSize()).width - 1, (getSize()).height - 1);
/* 43 */     paramGraphics.drawString(this.tip, 3, (getSize()).height - 3);
/*    */   }
/*    */ 
/*    */   
/*    */   private void addToolTip() {
/* 48 */     this.mainContainer.setLayout(null);
/* 49 */     FontMetrics fontMetrics = getFontMetrics(this.owner.getFont());
/* 50 */     setSize(fontMetrics.stringWidth(this.tip) + 10, fontMetrics.getHeight());
/* 51 */     setLocation((this.owner.getLocationOnScreen()).x - (this.mainContainer.getLocationOnScreen()).x, (this.owner.getLocationOnScreen()).y - (this.mainContainer.getLocationOnScreen()).y - 10);
/* 52 */     if ((this.mainContainer.getSize()).width < (getLocation()).x + (getSize()).width) {
/* 53 */       setLocation((this.mainContainer.getSize()).width - (getSize()).width + 10, (getLocation()).y - 10);
/*    */     }
/* 55 */     this.mainContainer.add(this, 0);
/* 56 */     this.mainContainer.validate();
/* 57 */     repaint();
/* 58 */     this.shown = true;
/*    */   }
/*    */ 
/*    */   
/*    */   private void removeToolTip() {
/* 63 */     if (this.shown) {
/* 64 */       this.mainContainer.remove(0);
/* 65 */       this.mainContainer.setLayout(this.mainLayout);
/* 66 */       this.mainContainer.validate();
/*    */     } 
/* 68 */     this.shown = false;
/*    */   }
/*    */ 
/*    */   
/*    */   private void findMainContainer() {
/* 73 */     Container container = this.owner.getParent();
/*    */     while (true) {
/* 75 */       if (container instanceof java.applet.Applet || container instanceof java.awt.Frame) {
/* 76 */         this.mainContainer = container;
/*    */         break;
/*    */       } 
/* 79 */       container = container.getParent();
/*    */     } 
/*    */     
/* 82 */     this.mainLayout = this.mainContainer.getLayout();
/*    */   }
/*    */ 
/*    */   
/*    */   public void mouseEntered(MouseEvent paramMouseEvent) {
/* 87 */     findMainContainer();
/* 88 */     addToolTip();
/*    */   }
/*    */ 
/*    */   
/*    */   public void mouseExited(MouseEvent paramMouseEvent) {
/* 93 */     removeToolTip();
/*    */   }
/*    */ 
/*    */   
/*    */   public void mousePressed(MouseEvent paramMouseEvent) {
/* 98 */     removeToolTip();
/*    */   }
/*    */   
/*    */   public void mouseClicked(MouseEvent paramMouseEvent) {}
/*    */   
/*    */   public void mouseReleased(MouseEvent paramMouseEvent) {}
/*    */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/ToolTip.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */