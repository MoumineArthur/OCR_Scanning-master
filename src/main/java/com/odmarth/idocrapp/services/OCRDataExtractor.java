package com.odmarth.idocrapp.services;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.odmarth.idocrapp.models.IDCardRectoBean;
import com.odmarth.idocrapp.models.IDCardVersoBean;

public class OCRDataExtractor {

	private static final Logger LOG = LoggerFactory.getLogger(OCRDataExtractor.class);
	 
	 public static IDCardRectoBean extractRectoBean(String rectoText) {
			
			Map<String, String> map = CarteIdentiteExtractor.extractRectoData(rectoText);
			IDCardRectoBean idCardRectoBean = new IDCardRectoBean();

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
			

			Map<String, String> map  = CarteIdentiteExtractor.extractVersoData(versoText);
			IDCardVersoBean idCardVersoBean = new IDCardVersoBean();
						
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
