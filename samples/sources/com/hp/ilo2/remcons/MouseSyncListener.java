package com.hp.ilo2.remcons;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

interface MouseSyncListener {
  void serverMove(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void serverPress(int paramInt);
  
  void serverRelease(int paramInt);
  
  void serverClick(int paramInt1, int paramInt2);
  
  void sendMouse(MouseEvent paramMouseEvent);
  
  void sendMouseScroll(MouseWheelEvent paramMouseWheelEvent);
  
  void requestScreenFocus(MouseEvent paramMouseEvent);
  
  void installKeyboardHook();
  
  void unInstallKeyboardHook();
}


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/MouseSyncListener.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */