package be.xplore.notifyme.service;

import java.security.Principal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.IDToken;

public interface ITokenService {

  IDToken getIdToken(Principal principal);

  KeycloakSecurityContext getSecurityContext(Principal principal);

  boolean hasRole(Principal principal, String rolename);
}
