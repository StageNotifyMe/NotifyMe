package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import be.xplore.notifyme.config.KeycloakSecurityConfig;
import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.exception.GeneralExceptionHandler;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.communication.UserOrgApplicationService;
import be.xplore.notifyme.communication.UserOrgService;
import be.xplore.notifyme.communication.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = {OrganisationManagerController.class})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {RestConfig.class, KeycloakSecurityConfig.class})
class OrganisationManagerControllerTest {
  private MockMvc mockMvc;

  @Autowired
  private OrganisationManagerController organisationManagerController;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(organisationManagerController)
        .setControllerAdvice(new GeneralExceptionHandler())
        .build();
  }

  @MockBean
  private UserOrgApplicationService userOrgApplicationService;
  @MockBean
  private UserOrgService userOrgService;
  @MockBean
  private UserService userService;

  @Test
  @WithMockUser(username = "orgmanager", roles = {"user", "organisation_manager"})
  void getUserApplications() throws Exception {
    var orgApps = setupOrgApplications();
    var userReps = setupUserReps();
    when(userOrgApplicationService.getOrgApplications(anyLong(), any())).thenReturn(orgApps);
    when(userService.getAllUserInfo()).thenReturn(userReps);
    mockMvc.perform(get("/omanager/userApplications?organisationId=" + 1))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "orgmanager", roles = {"user", "organisation_manager"})
  void respondToApplication() throws Exception {
    doNothing().when(userOrgApplicationService).respondToApplication(any(), anyBoolean(), any());
    mockMvc.perform(
        post("/omanager/userApplication").content("{ "
            + "\"organisationUserKey\": { "
            + "\"organisationId\": 1, "
            + "\"userId\": \"testId\""
            + " },"
            + " \"accepted\": true"
            + " }").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "orgmanager", roles = {"user", "organisation_manager"})
  void respondToApplicationNotCorrectOrgManager() throws Exception {
    doThrow(new UnauthorizedException("User is not a manager of this organisation."))
        .when(userOrgApplicationService).respondToApplication(any(), anyBoolean(), any());
    mockMvc.perform(
        post("/omanager/userApplication").content("{ "
            + "\"organisationUserKey\": { "
            + "\"organisationId\": 1, "
            + "\"userId\": \"testId\""
            + " },"
            + " \"accepted\": true"
            + " }").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "orgmanager", roles = {"user", "organisation_manager"})
  void getOrganisationsOfManager() throws Exception {
    var orgs = setupOrganisations();
    when(userOrgService.getOrgManagerOrganisations(any())).thenReturn(orgs);
    mockMvc.perform(get("/omanager/organisations"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "orgmanager", roles = {"user", "organisation_manager"})
  void getOrgInfo() throws Exception {
    var org = new Organisation();
    org.setId(1L);
    org.setName("orgje");
    when(userOrgService.getOrgInfoAsManager(anyLong(),any())).thenReturn(org);
    mockMvc.perform(get("/omanager/organisation?organisationId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  private List<Organisation> setupOrganisations() {
    var org = new Organisation();
    org.setId(1L);
    org.setName("TestOrg");
    org.setUsers(new ArrayList<>());
    return List.of(org);
  }

  private List<UserOrgApplication> setupOrgApplications() {
    var user = new User();
    user.setUserId("testId");
    user.setAppliedOrganisations(new ArrayList<>());
    var org = new Organisation();
    org.setId(1L);
    org.setAppliedUsers(new ArrayList<>());
    var orgApps = new ArrayList<UserOrgApplication>();
    orgApps.add(new UserOrgApplication(org, user, OrgApplicationStatus.APPLIED));
    return orgApps;
  }

  private List<UserRepresentation> setupUserReps() {
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setId("testId");
    userRepresentation.setUsername("testUser");
    return List.of(userRepresentation);
  }
}