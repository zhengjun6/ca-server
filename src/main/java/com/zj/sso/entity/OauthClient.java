package com.zj.sso.entity;

public class OauthClient {
  
  String ClientId;
  String ClientSecret;
  String ServiceId;
  String ClientName;
  String grantType;
  String accessRes;
  
  public String getAccessRes() {
    return accessRes;
  }
  public void setAccessRes(String accessRes) {
    this.accessRes = accessRes;
  }
  public String getGrantType() {
    return grantType;
  }
  public void setGrantType(String grantType) {
    this.grantType = grantType;
  }
  public String getClientId() {
    return ClientId;
  }
  public void setClientId(String clientId) {
    ClientId = clientId;
  }
  public String getClientSecret() {
    return ClientSecret;
  }
  public void setClientSecret(String clientSecret) {
    ClientSecret = clientSecret;
  }
  public String getServiceId() {
    return ServiceId;
  }
  public void setServiceId(String serviceId) {
    ServiceId = serviceId;
  }
  public String getClientName() {
    return ClientName;
  }
  public void setClientName(String clientName) {
    ClientName = clientName;
  }
  
}
