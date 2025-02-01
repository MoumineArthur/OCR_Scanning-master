package com.odmarth.idocrapp.services;
import java.util.regex.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class CarteIdentiteExtractor {
	 private static final Logger LOG = LoggerFactory.getLogger(CarteIdentiteExtractor.class);
	 public static final Pattern NUM_PIECE_REG_Pattern = Pattern.compile("([A-Z-a-z]{1})[0-9]{7,8}$");
	 public static final Pattern NIP_REG_PATTERN = Pattern.compile("\\d{17}");
	 public static final Pattern DATE_REG_PATTERN = Pattern.compile("((((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d)|(29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96))");
	 
	 
	 public static Map<String, String> extractRectoData(String ocrText) {
		 
		 if (ocrText.toLowerCase().contains("spor") || ocrText.toLowerCase().contains("sepor")) {
			 return extractPassportData(ocrText);
	    	}else {
	    		return extractCIBData(ocrText);
	    	}
	 }
	 
	 public static Map<String, String> extractVersoData(String lines) {
		    String[] data = lines.split("\n");
		    Map<String, String> map = new HashMap<>();
		    
		    // Conversion de lines en minuscules pour éviter des appels répétés
		   // String lowerCaseLines = lines.toLowerCase();

		    for (int i = 0; i < data.length - 1; i++) {
		        String l = data[i].toLowerCase();
		        LOG.error("EXTRACT VERSO LIGNE", l);
		        
		        // Secteur
		        if (l.contains("secteur")) {
		            map.put("secteur", informationRetrievalBetweenTword(lines, "secteur", "personne"));
		        }
		        
		        // Residence
		        if (l.contains("sidenc")) {
		            String resid = informationRetrievalBetweenTword(lines, "sidence", "secteur");
		            LOG.info("resid.....", resid);
		            resid = resid.replaceAll("[:\\s,]+", "");  // Optimisation de la suppression des caractères inutiles
		            map.put("residence", resid);
		        }
		        
		        // Province et département
		        if (l.contains("ovince") && l.contains("partement")) {
		            String[] provinceDepartement = l.substring(l.lastIndexOf("partement") + "partement:".length()).split(",");
		            if (provinceDepartement.length > 0) {
		                map.put("province", provinceDepartement[0].trim());
		                if (provinceDepartement.length > 1) map.put("departement", provinceDepartement[1].trim());
		            }
		        }

		        // Téléphone
		        if (l.contains("tel")) {
		            map.put("telephonepap", informationRetrievalBetweenTword(lines, "tel", "i"));
		        }

		        // En cas de besoin (nom et prénom du contact d'urgence)
		        if (l.contains("en cas de besoin")) {
		            String[] pap = informationRetrievalBetweenTword(lines, "en cas de besoin", "tel").split(" ");
		            LOG.info("pap1...", informationRetrievalBetweenTword(lines, "en cas de besoin", "tel"));
		            String prenompap = "";
		            if (pap.length > 0) {
		                if (pap[0].length() > 0 && !pap[0].contains(":")) {
		                    map.put("nompap", pap[0]);
		                    if (pap.length > 1) {
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

		public static String informationRetrievalBetweenTword(String input, String startChar, String endChar) {
		    try {
		        input = input.toLowerCase();
		        startChar = startChar.toLowerCase();
		        endChar = endChar.toLowerCase();
		        int start = input.indexOf(startChar);
		        if (start != -1) {
		            int end = input.indexOf(endChar, start + startChar.length());
		            if (end != -1) {
		                return input.substring(start + startChar.length(), end).trim();
		            }
		        }
		    } catch (Exception e) {
		        LOG.error("Error retrieving information between words", e);
		    }
		    return "";
		}


   
    private static Map<String, String> extractCIBData(String ocrText) {
        Map<String, String> extractedData = new HashMap<>();
      
        // Supprimer les accents et mettre en majuscules pour homogénéité
        String normalizedText = normalizeText(ocrText);
        String[] lines = normalizedText.split("\\R");
        
        extractedData.put("type", "CNIB");
        for(String ligne: lines) {
        	Pattern idPattern = Pattern.compile("\\b([A-Z]\\d{8})\\b");
        	Matcher m = idPattern.matcher(ligne);
            if (m.find()) {
                String ref = m.group(1);
                extractedData.put("numeropiece", ref);
            }
            
         // Expression régulière pour le nom
            Pattern namePattern = Pattern.compile("(?i)NOM[:?|.?\\s]+([A-Z\\s']+)");
            Matcher nameMatcher = namePattern.matcher(ligne);
            if (nameMatcher.find()) {
                extractedData.put("nom", nameMatcher.group(1).trim());
            }
            
            // Expression régulière pour les prénoms
            Pattern firstNamePattern = Pattern.compile("(?i)PRENOMS?[:?|.?\\s]+([A-Z\\s']+)");
            Matcher firstNameMatcher = firstNamePattern.matcher(ligne);
            if (firstNameMatcher.find()) {
                extractedData.put("prenoms", firstNameMatcher.group(1).trim());
            }
            
            // Expression régulière pour la date et le lieu de naissance
            Pattern birthPattern = Pattern.compile("(?i)NE\\(E\\) LE[:?|.?\\s]+(\\d{2}/\\d{2}/\\d{4})\\s+A\\s+([A-Z\\s']+)");
            Matcher birthMatcher = birthPattern.matcher(ligne);
            
            if (birthMatcher.find()) {
                extractedData.put("datenaiss", birthMatcher.group(1).trim());
                extractedData.put("lieunaiss", birthMatcher.group(2).trim());
            }
            
         // Expression régulière pour le sexe
            Pattern sexPattern = Pattern.compile("(?i)SEXE[:?|.?|,?\\s]+(M|F)");
            Matcher sexMatcher = sexPattern.matcher(ligne);
            if (sexMatcher.find()) {
                extractedData.put("sexe", sexMatcher.group(1).trim());
            }
            
            
            // Expression régulière pour la profession
            ///Pattern professionPattern = Pattern.compile("(?i)PROFESSION[:?|.?\\s]+([A-Z\\s']+)");
            Pattern professionPattern = Pattern.compile("(?i)(profession|occupation)[:?|.?\\s]+([a-z\\s\\.']+)");
            Matcher professionMatcher = professionPattern.matcher(ligne);
            if (professionMatcher.find()) {
                extractedData.put("profession", professionMatcher.group(2).trim());
            }
            
       
        //    Pattern DATE_REG_PATTERN = Pattern.compile("(?i)(delivrée|livrée\\s+le|livr|vrance|date\\s+of\\s+issue)[^a-zA-Z\\d]*[:?=,\\-]*\\s*(\\d{2}/\\d{2}/\\d{4})");
            Pattern DATE_REG_PATTERN = Pattern.compile("(?i)(delivrée|livrée\\s*le|livr|vrance|date\\s+of\\s+issue)[^\\d]*\\s*(\\d{2}/\\d{2}/\\d{4})");
            Matcher issueDateMatcher = DATE_REG_PATTERN.matcher(ligne);
            if (issueDateMatcher.find()) {
                extractedData.put("datedelivrance", issueDateMatcher.group(2).trim());
            }
            
            if (ligne.toLowerCase().contains("urkina")) {
            	extractedData.put("nationalite", "BURKINABE");
            }
           
            Pattern expiryDatePattern = Pattern.compile("(?i)(expirat|expiry\\s*le|expir|vrance|ire\\s+le)[^\\d]*\\s*(\\d{2}/\\d{2}/\\d{4})");
            Matcher expiryDateMatcher = expiryDatePattern.matcher(ligne);
            if (expiryDateMatcher.find()) {
                extractedData.put("dateexpiration", expiryDateMatcher.group(2).trim());
            }
            Matcher nipMatcher = NIP_REG_PATTERN.matcher(ligne);
            if (nipMatcher.find()) {
                String nip = nipMatcher.group();
                extractedData.put("nip", nip);
            }
            
        }
        extractedData.put("autorite", "ONI OUAGA");
        extractedData.put("lieuemission", "OUAGA");
        return extractedData;
    }

    
    private static Map<String, String> extractPassportData(String ocrText) {
        Map<String, String> map = new HashMap<>();
        StringBuilder mrz = new StringBuilder();
        String normalizedText = normalizeText(ocrText);
        String[] lignes = normalizedText.split("\\R");
        map.put("type", "PASSEPORT");
        // Vérifier si le tableau de données est nul ou vide pour éviter NullPointerException
        if (lignes == null || lignes.length == 0) {
            throw new IllegalArgumentException("Le tableau de données est vide ou nul.");
        }

        for (int i = 0; i < lignes.length - 1; i++) {
            String ligne = lignes[i];
            
            // Vérifier que la ligne n'est pas nulle et la convertir en minuscule
            if (ligne == null || ligne.trim().isEmpty()) {
                continue;  // Passer à la ligne suivante si celle-ci est nulle ou vide
            }
            
            ligne = ligne.toLowerCase();  // Conversion en minuscule une seule fois

            // Traitement MRZ
            if (ligne.contains("<") && ligne.contains("<<")) {
                mrz.append("\n").append(lignes[i]);
            }

            // Extraction du numéro de pièce avec un Regex (exemple de regex pour un numéro de pièce de passeport)
            Matcher m = NUM_PIECE_REG_Pattern.matcher(ligne);
            if (m.find()) {
                map.put("numeropiece", m.group());
            }

            // Extraction du nom
            if (ligne.contains("nom") && (ligne.contains("urname") || ligne.contains("uname") || ligne.contains("su"))) {
                // Assurez-vous que l'indice suivant existe pour éviter IndexOutOfBoundsException
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("nom", lignes[i + 1]);
                }
            }

            // Extraction des prénoms
            if (ligne.contains("énoms") || ligne.contains("names")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("prenoms", lignes[i + 1]);
                }
            }

            // Extraction de la date de naissance avec une regex
            if (ligne.contains("date de naiss") || ligne.contains("te of birth")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("datenaiss", readPassportDates(lignes[i + 1]));
                }
            }

            // Extraction du lieu de naissance
            if (ligne.contains("ieu de naiss") || ligne.contains("ace of birth") || ligne.contains("ce of birth")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("lieunaiss", lignes[i + 1]);
                }
            }

            // Extraction du lieu d'émission
            if (ligne.contains("ieu d'emission") || ligne.contains("ce of issue") || ligne.contains("ission'")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("lieuemission", lignes[i + 1]);
                }
            }

            // Extraction du sexe
            if (ligne.contains("sexe") || ligne.contains("sex")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("sexe", lignes[i + 1]);
                }
            }

            // Extraction de la date de délivrance
            if (ligne.contains("vrance") || ligne.contains("date of issue")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("datedelivrance", readPassportDates(lignes[i + 1]));
                }
            }

            // Extraction de la date d'expiration
            if (ligne.contains("expirat") || ligne.contains("expiry") || ligne.contains("expir")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null) {
                    map.put("dateexpiration", readPassportDates(lignes[i + 1]));
                }
            }

            // Extraction de la nationalité
            if (ligne.contains("urkina")) {
                map.put("nationalite", "BURKINABE");
            }

            // Extraction de l'autorité de délivrance
            if (ligne.contains("torité") || ligne.contains("using aut") || ligne.contains("ssuin") || ligne.contains("ssuing auth")) {
                if (i + 1 < lignes.length && lignes[i + 1] != null && i + 2 < lignes.length && lignes[i + 2] != null) {
                    map.put("autorite", lignes[i + 1] + " " + lignes[i + 2]);
                }
            }
        }

        // Ajouter MRZ dans le map si trouvé
        if (mrz.length() > 0) {
            map.put("mrz", mrz.toString());
        }

        return map;
    }

    
    /**
     * Normalise le texte en supprimant les accents et en mettant en majuscules.
     *
     * @param text Le texte à normaliser
     * @return Le texte normalisé
     */
    private static String normalizeText(String text) {
        return text.toUpperCase()
                   .replaceAll("[ÀÁÂÃÄÅ]", "A")
                   .replaceAll("[ÈÉÊË]", "E")
                   .replaceAll("[ÌÍÎÏ]", "I")
                   .replaceAll("[ÒÓÔÕÖØ]", "O")
                   .replaceAll("[ÙÚÛÜ]", "U")
                   .replaceAll("[Ç]", "C")
                   .replaceAll("[Ñ]", "N");
    }

    private static String readPassportDates(String s) {
        // Expression régulière pour extraire la date (jour, mois et année)
        String dateReg = "(\\b\\d{1,2})\\D{0,3}\\s([a-zA-Z]+)\\s*(\\d{2,4})";

        Pattern pattern = Pattern.compile(dateReg);
        Matcher matcher = pattern.matcher(s);

        // Vérification de la correspondance
        if (matcher.find()) {
            // Récupérer le jour, le mois (en texte) et l'année
            String day = matcher.group(1);
            String monthText = matcher.group(2).toLowerCase();
            String year = matcher.group(3);

            // Conversion du mois (en texte) en mois numérique
            String monthNumber = convertMonthToNumber(monthText);
            if (monthNumber == null) {
            	System.out.println("Mois inconnu détecté dans la date: " + monthText);
                return ""; // Retourner une chaîne vide si le mois n'est pas reconnu
            }

            // Vérifier si l'année est au format 2 chiffres et la convertir en 4 chiffres si nécessaire
            if (year.length() == 2) {
                year = "20" + year; // Ajout du préfixe pour obtenir une année complète (ex : 22 -> 2022)
            }

            // Formater la date dans le format DD/MM/YYYY
            String birthDate = day + "/" + monthNumber + "/" + year;

           System.out.println("Date formatée: " + birthDate);
            return birthDate;
        }

        // Si aucune correspondance n'est trouvée, retourner une chaîne vide
        return "";
    }

    // Méthode pour convertir un mois écrit en texte en un numéro de mois
    private static String convertMonthToNumber(String monthText) {
        switch (monthText) {
            case "jan":
            case "january":
                return "01";
            case "feb":
            case "february":
                return "02";
            case "mar":
            case "march":
                return "03";
            case "apr":
            case "april":
                return "04";
            case "may":
            case "mai":
                return "05";
            case "jun":
            case "june":
                return "06";
            case "jul":
            case "july":
                return "07";
            case "aug":
            case "aout":
                return "08";
            case "sep":
            case "september":
                return "09";
            case "oct":
            case "october":
                return "10";
            case "nov":
            case "november":
                return "11";
            case "dec":
            case "december":
                return "12";
            default:
                return null; // Mois non reconnu
        }
        
        
    }

}
