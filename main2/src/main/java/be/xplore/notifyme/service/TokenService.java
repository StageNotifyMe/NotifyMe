package be.xplore.notifyme.service;

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
    KeycloakAuthenticationToken keycloakAuthenticationToken =
        (KeycloakAuthenticationToken) principal;
    return keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext()
        .getToken();
  }
}
