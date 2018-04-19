package com.zj.sso.ui;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.profile.DefaultOAuth20ProfileScopeToAttributesFilter;
import org.apereo.cas.support.oauth.profile.OAuth20ProfileScopeToAttributesFilter;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.support.oauth.util.OAuth20Utils;
import org.apereo.cas.support.oauth.web.views.OAuth20DefaultUserProfileViewRenderer;
import org.apereo.cas.support.oauth.web.views.OAuth20UserProfileViewRenderer;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.TicketState;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.Pac4jUtils;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.J2EContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zj.sso.entity.OauthClient;
import com.zj.sso.service.OauthService;

@Configuration("oauthControlConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Controller
public class OauthController {

  private static final JsonFactory JSON_FACTORY =
      new JsonFactory(new ObjectMapper().findAndRegisterModules());
  @Autowired
  private OauthService service;

  private static final Logger LOGGER = LoggerFactory.getLogger(OauthController.class);

  @Autowired
  @Qualifier("ticketRegistry")
  private TicketRegistry ticketRegistry;

  @Autowired
  @Qualifier("servicesManager")
  private ServicesManager servicesManager;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private CasConfigurationProperties casProperties;

  @ConditionalOnMissingBean(name = "msiOauthUserProfileViewRenderer")
  @Bean
  @RefreshScope
  public OAuth20UserProfileViewRenderer oauthUserProfileViewRenderer() {
    return new OAuth20DefaultUserProfileViewRenderer(casProperties.getAuthn().getOauth());
  }

  @ConditionalOnMissingBean(name = "msiProfileScopeToAttributesFilter")
  @Bean
  public OAuth20ProfileScopeToAttributesFilter profileScopeToAttributesFilter() {
    return new DefaultOAuth20ProfileScopeToAttributesFilter();
  }

  @GetMapping(path = "/addMsiOauth")
  public void msiOauthCreate(final HttpServletRequest request, final HttpServletResponse response) {
    // ModelAndView mav = new ModelAndView("MsiOauthPage");
    OauthClient moc = new OauthClient();
    moc.setClientId(request.getParameter("clientId"));
    moc.setClientName(request.getParameter("clientName"));
    moc.setClientSecret(request.getParameter("clientSecret"));
    moc.setServiceId(request.getParameter("serviceId"));
    moc.setGrantType(request.getParameter("grantType"));
    String res = service.crateOauthService(moc);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    try (JsonGenerator jsonGenerator = getResponseJsonGenerator(response)) {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField("result", res);
      jsonGenerator.writeEndObject();
    } catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    // mav.addObject("status", res);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  @GetMapping(path = OAuth20Constants.BASE_OAUTH20_URL + "/msiOauth")
  public void msiOauthCode(final HttpServletRequest request, final HttpServletResponse response) {
    String res = request.getParameter("code");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    try (JsonGenerator jsonGenerator = getResponseJsonGenerator(response)) {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField("result", res);
      jsonGenerator.writeEndObject();
    } catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    response.setStatus(HttpServletResponse.SC_OK);
  }

  @GetMapping(path = "/msiOauthValidate")
  public ResponseEntity<String> validateToken(final HttpServletRequest request,
      final HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    final J2EContext context = Pac4jUtils.getPac4jJ2EContext(request, response);

    final String accessToken = getAccessTokenFromRequest(request);

    if (StringUtils.isBlank(accessToken)) {
      LOGGER.error("Missing [{}]", OAuth20Constants.ACCESS_TOKEN);
      return buildUnauthorizedResponseEntity(OAuth20Constants.MISSING_ACCESS_TOKEN);
    }

    final AccessToken accessTokenTicket =
        this.ticketRegistry.getTicket(accessToken, AccessToken.class);
    if (accessTokenTicket == null || accessTokenTicket.isExpired()) {
      LOGGER.error("Expired/Missing access token: [{}]", accessToken);
      return buildUnauthorizedResponseEntity(OAuth20Constants.EXPIRED_ACCESS_TOKEN);
    }

    final TicketGrantingTicket ticketGrantingTicket = accessTokenTicket.getGrantingTicket();
    if (ticketGrantingTicket == null || ticketGrantingTicket.isExpired()) {
      LOGGER.error(
          "Ticket granting ticket [{}] parenting access token [{}] has expired or is not found",
          ticketGrantingTicket, accessTokenTicket);
      this.ticketRegistry.deleteTicket(accessToken);
      return buildUnauthorizedResponseEntity(OAuth20Constants.EXPIRED_ACCESS_TOKEN);
    }

    updateAccessTokenUsage(accessTokenTicket);

    final Map<String, Object> map = writeOutProfileResponse(accessTokenTicket, context);

    finalizeProfileResponse(accessTokenTicket, map);

    // buildAccessUrlInfoResponse(accessUrl, map);

    final String value = oauthUserProfileViewRenderer().render(map, accessTokenTicket);
    return new ResponseEntity<>(value, HttpStatus.OK);
  }

  protected Map<String, Object> writeOutProfileResponse(final AccessToken accessToken,
      final J2EContext context) {
    final Principal principal = getAccessTokenAuthenticationPrincipal(accessToken, context);
    final Map<String, Object> map = new HashMap<>();
    map.put(OAuth20UserProfileViewRenderer.MODEL_ATTRIBUTE_ID, principal.getId());
    map.put(OAuth20UserProfileViewRenderer.MODEL_ATTRIBUTE_ATTRIBUTES, principal.getAttributes());
    return map;
  }

  private void finalizeProfileResponse(final AccessToken accessTokenTicket,
      final Map<String, Object> map) {
    final Service service = accessTokenTicket.getService();
    final RegisteredService registeredService = servicesManager.findServiceBy(service);
    if (registeredService instanceof OAuthRegisteredService) {
      final OAuthRegisteredService oauth = (OAuthRegisteredService) registeredService;
      map.put(OAuth20Constants.CLIENT_ID, oauth.getClientId());
      map.put(CasProtocolConstants.PARAMETER_SERVICE, service.getId());
    }
    if (null == registeredService && null != service) {
      map.put(OAuth20Constants.CLIENT_ID, service.getId());
    }
  }

  private void buildAccessUrlInfoResponse(final String accessUrl, final Map<String, Object> map) {
    Object response = this.restTemplate.getForObject(accessUrl, Object.class);
    map.put("apiResults", response);
  }

  protected Principal getAccessTokenAuthenticationPrincipal(final AccessToken accessToken,
      final J2EContext context) {
    final Service service = accessToken.getService();
    final RegisteredService registeredService = servicesManager.findServiceBy(service);

    final Principal currentPrincipal = accessToken.getAuthentication().getPrincipal();
    LOGGER.debug("Preparing user profile response based on CAS principal [{}]", currentPrincipal);

    final Principal principal = profileScopeToAttributesFilter().filter(accessToken.getService(),
        currentPrincipal, registeredService, context);
    LOGGER.debug("Created CAS principal [{}] based on requested/authorized scopes", principal);
    return principal;
  }

  private static ResponseEntity buildUnauthorizedResponseEntity(final String code) {
    final LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>(1);
    map.add(OAuth20Constants.ERROR, code);
    final String value = OAuth20Utils.jsonify(map);
    return new ResponseEntity<>(value, HttpStatus.UNAUTHORIZED);
  }

  private void updateAccessTokenUsage(final AccessToken accessTokenTicket) {
    final TicketState accessTokenState = TicketState.class.cast(accessTokenTicket);
    accessTokenState.update();
    if (accessTokenTicket.isExpired()) {
      this.ticketRegistry.deleteTicket(accessTokenTicket.getId());
    } else {
      this.ticketRegistry.updateTicket(accessTokenTicket);
    }
  }

  protected String getAccessUrlFromRequest(final AccessToken accessTokenTicket,
      final HttpServletRequest request) {
    String accessUrl = request.getParameter("accessUrl");
    Boolean flag = false;
    if (StringUtils.isBlank(accessUrl)) {
      accessUrl = "";
    }
    final Service service = accessTokenTicket.getService();
    final RegisteredService registeredService = servicesManager.findServiceBy(service);
    if (registeredService instanceof OAuthRegisteredService) {
      final OAuthRegisteredService oauth = (OAuthRegisteredService) registeredService;
      Set<String> accessRes = oauth.getProperties().get("accessRes").getValues();
      for (String str : accessRes) {
        if (accessUrl.contains(str)) {
          return accessUrl;
        }
      }
    }
    return null;
  }

  protected String getAccessTokenFromRequest(final HttpServletRequest request) {
    String accessToken = request.getParameter(OAuth20Constants.ACCESS_TOKEN);
    if (StringUtils.isBlank(accessToken)) {
      final String authHeader = request.getHeader(HttpConstants.AUTHORIZATION_HEADER);
      if (StringUtils.isNotBlank(authHeader) && authHeader.toLowerCase()
          .startsWith(OAuth20Constants.BEARER_TOKEN.toLowerCase() + ' ')) {
        accessToken = authHeader.substring(OAuth20Constants.BEARER_TOKEN.length() + 1);
      }
    }
    LOGGER.debug("[{}]: [{}]", OAuth20Constants.ACCESS_TOKEN, accessToken);
    return accessToken;
  }

  @GetMapping(path = "/delMsiOauth")
  public void msiOauthDel(final HttpServletRequest request, final HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    String serviceid = request.getParameter("serviceId");
    String res = "";
    try {
      service.delOauthService(serviceid);
    } catch (Exception e) {
      if (null == servicesManager.findServiceBy(serviceid)) {
        res = "success";
        servicesManager.load();
      } else {
        res = "failed";
      }
    }

    try (JsonGenerator jsonGenerator = getResponseJsonGenerator(response)) {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField("result", res);
      jsonGenerator.writeEndObject();
    } catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    response.setStatus(HttpServletResponse.SC_OK);
  }


  protected JsonGenerator getResponseJsonGenerator(final HttpServletResponse response)
      throws IOException {
    return JSON_FACTORY.createGenerator(response.getWriter());
  }

}
