package be.xplore.notifyme.service;

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