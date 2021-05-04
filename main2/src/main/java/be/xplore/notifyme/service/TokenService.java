package be.xplore.notifyme.service;

import be.xplore.notifyme.exception.TokenHandlerException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Class used for handling and parsing Oauth 2.0 tokens.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

  @Value("${keycloak.resource}")
  private String clientId;
  @Value("${keycloak.credentials.secret}")
  private String clientSecret;
  @Value("${userservice.login.url}")
  private String tokenUri;
  private final RestTemplate restTemplate;
  @Qualifier("xformRequest")
  @Autowired
  private HttpHeaders httpXformHeader;

  /**
   * Uses a principal object to create an IDToken.
   *
   * @param principal contains information about the user calling the method.
   * @return an IDToken containing user information.
   */
  public IDToken getIDToken(Principal principal) {
    try {
      var keycloakAuthenticationToken =
          (KeycloakAuthenticationToken) principal;
      return keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext()
          .getToken();
    } catch (Exception e) {
      throw new TokenHandlerException(
          String.format("Could not extract IDToken from principal object: %s", e.getMessage()));
    }
  }

  /**
   * Uses a principal to get KeycloakSecurityContext.
   *
   * @param principal contains information about the user calling the method.
   * @return a KeycloakSecurityContext.
   */
  public KeycloakSecurityContext getSecurityContext(Principal principal) {
    try {
      var keycloakAuthenticationToken =
          (KeycloakAuthenticationToken) principal;
      return keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext();
    } catch (Exception e) {
      throw new TokenHandlerException(
          String.format("Could not extract IDToken from principal object: %s", e.getMessage()));
    }
  }

  /**
   * Gets a service account admin access token so spring can execute management actions on
   * keycloak.
   *
   * @return ReponseEntity that if successful contains the accesstoken.
   */
  public ResponseEntity<String> getAdminAccesstoken() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");
    map = addAuthorization(map);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }

  /**
   * Methods takes a multimap and adds client_id and client_secret to it.
   *
   * @param map containing values for a body.
   * @return the map with the client_id and client_secret values added.
   */
  public MultiValueMap<String, String> addAuthorization(MultiValueMap<String, String> map) {
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    return map;
  }
}
