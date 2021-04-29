package be.xplore.notifyme.service;

import be.xplore.notifyme.exception.TokenHandlerException;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.stereotype.Service;

/**
 * Class used for handling and parsing Oauth 2.0 tokens.
 */
@Service
@Slf4j
public class TokenService {

  /**
   * Uses a principal object to create an IDToken.
   *
   * @param principal contains information about the user calling the method.
   * @return an IDToken containing user information.
   */
  public IDToken decodeToken(Principal principal) {
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
}
