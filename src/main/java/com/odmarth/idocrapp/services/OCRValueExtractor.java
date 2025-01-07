package com.odmarth.idocrapp.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.odmarth.idocrapp.models.IDCardRectoBean;
import com.odmarth.idocrapp.models.IDCardVersoBean;



public class OCRValueExtractor {
	 private static final Logger LOG = LoggerFactory.getLogger(OCRValueExtractor.class);
	 public static final Pattern NUM_PIECE_REG_Pattern = Pattern.compile("([A-Z-a-z]{1})[0-9]{7,8}$");
	 public static final Pattern NIP_REG_PATTERN = Pattern.compile("\\d{17}");
	 public static final Pattern DATE_REG_PATTERN = Pattern.compile("((((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d)|(29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96))");

   /// private  Map<String, String> mapper;
    public static String informationRetrievalBetweenTword(String input, String startChar, String endChar) {
        try {
            input = input.toLowerCase();
            startChar = startChar.toLowerCase();
            endChar = endChar.toLowerCase();
            int start = input.indexOf(startChar);
            if (start != -1) {
                int end = input.indexOf(endChar, start + startChar.length());
                if (end != -1) {
                    return input.substring(start + startChar.length(), end);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public String informationRetrievalBetweenTwChar(String str, String firtsChar, String endChar) {
        String strd;
        try {
            strd = str.substring(str.lastIndexOf(firtsChar) + 1, str.lastIndexOf(endChar));
        } catch (Exception e) {
            strd = "";
        }
        return strd;

    }



    public static Map<String, String> extractRectoMap(String lines) {
    	
        String[] data = lines.split("\n");
        Map<String, String> map = new HashMap<String, String>();

        if (lines.toLowerCase().contains("spor") || lines.toLowerCase().contains("sepor")) {
            try {
                map = ecxtractPassportData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            map.put("type", "PASSEPORT");
            LOG.info("type... ", map.toString());

        } else {
        	 LOG.error("TETT", "CNIB");
            extractCNIBData(data, map);
            
            System.out.println("LINESS ==>" + lines);
            Matcher m = NUM_PIECE_REG_Pattern.matcher(lines);
            if (m.find()) {
                String ref = m.group();
                map.put("numeropiece", ref);
            }
            map.put("type", "CNIB");
            String dat = informationRetrievalBetweenTword(lines, ") le", "sex");
            try {
                String[] da = dat.substring(dat.lastIndexOf(":") + 1).trim().split(" ");
                String[] d = da[0].split("/");
                map.put("datenaiss", d[0] + "-" + d[1] + "-" + d[2]);
                map.put("lieunaiss", da[2].toUpperCase());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return map;
    }

    private static Map<String, String> ecxtractPassportData(String[] data) throws IOException {

        Map<String, String> map = new HashMap<String, String>();
        StringBuilder mrz = new StringBuilder();

        for (int i = 0; i < data.length - 1; i++) {
            String l = data[i];
            Matcher m = NUM_PIECE_REG_Pattern.matcher(l);
            if(l.contains("<") && l.contains("<<")) mrz.append("\n").append(l);
            if (m.find()) {
                String ref = m.group();
                map.put("numeropiece", l);
            }
            if (l.toLowerCase().contains("nom") && (l.toLowerCase().contains("urname") || l.toLowerCase().contains("uname") || l.toLowerCase().contains("su"))) {
                map.put("nom", data[i + 1]);
            }
            if (l.toLowerCase().contains("énoms") || l.toLowerCase().contains("names")) {
                map.put("prenoms", data[i + 1]);
            }
            if (l.toLowerCase().contains("date de naissanc") || l.toLowerCase().contains("te of birth")) {
                map.put("datenaiss", readPassportDates(data[i + 1]));
            }
            if (l.toLowerCase().contains("ieu de naiss") || l.toLowerCase().contains("ace of birth")|| l.toLowerCase().contains("ce of birth")) {
                map.put("lieunaiss", data[i + 1]);
            }

            if (l.toLowerCase().contains("ieu d'émission'") || l.toLowerCase().contains("ce of issue") || l.toLowerCase().contains("ission'")) {
                map.put("lieuemission", data[i + 1]);
            }

            if (l.toLowerCase().contains("sexe") || l.toLowerCase().contains("sex") || l.toLowerCase().contains("sex")) {
                map.put("sexe", data[i + 1]);
            }
            if (l.toLowerCase().contains("vrance") || l.toLowerCase().contains("Date of issue")) {
                map.put("datedelivrance", readPassportDates(data[i + 1]));
            }
            if (l.toLowerCase().contains("expirat") || l.toLowerCase().contains("expiry") || l.toLowerCase().contains("expir")) {
                map.put("dateexpiration", readPassportDates(data[i + 1]));
            }
            if (l.toLowerCase().contains("urkina")) {
                map.put("nationalite", "BURKINABE");
            }
            if(l.toLowerCase().contains("torité") || l.toLowerCase().contains("using aut") || l.toLowerCase().contains("ssuin") || l.toLowerCase().contains("ssuing auth")){
                map.put("autorite", data[i+1] +" "+ data[i+2]);
            }
        }
        return map;
    }


    private static void extractCNIBData(String[] data, Map<String, String> map) {
        for (int i = 0; i < data.length - 1; i++) {
            String l = data[i];
           // LOG.error("Ligne......", l);
            Matcher dateMatcher = DATE_REG_PATTERN.matcher(l);
            Matcher m = NUM_PIECE_REG_Pattern.matcher(l);
            if (m.find()) {
                String ref = m.group();
                map.put("numeropiece", ref);
            }

            Matcher nipMatcher = NIP_REG_PATTERN.matcher(l);
            if (nipMatcher.find()) {
                String nip = nipMatcher.group();
                map.put("nip", nip);
            }
            if (l.toLowerCase().contains("nom") && !l.toLowerCase().contains("noms")) {
                map.put("nom", l.substring(4).toUpperCase());
            }
            if (l.toLowerCase().contains("pre") || l.toLowerCase().contains("noms") || l.toLowerCase().contains("preno") ) {
                map.put("prenoms", l.substring(8).toUpperCase());
            }
            if (l.toLowerCase().contains("profession") || l.toLowerCase().contains("prof")) {
                map.put("profession", l.substring(11).toUpperCase());
            }
            if (l.toLowerCase().contains("sexe")) {
                //map.put("sexe", l.substring(5, 6).toUpperCase());
                if (!l.toLowerCase().contains("m")) {
                    map.put("sexe", data[i]);
                } else {
                    map.put("sexe", l.substring(5, 7).toUpperCase());
                }
            }
            if (l.toLowerCase().contains("livrée le") || l.toLowerCase().contains("livr") ) {
            	
                if (dateMatcher.find()) {
                    String dateFind = dateMatcher.group();
                    System.out.println("DATE DELI FIND "+ dateFind);
                    map.put("datedelivrance", dateFind);
                }
            }
            if (l.toLowerCase().contains("expire") || l.toLowerCase().contains("ire le") || l.toLowerCase().contains("expir")) {

                 if (dateMatcher.find()) {
                     String dateFind = dateMatcher.group();
                     System.out.println("DATE EXP FIND "+ dateFind);
                     map.put("dateexpiration", dateFind);
                 }
                 
            	
            }
            if (l.toLowerCase().contains("urkina")) {
                map.put("nationalite", "BURKINABE");
            }

        }
    }
    public static Map<String, String> extractVersoMap(String lines) {
        String[] data = lines.split("\n");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < data.length - 1; i++) {
            String l = data[i];
            LOG.error("EXTRACT VERSO LIGNE", l);
            if (l.toLowerCase().contains("secteur")) {
                map.put("secteur", informationRetrievalBetweenTword(lines, "Secteur", "Personne"));
            }
            if (l.toLowerCase().contains("sidenc")) {
                String resid = informationRetrievalBetweenTword(lines, "sidence", "Secteur");
                LOG.info("resid.....", resid);
                resid = resid.replaceAll(":", "").replaceAll(": ", "").replaceAll(",", "");
                map.put("residence", resid);
            }
            if (l.toLowerCase().contains("ovince") && l.toLowerCase().contains("partement")) {
                String text = l.toLowerCase();
                String[] provinceDepartement = text.substring(text.lastIndexOf("partement") + "partement:".length()).split(",");
                if(provinceDepartement.length>0){
                    map.put("province", provinceDepartement[0]);
                   if(provinceDepartement.length>1) map.put("departement", provinceDepartement[1]);
                }

            }

            if (l.toLowerCase().contains("tel")) {
                map.put("telephonepap", informationRetrievalBetweenTword(lines, "TEL", "I"));
            }
            if (l.toLowerCase().contains("en cas de besoin")) {
                String pap[] = informationRetrievalBetweenTword(lines, "en cas de besoin", "TEL").split(" ");
                LOG.info("pap1...", informationRetrievalBetweenTword(lines, "en cas de besoin", "TEL"));
                String prenompap = "";
                if (pap.length > 0) {
                    LOG.info("pap...", "" + pap.length);
                    //Log.i("pap...", pap[0] + "..." + pap[1] + "..." + pap[2]);
                    if (pap[0].length() > 0 && !pap[0].contains(":")) {
                        map.put("nompap", pap[0]);
                        if (pap.length > 1) {
                            LOG.info("ok1...", "");
                            for (int k = 1; k < pap.length; k++) {
                                prenompap += " " + pap[k];
                            }
                        }
                    } else if (pap[0].length() > 0 && pap[0].contains(":")) {
                        if (pap.length > 1) {
                            map.put("nompap", pap[1]);
                            if (pap.length > 2) {
                                for (int k = 2; k < pap.length; k++) {
                                    prenompap += " " + pap[k];
                                }
                            }
                        }
                    } else {
                        LOG.info("ok2...", "");
                        pap = pap[1].split("\n");
                        if (pap.length > 0) {
                            map.put("nompap", pap[0]);
                            if (pap.length > 1) {
                                for (int k = 1; k < pap.length; k++) {
                                    prenompap += " " + pap[k];
                                }
                            }
                        }
                    }
                    map.put("prenompap", prenompap);
                }
            }
        }

        return map;
    }

    private static String readPassportDates(String s) {
        String dateReg ="(\\b\\d{1,2}\\D{0,3})\\s([a-zA-Z]+\\/?\\s?[a-zA-Z]+)\\s?((19[7-9]\\d|20\\d{2})|\\d{2})";

        Pattern pattern = Pattern.compile(dateReg);
        Matcher matcher = pattern.matcher(s);
        boolean matches = matcher.matches();
        if(matches){
            String monthgroup = matcher.group(2);
            String pos2 = "";
            LOG.error("DATA", "DATTT ===>"+monthgroup);
            assert monthgroup != null;
            if(monthgroup.toLowerCase().contains("jan")){
                pos2 = "01";
            }else if(monthgroup.toLowerCase().contains("fev")){
                pos2 = "02";
            }else if(monthgroup.toLowerCase().contains("mar")){
                pos2 = "03";
            }else if(monthgroup.toLowerCase().contains("avr") || monthgroup.toLowerCase().contains("apr")){
                pos2 = "04";
            }else if(monthgroup.toLowerCase().contains("mai") || monthgroup.toLowerCase().contains("may")){
                pos2 = "05";
            } else if (monthgroup.toLowerCase().contains("juin") || monthgroup.toLowerCase().contains("jun") ){
                pos2 = "06";
            }else if(monthgroup.toLowerCase().contains("jan") || monthgroup.toLowerCase().contains("jul")){
                pos2 = "07";
            }else if(monthgroup.toLowerCase().contains("aout") || monthgroup.toLowerCase().contains("aug")){
                pos2 = "08";
            }else if(monthgroup.toLowerCase().contains("sept") || monthgroup.toLowerCase().contains("sep")){
                pos2 = "09";
            }else if(monthgroup.toLowerCase().contains("oct")){
                pos2 = "10";
            } else if(monthgroup.toLowerCase().contains("nov")){
                pos2 = "11";
            }else if(monthgroup.toLowerCase().contains("dec")){
                pos2 = "12";
            }
            String birthDate = matcher.group(1)+"/"+pos2+"/"+matcher.group(3);

            LOG.error("BURKRDATA", ""+birthDate);
            return birthDate;
        }
        
        return "";
    }

//    public static Bitmap createBlackAndWhite(Bitmap src) {
//        int width = src.getWidth();
//        int height = src.getHeight();
//
//        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//        final float factor = 255f;
//        final float redBri = 0.2126f;
//        final float greenBri = 0.2126f;
//        final float blueBri = 0.0722f;
//
//        int length = width * height;
//        int[] inpixels = new int[length];
//        int[] oupixels = new int[length];
//
//        src.getPixels(inpixels, 0, width, 0, 0, width, height);
//
//        int point = 0;
//        for (int pix : inpixels) {
//            int R = (pix >> 16) & 0xFF;
//            int G = (pix >> 8) & 0xFF;
//            int B = pix & 0xFF;
//
//            float lum = (redBri * R / factor) + (greenBri * G / factor) + (blueBri * B / factor);
//
//            if (lum > 0.4) {
//                oupixels[point] = 0xFFFFFFFF;
//            } else {
//                oupixels[point] = 0xFF000000;
//            }
//            point++;
//        }
//        // bmOut.setPixels(oupixels, 0, width, 0, 0, width, height);
//        return bmOut;
//    }

//    public static Bitmap toGrayscale(Bitmap bmpOriginal)
//    {
//        int width, height;
//        height = bmpOriginal.getHeight();
//        width = bmpOriginal.getWidth();
//
//        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bmpGrayscale);
//        Paint paint = new Paint();
//        ColorMatrix cm = new ColorMatrix();
//        cm.setSaturation(0);
//        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//        paint.setColorFilter(f);
//        c.drawBitmap(bmpOriginal, 0, 0, paint);
//        return bmpGrayscale;
//    }
    
    public static IDCardRectoBean extractRectoBean(String rectoText) {
		
		Map<String, String> map = extractRectoMap(rectoText);
		IDCardRectoBean idCardRectoBean = new IDCardRectoBean();

		LOG.error("lines extrated... ", map.toString());
		idCardRectoBean.setLastName(map.get("nom"));
		idCardRectoBean.setNip(map.get("nip"));
		idCardRectoBean.setFirstName(map.get("prenoms"));
		idCardRectoBean.setProfession(map.get("profession"));
		idCardRectoBean.setCardNumber(map.get("numeropiece"));
		idCardRectoBean.setBirthPlace(map.get("lieunaiss"));
		idCardRectoBean.setBirthDay(map.get("datenaiss"));
		idCardRectoBean.setCardDeliverDate(map.get("datedelivrance"));
		idCardRectoBean.setCardExpireDate(map.get("dateexpiration"));
		idCardRectoBean.setIssuePlace(map.get("lieuemission"));
		idCardRectoBean.setNationality(map.get("nationalite"));
		idCardRectoBean.setGender(map.get("sexe"));
		idCardRectoBean.setAutority(map.get("autorite"));


		return idCardRectoBean;
	}

    public static IDCardVersoBean extractVersoBean(String versoText) {
		

		Map<String, String> map  = extractVersoMap(versoText);
		IDCardVersoBean idCardVersoBean = new IDCardVersoBean();
		
		LOG.error("lines extrated... ", map.toString());
		
		idCardVersoBean.setLastNamePAP(map.get("nompap"));
		idCardVersoBean.setFirstNamePAP(map.get("prenompap"));
		idCardVersoBean.setPhonePAP(map.get("telephonepap"));
		idCardVersoBean.setSecteur(map.get("secteur"));
		idCardVersoBean.setResidence(map.get("residence"));
		idCardVersoBean.setProvince(map.get("province"));
		idCardVersoBean.setDepartement(map.get("departement"));   
		
		return idCardVersoBean;
	}
}
