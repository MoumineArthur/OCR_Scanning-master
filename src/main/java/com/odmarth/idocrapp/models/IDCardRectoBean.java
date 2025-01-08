package com.odmarth.idocrapp.models;

import lombok.ToString;

@ToString
public class IDCardRectoBean {

	private String nip;
	private String cardNumber;
	private String firstName;
	private String lastName;
	private String   birthDay;
	private String birthPlace;
	private String gender;
	private String profession;
	private String cardDeliverDate;
	private String cardExpireDate;
	private String autority;
	private String issuePlace; //Lieu d'etablissement
	private String nationality;
	
	public String getNip() {
		return nip;
	}
	public void setNip(String nip) {
		this.nip = nip;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getBirthPlace() {
		return birthPlace;
	}
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getCardDeliverDate() {
		return cardDeliverDate;
	}
	public void setCardDeliverDate(String cardDeliverDate) {
		this.cardDeliverDate = cardDeliverDate;
	}
	public String getCardExpireDate() {
		return cardExpireDate;
	}
	public void setCardExpireDate(String cardExpireDate) {
		this.cardExpireDate = cardExpireDate;
	}
	public String getAutority() {
		return autority;
	}
	public void setAutority(String autority) {
		this.autority = autority;
	}
	public String getIssuePlace() {
		return issuePlace;
	}
	public void setIssuePlace(String issuePlace) {
		this.issuePlace = issuePlace;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	

}
