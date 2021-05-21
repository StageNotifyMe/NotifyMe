package be.xplore.notifyme.communication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.account.UserRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {OrganisationService.class})
class OrganisationServiceTest {

  @Autowired
  private OrganisationService organisationService;
  @MockBean
  private IOrganisationRepo organisationRepo;
  @MockBean
  private UserService userService;

  private final Organisation testOrg = new Organisation(1L, "testOrg", new ArrayList<>());

  @Test
  void createOrganisation() {
    String orgname = "testOrg";
    Organisation testOrg = new Organisation(1L, orgname, new ArrayList<>());
    when(organisationRepo.save(any())).thenReturn(testOrg);
    assertEquals(testOrg, organisationService.createOrganisation(orgname));
  }

  @Test
  void createOrganisationDbNotWorking() {
    String orgname = "testOrg";
    when(organisationRepo.save(any())).thenThrow(new CrudException("HBE"));
    assertThrows(CrudException.class, () ->
        organisationService.createOrganisation(orgname));
  }

  @Test
  void getOrganisations() {
    String orgname = "testOrg";
    Organisation testOrg = new Organisation(1L, orgname, new ArrayList<>());
    List<Organisation> organisations = List.of(testOrg);
    when(organisationRepo.findAll()).thenReturn(organisations);
    assertEquals(organisations, organisationService.getOrganisations());
  }

  @Test
  void getOrganisationsDbNotWorking() {
    when(organisationRepo.findAll()).thenThrow(new CrudException("HBE"));
    assertThrows(CrudException.class, () ->
        organisationService.getOrganisations());
  }

  @Test
  void getOrganisation() {
    String orgname = "testOrg";
    Organisation testOrg = new Organisation(1L, orgname, new ArrayList<>());
    when(organisationRepo.findById(anyLong())).thenReturn(Optional.of(testOrg));
    assertEquals(testOrg, organisationService.getOrganisation(1L));
  }

  @Test
  void getOrganisationDoesNotExist() {
    when(organisationRepo.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(CrudException.class, () ->
        organisationService.getOrganisation(1L));
  }

  @Test
  void getOrganisationDbNotWorking() {
    when(organisationRepo.findById(anyLong())).thenThrow(new CrudException("HBE"));
    assertThrows(CrudException.class, () ->
        organisationService.getOrganisation(1L));
  }

  @Test
  void promoteUserToOrgManager() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    var userRepresentation = new UserRepresentation();
    userRepresentation.setId("TestId");
    var user = new User();
    user.setUserId("TestId");
    setupPromotionMocking(principal, userRepresentation, user);
    assertEquals(testOrg, organisationService.promoteUserToOrgManager("testuser", 1L, principal));
  }

  private void setupPromotionMocking(Principal principal, UserRepresentation userRepresentation,
      User user) {
    when(organisationRepo.findById(anyLong())).thenReturn(Optional.of(testOrg));
    when(userService.getUserInfo(anyString(), any(Principal.class))).thenReturn(userRepresentation);
    when(userService.getUser(any())).thenReturn(user);
    when(organisationRepo.addToOrgManagers(anyLong(), anyString())).thenReturn(testOrg);
    doNothing().when(userService).grantUserRole(anyString(), anyString());
  }

  @Test
  void promoteUserToOrgManagerDbNotWorking() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    var userRepresentation = new UserRepresentation();
    userRepresentation.setId("TestId");
    var user = new User();
    user.setUserId("TestId");
    when(organisationRepo.findById(anyLong())).thenReturn(Optional.of(testOrg));
    when(userService.getUserInfo(anyString(), any(Principal.class))).thenReturn(userRepresentation);
    when(userService.getUser(any())).thenReturn(user);
    when(organisationRepo.addToOrgManagers(anyLong(), anyString()))
        .thenThrow(new CrudException("HBE"));
    assertThrows(CrudException.class, () ->
        organisationService.promoteUserToOrgManager("testUser", 1L, principal));
  }

  @Test
  void saveSuccessful() {
    var org = new Organisation();
    assertDoesNotThrow(() -> {
      organisationService.save(org);
    });
  }

  @Test
  void saveFails() {
    var org = new Organisation();
    doThrow(RuntimeException.class).when(organisationRepo).save(any());

    assertThrows(CrudException.class, () -> {
      organisationService.save(org);
    });
  }

  @Test
  void findByIdIncAppliedUsers() {
    var org = Organisation.builder().build();
    when(organisationRepo.findByIdIncAppliedUsers(anyLong())).thenReturn(Optional.ofNullable(org));
    assertEquals(org, organisationService.getOrganisationIncAppliedUsers(1L));
  }

  @Test
  void findByIdIncAppliedUsersNotExisting() {
    var org = Organisation.builder().build();
    when(organisationRepo.findByIdIncAppliedUsers(anyLong())).thenReturn(Optional.empty());
    assertThrows(CrudException.class, () -> organisationService.getOrganisationIncAppliedUsers(1L));
  }

  @Test
  void getOrganisationIncAppliedUsers() {
    when(organisationRepo.findByIdIncAppliedUsers(1L)).thenReturn(Optional.of(testOrg));
    assertDoesNotThrow(() -> {
      organisationService.getOrganisationIncAppliedUsers(1L);
    });
    when(organisationRepo.findByIdIncAppliedUsers(2L)).thenReturn(Optional.empty());
    assertThrows(CrudException.class, () -> {
      organisationService.getOrganisationIncAppliedUsers(2L);
    });
  }

  @Test
  void addUserToOrganisation() {
    var org = new Organisation();
    when(organisationRepo.addUserToOrganisation(anyString(), anyLong())).thenReturn(org);
    assertEquals(org, organisationService.addUserToOrganisation("iets", 1L));
  }

  @Test
  void changeApplicationStatus() {
    var org = new Organisation();
    when(organisationRepo.changeApplicationStatus(anyString(), anyLong(), any())).thenReturn(org);
    assertEquals(org,
        organisationService.changeApplicationStatus("iets", 1L, OrgApplicationStatus.APPLIED));
  }

}