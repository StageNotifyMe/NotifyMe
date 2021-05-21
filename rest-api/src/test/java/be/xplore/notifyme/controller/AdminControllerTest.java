package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import be.xplore.notifyme.config.KeycloakSecurityConfig;
import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.GeneralExceptionHandler;
import be.xplore.notifyme.services.KeycloakCommunicationService;
import be.xplore.notifyme.services.OrganisationService;
import be.xplore.notifyme.services.UserService;
import be.xplore.notifyme.services.VenueService;
import java.util.HashSet;
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

@SpringBootTest(classes = {AdminController.class})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {RestConfig.class, KeycloakSecurityConfig.class})
class AdminControllerTest {
  private MockMvc mockMvc;

  @Autowired
  private AdminController adminController;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(adminController)
        .setControllerAdvice(new GeneralExceptionHandler())
        .build();
  }


  @MockBean
  private OrganisationService organisationService;
  @MockBean
  private KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  private UserService userService;
  @MockBean
  private VenueService venueService;

  @Test
  @WithMockUser(username = "adminUser", roles = {"user"})
  void adminInfoTestNotAdmin() throws Exception {
    mockMvc.perform(get("/admin/adminTest"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void adminInfoTestIsAdmin() throws Exception {
    mockMvc.perform(get("/admin/adminTest"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Well hello there, admin!"));
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void createOrganisation() throws Exception {
    when(organisationService.createOrganisation(anyString()))
        .thenReturn(new Organisation("testOrg"));

    mockMvc.perform(post("/admin/organisation?name=TestOrg"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void getOrganisation() throws Exception {
    when(organisationService.getOrganisation(anyLong()))
        .thenReturn(new Organisation("testOrg"));

    mockMvc.perform(get("/admin/organisation?id=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void getOrganisationNonExisting() throws Exception {
    when(organisationService.getOrganisation(anyLong()))
        .thenThrow(new CrudException("Organisation does not exist in db."));

    mockMvc.perform(get("/admin/organisation?id=1"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void getOrganisations() throws Exception {
    when(organisationService.getOrganisations())
        .thenReturn(List.of(new Organisation("testOrg")));

    mockMvc.perform(get("/admin/organisations"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void getOrganisationsNotWorking() throws Exception {
    when(organisationService.getOrganisations())
        .thenThrow(new CrudException("Could not get list of orgs."));

    mockMvc.perform(get("/admin/organisations"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void getUsers() throws Exception {
    String token = "{ \n\"access_token\"=\"token\","
        + "\n\"expires_in\"=200,\n\"refresh_expires_in\"=100,\n\"token_type\"=\"token\","
        + "\n\"not-before-policy\"=50,\n\"scope\"=\"roles, users\" }";
    when(keycloakCommunicationService.getAdminAccesstoken())
        .thenReturn(token);
    when(userService.getAllUserInfo()).thenReturn(List.of(new UserRepresentation()));

    mockMvc.perform(get("/admin/users"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void promoteUserToOrgMgr() throws Exception {
    when(organisationService.promoteUserToOrgManager(anyString(), anyLong(), any()))
        .thenReturn(new Organisation());


    mockMvc.perform(post("/admin/promoteUserToOrgMgr").content("{"
        + "\n\"username\"" + ": \"testuser\","
        + "\n\"organisationId\"" + ": 1" + "\n}").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void createVenue() throws Exception {
    when(venueService.createVenue(any(), any()))
        .thenReturn(new Venue());

    mockMvc.perform(post("/admin/venue").content("{"
        + "\n\"name\"" + ": \"testname\","
        + "\n\"description\"" + ": \"testdescription\","
        + "\n\"streetAndNumber\"" + ": \"Teststraat 100\","
        + "\n\"postalCode\"" + ": \"2000\","
        + "\n\"village\"" + ": \"testVillage\","
        + "\n\"country\"" + ": \"Belgium\""
        + "\n}")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void promoteUserToVmanagerSuccessful() throws Exception {
    doNothing().when(venueService).makeUserVenueManager(anyString(),
        anyLong());

    mockMvc.perform(post("/admin/promoteUserToVmanager?userId=id&venueId=1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }


  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void getAllVenueManagersSuccessful() throws Exception {
    final User testUser = new User();
    testUser.setUserId("userid");
    final Venue venue = new Venue();
    venue.setManagers(new HashSet<>());
    venue.getManagers().add(testUser);
    when(venueService.getVenue(anyLong())).thenReturn(venue);

    mockMvc.perform(get("/admin/venueManagers?venueId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}