package com.hp.ilo2.intgapp;

import com.hp.ilo2.remcons.URLDialog;
import com.hp.ilo2.remcons.remcons;
import com.hp.ilo2.remcons.telnet;
import com.hp.ilo2.virtdevs.MediaAccess;
import com.hp.ilo2.virtdevs.VErrorDialog;
import com.hp.ilo2.virtdevs.VFileDialog;
import com.hp.ilo2.virtdevs.virtdevs;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class intgapp extends JApplet implements Runnable, ActionListener, ItemListener {
    public virtdevs virtdevsObj;
    public remcons remconsObj;
    public locinfo locinfoObj;
    public jsonparser jsonObj;
    public String optional_features;
    public String moniker;
    public boolean moniker_cached = false;
    public JFrame dispFrame;
    public JPanel mainPanel;
    JMenuBar mainMenuBar;
    JMenu psMenu;
    JMenu vdMenu;
    JMenu kbMenu;
    JMenu kbCAFMenu;
    JMenu kbAFMenu;
    JMenu kbLangMenu;
    JMenu hlpMenu;
    int vdmenuIndx;
    int fdMenuItems;
    int cdMenuItems;
    private MediaAccess ma;
    JCheckBoxMenuItem[] vdMenuItems;
    public JMenuItem vdMenuItemCrImage;
    JMenuItem momPress;
    public int blade = 0;
    JMenuItem pressHold;
    JMenuItem powerCycle;
    JMenuItem sysReset;
    JMenuItem ctlAltDel;
    JMenuItem numLock;
    JMenuItem capsLock;
    JMenuItem ctlAltBack;
    JMenuItem hotKeys;
    JMenuItem aboutJirc;
    JMenuItem[] ctlAltFn;
    JMenuItem[] AltFn;
    JCheckBoxMenuItem[] localKbdLayout;
    JPanel dispStatusBar;
    JMenuItem mdebug1;
    JMenuItem mdebug2;
    JMenuItem mdebug3;
    JScrollPane scroller;
    public String enc_key;
    public String rc_port;
    public String vm_key;
    public String vm_port;
    public String server_name;
    public String ilo_fqdn;
    public String enclosure;
    public int bay = 0;
    public byte[] enc_key_val = new byte[16];

    String rcErrMessage;

    public int dwidth;
    public int dheight;
    public boolean exit = false;
    public boolean fdSelected = false;
    public boolean cdSelected = false;
    public boolean in_enclosure = false;
    private int REMCONS_MAX_FN_KEYS = 12;
    private int REMCONS_MAX_KBD_LAYOUT = 17;

    public String getLocalString(int paramInt) {
        String str = "";
        try {
            str = this.locinfoObj.getLocString(paramInt);
        } catch (Exception exception) {

            System.out.println("remcons:getLocalString" + exception.getMessage());
        }
        return str;
    }

    public intgapp() {
        this.virtdevsObj = new virtdevs(this);
        this.remconsObj = new remcons(this);
        this.locinfoObj = new locinfo(this);
        this.jsonObj = new jsonparser(this);
    }

    public void init() {
        boolean bool = true;

        System.out.println("Started Retrieving parameters from ILO..");
        String str = this.jsonObj.getJSONRequest("rc_info");
        if (str != null) {

            ApplyRcInfoParameters(str);
            System.out.println("Completed Retrieving parameters from ILO");
        }
        bool = this.locinfoObj.initLocStrings();

        this.virtdevsObj.init();
        this.remconsObj.init();

        ui_init();

        if (null == str) {
            System.out.println("Failed to retrive parameters from ILO");
            new VErrorDialog(this.dispFrame, getLocalString(8212), this.rcErrMessage, true);
            this.dispFrame.setVisible(false);
        } else if (false == bool) {
            new VErrorDialog(this.dispFrame, getLocalString(8212), this.locinfoObj.rcErrMessage, true);
            this.dispFrame.setVisible(false);
        }
    }

    public void start() {
        try {
            this.virtdevsObj.start();
            this.remconsObj.start();

            this.dispFrame.getContentPane().add(this.scroller, "Center");
            this.dispFrame.getContentPane().add(this.dispStatusBar, "South");
            this.scroller.validate();
            this.dispStatusBar.validate();
            this.dispFrame.validate();

            System.out.println("Set Inital focus for session..");
            this.remconsObj.session.requestFocus();

            run();
        } catch (Exception exception) {

            System.out.println("FAILURE: exception starting applet");
            exception.printStackTrace();
        }
    }

    public void stop() {
        this.exit = true;
        this.virtdevsObj.stop();
        this.remconsObj.remconsUnInstallKeyboardHook();
        this.remconsObj.stop();
    }

    public void destroy() {
        System.out.println("Destroying subsustems");
        this.exit = true;
        this.remconsObj.remconsUnInstallKeyboardHook();
        this.virtdevsObj.destroy();
        this.remconsObj.destroy();
        this.dispFrame.dispose();
    }

    public synchronized void run() {
        byte b = 0;
        boolean bool = false;
        while (true) {
            try {
                b = 0;
                byte b1 = 0;
                this.ma = new MediaAccess();
                String[] arrayOfString = this.ma.devices();
                for (byte b2 = 0; arrayOfString != null && b2 < arrayOfString.length; b2++) {
                    int i = this.ma.devtype(arrayOfString[b2]);
                    if (i == 2 || i == 5) {
                        b1++;
                    }
                }

                if (b1 > this.vdmenuIndx - 4) {
                    ClassLoader classLoader = getClass().getClassLoader();
                    for (byte b3 = 0; arrayOfString != null && b3 < arrayOfString.length; b3++) {
                        bool = false;
                        int i = this.ma.devtype(arrayOfString[b3]);
                        for (byte b4 = 0; b4 < this.vdmenuIndx - 4; b4++) {
                            if (arrayOfString[b3].equals(this.vdMenu.getItem(b4).getText())) {
                                bool = true;
                                b++;
                            }
                        }
                        if (!bool) {
                            if (i == 2) {
                                System.out.println("Device attached: " + arrayOfString[b3]);
                                this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b3]);
                                this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + arrayOfString[b3]);
                                this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                                if (arrayOfString[b3].equals("A:") || arrayOfString[b3].equals("B:")) {
                                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(
                                            classLoader.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
                                } else {
                                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(
                                            getImage(classLoader.getResource("com/hp/ilo2/remcons/images/usb.png"))));
                                }
                                this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx], b);
                                this.vdMenu.updateUI();
                                this.vdmenuIndx++;
                                break;
                            }
                            if (i == 5) {
                                System.out.println("CDROM Hot plug device auto-update no supported at this time");
                            }
                        }

                    }
                } else if (b1 < this.vdmenuIndx - 4) {
                    for (byte b3 = 0; b3 < this.vdmenuIndx - 4; b3++) {
                        bool = false;
                        for (byte b4 = 0; arrayOfString != null && b4 < arrayOfString.length; b4++) {
                            int i = this.ma.devtype(arrayOfString[b4]);
                            if ((i == 2 || i == 5) && this.vdMenu.getItem(b3).getText().equals(arrayOfString[b4])) {
                                bool = true;
                            }
                        }

                        if (!bool) {
                            System.out.println("Device removed: " + this.vdMenu.getItem(b3).getText());
                            this.vdMenu.remove(b3);
                            this.vdMenu.updateUI();
                            this.vdmenuIndx--;
                            break;
                        }
                    }
                }
                this.ma = null;
                this.remconsObj.session.set_status(3, "");
                this.remconsObj.sleepAtLeast(5000L);
                if (this.exit) {
                    break;
                }
            } catch (InterruptedException interruptedException) {

                System.out.println("Exception on intgapp");
            }
        }
        System.out.println("Intgapp stopped running");
    }

    public void paintComponent(Graphics paramGraphics) {
        paintComponents(paramGraphics);
        paramGraphics.drawString("Remote Console JApplet Loaded", 10, 50);
    }

    public void ui_init() {
        String str1 = "";

        System.out.println("Message from ui_init55");
        this.dispFrame = new JFrame("JavaApplet IRC Window");
        this.dispFrame.getContentPane().setLayout(new BorderLayout());
        this.dispFrame.addWindowListener(new WindowCloser(this));
        this.mainMenuBar = new JMenuBar();
        this.dispStatusBar = new JPanel(new BorderLayout());
        this.dispStatusBar.add(((telnet) this.remconsObj.session).status_box, "West");
        this.dispStatusBar.add(this.remconsObj.pwrStatusPanel, "East");

        String str3 = this.jsonObj.getJSONRequest("session_info");
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        this.dispFrame.setJMenuBar(this.mainMenuBar);
        if (str3 != null) {

            makePsMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(str3, "reset_priv"));
            makeVdMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(str3, "virtual_media_priv"));
        }
        makeKbMenu(this.mainMenuBar);
        String str2 = this.jsonObj.getJSONRequest("login_session");

        if (str2 != null) {

            str1 = this.jsonObj.getJSONObject(str2, "alt");
            if (str1 == null || (str1 != null && this.jsonObj.getJSONNumber(str1, "mode") == 0)) {
                makeHlpMenu(this.mainMenuBar);
            }
        }

        this.scroller = new JScrollPane((Component) this.remconsObj.session, 20, 30);
        this.scroller.setVisible(true);

        try {
            String str = getLocalString(4132) + " " + this.server_name + " " + getLocalString(4133) + " "
                    + this.ilo_fqdn;

            if (this.blade == 1 && this.in_enclosure == true) {
                str = str + " " + getLocalString(4134) + " " + this.enclosure + " " + getLocalString(4135) + " "
                        + this.bay;
            }

            this.dispFrame.setTitle(str);
        } catch (Exception exception) {

            this.dispFrame.setTitle(getLocalString(4132) + " " + getCodeBase().getHost());
            System.out.println("IRC title not available");
        }
        int i = (Toolkit.getDefaultToolkit().getScreenSize()).width;
        int j = (Toolkit.getDefaultToolkit().getScreenSize()).height;

        boolean bool1 = (i < 1054) ? i : true;
        boolean bool2 = (j < 874) ? (j - 30) : true;
        boolean bool3 = (i > 1054) ? ((i - 1054) / 2) : false;
        boolean bool4 = (j > 874) ? ((j - 874) / 2) : false;

        this.dispFrame.setSize(bool1, bool2);
        this.dispFrame.setLocation(bool3, bool4);
        System.out.println("check dimensions " + bool1 + " " + bool2 + " " + bool3 + " " + bool4);
        this.dispFrame.setVisible(true);

        try {
            Insets insets = this.dispFrame.getInsets();
            ClassLoader classLoader = getClass().getClassLoader();
            if (str1 == null || (str1 != null && this.jsonObj.getJSONNumber(str1, "mode") == 0)) {
                this.dispFrame
                        .setIconImage(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/ilo_logo.png")));
            }
            Image image = this.dispFrame.getIconImage();
            if (image == null) {
                System.out.println("Dimage is null");
            }
        } catch (Exception exception) {

            System.out.println("JIRC icon not available");
        }
    }

    protected void makeHlpMenu(JMenuBar paramJMenuBar) {
        this.hlpMenu = new JMenu(getLocalString(4136));
        this.aboutJirc = new JMenuItem(getLocalString(4137));
        this.aboutJirc.addActionListener(this);
        this.hlpMenu.add(this.aboutJirc);
        paramJMenuBar.add(this.hlpMenu);
    }

    protected void makeVdMenu(JMenuBar paramJMenuBar, int paramInt) {
        this.vdMenu = new JMenu(getLocalString(4098));
        if (paramInt == 1) {
            paramJMenuBar.add(this.vdMenu);
        }
    }

    public void updateVdMenu() {
        this.ma = new MediaAccess();
        ClassLoader classLoader = getClass().getClassLoader();

        String str1 = this.jsonObj.getJSONRequest("vm_status");

        String str2 = this.jsonObj.getJSONArray(str1, "options", 0);

        String str3 = this.jsonObj.getJSONArray(str1, "options", 1);

        String[] arrayOfString = this.ma.devices();
        this.vdmenuIndx = 0;
        if (arrayOfString != null) {
            this.vdMenuItems = new JCheckBoxMenuItem[arrayOfString.length + 5];
            for (byte b = 0; b < arrayOfString.length; b++) {
                int i = this.ma.devtype(arrayOfString[b]);
                if (i == 5) {
                    this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b]);
                    this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + arrayOfString[b]);
                    this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                    this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(
                            getImage(classLoader.getResource("com/hp/ilo2/remcons/images/CD_Drive.png"))));
                    this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
                    this.vdmenuIndx++;
                } else if (i == 2) {
                    this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b]);
                    this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + arrayOfString[b]);
                    this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
                    if (arrayOfString[b].equals("A:") || arrayOfString[b].equals("B:")) {
                        this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(
                                getImage(classLoader.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
                    } else {
                        this.vdMenuItems[this.vdmenuIndx].setIcon(
                                new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/usb.png"))));
                    }
                    this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
                    this.vdmenuIndx++;
                }
            }
        } else {
            this.vdMenuItems = new JCheckBoxMenuItem[5];
            System.out.println("Media Access not available...");
        }
        this.ma = null;

        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4130) + " " + getLocalString(4106));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + getLocalString(12567));
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdMenuItems[this.vdmenuIndx]
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdmenuIndx++;

        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4131) + getLocalString(4106));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("FLOPPY");
        this.vdMenuItems[this.vdmenuIndx]
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Network.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdmenuIndx++;
        if (this.jsonObj.getJSONNumber(str2, "vm_url_connected") == 1
                && this.jsonObj.getJSONNumber(str2, "vm_connected") == 1) {
            this.fdSelected = true;
            lockFdMenu(false, "URL Removable Media");
        }

        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4130) + " " + getLocalString(4107));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + getLocalString(12567));
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdMenuItems[this.vdmenuIndx]
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdmenuIndx++;

        this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4131) + getLocalString(4107));
        this.vdMenuItems[this.vdmenuIndx].setActionCommand("CDROM");
        this.vdMenuItems[this.vdmenuIndx]
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Network.png"))));
        this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
        this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
        this.vdmenuIndx++;
        if (this.jsonObj.getJSONNumber(str3, "vm_url_connected") == 1
                && this.jsonObj.getJSONNumber(str3, "vm_connected") == 1) {
            this.cdSelected = true;
            lockCdMenu(false, "URL CD/DVD-ROM");
        }

        this.vdMenu.addSeparator();
        this.vdMenuItemCrImage = new JMenuItem(getLocalString(4109));
        this.vdMenuItemCrImage.setActionCommand("CreateDiskImage");
        this.vdMenuItemCrImage.addActionListener(this);
        this.vdMenu.add(this.vdMenuItemCrImage);
    }

    public void lockCdMenu(boolean paramBoolean, String paramString) {
        byte b = 0;

        for (b = 0; b < this.vdmenuIndx; b++) {
            this.vdMenu.getItem(b).removeItemListener(this);

            if (this.vdMenu.getItem(b).getActionCommand().startsWith("cd")
                    || this.vdMenu.getItem(b).getActionCommand().equals("CDROM")) {
                if (paramString.equals(this.vdMenu.getItem(b).getText())) {

                    this.vdMenu.getItem(b).setSelected(!paramBoolean);
                } else {

                    this.vdMenu.getItem(b).setSelected(false);
                    this.vdMenu.getItem(b).setEnabled(paramBoolean);
                }
            }
            this.vdMenu.getItem(b).addItemListener(this);
        }
    }

    public void lockFdMenu(boolean paramBoolean, String paramString) {
        byte b = 0;

        for (b = 0; b < this.vdmenuIndx; b++) {
            this.vdMenu.getItem(b).removeItemListener(this);

            if (this.vdMenu.getItem(b).getActionCommand().startsWith("fd")
                    || this.vdMenu.getItem(b).getActionCommand().equals("FLOPPY")) {
                if (paramString.equals(this.vdMenu.getItem(b).getText())) {

                    this.vdMenu.getItem(b).setSelected(!paramBoolean);
                } else {

                    this.vdMenu.getItem(b).setSelected(false);
                    this.vdMenu.getItem(b).setEnabled(paramBoolean);
                }
            }
            this.vdMenu.getItem(b).addItemListener(this);
        }
    }

    protected void makePsMenu(JMenuBar paramJMenuBar, int paramInt) {
        ClassLoader classLoader = getClass().getClassLoader();

        this.psMenu = new JMenu(getLocalString(4097));

        this.momPress = new JMenuItem(getLocalString(4100));
        this.momPress.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/press.png"))));
        this.momPress.setActionCommand("psMomPress");
        this.momPress.addActionListener(this);
        this.psMenu.add(this.momPress);

        this.pressHold = new JMenuItem(getLocalString(4101));
        this.pressHold.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/hold.png"))));
        this.pressHold.setActionCommand("psPressHold");
        this.pressHold.addActionListener(this);

        this.powerCycle = new JMenuItem(getLocalString(4102));
        this.powerCycle
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/coldboot.png"))));
        this.powerCycle.setActionCommand("psPowerCycle");
        this.powerCycle.addActionListener(this);

        this.sysReset = new JMenuItem(getLocalString(4103));
        this.sysReset.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/reset.png"))));
        this.sysReset.setActionCommand("psSysReset");
        this.sysReset.addActionListener(this);

        if (paramInt == 1) {
            paramJMenuBar.add(this.psMenu);
        }
    }

    public void updatePsMenu(int paramInt) {
        if (0 == paramInt) {
            this.psMenu.remove(this.pressHold);
            this.psMenu.remove(this.powerCycle);
            this.psMenu.remove(this.sysReset);
        } else {

            this.psMenu.remove(this.pressHold);
            this.psMenu.remove(this.powerCycle);
            this.psMenu.remove(this.sysReset);

            this.psMenu.add(this.pressHold);
            this.psMenu.add(this.powerCycle);
            this.psMenu.add(this.sysReset);
        }
    }

    protected void makeKbMenu(JMenuBar paramJMenuBar) {
        ClassLoader classLoader = getClass().getClassLoader();
        this.kbMenu = new JMenu(getLocalString(4099));
        this.kbCAFMenu = new JMenu("CTRL-ALT-Fn");
        this.kbAFMenu = new JMenu("ALT-Fn");
        this.kbLangMenu = new JMenu(getLocalString(4110));

        this.ctlAltDel = new JMenuItem(getLocalString(4104));
        this.ctlAltDel
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.ctlAltDel.setActionCommand("kbCtlAltDel");
        this.ctlAltDel.addActionListener(this);
        this.kbMenu.add(this.ctlAltDel);

        this.numLock = new JMenuItem(getLocalString(4105));
        this.numLock
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.numLock.setActionCommand("kbNumLock");
        this.numLock.addActionListener(this);
        this.kbMenu.add(this.numLock);

        this.capsLock = new JMenuItem(getLocalString(4128));
        this.capsLock
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.capsLock.setActionCommand("kbCapsLock");
        this.capsLock.addActionListener(this);
        this.kbMenu.add(this.capsLock);

        this.ctlAltBack = new JMenuItem("CTRL-ALT-BACKSPACE");
        this.ctlAltBack
                .setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
        this.ctlAltBack.setActionCommand("kbCtlAltBack");
        this.ctlAltBack.addActionListener(this);

        this.ctlAltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];
        byte b;
        for (b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
            this.ctlAltFn[b] = new JMenuItem("CTRL-ALT-F" + (b + 1));
            this.ctlAltFn[b].setActionCommand("kbCtrlAltFn" + b);

            this.ctlAltFn[b].addActionListener(this);
            this.kbCAFMenu.add(this.ctlAltFn[b]);
        }
        this.AltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];
        for (b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
            this.AltFn[b] = new JMenuItem("ALT-F" + (b + 1));
            this.AltFn[b].setActionCommand("kbAltFn" + b);
            this.AltFn[b].addActionListener(this);
            this.kbAFMenu.add(this.AltFn[b]);
        }

        this.localKbdLayout = new JCheckBoxMenuItem[this.REMCONS_MAX_KBD_LAYOUT];
        for (b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
            this.localKbdLayout[b] = new JCheckBoxMenuItem(getLocalString(4111 + b));
            this.localKbdLayout[b].setActionCommand("localKbdLayout" + b);
            this.localKbdLayout[b].addItemListener(this);
            this.kbLangMenu.add(this.localKbdLayout[b]);
        }
        this.localKbdLayout[0].setSelected(true);

        String str = System.getProperty("os.name").toLowerCase();
        if (!str.startsWith("windows")) {
            this.kbMenu.add(this.ctlAltBack);
            this.kbMenu.add(this.kbCAFMenu);
            this.kbMenu.add(this.kbAFMenu);
            this.kbMenu.add(this.kbLangMenu);
        }

        this.kbMenu.addSeparator();
        this.hotKeys = new JMenuItem(getLocalString(4129));
        this.hotKeys.addActionListener(this);
        this.kbMenu.add(this.hotKeys);

        paramJMenuBar.add(this.kbMenu);
    }

    public void actionPerformed(ActionEvent paramActionEvent) {
        if (paramActionEvent.getSource() == this.momPress) {
            this.remconsObj.session.sendMomPress();
        } else if (paramActionEvent.getSource() == this.pressHold) {
            this.remconsObj.session.sendPressHold();
        } else if (paramActionEvent.getSource() == this.powerCycle) {
            this.remconsObj.session.sendPowerCycle();
        } else if (paramActionEvent.getSource() == this.sysReset) {
            this.remconsObj.session.sendSystemReset();

        } else if (paramActionEvent.getSource() == this.ctlAltDel) {
            this.remconsObj.session.send_ctrl_alt_del();
        } else if (paramActionEvent.getSource() == this.numLock) {
            this.remconsObj.session.send_num_lock();
        } else if (paramActionEvent.getSource() == this.capsLock) {
            this.remconsObj.session.send_caps_lock();
        } else if (paramActionEvent.getSource() == this.ctlAltBack) {
            this.remconsObj.session.send_ctrl_alt_back();
        } else if (paramActionEvent.getSource() == this.hotKeys) {
            this.remconsObj.viewHotKeys();

        } else if (paramActionEvent.getSource() == this.vdMenuItemCrImage) {
            this.virtdevsObj.createImage();

        } else if (paramActionEvent.getSource() == this.aboutJirc) {
            this.remconsObj.viewAboutJirc();
        } else {

            for (byte b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
                if (paramActionEvent.getSource() == this.ctlAltFn[b]) {
                    this.remconsObj.session.send_ctrl_alt_fn(b);
                    break;
                }
                if (paramActionEvent.getSource() == this.AltFn[b]) {
                    this.remconsObj.session.send_alt_fn(b);
                    break;
                }
            }
            if (b >= this.REMCONS_MAX_FN_KEYS) {
                System.out.println("Unhandled ActionItem" + paramActionEvent.getActionCommand());
            }
        }
    }

    public void itemStateChanged(ItemEvent paramItemEvent) {
        boolean bool = false;
        JCheckBoxMenuItem jCheckBoxMenuItem = null;
        String str1 = null;
        String str2 = null;
        int i = paramItemEvent.getStateChange();

        byte b;
        for (b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
            if (this.localKbdLayout[b] == paramItemEvent.getSource() && i == 1) {
                System.out.println(b);
                this.localKbdLayout[b].setSelected(true);
                kbdLayoutMenuHandler(b);

                return;
            }
        }

        for (b = 0; b < this.vdmenuIndx; b++) {
            if (this.vdMenuItems[b] == paramItemEvent.getSource()) {
                jCheckBoxMenuItem = this.vdMenuItems[b];
                str1 = jCheckBoxMenuItem.getActionCommand();

                str2 = jCheckBoxMenuItem.getLabel();

                break;
            }
        }

        if (jCheckBoxMenuItem == null || str1 == null) {
            System.out.println("Unhandled item event");

            return;
        }

        if (str1.equals("fd" + getLocalString(12567))) {
            String str = null;
            if (i == 2) {
                bool = this.virtdevsObj.do_floppy(str2);
                lockFdMenu(true, str2);
            } else if (i == 1) {

                this.dispFrame.setVisible(false);
                VFileDialog vFileDialog = new VFileDialog(getLocalString(8261), "*.img");
                str = vFileDialog.getString();
                this.dispFrame.setVisible(true);

                if (str != null) {
                    if (this.virtdevsObj.fdThread != null)
                        this.virtdevsObj.change_disk(this.virtdevsObj.fdConnection, str);
                    System.out.println("Image file: " + str);
                    bool = this.virtdevsObj.do_floppy(str);
                    if (!bool) {

                        lockFdMenu(true, str2);
                    } else {
                        lockFdMenu(false, str2);
                    }
                } else {
                    lockFdMenu(true, str2);
                }
            }

            return;
        }

        if (str1.equals("cd" + getLocalString(12567))) {
            String str = null;
            if (i == 2) {
                bool = this.virtdevsObj.do_cdrom(str2);
                lockCdMenu(true, str2);
            } else if (i == 1) {

                this.dispFrame.setVisible(false);
                VFileDialog vFileDialog = new VFileDialog(getLocalString(8261), "*.iso");
                str = vFileDialog.getString();
                this.dispFrame.setVisible(true);

                if (str != null) {
                    if (this.virtdevsObj.cdThread != null)
                        this.virtdevsObj.change_disk(this.virtdevsObj.cdConnection, str);
                    System.out.println("Image file: " + str);
                    bool = this.virtdevsObj.do_cdrom(str);
                    if (!bool) {

                        lockCdMenu(true, str2);
                    } else {
                        lockCdMenu(false, str2);
                    }
                } else {
                    lockCdMenu(true, str2);
                }
            }

            return;
        }

        if (str1.startsWith("cd")) {
            bool = this.virtdevsObj.do_cdrom(str2);
            if (bool) {
                lockCdMenu((i != 1), str2);
            }

            return;
        }

        if (str1.startsWith("fd")) {
            bool = this.virtdevsObj.do_floppy(str2);
            if (bool) {
                lockFdMenu((i != 1), str2);
            }

            return;
        }

        if (str1.equals("FLOPPY") || str1.equals("CDROM")) {

            String str = "";
            boolean bool1 = false;

            if (i == 2) {
                String str3 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1
                        + "\", \"command\":\"EJECT\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";
                str = this.jsonObj.postJSONRequest("vm_status", str3);
                this.remconsObj.session.set_status(3, "Unmounted URL");
            } else if (i == 1) {
                this.remconsObj.setDialogIsOpen(true);
                URLDialog uRLDialog = new URLDialog(this.remconsObj);
                String str3 = uRLDialog.getUserInput();

                if (str3.compareTo("userhitcancel") == 0 || str3.compareTo("userhitclose") == 0) {
                    str3 = null;
                }

                if (str3 != null) {
                    str3 = str3.replaceAll("[\000-\037]", "");
                    System.out.println("url:  " + str3);
                }

                this.remconsObj.setDialogIsOpen(false);
                if (str3 != null) {
                    String str4 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1
                            + "\", \"command\":\"INSERT\", \"url\":\"" + str3 + "\", \"session_key\":\""
                            + getParameter("RCINFO1") + "\"}";

                    str = this.jsonObj.postJSONRequest("vm_status", str4);
                    if (str == "Success") {
                        str4 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1
                                + "\", \"boot_option\":\"CONNECT\", \"command\":\"SET\", \"url\":\"" + str3
                                + "\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";

                        str = this.jsonObj.postJSONRequest("vm_status", str4);
                    }

                    if (str == "SCSI_ERR_NO_LICENSE") {
                        String str5 = "<html>" + getLocalString(8213) + " " + getLocalString(8214) + " "
                                + getLocalString(8237) + "<br><br>" + getLocalString(8238) + "</html>";

                        new VErrorDialog(this.dispFrame, getLocalString(8236), str5, true);
                    } else if (str != "Success") {
                        new VErrorDialog(this.dispFrame, getLocalString(8212), getLocalString(8292), true);
                    } else {
                        bool1 = true;
                        this.remconsObj.session.set_status(3, getLocalString(12581));
                    }
                }
            }

            if (str1.equals("FLOPPY")) {
                lockFdMenu(!bool1, str2);
            } else if (str1.equals("CDROM")) {

                lockCdMenu(!bool1, str2);
            }
            return;
        }
    }

    public void kbdLayoutMenuHandler(int paramInt) {
        for (byte b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
            if (b != paramInt) {
                this.localKbdLayout[b].setSelected(false);
            }
        }

        this.remconsObj.setLocalKbdLayout(paramInt);
    }

    class WindowCloser extends WindowAdapter {
        private final intgapp this$0;

        WindowCloser(intgapp this$0) {
            this.this$0 = this$0;
        }

        public void windowClosing(WindowEvent param1WindowEvent) {
            this.this$0.stop();
            this.this$0.exit = true;
        }
    }

    private void ApplyRcInfoParameters(String paramString) {
        this.enc_key = this.rc_port = this.vm_key = this.vm_port = null;
        Arrays.fill(this.enc_key_val, (byte) 0);

        paramString = paramString.trim();
        paramString = paramString.substring(1, paramString.length() - 1);
        String[] arrayOfString = paramString.split(",");

        for (byte b = 0; b < arrayOfString.length; b++) {
            String[] arrayOfString1 = arrayOfString[b].split(":");
            if (arrayOfString1.length != 2) {
                System.out.println("Error in ApplyRcInfoParameters");

                return;
            }
            String str1 = arrayOfString1[0].trim();
            str1 = str1.substring(1, str1.length() - 1);

            String str2 = arrayOfString1[1].trim();
            if (str2.charAt(0) == '"') {
                str2 = str2.substring(1, str2.length() - 1);
            }

            if (str1.compareToIgnoreCase("enc_key") == 0) {

                this.enc_key = str2;
                for (byte b1 = 0; b1 < this.enc_key_val.length; b1++) {
                    String str = this.enc_key.substring(b1 * 2, b1 * 2 + 2);
                    try {
                        this.enc_key_val[b1] = (byte) Integer.parseInt(str, 16);
                    } catch (NumberFormatException numberFormatException) {

                        System.out.println("Failed to Parse enc_key");
                    }

                }

            } else if (str1.compareToIgnoreCase("rc_port") == 0) {
                System.out.println("rc_port:" + str2);
                this.rc_port = str2;
            } else if (str1.compareToIgnoreCase("vm_key") == 0) {

                this.vm_key = str2;
            } else if (str1.compareToIgnoreCase("vm_port") == 0) {
                System.out.println("vm_port:" + str2);
                this.vm_port = str2;
            } else if (str1.equalsIgnoreCase("optional_features")) {
                System.out.println("optional_features:" + str2);
                this.optional_features = str2;
            } else if (str1.compareToIgnoreCase("server_name") == 0) {
                System.out.println("server_name:" + str2);
                this.server_name = str2;
            } else if (str1.compareToIgnoreCase("ilo_fqdn") == 0) {
                System.out.println("ilo_fqdn:" + str2);
                this.ilo_fqdn = str2;
            } else if (str1.compareToIgnoreCase("blade") == 0) {
                this.blade = Integer.parseInt(str2);
                System.out.println("blade:" + this.blade);
            } else if (this.blade == 1 && str1.compareToIgnoreCase("enclosure") == 0) {
                if (!str2.equals("null")) {
                    this.in_enclosure = true;
                    System.out.println("enclosure:" + str2);
                    this.enclosure = str2;
                }

            } else if (this.blade == 1 && str1.compareToIgnoreCase("bay") == 0) {
                this.bay = Integer.parseInt(str2);
                System.out.println("bay:" + this.bay);
            }
        }
    }

    public void moveUItoInit(boolean paramBoolean) {
        System.out.println("Disable Menus\n");
        this.psMenu.setEnabled(paramBoolean);
        this.vdMenu.setEnabled(paramBoolean);
        this.kbMenu.setEnabled(paramBoolean);
    }

    public String rebrandToken(String paramString) {
        if (!this.moniker_cached) {

            String str1 = this.jsonObj.getJSONRequest("login_session");
            if (str1 == null) {
                return paramString;
            }
            this.moniker = this.jsonObj.getJSONObject(str1, "moniker");
            if (this.moniker == null) {
                return paramString;
            }
            this.moniker_cached = true;
        }
        String str = this.jsonObj.getJSONString(this.moniker, paramString);
        if (str == "") {
            return paramString;
        }
        return str;
    }
}
