package be.xplore.notifyme.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.AvailableLanguages;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.dto.RelevantClientInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IUserRepo;
import be.xplore.notifyme.services.implementations.CommunicationPreferenceService;
import be.xplore.notifyme.services.implementations.KeycloakCommunicationService;
import be.xplore.notifyme.services.implementations.TokenService;
import be.xplore.notifyme.services.implementations.UserService;
import com.c4_soft.springaddons.security.oauth2.test.annotations.OidcStandardClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = {UserService.class})
class UserServiceTest {

  @Autowired
  private UserService userService;
  @MockBean
  private KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private IUserRepo userRepo;
  @MockBean
  private CommunicationPreferenceService communicationPreferenceService;

  private void mockSave() {
    when(userRepo.save(any())).thenAnswer(new Answer<User>() {
      @Override
      public User answer(InvocationOnMock invocation) throws Throwable {
        return (User) invocation.getArgument(0);
      }
    });
  }


  @Test
  void getAllUserInfo() {
    final var decodedReturn = new ArrayList<UserRepresentation>();
    when(keycloakCommunicationService.getAllUserInfoRest(anyString()))
        .thenReturn(decodedReturn);
    assertEquals(decodedReturn, userService.getAllUserInfo());
  }

  @Test
  void getAllUserInfoCommunicationFail() {
    doThrow(CrudException.class).when(keycloakCommunicationService).getAllUserInfoRest(anyString());
    when(keycloakCommunicationService.getAdminAccesstoken()).thenReturn("admintoken");
    assertThrows(CrudException.class, () -> {
      userService.getAllUserInfo();
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
    mockKeycloakSecurityContext(userRep, true);

    assertEquals(userRep, userService.getUserInfo("Testuser", getKeycloakPrincipal()));
  }

  @Test
  @WithMockKeycloakAuth(oidc = @OidcStandardClaims(
      email = "test@test.com",
      emailVerified = true,
      nickName = "ElTestor",
      preferredUsername = "Test"))
  void getUserInfoOfOwn() {
    var userRep = new UserRepresentation();
    mockKeycloakSecurityContext(userRep, false);

    assertEquals(userRep, userService.getUserInfo("Test", getKeycloakPrincipal()));
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
    var keycloakPrincipal = getKeycloakPrincipal();
    mockKeycloakSecurityContext(userRep, false);
    assertThrows(UnauthorizedException.class,
        () -> userService.getUserInfo("OtherUser", keycloakPrincipal));
  }

  private void mockKeycloakSecurityContext(UserRepresentation userRep,
                                           Boolean hasRequiredPermission) {
    KeycloakAuthenticationToken keycloakPrincipal = getKeycloakPrincipal();

    KeycloakSecurityContext keycloakSecurityContext = Mockito.mock(KeycloakSecurityContext.class);
    when(tokenService.getIdToken(any()))
        .thenReturn(keycloakPrincipal.getAccount().getKeycloakSecurityContext().getIdToken());
    when(tokenService.getSecurityContext(any()))
        .thenReturn(keycloakSecurityContext);
    when(keycloakCommunicationService.getUserInfoUsername(anyString())).thenReturn(userRep);
    AuthorizationContext mockAuthContext = Mockito.mock(AuthorizationContext.class);
    when((keycloakSecurityContext.getAuthorizationContext())).thenReturn(mockAuthContext);
    when(tokenService.hasRole(any(), anyString())).thenReturn(hasRequiredPermission);

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
    assertThrows(RuntimeException.class, () -> userService.getUserFromPrincipal(keycloakPrincipal));
  }

  @Test
  void getUser() {
    var returnUser = new User("UserId", "username");
    when(userRepo.findById(anyString())).thenReturn(Optional.of(returnUser));
    assertEquals(returnUser, userService.getUser("testId"));
  }

  @Test
  void getUserIncOrganisations() {
    var returnUser = new User("UserId", "username");
    when(userRepo.findByIdIncOrganisations(anyString())).thenReturn(returnUser);
    assertEquals(returnUser, userService.getUserIncOrganisations("testId"));
  }

  @Test
  void getUserNotFound() {
    when(userRepo.findById(anyString())).thenReturn(Optional.empty());
    assertThrows(CrudException.class, () -> userService.getUser("testId"));
  }


  private void getUserInfoAndSendVerification() {
    var userRep = new UserRepresentation();
    when(keycloakCommunicationService.getUserInfoUsername(anyString())).thenReturn(userRep);
    doNothing().when(keycloakCommunicationService).sendEmailVerificationRequest(anyString());
  }

  private void getUserInfoAndSendVerificationFail() {
    var userRep = new UserRepresentation();
    userRep.setId("testUser");
    when(keycloakCommunicationService.getUserInfoUsername(anyString())).thenReturn(userRep);
    doThrow(new CrudException("Could not send request to keycloak"))
        .when(keycloakCommunicationService).sendEmailVerificationRequest(anyString());
  }

  @Test
  void register() {
    var registerDto = new UserRegistrationDto();
    registerDto.setUsername("TestUsername");
    doNothing().when(keycloakCommunicationService).register(any());
    getUserInfoAndSendVerification();
    when(userRepo.save(any())).thenReturn(new User());
    assertDoesNotThrow(() -> userService.register(registerDto));
  }

  @Test
  void registerFail() {
    doNothing().when(keycloakCommunicationService).register(any());
    getUserInfoAndSendVerification();
    when(userRepo.save(any()))
        .thenThrow(new CrudException("User could not be saved to repository"));
    var registerDto = new UserRegistrationDto();
    assertThrows(SaveToDatabaseException.class, () ->
        userService.register(registerDto));
  }

  @Test
  void registerFailVerificationMail() {
    doNothing().when(keycloakCommunicationService).register(any());
    getUserInfoAndSendVerificationFail();
    when(userRepo.save(any())).thenReturn(new User());
    var registerDto = new UserRegistrationDto();
    assertThrows(SaveToDatabaseException.class, () ->
        userService.register(registerDto));
  }

  @Test
  void getUserInfoAndSendVerificationEmailFailEmail() {
    final UserRepresentation userRepresentation = mock(UserRepresentation.class);
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("firstname", "lastname", "email@mail.com", "+32123456789",
            "username", "password");
    when(keycloakCommunicationService.getUserInfoUsername(anyString()))
        .thenReturn(userRepresentation);
    when(userRepresentation.getId()).thenReturn("id");
    doThrow(CrudException.class).when(keycloakCommunicationService)
        .sendEmailVerificationRequest("id");
    doThrow(CrudException.class).when(userRepo)
        .save(any());
    doNothing().when(keycloakCommunicationService).register(any(UserRegistrationDto.class));

    assertThrows(SaveToDatabaseException.class, () -> userService.register(userRegistrationDto));
  }

  @Test
  void getUsers() {
    var users = new ArrayList<User>();
    when(userService.getUsers()).thenReturn(users);
    assertEquals(users, userService.getUsers());
  }

  @Test
  void getUsersNotWorking() {
    when(userService.getUsers()).thenThrow(new RuntimeException("Could not get users"));
    assertThrows(CrudException.class, () -> userService.getUsers());
  }

  @Test
  void grantUserRoleSuccessful() {
    final RelevantClientInfoDto relevantClientInfoDto = new RelevantClientInfoDto("id", "clientid");
    final RoleRepresentation roleRepresentation = new RoleRepresentation();

    when(keycloakCommunicationService.getClient(anyString())).thenReturn(relevantClientInfoDto);
    when(keycloakCommunicationService.getClientRole(anyString(), anyString()))
        .thenReturn(roleRepresentation);
    doNothing().when(keycloakCommunicationService)
        .giveUserRole(anyString(), eq(roleRepresentation), anyString());

    assertDoesNotThrow(() -> userService.grantUserRole("userid", "rolename"));
  }

  @Test
  void grantUserRoleFailA() {
    doThrow(CrudException.class).when(keycloakCommunicationService).getClient(anyString());

    assertThrows(CrudException.class, () -> userService.grantUserRole("userid", "rolename"));
  }

  @Test
  void grantUserRoleFailB() {
    final RelevantClientInfoDto relevantClientInfoDto = new RelevantClientInfoDto("id", "clientid");

    when(keycloakCommunicationService.getClient(anyString())).thenReturn(relevantClientInfoDto);
    doThrow(CrudException.class).when(keycloakCommunicationService)
        .getClientRole(anyString(), anyString());

    assertThrows(CrudException.class, () -> userService.grantUserRole("userid", "rolename"));
  }

  @Test
  void grantUserRoleFailC() {
    final RelevantClientInfoDto relevantClientInfoDto = new RelevantClientInfoDto("id", "clientid");
    final RoleRepresentation roleRepresentation = new RoleRepresentation();

    when(keycloakCommunicationService.getClient(anyString())).thenReturn(relevantClientInfoDto);
    when(keycloakCommunicationService.getClientRole(anyString(), anyString()))
        .thenReturn(roleRepresentation);
    doThrow(CrudException.class).when(keycloakCommunicationService)
        .giveUserRole(anyString(), any(RoleRepresentation.class), anyString());

    assertThrows(CrudException.class, () -> userService.grantUserRole("userid", "rolename"));
  }

  @Test
  void updateUser() {
    var user = new User();
    when(userRepo.save(any())).thenReturn(user);

    assertEquals(user, userService.updateUser(user));
  }

  @Test
  void updateUserFail() {
    var testUser = new User();

    when(userRepo.save(any())).thenThrow(new CrudException("Could not update user."));

    assertThrows(CrudException.class, () ->
        userService.updateUser(testUser));
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getUserFromPrincipalIncOrganisations() {
    var mockToken = mock(IDToken.class);
    when(tokenService.getIdToken(any())).thenReturn(mockToken);
    when(mockToken.getSubject()).thenReturn("userId");
    var mockUser = mock(User.class);
    when(userRepo.findByIdIncOrganisations("userId")).thenReturn(mockUser);

    assertEquals(mockUser,
        userService.getUserFromprincipalIncOrganisations(getKeycloakPrincipal()));
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getUserFromPrincipalIncTeamApplications() {
    var mockToken = mock(IDToken.class);
    when(tokenService.getIdToken(any())).thenReturn(mockToken);
    when(mockToken.getSubject()).thenReturn("userId");
    var mockUser = mock(User.class);
    when(userRepo.findByIdIncTeamApplications("userId")).thenReturn(mockUser);

    assertEquals(mockUser,
        userService.getUserFromPrincipalIncTeamApplications(getKeycloakPrincipal()));
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getUserFromPrincipalIncAppliedUsers() {
    var mockToken = mock(IDToken.class);
    when(tokenService.getIdToken(any())).thenReturn(mockToken);
    when(mockToken.getSubject()).thenReturn("userId");
    var mockUser = mock(User.class);
    when(userRepo.findByIdIncAppliedUsers("userId")).thenReturn(mockUser);

    assertEquals(mockUser,
        userService.getUserFromPrincipalIncAppliedUsers(getKeycloakPrincipal()));
  }

  @Test
  @WithMockKeycloakAuth(authorities = "user",
      oidc = @OidcStandardClaims(
          email = "test@test.com",
          emailVerified = true,
          nickName = "ElTestor",
          preferredUsername = "Test"))
  void getUserIdFromPrincipal() {
    var mockToken = mock(IDToken.class);
    when(tokenService.getIdToken(any())).thenReturn(mockToken);
    when(mockToken.getSubject()).thenReturn("userId");
    assertEquals("userId", userService.getUserIdFromPrincipal(getKeycloakPrincipal()));
  }

  @Test
  void updateAccountInfo() {
    mockSave();
    mockUpdateUserRepresentation();
    mockUpdateUser();

    //Only simple updates
    assertDoesNotThrow(() -> {
      userService
          .updateAccountInfo("userId", "username", "firstName2", "lastName2", "email@mail.com",
              "+32123456789", "EN");
    });
    //Change preferedLanguage
    assertDoesNotThrow(() -> {
      userService
          .updateAccountInfo("userId", "username", "firstName2", "lastName2", "email@mail.com",
              "+32123456789", "NL");
    });
    //Change email
    assertDoesNotThrow(() -> {
      userService
          .updateAccountInfo("userId", "username", "firstName2", "lastName2", "email2@mail.com",
              "+32123456789", "EN");
    });

    //Change phone
    userService
        .updateAccountInfo("userId", "username", "firstName2", "lastName2", "email@mail.com",
            "+32123456780", "EN");

  }

  private void mockUpdateUserRepresentation() {
    when(keycloakCommunicationService.getUserInfoId(anyString()))
        .thenReturn(getDummyUserRepresentation());
    doNothing().when(keycloakCommunicationService)
        .updateUserInfo(any(), anyBoolean(), anyBoolean());
  }

  private void mockUpdateUser() {
    var user = User.builder().userId("userId").userName("username").preferedLanguage(
        AvailableLanguages.EN).build();
    when(userRepo.findById(anyString())).thenReturn(Optional.of(user));
  }

  private UserRepresentation getDummyUserRepresentation() {
    var userRep = new UserRepresentation();
    userRep.setId("userId");
    userRep.setUsername("username");
    userRep.setFirstName("firstName");
    userRep.setLastName("lastName");
    userRep.setEmail("email@mail.com");
    userRep.setEmailVerified(true);
    var attributes = getAttributesForDummyUserRep();
    userRep.setAttributes(attributes);
    return userRep;
  }

  private HashMap<String, List<String>> getAttributesForDummyUserRep() {
    var attributes = new HashMap<String, List<String>>();
    attributes.put("phone_number", List.of("+32123456789"));
    attributes.put("phone_number_verification_code", List.of("code"));
    attributes.put("phone_number_verified", List.of("true"));
    return attributes;
  }
}