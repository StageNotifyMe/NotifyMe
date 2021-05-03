package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class OrganisationServiceTest {

  @Autowired
  private OrganisationService organisationService;
  @MockBean
  private IOrganisationRepo organisationRepo;
  @MockBean
  private UserService userService;

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
    Organisation testOrg = new Organisation(1L, orgname, new ArrayList<>());
    when(organisationRepo.save(any())).thenThrow(new HibernateException("HBE"));
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
    String orgname = "testOrg";
    Organisation testOrg = new Organisation(1L, orgname, new ArrayList<>());
    when(organisationRepo.findAll()).thenThrow(new HibernateException("HBE"));
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
    when(organisationRepo.findById(anyLong())).thenThrow(new HibernateException("HBE"));
    assertThrows(CrudException.class, () ->
        organisationService.getOrganisation(1L));
  }

  @Test
  void promoteUserToOrgManager() {
    Organisation testOrg = new Organisation(1L, "testOrg", new ArrayList<>());
    var userRepresentation = new UserRepresentation();
    userRepresentation.setId("TestId");
    var user = new User();
    user.setUserId("TestId");
    when(organisationRepo.findById(anyLong())).thenReturn(Optional.of(testOrg));
    when(userService.getUserInfo(anyString())).thenReturn(userRepresentation);
    when(userService.getUser(any())).thenReturn(user);
    when(organisationRepo.save(any())).thenReturn(testOrg);
    assertEquals(testOrg, organisationService.promoteUserToOrgManager("testuser", 1L));
  }

  @Test
  void promoteUserToOrgManagerDbNotWorking() {
    Organisation testOrg = new Organisation(1L, "testorg", new ArrayList<>());
    var userRepresentation = new UserRepresentation();
    userRepresentation.setId("TestId");
    var user = new User();
    user.setUserId("TestId");
    when(organisationRepo.findById(anyLong())).thenReturn(Optional.of(testOrg));
    when(userService.getUserInfo(anyString())).thenReturn(userRepresentation);
    when(userService.getUser(any())).thenReturn(user);
    when(organisationRepo.save(any())).thenThrow(new HibernateException("HBE"));
    assertThrows(CrudException.class, () ->
        organisationService.promoteUserToOrgManager("testUser", 1L));
  }
}