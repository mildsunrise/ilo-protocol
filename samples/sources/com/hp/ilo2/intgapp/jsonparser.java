/*     */ package com.hp.ilo2.intgapp;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
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
/*     */ public class jsonparser
/*     */ {
/*     */   private intgapp ParentApp;
/*     */   
/*     */   public jsonparser(intgapp paramintgapp) {
/*  33 */     this.ParentApp = paramintgapp;
/*     */   }
/*     */ 
/*     */   
/*     */   public String postJSONRequest(String paramString1, String paramString2) {
/*  38 */     HttpURLConnection httpURLConnection = null;
/*  39 */     OutputStreamWriter outputStreamWriter = null;
/*  40 */     BufferedReader bufferedReader = null;
/*  41 */     StringBuffer stringBuffer = null;
/*  42 */     String str1 = null;
/*  43 */     String str2 = null;
/*  44 */     String str3 = null;
/*  45 */     String str4 = null;
/*  46 */     String str5 = null;
/*  47 */     URL uRL = null;
/*  48 */     String str6 = null;
/*  49 */     int i = 0;
/*  50 */     int j = 0;
/*     */     
/*     */     try {
/*  53 */       System.out.println("Making JSON POST Request: " + paramString1);
/*  54 */       System.out.println("json data: " + paramString2);
/*  55 */       str2 = this.ParentApp.getCodeBase().getHost();
/*  56 */       i = this.ParentApp.getCodeBase().getPort();
/*  57 */       System.out.println("chk getPort: " + i);
/*  58 */       if (i >= 0) {
/*  59 */         str3 = ":" + Integer.toString(i);
/*     */       } else {
/*     */         
/*  62 */         str3 = "";
/*     */       } 
/*  64 */       str5 = "https://" + str2 + str3 + "/json/" + paramString1;
/*  65 */       str4 = this.ParentApp.getParameter("RCINFO1");
/*     */ 
/*     */       
/*  68 */       uRL = new URL(str5);
/*  69 */       httpURLConnection = null;
/*  70 */       httpURLConnection = (HttpURLConnection)uRL.openConnection();
/*  71 */       httpURLConnection.setRequestMethod("POST");
/*  72 */       httpURLConnection.setDoOutput(true);
/*  73 */       httpURLConnection.setDoInput(true);
/*  74 */       httpURLConnection.setUseCaches(false);
/*     */ 
/*     */       
/*  77 */       String str = "sessionKey=" + str4;
/*  78 */       httpURLConnection.setRequestProperty("Cookie", str);
/*     */       
/*  80 */       httpURLConnection.connect();
/*     */ 
/*     */       
/*  83 */       outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
/*  84 */       outputStreamWriter.write(paramString2, 0, (paramString2.getBytes()).length);
/*  85 */       outputStreamWriter.flush();
/*  86 */       outputStreamWriter.close();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  91 */       j = httpURLConnection.getResponseCode();
/*  92 */       System.out.println("connect.response code =  " + j);
/*     */       
/*  94 */       if (j == 200) {
/*  95 */         bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
/*  96 */         str6 = "Success";
/*     */       } else {
/*     */         
/*  99 */         bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
/*     */       } 
/*     */       
/* 102 */       stringBuffer = new StringBuffer();
/*     */       
/* 104 */       while ((str1 = bufferedReader.readLine()) != null) {
/* 105 */         stringBuffer.append(str1 + '\n');
/*     */       }
/*     */       
/* 108 */       System.out.println("Response Message = " + stringBuffer.toString());
/*     */       
/* 110 */       if (str6 != "Success")
/*     */       {
/* 112 */         int k = 0;
/* 113 */         k = stringBuffer.toString().indexOf("SCSI_ERR_NO_LICENSE");
/* 114 */         if (k != -1) {
/*     */           
/* 116 */           str6 = "SCSI_ERR_NO_LICENSE";
/*     */         } else {
/*     */           
/* 119 */           str6 = stringBuffer.toString();
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 127 */     catch (Exception exception) {
/*     */       
/* 129 */       String str = System.getProperty("line.separator");
/* 130 */       this.ParentApp.rcErrMessage = exception.getMessage() + "." + str + str + "Your browser session may have timed out.";
/* 131 */       exception.printStackTrace();
/*     */     }
/*     */     finally {
/*     */       
/* 135 */       httpURLConnection.disconnect();
/* 136 */       bufferedReader = null;
/* 137 */       stringBuffer = null;
/* 138 */       outputStreamWriter = null;
/* 139 */       httpURLConnection = null;
/*     */     } 
/* 141 */     return str6;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getJSONRequest(String paramString) {
/* 146 */     HttpURLConnection httpURLConnection = null;
/* 147 */     Object object = null;
/* 148 */     BufferedReader bufferedReader = null;
/* 149 */     StringBuffer stringBuffer = null;
/* 150 */     String str1 = null;
/* 151 */     String str2 = null;
/* 152 */     String str3 = null;
/* 153 */     String str4 = null;
/* 154 */     String str5 = null;
/* 155 */     URL uRL = null;
/* 156 */     String str6 = null;
/* 157 */     int i = 0;
/*     */ 
/*     */     
/*     */     try {
/* 161 */       str2 = this.ParentApp.getCodeBase().getHost();
/* 162 */       i = this.ParentApp.getCodeBase().getPort();
/* 163 */       System.out.println("chk getPort: " + i);
/* 164 */       if (i >= 0) {
/* 165 */         str3 = ":" + Integer.toString(i);
/*     */       } else {
/*     */         
/* 168 */         str3 = "";
/*     */       } 
/* 170 */       str5 = "https://" + str2 + str3 + "/json/" + paramString;
/* 171 */       str4 = this.ParentApp.getParameter("RCINFO1");
/*     */ 
/*     */       
/* 174 */       uRL = new URL(str5);
/* 175 */       httpURLConnection = null;
/* 176 */       httpURLConnection = (HttpURLConnection)uRL.openConnection();
/* 177 */       httpURLConnection.setRequestMethod("GET");
/* 178 */       httpURLConnection.setDoOutput(true);
/* 179 */       httpURLConnection.setUseCaches(false);
/*     */ 
/*     */       
/* 182 */       String str = "sessionKey=" + str4;
/* 183 */       httpURLConnection.setRequestProperty("Cookie", str);
/*     */       
/* 185 */       httpURLConnection.connect();
/*     */ 
/*     */       
/* 188 */       bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
/* 189 */       stringBuffer = new StringBuffer();
/*     */       
/* 191 */       while ((str1 = bufferedReader.readLine()) != null) {
/* 192 */         stringBuffer.append(str1 + '\n');
/*     */       }
/* 194 */       str6 = stringBuffer.toString();
/*     */ 
/*     */     
/*     */     }
/* 198 */     catch (Exception exception) {
/*     */       
/* 200 */       String str = System.getProperty("line.separator");
/* 201 */       this.ParentApp.rcErrMessage = exception.getMessage() + "." + str + str + "Your browser session may have timed out.";
/* 202 */       exception.printStackTrace();
/* 203 */       str6 = null;
/*     */     }
/*     */     finally {
/*     */       
/* 207 */       httpURLConnection.disconnect();
/* 208 */       bufferedReader = null;
/* 209 */       stringBuffer = null;
/* 210 */       object = null;
/* 211 */       httpURLConnection = null;
/*     */     } 
/* 213 */     return str6;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getJSONObject(String paramString1, String paramString2) {
/* 219 */     String str = "";
/* 220 */     paramString1 = paramString1.trim();
/* 221 */     while (paramString1.indexOf(":{") > -1) {
/*     */ 
/*     */       
/* 224 */       str = paramString1.substring(1, paramString1.indexOf(":{") - 1);
/* 225 */       str = str.substring(str.lastIndexOf(",") + 2);
/* 226 */       if (str.compareToIgnoreCase(paramString2) == 0) {
/* 227 */         String str1 = paramString1.substring(paramString1.indexOf(":{") + 1);
/* 228 */         return str1.substring(0, str1.indexOf("}") + 1);
/*     */       } 
/* 230 */       if (paramString1.indexOf("},") > -1) {
/*     */         
/* 232 */         paramString1 = paramString1.substring(paramString1.indexOf("},") + 2);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/*     */       break;
/*     */     } 
/* 239 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getJSONNumber(String paramString1, String paramString2) {
/* 244 */     paramString1 = paramString1.trim();
/*     */     
/* 246 */     paramString1 = paramString1.substring(1, paramString1.length() - 1);
/* 247 */     String[] arrayOfString = paramString1.split(",");
/* 248 */     for (byte b = 0; b < arrayOfString.length; b++) {
/* 249 */       String[] arrayOfString1 = arrayOfString[b].split(":");
/* 250 */       String str = arrayOfString1[0].trim();
/* 251 */       str = str.substring(1, str.length() - 1);
/* 252 */       if (str.compareToIgnoreCase(paramString2) == 0) {
/* 253 */         return Integer.parseInt(arrayOfString1[1].trim());
/*     */       }
/*     */     } 
/* 256 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getJSONString(String paramString1, String paramString2) {
/* 261 */     paramString1 = paramString1.trim();
/*     */     
/* 263 */     paramString1 = paramString1.substring(1, paramString1.length() - 1);
/* 264 */     String[] arrayOfString = paramString1.split(",");
/* 265 */     for (byte b = 0; b < arrayOfString.length; b++) {
/* 266 */       String[] arrayOfString1 = arrayOfString[b].split(":");
/* 267 */       String str = arrayOfString1[0].trim();
/* 268 */       str = str.substring(1, str.length() - 1);
/* 269 */       if (str.compareToIgnoreCase(paramString2) == 0) {
/* 270 */         return arrayOfString1[1].trim().substring(1, arrayOfString1[1].length() - 1);
/*     */       }
/*     */     } 
/* 273 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getJSONArray(String paramString1, String paramString2, int paramInt) {
/* 278 */     paramString1 = paramString1.trim();
/*     */     
/* 280 */     paramString1 = paramString1.substring(paramString1.indexOf("[") + 1);
/* 281 */     paramString1 = paramString1.substring(0, paramString1.indexOf("]") + 1);
/* 282 */     paramString1 = paramString1.substring(1, paramString1.length() - 1);
/* 283 */     String[] arrayOfString = paramString1.split("\\},\\{");
/* 284 */     return "{" + arrayOfString[paramInt] + "}";
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/intgapp/jsonparser.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */