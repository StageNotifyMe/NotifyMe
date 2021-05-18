package be.xplore.notifyme.service;

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
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.exception.OrgApplicationNotFoundException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import be.xplore.notifyme.service.security.OrganisationSecurityService;
import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
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

  @Test
  void applyToOrganisation() {
    var user = new User();
    user.setAppliedOrganisations(new ArrayList<>());
    when(userService.getUserFromPrincipal(any())).thenReturn(user);
    when(organisationService.getOrganisation(anyLong())).thenReturn(new Organisation());
    when(userService.updateUser(any())).thenReturn(new User());

    assertDoesNotThrow(() -> {
      userOrgApplicationService.applyToOrganisation(1L, getKeycloakPrincipal());
    });
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
    when(organisationService.getOrganisationIncAppliedUsers(anyLong())).thenReturn(mockOrg);
    var userOrgApplication = new UserOrgApplication();
    var user = mock(User.class);
    when(userService.getUser(anyString())).thenReturn(user);
    userOrgApplication.setAppliedUser(user);
    var appliedUserList = new ArrayList<UserOrgApplication>();
    appliedUserList.add(userOrgApplication);
    when(user.getUserId()).thenReturn("userid");
    when(mockOrg.getAppliedUsers()).thenReturn(appliedUserList);
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
