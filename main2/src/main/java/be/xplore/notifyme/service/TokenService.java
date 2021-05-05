package be.xplore.notifyme.service;

import be.xplore.notifyme.exception.TokenHandlerException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.stereotype.Service;

/**
 * Class used for handling and parsing Oauth 2.0 tokens.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

  /**
   * Uses a principal object to create an IDToken.
   *
   * @param principal contains information about the user calling the method.
   * @return an IDToken containing user information.
   */
  public IDToken getIdToken(Principal principal) {
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
}
