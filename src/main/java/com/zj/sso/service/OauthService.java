package com.zj.sso.service;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apereo.cas.authentication.principal.DefaultPrincipalAttributesRepository;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.DefaultRegisteredServiceUsernameProvider;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.util.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.zj.sso.entity.OauthClient;

@Configuration("msiOauthConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Service
public class OauthService extends WebMvcConfigurerAdapter {

  @Autowired
  @Qualifier("servicesManager")
  private ServicesManager servicesManager;

  @Autowired
  private RestTemplateBuilder builder;

  @Bean
  public RestTemplate restTemplate() throws Exception {
    RestTemplate restTemplate = null;
    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

    SSLContext sslContext =
        SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();

    requestFactory.setHttpClient(httpClient);

    restTemplate = new RestTemplate(requestFactory);
    // restTemplate = builder.build();
    return restTemplate;
  }

  public String crateOauthService(OauthClient moc) {
    OAuthRegisteredService service = new OAuthRegisteredService();
    DefaultRegisteredServiceProperty propertie = new DefaultRegisteredServiceProperty();
    // ReturnRestfulAttributeReleasePolicy rrarp = new ReturnRestfulAttributeReleasePolicy();
    // rrarp.setEndpoint("http://dev.skymsi.com/get_json");
    ReturnAllAttributeReleasePolicy raarp = new ReturnAllAttributeReleasePolicy();
    raarp.setPrincipalAttributesRepository(new DefaultPrincipalAttributesRepository());
    raarp.setAuthorizedToReleaseCredentialPassword(true);
    raarp.setAuthorizedToReleaseProxyGrantingTicket(true);
    raarp.setExcludeDefaultAttributes(false);
    raarp.setPrincipalIdAttribute("username");
    DefaultRegisteredServiceAccessStrategy drsas = new DefaultRegisteredServiceAccessStrategy();
    drsas.setRequireAllAttributes(true);
    drsas.setEnabled(true);
    drsas.setSsoEnabled(true);
    service.setId(Math.abs(RandomUtils.getInstanceNative().nextLong()));
    service.setClientId(moc.getClientId());
    service.setClientSecret(moc.getClientSecret());
    service.setServiceId(moc.getServiceId());
    service.setName(moc.getClientName());
    service.setGenerateRefreshToken(true);
    service.setJsonFormat(true);
    service.setAttributeReleasePolicy(raarp);
    service.setTheme("oauth");
    service.setUsernameAttributeProvider(new DefaultRegisteredServiceUsernameProvider());
    service.setLogoutType(RegisteredService.LogoutType.FRONT_CHANNEL);
    service.setAccessStrategy(drsas);
    Set<String> grantTypes = new HashSet<String>();
    String grantType = moc.getGrantType();
    if (grantType.equals("password")) {
      Set<String> authHandler = new HashSet<String>();
      authHandler.add("DatabaseAuthenticationHandler");
      service.setRequiredHandlers(authHandler);
    }
    if (grantType.equals("refresh_token")) {
      grantTypes.add(grantType);
    } else {
      grantTypes.add(grantType);
      grantTypes.add("refresh_token");
    }
    if (grantType.equals("authorization_code")) {
      service.setBypassApprovalPrompt(false);
    }
    service.setSupportedGrantTypes(grantTypes);
    servicesManager.save(service);
    servicesManager.load();
    return "success";
  }

  public void delOauthService(String serviceid) {

    RegisteredService svc = servicesManager.findServiceBy(serviceid);
    servicesManager.delete(svc);

  }


}
