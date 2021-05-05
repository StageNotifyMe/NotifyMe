package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IUserRepo;
import com.c4_soft.springaddons.security.oauth2.test.annotations.OidcStandardClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.account.UserRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class UserServiceTest {

  @Autowired
  private UserService userService;
  @MockBean
  private KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private IUserRepo userRepo;


  @Test
  void getAllUserInfo() {
    final var decodedReturn = new ArrayList<UserRepresentation>();
    when(keycloakCommunicationService.getAllUserInfoRest(anyString()))
        .thenReturn(decodedReturn);
    assertEquals(decodedReturn, userService.getAllUserInfo("specialToken"));
  }

  @Test
  void getAllUserInfoCommunicationFail() {
    doThrow(CrudException.class).when(keycloakCommunicationService).getAllUserInfoRest(anyString());

    assertThrows(CrudException.class, () -> {
      userService.getAllUserInfo("specialToken");
    });
  }

  @Test
  @WithMockKeycloakAuth(authorities = {"admin"}, oidc = @OidcStandardClaims(
      email = "test@test.com",
      emailVerified = true,
      nickName = "ElTestor",
      preferredUsername = "admin"))
  void getUserInfoAsAdmin() {
    var userRep = new UserRepresentation();
    KeycloakAuthenticationToken keycloakPrincipal = getKeycloakPrincipal();
    KeycloakSecurityContext keycloakSecurityContext = Mockito.mock(KeycloakSecurityContext.class);
    when(tokenService.getIdToken(any()))
        .thenReturn(keycloakPrincipal.getAccount().getKeycloakSecurityContext().getIdToken());
    when(tokenService.getSecurityContext(any()))
        .thenReturn(keycloakSecurityContext);
    when(keycloakCommunicationService.getUserInfo(anyString())).thenReturn(userRep);
    AuthorizationContext mockAuthContext = Mockito.mock(AuthorizationContext.class);
    when((keycloakSecurityContext.getAuthorizationContext())).thenReturn(mockAuthContext);
    when(mockAuthContext.hasScopePermission("admin")).thenReturn(true);

    assertEquals(userRep, userService.getUserInfo("Testuser", keycloakPrincipal));
  }

  @Test
  @WithMockKeycloakAuth(oidc = @OidcStandardClaims(
      email = "test@test.com",
      emailVerified = true,
      nickName = "ElTestor",
      preferredUsername = "Test"))
  void getUserInfoOfOwn() {
    var userRep = new UserRepresentation();
    KeycloakAuthenticationToken keycloakPrincipal = getKeycloakPrincipal();
    KeycloakSecurityContext keycloakSecurityContext = Mockito.mock(KeycloakSecurityContext.class);
    when(tokenService.getIdToken(any()))
        .thenReturn(keycloakPrincipal.getAccount().getKeycloakSecurityContext().getIdToken());
    when(tokenService.getSecurityContext(any()))
        .thenReturn(keycloakSecurityContext);
    when(keycloakCommunicationService.getUserInfo(anyString())).thenReturn(userRep);
    AuthorizationContext mockAuthContext = Mockito.mock(AuthorizationContext.class);
    when((keycloakSecurityContext.getAuthorizationContext())).thenReturn(mockAuthContext);
    when(mockAuthContext.hasScopePermission("admin")).thenReturn(true);

    assertEquals(userRep, userService.getUserInfo("Test", keycloakPrincipal));
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getUserInfoOfOther() {
    var userRep = new UserRepresentation();
    KeycloakAuthenticationToken keycloakPrincipal = getKeycloakPrincipal();
    KeycloakSecurityContext keycloakSecurityContext = Mockito.mock(KeycloakSecurityContext.class);
    when(tokenService.getIdToken(any()))
        .thenReturn(keycloakPrincipal.getAccount().getKeycloakSecurityContext().getIdToken());
    when(tokenService.getSecurityContext(any()))
        .thenReturn(keycloakSecurityContext);
    when(keycloakCommunicationService.getUserInfo(anyString())).thenReturn(userRep);
    AuthorizationContext mockAuthContext = Mockito.mock(AuthorizationContext.class);
    when((keycloakSecurityContext.getAuthorizationContext())).thenReturn(mockAuthContext);
    when(mockAuthContext.hasScopePermission("admin")).thenReturn(false);

    assertThrows(UnauthorizedException.class, () -> {
      userService.getUserInfo("OtherUser", keycloakPrincipal);
    });
  }

  private KeycloakAuthenticationToken getKeycloakPrincipal() {
    return (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getUserFromPrincipal() {
    KeycloakAuthenticationToken keycloakPrincipal = getKeycloakPrincipal();
    User foundUser = new User();
    when(tokenService.getIdToken(any()))
        .thenReturn(keycloakPrincipal.getAccount().getKeycloakSecurityContext().getIdToken());
    when(userRepo.findById(anyString())).thenReturn(Optional.of(foundUser));
    assertEquals(foundUser, userService.getUserFromPrincipal(keycloakPrincipal));
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getNonexistingUserFromPrincipal() {
    KeycloakAuthenticationToken keycloakPrincipal = getKeycloakPrincipal();
    when(tokenService.getIdToken(any()))
        .thenThrow(new RuntimeException("Could not get id token from principal."));
    assertThrows(RuntimeException.class, () -> {
      userService.getUserFromPrincipal(keycloakPrincipal);
    });
  }

  @Test
  void getUser() {
    var returnUser = new User();
    when(userRepo.findById(anyString())).thenReturn(Optional.of(returnUser));
    assertEquals(returnUser, userService.getUser("testId"));
  }

  @Test
  void getUserNotFound() {
    when(userRepo.findById(anyString())).thenThrow(new CrudException("Could not get user"));
    assertThrows(CrudException.class,()->userService.getUser("testId"));
  }

  void getUserInfoAndSendVerification
}