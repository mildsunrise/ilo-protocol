/*     */ package com.hp.ilo2.intgapp;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
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
/*     */ public class locinfo
/*     */ {
/*     */   private DocumentBuilderFactory dbf;
/*     */   private DocumentBuilder db;
/*     */   private Document document;
/*     */   private File file;
/*     */   public static int UID;
/*     */   private intgapp ParentApp;
/*     */   private String localLocStrFile;
/*  46 */   private String lstrVersion = "0001";
/*     */   
/*  48 */   public String rcErrMessage = "";
/*     */   
/*     */   public static final int MENUSTR_1001 = 4097;
/*     */   
/*     */   public static final int MENUSTR_1002 = 4098;
/*     */   
/*     */   public static final int MENUSTR_1003 = 4099;
/*     */   
/*     */   public static final int MENUSTR_1004 = 4100;
/*     */   
/*     */   public static final int MENUSTR_1005 = 4101;
/*     */   
/*     */   public static final int MENUSTR_1006 = 4102;
/*     */   
/*     */   public static final int MENUSTR_1007 = 4103;
/*     */   
/*     */   public static final int MENUSTR_1008 = 4104;
/*     */   
/*     */   public static final int MENUSTR_1009 = 4105;
/*     */   
/*     */   public static final int MENUSTR_100A = 4106;
/*     */   
/*     */   public static final int MENUSTR_100B = 4107;
/*     */   
/*     */   public static final int MENUSTR_100C = 4108;
/*     */   
/*     */   public static final int MENUSTR_100D = 4109;
/*     */   
/*     */   public static final int MENUSTR_100E = 4110;
/*     */   public static final int MENUSTR_100F = 4111;
/*     */   public static final int MENUSTR_1010 = 4112;
/*     */   public static final int MENUSTR_1011 = 4113;
/*     */   public static final int MENUSTR_1012 = 4114;
/*     */   public static final int MENUSTR_1013 = 4115;
/*     */   public static final int MENUSTR_1014 = 4116;
/*     */   public static final int MENUSTR_1015 = 4117;
/*     */   public static final int MENUSTR_1016 = 4118;
/*     */   public static final int MENUSTR_1017 = 4119;
/*     */   public static final int MENUSTR_1018 = 4120;
/*     */   public static final int MENUSTR_1019 = 4121;
/*     */   public static final int MENUSTR_101A = 4122;
/*     */   public static final int MENUSTR_101B = 4123;
/*     */   public static final int MENUSTR_101C = 4124;
/*     */   public static final int MENUSTR_101D = 4125;
/*     */   public static final int MENUSTR_101E = 4126;
/*     */   public static final int MENUSTR_101F = 4127;
/*     */   public static final int MENUSTR_1020 = 4128;
/*     */   public static final int MENUSTR_1021 = 4129;
/*     */   public static final int MENUSTR_1022 = 4130;
/*     */   public static final int MENUSTR_1023 = 4131;
/*     */   public static final int MENUSTR_1024 = 4132;
/*     */   public static final int MENUSTR_1025 = 4133;
/*     */   public static final int MENUSTR_1026 = 4134;
/*     */   public static final int MENUSTR_1027 = 4135;
/*     */   public static final int MENUSTR_1028 = 4136;
/*     */   public static final int MENUSTR_1029 = 4137;
/*     */   public static final int DIALOGSTR_2001 = 8193;
/*     */   public static final int DIALOGSTR_2002 = 8194;
/*     */   public static final int DIALOGSTR_2003 = 8195;
/*     */   public static final int DIALOGSTR_2004 = 8196;
/*     */   public static final int DIALOGSTR_2005 = 8197;
/*     */   public static final int DIALOGSTR_2006 = 8198;
/*     */   public static final int DIALOGSTR_2007 = 8199;
/*     */   public static final int DIALOGSTR_2008 = 8200;
/*     */   public static final int DIALOGSTR_2009 = 8201;
/*     */   public static final int DIALOGSTR_200a = 8202;
/*     */   public static final int DIALOGSTR_200b = 8203;
/*     */   public static final int DIALOGSTR_200c = 8204;
/*     */   public static final int DIALOGSTR_200d = 8205;
/*     */   public static final int DIALOGSTR_200e = 8206;
/*     */   public static final int DIALOGSTR_200f = 8207;
/*     */   public static final int DIALOGSTR_2010 = 8208;
/*     */   public static final int DIALOGSTR_2011 = 8209;
/*     */   public static final int DIALOGSTR_2012 = 8210;
/*     */   public static final int DIALOGSTR_2013 = 8211;
/*     */   public static final int DIALOGSTR_2014 = 8212;
/*     */   public static final int DIALOGSTR_2015 = 8213;
/*     */   public static final int DIALOGSTR_2016 = 8214;
/*     */   public static final int DIALOGSTR_2017 = 8215;
/*     */   public static final int DIALOGSTR_2021 = 8225;
/*     */   public static final int DIALOGSTR_2022 = 8226;
/*     */   public static final int DIALOGSTR_2023 = 8227;
/*     */   public static final int DIALOGSTR_2024 = 8228;
/*     */   public static final int DIALOGSTR_2025 = 8229;
/*     */   public static final int DIALOGSTR_2026 = 8230;
/*     */   public static final int DIALOGSTR_2027 = 8231;
/*     */   public static final int DIALOGSTR_2028 = 8232;
/*     */   public static final int DIALOGSTR_2029 = 8233;
/*     */   public static final int DIALOGSTR_202a = 8234;
/*     */   public static final int DIALOGSTR_202b = 8235;
/*     */   public static final int DIALOGSTR_202c = 8236;
/*     */   public static final int DIALOGSTR_202d = 8237;
/*     */   public static final int DIALOGSTR_202e = 8238;
/*     */   public static final int DIALOGSTR_202f = 8239;
/*     */   public static final int DIALOGSTR_2030 = 8240;
/*     */   public static final int DIALOGSTR_2031 = 8241;
/*     */   public static final int DIALOGSTR_2032 = 8242;
/*     */   public static final int DIALOGSTR_2033 = 8243;
/*     */   public static final int DIALOGSTR_2034 = 8244;
/*     */   public static final int DIALOGSTR_2035 = 8245;
/*     */   public static final int DIALOGSTR_2036 = 8246;
/*     */   public static final int DIALOGSTR_2037 = 8247;
/*     */   public static final int DIALOGSTR_2038 = 8248;
/*     */   public static final int DIALOGSTR_2039 = 8249;
/*     */   public static final int DIALOGSTR_203a = 8250;
/*     */   public static final int DIALOGSTR_203b = 8251;
/*     */   public static final int DIALOGSTR_203c = 8252;
/*     */   public static final int DIALOGSTR_203d = 8253;
/*     */   public static final int DIALOGSTR_203e = 8254;
/*     */   public static final int DIALOGSTR_203f = 8255;
/*     */   public static final int DIALOGSTR_2040 = 8256;
/*     */   public static final int DIALOGSTR_2041 = 8257;
/*     */   public static final int DIALOGSTR_2042 = 8258;
/*     */   public static final int DIALOGSTR_2043 = 8259;
/*     */   public static final int DIALOGSTR_2044 = 8260;
/*     */   public static final int DIALOGSTR_2045 = 8261;
/*     */   public static final int DIALOGSTR_2046 = 8262;
/*     */   public static final int DIALOGSTR_2047 = 8263;
/*     */   public static final int DIALOGSTR_2048 = 8264;
/*     */   public static final int DIALOGSTR_2049 = 8265;
/*     */   public static final int DIALOGSTR_205a = 8282;
/*     */   public static final int DIALOGSTR_205b = 8283;
/*     */   public static final int DIALOGSTR_205c = 8284;
/*     */   public static final int DIALOGSTR_205d = 8285;
/*     */   public static final int DIALOGSTR_205e = 8286;
/*     */   public static final int DIALOGSTR_205f = 8287;
/*     */   public static final int DIALOGSTR_2060 = 8288;
/*     */   public static final int DIALOGSTR_2061 = 8289;
/*     */   public static final int DIALOGSTR_2062 = 8290;
/*     */   public static final int DIALOGSTR_2063 = 8291;
/*     */   public static final int DIALOGSTR_2064 = 8292;
/*     */   public static final int DIALOGSTR_2065 = 8293;
/*     */   public static final int DIALOGSTR_2066 = 8294;
/*     */   public static final int DIALOGSTR_2067 = 8295;
/*     */   public static final int STATUSSTR_3001 = 12289;
/*     */   public static final int STATUSSTR_3002 = 12290;
/*     */   public static final int STATUSSTR_3003 = 12291;
/*     */   public static final int STATUSSTR_3004 = 12292;
/*     */   public static final int STATUSSTR_3005 = 12293;
/*     */   public static final int STATUSSTR_3006 = 12294;
/*     */   public static final int STATUSSTR_3007 = 12295;
/*     */   public static final int STATUSSTR_3008 = 12296;
/*     */   public static final int STATUSSTR_3009 = 12297;
/*     */   public static final int STATUSSTR_300a = 12298;
/*     */   public static final int STATUSSTR_300b = 12299;
/*     */   public static final int STATUSSTR_300c = 12300;
/*     */   public static final int STATUSSTR_300d = 12301;
/*     */   public static final int STATUSSTR_300e = 12302;
/*     */   public static final int STATUSSTR_300f = 12303;
/*     */   public static final int STATUSSTR_3010 = 12304;
/*     */   public static final int STATUSSTR_3011 = 12305;
/*     */   public static final int STATUSSTR_3012 = 12306;
/*     */   public static final int STATUSSTR_3013 = 12307;
/*     */   public static final int STATUSSTR_3014 = 12308;
/*     */   public static final int STATUSSTR_3100 = 12544;
/*     */   public static final int STATUSSTR_3101 = 12545;
/*     */   public static final int STATUSSTR_3102 = 12546;
/*     */   public static final int STATUSSTR_3103 = 12547;
/*     */   public static final int STATUSSTR_3104 = 12548;
/*     */   public static final int STATUSSTR_3105 = 12549;
/*     */   public static final int STATUSSTR_3106 = 12550;
/*     */   public static final int STATUSSTR_3107 = 12551;
/*     */   public static final int STATUSSTR_3108 = 12552;
/*     */   public static final int STATUSSTR_3109 = 12553;
/*     */   public static final int STATUSSTR_310a = 12554;
/*     */   public static final int STATUSSTR_310b = 12555;
/*     */   public static final int STATUSSTR_310c = 12556;
/*     */   public static final int STATUSSTR_310d = 12557;
/*     */   public static final int STATUSSTR_310e = 12558;
/*     */   public static final int STATUSSTR_310f = 12559;
/*     */   public static final int STATUSSTR_3110 = 12560;
/*     */   public static final int STATUSSTR_3111 = 12561;
/*     */   public static final int STATUSSTR_3112 = 12562;
/*     */   public static final int STATUSSTR_3113 = 12563;
/*     */   public static final int STATUSSTR_3114 = 12564;
/*     */   public static final int STATUSSTR_3115 = 12565;
/*     */   public static final int STATUSSTR_3116 = 12566;
/*     */   public static final int STATUSSTR_3117 = 12567;
/*     */   public static final int STATUSSTR_3118 = 12568;
/*     */   public static final int STATUSSTR_3119 = 12569;
/*     */   public static final int STATUSSTR_3120 = 12576;
/*     */   public static final int STATUSSTR_3121 = 12577;
/*     */   public static final int STATUSSTR_3122 = 12578;
/*     */   public static final int STATUSSTR_3123 = 12579;
/*     */   public static final int STATUSSTR_3124 = 12580;
/*     */   public static final int STATUSSTR_3125 = 12581;
/*     */   public static final int STATUSSTR_3126 = 12582;
/*     */   public static final int TOOLSTR_4001 = 16385;
/*     */   public static final int TOOLSTR_4002 = 16386;
/*     */   public static final int TOOLSTR_4003 = 16387;
/*     */   public static final int TOOLSTR_4004 = 16388;
/*     */   
/*     */   public locinfo(intgapp paramintgapp) {
/* 241 */     this.ParentApp = paramintgapp;
/* 242 */     this.dbf = null;
/* 243 */     this.db = null;
/* 244 */     this.document = null;
/* 245 */     this.file = null;
/* 246 */     this.lstrVersion = "0001";
/* 247 */     this.rcErrMessage = "";
/* 248 */     this.localLocStrFile = "";
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
/*     */   
/*     */   public boolean retrieveLocStrings(boolean paramBoolean) {
/* 265 */     HttpURLConnection httpURLConnection = null;
/* 266 */     String str1 = null;
/* 267 */     String str2 = null;
/* 268 */     Object object = null;
/* 269 */     String str3 = null;
/* 270 */     URL uRL = null;
/* 271 */     int i = 0;
/*     */ 
/*     */     
/* 274 */     String str4 = System.getProperty("java.io.tmpdir");
/* 275 */     String str5 = System.getProperty("os.name").toLowerCase();
/* 276 */     String str6 = System.getProperty("file.separator");
/* 277 */     boolean bool = false;
/* 278 */     String str7 = "com/hp/ilo2/intgapp/";
/* 279 */     String str8 = "jirc_strings";
/* 280 */     String str9 = ".xml";
/* 281 */     String str10 = this.ParentApp.getParameter("RCINFOLANG");
/* 282 */     String str11 = null;
/* 283 */     if (UID == 0)
/* 284 */       UID = hashCode(); 
/* 285 */     String str12 = Integer.toHexString(UID);
/*     */ 
/*     */     
/* 288 */     if (null != str10 && !str10.equalsIgnoreCase("")) {
/* 289 */       System.out.println("langStr received:" + str10);
/* 290 */       str11 = "lang/" + str10 + "/jirc_strings.xml";
/* 291 */       System.out.println("lolcalized xml file shoudl be:" + str11);
/*     */     } else {
/* 293 */       paramBoolean = false;
/*     */     } 
/*     */ 
/*     */     
/* 297 */     if (str4 == null) {
/* 298 */       str4 = str5.startsWith("windows") ? "C:\\TEMP" : "/tmp";
/*     */     }
/*     */     
/* 301 */     File file1 = new File(str4);
/* 302 */     if (!file1.exists()) {
/* 303 */       file1.mkdir();
/*     */     }
/* 305 */     if (!str4.endsWith(str6)) {
/* 306 */       str4 = str4 + str6;
/*     */     }
/* 308 */     str4 = str4 + str8 + str12 + str9;
/* 309 */     this.localLocStrFile = str4;
/* 310 */     File file2 = new File(str4);
/* 311 */     if (file2.exists()) {
/* 312 */       System.out.println(this.localLocStrFile + " already exists.");
/* 313 */       bool = true;
/* 314 */       return bool;
/*     */     } 
/* 316 */     byte[] arrayOfByte = new byte[4096];
/* 317 */     System.out.println("Creating" + this.localLocStrFile + "...");
/*     */ 
/*     */     
/* 320 */     if (null != str11 && true == paramBoolean) {
/*     */       try {
/* 322 */         System.out.println("try localize file from webserver..");
/* 323 */         str1 = this.ParentApp.getCodeBase().getHost();
/* 324 */         i = this.ParentApp.getCodeBase().getPort();
/*     */ 
/*     */         
/* 327 */         if (i >= 0) {
/* 328 */           str2 = ":" + Integer.toString(i);
/*     */         } else {
/*     */           
/* 331 */           str2 = "";
/*     */         } 
/* 333 */         str3 = "http://" + str1 + str2 + "/" + str11;
/* 334 */         System.out.println("trying to retreive webser localize file:" + str3);
/*     */ 
/*     */         
/* 337 */         uRL = new URL(str3);
/* 338 */         httpURLConnection = null;
/* 339 */         httpURLConnection = (HttpURLConnection)uRL.openConnection();
/* 340 */         httpURLConnection.setRequestMethod("GET");
/* 341 */         httpURLConnection.setDoOutput(true);
/* 342 */         httpURLConnection.setUseCaches(false);
/* 343 */         httpURLConnection.connect();
/*     */         
/* 345 */         InputStream inputStream = httpURLConnection.getInputStream();
/* 346 */         FileOutputStream fileOutputStream = new FileOutputStream(this.localLocStrFile); int j;
/* 347 */         while ((j = inputStream.read(arrayOfByte, 0, 4096)) != -1)
/* 348 */           fileOutputStream.write(arrayOfByte, 0, j); 
/* 349 */         System.out.println("Write xml to" + this.localLocStrFile + "complete");
/* 350 */         inputStream.close();
/* 351 */         fileOutputStream.close();
/* 352 */         bool = true;
/* 353 */         System.out.println("Message after comp of webserver retrieval");
/*     */       
/*     */       }
/* 356 */       catch (Exception exception) {
/*     */         
/* 358 */         String str = System.getProperty("line.separator");
/* 359 */         this.rcErrMessage = exception.getMessage() + "." + str + str + "Your browser session may have timed out.";
/* 360 */         exception.printStackTrace();
/*     */       }
/*     */       finally {
/*     */         
/* 364 */         httpURLConnection.disconnect();
/* 365 */         httpURLConnection = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 372 */     if (!bool || !paramBoolean) {
/* 373 */       System.out.println("try localize file from applet..");
/* 374 */       ClassLoader classLoader = getClass().getClassLoader();
/* 375 */       String str = str7 + str8 + str9;
/*     */       try {
/* 377 */         InputStream inputStream = classLoader.getResourceAsStream(str);
/* 378 */         FileOutputStream fileOutputStream = new FileOutputStream(this.localLocStrFile);
/*     */         int j;
/* 380 */         while ((j = inputStream.read(arrayOfByte, 0, 4096)) != -1) {
/* 381 */           fileOutputStream.write(arrayOfByte, 0, j);
/*     */         }
/*     */         
/* 384 */         inputStream.close();
/* 385 */         fileOutputStream.close();
/* 386 */         bool = true;
/* 387 */         System.out.println("Message after default xml initialization");
/*     */       } catch (IOException iOException) {
/*     */         
/* 390 */         System.out.println("xmlExtract: " + iOException);
/* 391 */         this.rcErrMessage = iOException.getMessage();
/* 392 */         iOException.printStackTrace();
/*     */       } 
/*     */     } 
/*     */     
/* 396 */     return bool;
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
/*     */   public boolean initLocStringsDefault() {
/* 410 */     boolean bool = false;
/* 411 */     byte b = 0;
/*     */ 
/*     */ 
/*     */     
/* 415 */     try { System.out.println("Message from beginning of initLocStringsDefault" + this.localLocStrFile);
/*     */       
/* 417 */       bool = retrieveLocStrings(false);
/* 418 */       if (false == bool) {
/* 419 */         b = 2;
/*     */       } else {
/*     */         
/* 422 */         this.file = new File(this.localLocStrFile);
/* 423 */         if (null == this.file) {
/* 424 */           b = 3;
/*     */         } else {
/*     */           
/* 427 */           this.dbf = DocumentBuilderFactory.newInstance();
/* 428 */           if (null == this.dbf) {
/* 429 */             b = 4;
/*     */           } else {
/*     */             
/* 432 */             this.db = this.dbf.newDocumentBuilder();
/* 433 */             if (null == this.db) {
/* 434 */               b = 5;
/*     */             } else {
/*     */               
/* 437 */               this.document = this.db.parse(this.file);
/* 438 */               if (null == this.document)
/* 439 */               { b = 6; }
/*     */               else
/*     */               
/* 442 */               { this.document.getDocumentElement().normalize();
/* 443 */                 bool = true;
/* 444 */                 System.out.println("Message after completion of initLocStringsDefault"); } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }  }
/* 449 */     catch (Exception exception) { String str = System.getProperty("line.separator");
/* 450 */       this.rcErrMessage = exception.getMessage() + "." + str + str + "Could not Parse the localization strings.";
/* 451 */       exception.printStackTrace(); }
/*     */     
/* 453 */     if (false == bool) {
/* 454 */       System.out.println("initLocStringsDefault:Error Parsing Xml file:%d" + b);
/*     */     }
/* 456 */     return bool;
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
/*     */   public boolean initLocStrings() {
/* 469 */     boolean bool = false;
/* 470 */     byte b = 0;
/*     */ 
/*     */ 
/*     */     
/* 474 */     try { System.out.println("Message from beginning of initLocStrings" + this.localLocStrFile);
/* 475 */       if (null != this.document) {
/* 476 */         b = 1;
/*     */       } else {
/*     */         
/* 479 */         bool = retrieveLocStrings(true);
/* 480 */         if (false == bool) {
/* 481 */           b = 2;
/*     */         } else {
/*     */           
/* 484 */           this.file = new File(this.localLocStrFile);
/* 485 */           if (null == this.file) {
/* 486 */             b = 3;
/*     */           } else {
/*     */             
/* 489 */             this.dbf = DocumentBuilderFactory.newInstance();
/* 490 */             if (null == this.dbf) {
/* 491 */               b = 4;
/*     */             } else {
/*     */               
/* 494 */               this.db = this.dbf.newDocumentBuilder();
/* 495 */               if (null == this.db)
/* 496 */               { b = 5; }
/*     */               else
/*     */               
/* 499 */               { this.document = this.db.parse(this.file);
/* 500 */                 if (null == this.document)
/* 501 */                 { b = 6; }
/*     */                 else
/*     */                 
/* 504 */                 { this.document.getDocumentElement().normalize();
/* 505 */                   bool = true;
/* 506 */                   System.out.println("Message after completion of initLocStrings"); }  } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }  }
/* 511 */     catch (Exception exception) { String str = System.getProperty("line.separator");
/* 512 */       this.rcErrMessage = exception.getMessage() + "." + str + str + "Could not Parse the localization strings.";
/* 513 */       exception.printStackTrace(); }
/*     */     
/* 515 */     if (false == bool)
/*     */     {
/*     */       
/* 518 */       bool = initLocStringsDefault();
/*     */     }
/* 520 */     if (false == bool) {
/* 521 */       System.out.println("Error Parsing Xml file:%d" + b);
/*     */     }
/* 523 */     return bool;
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
/*     */   public String getLocString(int paramInt) {
/* 536 */     boolean bool = false;
/* 537 */     String str1 = "ID_" + Integer.toHexString(paramInt);
/* 538 */     String str2 = "";
/* 539 */     String str3 = "";
/* 540 */     String str4 = "";
/* 541 */     byte b = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 547 */       if (null == this.document) {
/* 548 */         b = 1;
/*     */       } else {
/*     */         
/* 551 */         Element element = this.document.getElementById(str1);
/* 552 */         if (null == element) {
/* 553 */           b = 2;
/*     */         } else {
/*     */           
/* 556 */           NodeList nodeList = element.getChildNodes();
/* 557 */           if (null == nodeList) {
/* 558 */             b = 3;
/*     */           } else {
/*     */             
/* 561 */             str2 = nodeList.item(0).getNodeValue();
/* 562 */             if (null == str2) {
/* 563 */               b = 4;
/*     */             }
/*     */             else {
/*     */               
/* 567 */               bool = true;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } catch (Exception exception) {
/* 573 */       exception.printStackTrace();
/*     */     } 
/* 575 */     if (false == bool) {
/* 576 */       str2 = "LS_NF";
/* 577 */       System.out.println("LSFNound:" + str1 + "rval:" + b);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 583 */     int i = str2.indexOf('#');
/* 584 */     if (i >= 0) {
/*     */ 
/*     */       
/* 587 */       str3 = str2.substring(0, i);
/*     */       
/* 589 */       int j = str2.indexOf('#', i + 1);
/* 590 */       str4 = str2.substring(i + 1, j);
/* 591 */       str3 = str3 + this.ParentApp.rebrandToken(str4);
/*     */       
/* 593 */       str3 = str3 + str2.substring(j + 1);
/* 594 */       return str3;
/*     */     } 
/*     */     
/* 597 */     return str2;
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
/*     */   public void dumpLocStrings() {
/*     */     try {
/* 612 */       NodeList nodeList = this.document.getElementsByTagName("javaIRC");
/*     */       
/* 614 */       for (byte b = 0; b < nodeList.getLength(); b++) {
/* 615 */         Node node = nodeList.item(b);
/* 616 */         if (node.getNodeType() == 1) {
/* 617 */           Element element = (Element)node;
/*     */           
/* 619 */           NodeList nodeList1 = element.getElementsByTagName("menu");
/*     */           
/* 621 */           for (byte b1 = 0; b1 < nodeList1.getLength(); b1++) {
/* 622 */             Element element1 = (Element)nodeList1.item(b1);
/* 623 */             NodeList nodeList5 = element1.getChildNodes();
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 629 */           NodeList nodeList2 = element.getElementsByTagName("dialog");
/*     */           
/* 631 */           for (byte b2 = 0; b2 < nodeList2.getLength(); b2++) {
/* 632 */             Element element1 = (Element)nodeList2.item(b2);
/* 633 */             NodeList nodeList5 = element1.getChildNodes();
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 639 */           NodeList nodeList3 = element.getElementsByTagName("status");
/*     */           
/* 641 */           for (byte b3 = 0; b3 < nodeList3.getLength(); b3++) {
/* 642 */             Element element1 = (Element)nodeList3.item(b3);
/* 643 */             NodeList nodeList5 = element1.getChildNodes();
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 649 */           NodeList nodeList4 = element.getElementsByTagName("tooltip");
/*     */           
/* 651 */           for (byte b4 = 0; b4 < nodeList4.getLength(); b4++) {
/* 652 */             Element element1 = (Element)nodeList4.item(b4);
/* 653 */             NodeList nodeList5 = element1.getChildNodes();
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     catch (Exception exception) {
/*     */       
/* 663 */       exception.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/intgapp/locinfo.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */