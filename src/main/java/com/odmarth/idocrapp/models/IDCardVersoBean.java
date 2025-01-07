package com.odmarth.idocrapp.models;

import lombok.Data;

@Data
public class IDCardVersoBean {
	private String province;
	private String departement;
	private String residence;
	private String secteur;
	private String firstNamePAP;
	private String lastNamePAP;
	private String phonePAP;
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getDepartement() {
		return departement;
	}
	public void setDepartement(String departement) {
		this.departement = departement;
	}
	public String getResidence() {
		return residence;
	}
	public void setResidence(String residence) {
		this.residence = residence;
	}
	public String getSecteur() {
		return secteur;
	}
	public void setSecteur(String secteur) {
		this.secteur = secteur;
	}
	public String getFirstNamePAP() {
		return firstNamePAP;
	}
	public void setFirstNamePAP(String firstNamePAP) {
		this.firstNamePAP = firstNamePAP;
	}
	public String getLastNamePAP() {
		return lastNamePAP;
	}
	public void setLastNamePAP(String secondNamePAP) {
		this.lastNamePAP = secondNamePAP;
	}
	public String getPhonePAP() {
		return phonePAP;
	}
	public void setPhonePAP(String phonePAP) {
		this.phonePAP = phonePAP;
	}
	
	
}
