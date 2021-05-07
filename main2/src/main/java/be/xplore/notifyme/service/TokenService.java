package be.xplore.notifyme.service;

import be.xplore.notifyme.exception.TokenHandlerException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Class used for handling and parsing Oauth 2.0 tokens.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

  @Value("${keycloak.resource}")
  String clientId;

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

  /**
   * Checks if a principal contains a certain role.
   *
   * @param principal gotten from the request context.
   * @param rolename  the name of the role to check.
   * @return if the principal contains the role.
   */
  public boolean hasRole(Principal principal, String rolename) {
    try {
      var keycloakAuthenticationToken =
          (KeycloakAuthenticationToken) principal;
      var roles = keycloakAuthenticationToken.getAccount().getRoles();
      return roles.contains(rolename);
    } catch (Exception e) {
      throw new TokenHandlerException(
          String.format("Could not extract IDToken from principal object: %s", e.getMessage()));
    }

  }
}
