package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.xplore.notifyme.exception.TokenHandlerException;
import com.c4_soft.springaddons.security.oauth2.test.annotations.OidcStandardClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class TokenServiceTest {

  @Autowired
  private TokenService tokenService;

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getIdTokenSuccessful() {


    var result = tokenService.getIdToken(getKeycloakPrincipal());
    assertEquals("Test", result.getPreferredUsername());
    assertEquals("ElTestor", result.getNickName());
    assertEquals("test@test.com", result.getEmail());
    assertEquals("user", result.getSubject());
    assertTrue(result.getEmailVerified());
  }

  @Test
  void getIdTokenNoAuthorization() {
    assertThrows(TokenHandlerException.class, () -> {
      tokenService.getIdToken(getKeycloakPrincipal());
    });
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getSecurityContextSuccessful() {
    var result = tokenService.getSecurityContext(getKeycloakPrincipal());
    assertTrue(result.getToken().getRealmAccess().getRoles().contains("user"));
    assertFalse(result.getToken().getRealmAccess().getRoles().contains("admin"));
  }

  @Test
  void getSecurityContextNoAuthorization() {
    assertThrows(TokenHandlerException.class, () -> {
      tokenService.getSecurityContext(getKeycloakPrincipal());
    });
  }


  private KeycloakAuthenticationToken getKeycloakPrincipal() {
    return (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }

}