/*
* Copyright &copy; <a href="http://www.fiberhome.com">·é»ð</a> All rights reserved.
*/
package com.zj.sso.entity;

import java.util.Date;


/**
 * User.
 * 
 * @author Administrator
 * @timestamp 2018-03-16 11:20:02
 */
public class UserBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private String acount;
	private String email;
	private String firstname;
	private String lastname;

	private String status;
	private String addr1;
	private String addr2;
	private String city;
	private String state;
	private String zip;
	private String country;
	private String phone;
	private String languageCode;
	private String password;
	private String orginfo;
	private String categoria;
	private String categoriaName;
	private String sid;
	private String saNo;
	private String abbreviation;
	private String titleDescription;
	private String deviceType;
	private String jpushId;
	private Date lastLoginDate;
	private Date lastLoginOutDate;
	private String receivePush;
	protected String passwordSalt;
	// 新添加的用户编号
	private String userNumber;
	private String personId;//人员ID
	private String type;//新增人员类型， 分为 医院内部用户(HOS) 和 医院外部用户(OUT)
	public String getAcount() {
		return acount;
	}
	public void setAcount(String acount) {
		this.acount = acount;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	public String getAddr2() {
		return addr2;
	}
	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOrginfo() {
		return orginfo;
	}
	public void setOrginfo(String orginfo) {
		this.orginfo = orginfo;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getCategoriaName() {
		return categoriaName;
	}
	public void setCategoriaName(String categoriaName) {
		this.categoriaName = categoriaName;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getSaNo() {
		return saNo;
	}
	public void setSaNo(String saNo) {
		this.saNo = saNo;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	public String getTitleDescription() {
		return titleDescription;
	}
	public void setTitleDescription(String titleDescription) {
		this.titleDescription = titleDescription;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getJpushId() {
		return jpushId;
	}
	public void setJpushId(String jpushId) {
		this.jpushId = jpushId;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Date getLastLoginOutDate() {
		return lastLoginOutDate;
	}
	public void setLastLoginOutDate(Date lastLoginOutDate) {
		this.lastLoginOutDate = lastLoginOutDate;
	}
	public String getReceivePush() {
		return receivePush;
	}
	public void setReceivePush(String receivePush) {
		this.receivePush = receivePush;
	}
	public String getPasswordSalt() {
		return passwordSalt;
	}
	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
	public String getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	


}