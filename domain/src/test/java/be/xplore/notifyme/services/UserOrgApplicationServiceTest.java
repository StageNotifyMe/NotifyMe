package be.xplore.notifyme.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.exception.OrgApplicationNotFoundException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import be.xplore.notifyme.services.security.OrganisationSecurityService;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = {UserOrgApplicationService.class})
@Import(RestConfig.class)
class UserOrgApplicationServiceTest {

  @Autowired
  private UserOrgApplicationService userOrgApplicationService;
  @MockBean
  private UserService userService;
  @MockBean
  private OrganisationService organisationService;
  @MockBean
  private OrganisationSecurityService organisationSecurityService;
  @MockBean
  private IOrganisationRepo organisationRepo;
  @MockBean
  private NotificationService notificationService;

  @Test
  void applyToOrganisation() {
    var user = User.builder().userId("testJoiner").userName("test.joiner")
        .appliedOrganisations(new ArrayList<>()).build();
    doNothing().when(notificationService)
        .createAndSendSystemNotification(anyString(), any(), any());
    when(userService.getUserFromPrincipal(any())).thenReturn(user);
    when(organisationService.getOrganisation(anyLong())).thenReturn(new Organisation());
    when(userService.updateUser(any())).thenReturn(new User());
    setUpApplyToOrganisation();

    assertDoesNotThrow(() -> {
      userOrgApplicationService.applyToOrganisation(1L, getKeycloakPrincipal());
    });
  }

  private void setUpApplyToOrganisation() {
    var org = Organisation.builder().name("testOrg").id(1L).build();
    var appliedUsers = new ArrayList<UserOrgApplication>();
    var appliedUser = UserOrgApplication.builder()
        .appliedUser(User.builder().userName("userName").userId("userId").build())
        .appliedOrganisation(org).build();
    appliedUsers.add(appliedUser);
    org.setAppliedUsers(appliedUsers);

    var orgUser = OrganisationUser.builder()
        .user(User.builder().userName("userName").userId("userId").build())
        .build();
    var orgUserList = new ArrayList<OrganisationUser>();
    orgUserList.add(orgUser);
    org.setUsers(orgUserList);
    when(organisationService.getOrganisationIncAppliedUsers(anyLong())).thenReturn(org);

  }

  private void setupNotifyOrgAdminsForApplication() {
    final OrganisationUser organisationUser = Mockito.mock(OrganisationUser.class);
    var user = User.builder().userId("testuser").userName("test.user").build();
    var org = Organisation.builder().id(1L).users(List.of(organisationUser)).name("testOrg")
        .build();
    var message = Message.builder().id(1L).build();
    when(notificationService.createMessage(anyString(), anyString())).thenReturn(message);
    when(organisationService.getOrganisationIncAppliedUsers(anyLong())).thenReturn(org);
    when(organisationUser.getUser()).thenReturn(user);
  }

  @Test
  void getUserOrgApplications() {
    var keycloakPrincipal = getKeycloakPrincipal();
    var user = new User();
    user.setAppliedOrganisations(new ArrayList<>());
    when(userService.getUserFromPrincipalIncAppliedUsers(any())).thenReturn(user);
    assertEquals(user.getAppliedOrganisations(),
        userOrgApplicationService.getUserOrgApplications(keycloakPrincipal));
  }

  @Test
  void getOrgApplicationsSuccessful() {
    var mockOrg = mock(Organisation.class);
    when(organisationService.getOrganisationIncAppliedUsers(anyLong())).thenReturn(mockOrg);
    var testList = new LinkedList<UserOrgApplication>();
    when(mockOrg.getAppliedUsers()).thenReturn(testList);

    mockSecureOrgManagerRequestFromPrincipal();

    assertEquals(testList,
        userOrgApplicationService.getOrgApplications(1L, getKeycloakPrincipal()));
  }

  @Test
  void respondToApplicationSuccessful() {
    mockRespondToApplication();

    var keycloakPrincipal = getKeycloakPrincipal();
    var orgUserKey = new OrganisationUserKey("userid", 1L);

    assertDoesNotThrow(() -> {
      userOrgApplicationService.respondToApplication(orgUserKey, true, keycloakPrincipal);

    });
    assertDoesNotThrow(() -> {
      userOrgApplicationService.respondToApplication(orgUserKey, false, keycloakPrincipal);

    });
  }

  @Test
  void respondToApplicationOrgApplicationNotFound() {
    mockRespondToApplication();

    var keycloakPrincipal = getKeycloakPrincipal();
    var orgUserKey = new OrganisationUserKey("notFoundId", 1L);

    assertThrows(OrgApplicationNotFoundException.class, () -> {
      userOrgApplicationService.respondToApplication(orgUserKey, true, keycloakPrincipal);
    });
  }

  private void mockRespondToApplication() {
    var mockOrg = mock(Organisation.class);
    when(organisationService.addUserToOrganisation(anyString(), anyLong())).thenReturn(mockOrg);
    var userOrgApplication = new UserOrgApplication();
    var user = mock(User.class);
    when(userService.getUser("userid")).thenReturn(user);
    when(userService.getUser("notFoundId")).thenThrow(OrgApplicationNotFoundException.class);
    userOrgApplication.setAppliedUser(user);
    when(user.getUserId()).thenReturn("userid");
    var message = Message.builder().id(1L).build();
    when(notificationService.createMessage(anyString(), anyString())).thenReturn(message);
  }

  private void mockSecureOrgManagerRequestFromPrincipal() {
    var testUser = new User();
    when(userService.getUserFromPrincipal(any())).thenReturn(testUser);
    doNothing().when(organisationSecurityService).checkUserIsOrgManager(any(), any());
  }

  private KeycloakAuthenticationToken getKeycloakPrincipal() {
    return (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }
}
