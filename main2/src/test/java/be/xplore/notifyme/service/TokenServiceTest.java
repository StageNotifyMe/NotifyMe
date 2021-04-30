package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenServiceTest {

  @Autowired
  private TokenService tokenService;

  /*@Test
  void decodeSuccessful() {
    AccessToken idToken = Mockito.mock(AccessToken.class);
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);

    when(
        any(KeycloakAuthenticationToken.class).getAccount().getKeycloakSecurityContext().getToken())
        .thenReturn(idToken);

    assertEquals(idToken, tokenService.decodeToken(principal));
  }*/

}