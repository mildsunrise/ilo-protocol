/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ class LocaleTranslator {
/*     */   Hashtable<String, Hashtable<Character, String>> locales;
/*     */   Hashtable<String, String> aliases;
/*     */   Hashtable<Character, String> selected;
/*     */   Hashtable<String, String> reverse_alias;
/*     */   public boolean showgui = false;
/*     */   public boolean windows = true;
/*     */   String selected_name;
/*  63 */   String euro1 = " €\033[+4";
/*  64 */   String euro2 = " €\033[+e";
/*     */ 
/*     */   
/*  67 */   String belgian = "\001\021 \021\001 \027\032 \032\027 !8 \"3 #\033[+3 $] %\" &1 '4 (5 )- *} +? ,m -= .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð =/ >ñ ?M @\033[+2 AQ M: QA WZ ZW [\033[+[ \\\033[+ð ]\033[+] ^[  _+ `\033[+\\  aq m; qa wz zw {\033[+9 |\033[+1 }\033[+0 ~\033[+/  £| §6 ¨{  °_ ²` ³~ ´\033[+'  µ\\ À\033[+\\Q Á\033[+'Q Â[Q Ã\033[+/Q Ä{Q È\033[+\\E É\033[+'E Ê[E Ë{E Ì\033[+\\I Í\033[+'I Î[I Ï{I Ñ\033[+/N Ò\033[+\\O Ó\033[+'O Ô[O Õ\033[+/O Ö{O Ù\033[+\\U Ú\033[+'U Û[U Ü{U Ý\033[+'Y à\033[+\\q á\033[+'q â[q ã\033[+/q ä{q ç9 è\033[+\\e é\033[+'e ê[e ë{e ì\033[+\\i í\033[+'i î[i ï{i ñ\033[+/n ò\033[+\\o ó\033[+'o ô[o õ\033[+/o ö{o ù\033[+\\u ú\033[+'u û[u ü{u ý\033[+'y ÿ{y";
/*     */ 
/*     */   
/*  70 */   String british = "\"@ #\\ @\" \\ð |ñ ~| £# ¦\033[+` ¬~ Á\033[+A á\033[+a É\033[+E é\033[+e Í\033[+I í\033[+i Ó\033[+O ó\033[+o Ú\033[+U ú\033[+u";
/*     */ 
/*     */   
/*  73 */   String danish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+ð ]\033[+9 ^}  _? `+  {\033[+7 |\033[+= }\033[+0 ~\033[+]  £\033[+3 ¤$ §~ ¨]  ´=  ½` À+A Á=A Â}A Ã\033[+]A Ä]A Å{ Æ: È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ø\" Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ æ; è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ø' ù+u ú=u û}u ü]u ý=y ÿ]y";
/*     */ 
/*     */   
/*  76 */   String finnish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+- ]\033[+9 ^}  _? `+  {\033[+7 |\033[+ð }\033[+0 ~\033[+]  £\033[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\033[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
/*     */ 
/*     */   
/*  79 */   String french = "\001\021 \021\001 \027\032 \032\027 !/ \"3 #\033[+3 $] %\" &1 '4 (5 )- *\\ ,m -6 .< /> 0) 1! 2@ 3# 4$ 5% 6^ 7& 8* 9( :. ;, <ð >ñ ?M @\033[+0 AQ M: QA WZ ZW [\033[+5 \\\033[+8 ]\033[+- ^\033[+9 _8 `\033[+7 aq m; qa wz zw {\033[+4 |\033[+6 }\033[+= ~\033[+2 £} ¤\033[+] §? ¨{  °_ ²` µ| Â[Q Ä{Q Ê[E Ë{E Î[I Ï{I Ô[O Ö{O Û[U Ü{U à0 â[q ä{q ç9 è7 é2 ê[e ë{e î[i ï{i ô[o ö{o ù' û[u ü{u ÿ{y";
/*     */ 
/*     */   
/*  82 */   String french_canadian = "\"@ #` '< /# <\\ >| ?^ @\033[+2 [\033[+[ \\\033[+` ]\033[+] ^[  `'  {\033[+' |~ }\033[+\\ ~\033[+; ¢\033[+4 £\033[+3 ¤\033[+5 ¦\033[+7 §\033[+o ¨}  «ð ¬\033[+6 ­\033[+. ¯\033[+, °\033[+ð ±\033[+1 ²\033[+8 ³\033[+9 ´\033[+/  µ\033[+m ¶\033[+p ¸]  »ñ ¼\033[+0 ½\033[+- ¾\033[+= À'A Á\033[+/A Â[A Ä}A Ç]C È'E É? Ê[E Ë}E Ì'I Í\033[+/I Î[I Ï}I Ò'O Ó\033[+/O Ô[O Ö}O Ù'U Ú\033[+/U Û[U Ü}U Ý\033[+/Y à'a á\033[+/a â[a ä}a ç]c è'e é\033[+/e ê[e ë}e ì'i í\033[+/i î[i ï}i ò'o ó\033[+/o ô[o ö}o ù'u ú\033[+/u û[u ü}u ý\033[+/y ÿ}y";
/*     */ 
/*     */   
/*  85 */   String german = "\031\032 \032\031 \"@ #\\ &^ '| (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+q YZ ZY [\033[+8 \\\033[+- ]\033[+9 ^`  _? `+  yz zy {\033[+7 |\033[+ð }\033[+0 ~\033[+] §# °~ ²\033[+2 ³\033[+3 ´=  µ\033[+m À+A Á=A Â`A Ä\" È+E É=E Ê`E Ì+I Í=I Î`I Ò+O Ó=O Ô`O Ö: Ù+U Ú=U Û`U Ü{ Ý=Z ß- à+a á=a â`a ä' è+e é=e ê`e ì+i í=i î`i ò+o ó=o ô`o ö; ù+u ú=u û`u ü[ ý=z";
/*     */ 
/*     */   
/*  88 */   String italian = "\"@ #\033[+' &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+; [\033[+[ \\` ]\033[+] ^+ _? |~ £# §| °\" à' ç: è[ é{ ì= ò; ù\\";
/*     */ 
/*     */   
/*  91 */   String japanese = "\"@ &^ '& (* )( *\" +: :' =_ @[ [] \\ò ]\\ ^= _ó `{ {} ¥ô |õ }| ~+";
/*     */ 
/*     */   
/*  94 */   String latin_american = "\"@ &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+q [\" \\\033[+- ]| ^\033[+'  _? `\033[+\\  {' |` }\\ ~\033[+] ¡+ ¨{  ¬\033[+` °~ ´[  ¿= À\033[+\\A Á[A Â\033[+'A Ä{A È\033[+\\E É[E Ê\033[+'E Ë{E Ì\033[+\\I Í[I Î\033[+'I Ï{I Ñ: Ò\033[+\\O Ó[O Ô\033[+'O Ö{O Ù\033[+\\U Ú[U Û\033[+'U Ü{U Ý[Y à\033[+\\a á[a â\033[+'a ä{a è\033[+\\e é[e ê\033[+'e ë{e ì\033[+\\i í[i î\033[+'i ï{i ñ; ò\033[+\\o ó[o ô\033[+'o ö{o ù\033[+\\u ú[u û\033[+'u ü{u ý[y ÿ{y";
/*     */ 
/*     */   
/*  97 */   String norwegian = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\= ]\033[+9 ^}  _? `+  {\033[+7 |` }\033[+0 ~\033[+]  £\033[+3 ¤$ §~ ¨]  ´\033[+=  À+A Á\033[+=A Â}A Ã\033[+]A Ä]A Å{ Æ\" È+E É\033[+=E Ê}E Ë]E Ì+I Í\033[+=I Î}I Ï]I Ñ\033[+]N Ò+O Ó\033[+=O Ô}O Õ\033[+]O Ö]O Ø: Ù+U Ú\033[+=U Û}U Ü]U Ý\033[+=Y à+a á\033[+=a â}a ã\033[+]a ä]a å[ æ' è+e é\033[+=e ê}e ë]e ì+i í\033[+=i î}i ï]i ñ\033[+]n ò+o ó\033[+=o ô}o õ\033[+]o ö]o ø; ù+u ú\033[+=u û}u ü]u ý\033[+=y ÿ]y";
/*     */ 
/*     */   
/* 100 */   String portuguese = "\"@ &^ '- (* )( *{ +[ -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\` ]\033[+9 ^|  _? `}  {\033[+7 |~ }\033[+0 ~\\  £\033[+3 §\033[+4 ¨\033[+[  ª\" «= ´]  º' »+ À}A Á]A Â|A Ã\\A Ä\033[+[A Ç: È}E É]E Ê|E Ë\033[+[E Ì}I Í]I Î|I Ï\033[+[I Ñ\\N Ò}O Ó]O Ô|O Õ\\O Ö\033[+[O Ù}U Ú]U Û|U Ü\033[+[U Ý]Y à}a á]a â|a ã\\a ä\033[+[a ç; è}e é]e ê|e ë\033[+[e ì}i í]i î|i ï\033[+[i ñ\\n ò}o ó]o ô|o õ\\o ö\033[+[o ù}u ú]u û|u ü\033[+[u ý]y ÿ\033[+[y";
/*     */ 
/*     */   
/* 103 */   String spanish = "\"@ #\033[+3 &^ '- (* )( *} +] -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+[ \\\033[+` ]\033[+] ^{  _? `[  {\033[+' |\033[+1 }\033[+\\ ¡= ¨\"  ª~ ¬\033[+6 ´'  ·# º` ¿+ À[A Á'A Â{A Ä\"A Ç| È[E É'E Ê{E Ë\"E Ì[I Í'I Î{I Ï\"I Ñ: Ò[O Ó'O Ô{O Ö\"O Ù[U Ú'U Û{U Ü\"U Ý'Y à[a á'a â{a ä\"a ç\\ è[e é'e ê{e ë\"e ì[i í'i î{i ï\"i ñ; ò[o ó'o ô{o ö\"o ù[u ú'u û{u ü\"u ý'y ÿ\"y";
/*     */ 
/*     */   
/* 106 */   String swedish = "\"@ $\033[+4 &^ '\\ (* )( *| +- -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 [\033[+8 \\\033[+- ]\033[+9 ^}  _? `+  {\033[+7 |\033[+ð }\033[+0 ~\033[+]  £\033[+3 ¤$ §` ¨]  ´=  ½~ À+A Á=A Â}A Ã\033[+]A Ä]A Å{ È+E É=E Ê}E Ë]E Ì+I Í=I Î}I Ï]I Ñ\033[+]N Ò+O Ó=O Ô}O Õ\033[+]O Ö]O Ù+U Ú=U Û}U Ü]U Ý=Y à+a á=a â}a ã\033[+]a ä]a å[ è+e é=e ê}e ë]e ì+i í=i î}i ï]i ñ\033[+]n ò+o ó=o ô}o õ\033[+]o ö]o ù+u ú=u û}u ü]u ý=y ÿ]y";
/*     */ 
/*     */   
/* 109 */   String swiss_french = "\031\032 \032\031 !} \"@ #\033[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 YZ ZY [\033[+[ \\\033[+ð ]\033[+] ^=  _? `+  yz zy {\033[+' |\033[+7 }\033[+\\ ~\033[+=  ¢\033[+8 £| ¦\033[+1 §` ¨]  ¬\033[+6 °~ ´\033[+-  À+A Á\033[+-A Â=A Ã\033[+=A Ä]A È+E É\033[+-E Ê=E Ë]E Ì+I Í\033[+-I Î=I Ï]I Ñ\033[+=N Ò+O Ó\033[+-O Ô=O Õ\033[+=O Ö]O Ù+U Ú\033[+-U Û=U Ü]U Ý\033[+-Z à+a á\033[+-a â=a ã\033[+=a ä]a ç$ è+e é\033[+-e ê=e ë]e ì+i í\033[+-i î=i ï]i ñ\033[+=n ò+o ó\033[+-o ô=o õ\033[+=o ö]o ù+u ú\033[+-u û=u ü]u ý\033[+-z ÿ]z";
/*     */ 
/*     */   
/* 112 */   String swiss_german = "\031\032 \032\031 !} \"@ #\033[+3 $\\ &^ '- (* )( *# +! -/ /& :> ;< <ð =) >ñ ?_ @\033[+2 YZ ZY [\033[+[ \\\033[+ð ]\033[+] ^=  _? `+  yz zy {\033[+' |\033[+7 }\033[+\\ ~\033[+=  ¢\033[+8 £| ¦\033[+1 §` ¨]  ¬\033[+6 °~ ´\033[+-  À+A Á\033[+-A Â=A Ã\033[+=A Ä]A È+E É\033[+-E Ê=E Ë]E Ì+I Í\033[+-I Î=I Ï]I Ñ\033[+=N Ò+O Ó\033[+-O Ô=O Õ\033[+=O Ö]O Ù+U Ú\033[+-U Û=U Ü]U Ý\033[+-Z à+a á\033[+-a â=a ã\033[+=a ä]a ç$ è+e é\033[+-e ê=e ë]e ì+i í\033[+-i î=i ï]i ñ\033[+=n ò+o ó\033[+-o ô=o õ\033[+=o ö]o ù+u ú\033[+-u û=u ü]u ý\033[+-z ÿ]z";
/*     */ 
/*     */   
/*     */   String create_accents(String paramString1, String paramString2) {
/* 116 */     StringBuffer stringBuffer = new StringBuffer(256);
/*     */ 
/*     */ 
/*     */     
/* 120 */     for (byte b = 0; b < paramString1.length(); b++) {
/* 121 */       char c = paramString1.charAt(b);
/* 122 */       if (c == '*') {
/* 123 */         stringBuffer.append(paramString2);
/*     */       } else {
/* 125 */         stringBuffer.append(c);
/*     */       } 
/*     */     } 
/* 128 */     return stringBuffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void parse_locale_str(String localeStr, Hashtable<Character, String> localeTable) {
/* 134 */     boolean constructing = false;
/* 135 */     char c = Character.MIN_VALUE;
/* 136 */     Character character = null;
/* 137 */     StringBuffer stringBuffer = new StringBuffer(16);
/*     */     
/* 139 */     for (byte i = 0; i < localeStr.length(); i++) {
/* 140 */       c = localeStr.charAt(i);
/* 141 */       if (!constructing && c != ' ') {
/* 142 */         constructing = true;
/* 143 */         character = new Character(c);
/*     */       } else {
/* 146 */         if (constructing && c != ' ') {
/* 148 */           if (c == '\u00a0')
/* 149 */             c = ' ';
/* 150 */           stringBuffer.append(c);
/*     */         }
/* 152 */         if (constructing && c == ' ') {
/* 154 */           localeTable.put(character, stringBuffer.toString());
/* 155 */           constructing = false;
/* 156 */           stringBuffer = new StringBuffer(16);
/*     */         }
/*     */       }
/*     */     }
/* 160 */     localeTable.put(character, stringBuffer.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void add_locale(String localeName, String localeStr, String alias) {
/* 166 */     Hashtable<Character, String> localeTable = new Hashtable<>();
/*     */ 
/*     */     
/* 169 */     parse_locale_str(localeStr, localeTable);
/* 170 */     this.locales.put(localeName, localeTable);
/* 171 */     this.aliases.put(alias, localeName);
/* 172 */     this.reverse_alias.put(localeName, alias);
/*     */   }
/*     */ 
/*     */   
/*     */   void add_iso_alias(String localeName, String isoAlias) {
/* 177 */     this.locales.put(isoAlias, this.locales.get(localeName));
/* 178 */     this.reverse_alias.put(isoAlias, this.reverse_alias.get(localeName));
/*     */   }
/*     */ 
/*     */   
/*     */   void add_alias(String localeName, String alias) {
/* 183 */     this.aliases.put(alias, localeName);
/* 184 */     this.reverse_alias.put(localeName, alias);
/*     */   }
/*     */ 
/*     */   
/*     */   public LocaleTranslator() {
/* 189 */     this.locales = new Hashtable<>();
/* 190 */     this.aliases = new Hashtable<>();
/* 191 */     this.reverse_alias = new Hashtable<>();
/*     */     
/* 193 */     String str = null;
/*     */ 
/*     */ 
/*     */     
/* 197 */     this.locales.put("en_US", new Hashtable<>());
/* 198 */     add_alias("en_US", "English (United States)");
/*     */     
/* 200 */     add_locale("en_GB", this.british + this.euro1, "English (United Kingdom)");
/* 201 */     add_locale("fr_FR", this.french + this.euro2, "French");
/* 202 */     add_locale("it_IT", this.italian + this.euro2, "Italian");
/* 203 */     add_locale("de_DE", this.german + this.euro2, "German");
/* 204 */     add_locale("es_ES", this.spanish + this.euro2, "Spanish (Spain)");
/*     */     
/* 206 */     add_locale("ja_JP", this.japanese, "Japanese");
/*     */     
/* 208 */     add_locale("es_MX", this.latin_american + this.euro2, "Spanish (Latin America)");
/* 209 */     add_iso_alias("es_MX", "es_AR");
/* 210 */     add_iso_alias("es_MX", "es_BO");
/* 211 */     add_iso_alias("es_MX", "es_CL");
/* 212 */     add_iso_alias("es_MX", "es_CO");
/* 213 */     add_iso_alias("es_MX", "es_CR");
/* 214 */     add_iso_alias("es_MX", "es_DO");
/* 215 */     add_iso_alias("es_MX", "es_EC");
/* 216 */     add_iso_alias("es_MX", "es_GT");
/* 217 */     add_iso_alias("es_MX", "es_HN");
/* 218 */     add_iso_alias("es_MX", "es_NI");
/* 219 */     add_iso_alias("es_MX", "es_PA");
/* 220 */     add_iso_alias("es_MX", "es_PE");
/* 221 */     add_iso_alias("es_MX", "es_PR");
/* 222 */     add_iso_alias("es_MX", "es_PY");
/* 223 */     add_iso_alias("es_MX", "es_SV");
/* 224 */     add_iso_alias("es_MX", "es_UY");
/* 225 */     add_iso_alias("es_MX", "es_VE");
/*     */     
/* 227 */     add_locale("fr_BE", this.belgian + this.euro2, "French Belgium");
/* 228 */     add_locale("fr_CA", this.french_canadian + this.euro2, "French Canadian");
/* 229 */     add_locale("da_DK", this.danish + this.euro2, "Danish");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 235 */     add_locale("no_NO", this.norwegian + this.euro2, "Norwegian");
/* 236 */     add_locale("pt_PT", this.portuguese + this.euro2, "Portugese");
/*     */ 
/*     */ 
/*     */     
/* 240 */     add_locale("sv_SE", this.swedish + this.euro2, "Swedish");
/* 241 */     add_locale("fi_FI", this.finnish + this.euro2, "Finnish");
/*     */     
/* 243 */     add_locale("fr_CH", this.swiss_french + this.euro2, "Swiss (French)");
/* 244 */     add_locale("de_CH", this.swiss_german + this.euro2, "Swiss (German)");
/*     */     
/* 246 */     Enumeration enumeration = remcons.prop.propertyNames();
/* 247 */     while (enumeration.hasMoreElements()) {
/* 248 */       String str1 = (String)enumeration.nextElement();
/* 249 */       if (str1.equals("locale.override")) {
/* 250 */         str = remcons.prop.getProperty(str1);
/* 251 */         System.out.println("Locale override: " + str); continue;
/* 252 */       }  if (str1.startsWith("locale.windows")) {
/* 253 */         this.windows = Boolean.valueOf(remcons.prop.getProperty(str1)).booleanValue(); continue;
/* 254 */       }  if (str1.startsWith("locale.showgui")) {
/* 255 */         this.showgui = Boolean.valueOf(remcons.prop.getProperty(str1)).booleanValue(); continue;
/* 256 */       }  if (str1.startsWith("locale.")) {
/* 257 */         String str2 = str1.substring(7);
/* 258 */         String str3 = remcons.prop.getProperty(str1);
/* 259 */         System.out.println("Adding user defined local for " + str2);
/* 260 */         add_locale(str2, str3, str2 + " (User Defined)");
/*     */       } 
/*     */     } 
/*     */     
/* 264 */     if (str != null) {
/* 265 */       System.out.println("Trying to select locale: " + str);
/* 266 */       if (selectLocale(str) != 0) {
/* 267 */         System.out.println("No keyboard definition for " + str);
/*     */       }
/*     */     } else {
/* 270 */       Locale locale = Locale.getDefault();
/* 271 */       System.out.println("Trying to select locale: " + locale.toString());
/* 272 */       if (selectLocale(locale.toString()) != 0) {
/* 273 */         System.out.println("No keyboard definition for '" + locale.toString() + "'");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int selectLocale(String localeName) {
/* 280 */     String str = (String)this.aliases.get(localeName);
/* 281 */     if (str != null) {
/* 282 */       localeName = str;
/*     */     }
/* 284 */     this.selected = this.locales.get(localeName);
/* 285 */     this.selected_name = this.reverse_alias.get(localeName);
/* 286 */     return (this.selected != null) ? 0 : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public String translate(char input) {
/* 291 */     Character inputChar = new Character(input);
/* 292 */     String str = null;
/*     */     
/* 294 */     if (this.selected != null) {
/* 295 */       str = (String)this.selected.get(inputChar);
/*     */     }
/*     */ 
/* 299 */     return (str == null) ? inputChar.toString() : str;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getLocales() {
/* 304 */     int i = this.aliases.size();
/* 305 */     String[] result = new String[i];
/*     */     
/* 307 */     Enumeration<String> aliasesNames = this.aliases.keys();
/*     */     
/* 309 */     byte b = 0;
/* 310 */     while (aliasesNames.hasMoreElements()) {
/* 311 */       result[b++] = aliasesNames.nextElement();
/*     */     }
/*     */     
/* 314 */     for (b = 0; b < i - 1; b++) {
/* 315 */       for (int j = b + 1; j < i; j++) {
/* 316 */         if (result[j].compareTo(result[b]) < 0) {
/* 317 */           String str = result[j];
/* 318 */           result[j] = result[b];
/* 319 */           result[b] = str;
/*     */         } 
/*     */       } 
/*     */     } 
/* 323 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSelected() {
/* 328 */     return this.selected_name;
/*     */   }
/*     */ }


/* Location:              /home/alba/Documents/dev/irc/samples/intgapp4_231.jar!/com/hp/ilo2/remcons/LocaleTranslator.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */