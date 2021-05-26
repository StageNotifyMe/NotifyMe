package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import be.xplore.notifyme.config.KeycloakSecurityConfig;
import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.exception.GeneralExceptionHandler;
import be.xplore.notifyme.services.CommunicationPreferenceService;
import be.xplore.notifyme.services.KeycloakCommunicationService;
import be.xplore.notifyme.services.NotificationService;
import be.xplore.notifyme.services.OrganisationService;
import be.xplore.notifyme.services.UserOrgApplicationService;
import be.xplore.notifyme.services.UserService;
import be.xplore.notifyme.services.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = {UserController.class})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {RestConfig.class, KeycloakSecurityConfig.class})
class UserControllerTest {

  @Autowired
  private UserController userController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new GeneralExceptionHandler())
        .build();
  }

  @MockBean
  private UserService userService;
  @MockBean
  private KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  private OrganisationService organisationService;
  @MockBean
  private UserOrgApplicationService userOrgApplicationService;
  @MockBean
  private CommunicationPreferenceService communicationPreferenceService;
  @MockBean
  private NotificationService notificationService;

  @Test
  void getAccessTokenForUserValid() throws Exception {
    when(keycloakCommunicationService.login(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok("userinfo"));
    mockMvc.perform(get("/user/token?username=test&password=test"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("userinfo"));
  }

  @Test
  void getAccessTokenForUserInvalid() throws Exception {
    when(keycloakCommunicationService.login(anyString(), anyString()))
        .thenReturn(ResponseEntity.status(401).build());
    mockMvc.perform(get("/user/token?username=test&password=test"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void registerNewUser() throws Exception {
    doNothing().when(userService).register(any(UserRegistrationDto.class));
    mockMvc.perform(post("/user/register").content("{\"firstname\":\"Arthur\",\n"
        + "    \"lastname\":\"De Craemer\",\n"
        + "    \"email\":\"adc@adc.be\",\n"
        + "    \"username\":\"arthur.decraemer\",\n"
        + "    \"password\":\"arthur123!\"}").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getUserInfoSuccessful() throws Exception {
    final UserRepresentation mockUserRep = mock(UserRepresentation.class);

    when(userService.getUserInfo(anyString(), any())).thenReturn(mockUserRep);
    when(mockUserRep.getId()).thenReturn("id");
    when(mockUserRep.getEmail()).thenReturn("test@test.com");
    when(mockUserRep.getLastName()).thenReturn("tester");
    when(mockUserRep.getFirstName()).thenReturn("test");
    when(mockUserRep.getUsername()).thenReturn("test.tester");

    mockMvc
        .perform(get("/user/userInfo?username=Test"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(
            "{\"id\":\"id\",\"username\":\"test.tester\",\"firstName\":\"test\","
                + "\"lastName\":\"tester\",\"email\":\"test@test.com\","
                + "\"emailVerified\":false,\"attributes\":{}}"));
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void getOrganisations() throws Exception {
    when(organisationService.getOrganisations()).thenReturn(new ArrayList<>());

    mockMvc
        .perform(get("/user/organisations"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void applyToOrganisation() throws Exception {
    doNothing().when(userOrgApplicationService).applyToOrganisation(anyLong(), any());

    mockMvc
        .perform(post("/user/orgApplication?organisationId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void getOrgApplications() throws Exception {
    var application = new UserOrgApplication();
    application.setApplicationStatus(OrgApplicationStatus.APPLIED);
    application.setAppliedOrganisation(new Organisation());
    application.setOrganisationUserKey(new OrganisationUserKey());
    when(userOrgApplicationService.getUserOrgApplications(any())).thenReturn(List.of(application));

    mockMvc
        .perform(get("/user/orgApplications"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void createCommunicationPreference() throws Exception {
    when(communicationPreferenceService
        .createCommunicationPreference(anyString(), anyBoolean(), anyBoolean(), anyBoolean(),
            any())).thenReturn(
        CommunicationPreference.builder().id(1).isActive(true).isDefault(true).isUrgent(true)
            .build());

    mockMvc.perform(post("/user/communicationpreference")
        .header("Content-Type", "application/json")
        .content("{\n"
            + "   \"userId\": \"05f6194f-15fe-4c7e-b5b3-642e051b0d6d\",\n"
            + "   \"isActive\": true,\n"
            + "   \"isDefault\": true,\n"
            + "   \"isUrgent\": true,\n"
            + "   \"communicationStrategy\": \"smscommunicationstrategy\"\n"
            + "}"))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void updateCommunicationPreference() throws Exception {
    var comstrat = mock(ICommunicationStrategy.class);
    when(comstrat.getName()).thenReturn("email");
    when(communicationPreferenceService
        .updateCommunicationPreference(anyLong(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(
            CommunicationPreference.builder().id(1).isActive(true).isDefault(true).isUrgent(true)
                .communicationStrategy(comstrat)
                .build());
    mockMvc.perform(put("/user/communicationpreference")
        .header("Content-Type", "application/json")
        .content(
            "{\n\"communicationPreferenceId\": 1,\n\"isActive\": true,\n\"isDefault\": false,\n"
                + "\"isUrgent\": false }"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void deleteCommunicationPreference() throws Exception {

    mockMvc.perform(delete("/user/communicationpreference?communicationPreferenceId=1")
    )
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void getAllCommunicationPreferences() throws Exception {
    mockMvc.perform(get("/user/communicationpreferences?userId=userId")
        .header("Content-Type", "application/json")
    ).andExpect(MockMvcResultMatchers.status().isOk());

    var prefList = new ArrayList<CommunicationPreference>();
    prefList.add(new CommunicationPreference(1L, new User(), true, true, false,
        new EmailCommunicationStrategy(null)));
    when(communicationPreferenceService.getAllCommunicationPreferencesForUser("userId"))
        .thenReturn(prefList);
    mockMvc.perform(get("/user/communicationpreferences?userId=userId")
        .header("Content-Type", "application/json")
    ).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content()
            .json("[{\n"
                + "\"id\": 1,\n" + "\"active\": true,\n"
                + "\"defaultt\": true,\n" + "\"communicationStrategy\": \"Email\"\n"
                + "}]"));
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void getNotifications() throws Exception {
    var msg = Message.builder().id(1L).text("eyo").title("eyotitle").build();
    var notif = Notification.builder().id(1L).message(msg)
        .communicationAddress("anAddress").build();
    when(notificationService.getNotificationsForUser(anyString())).thenReturn(List.of(notif));
    when(userService.getUserFromPrincipal(any()))
        .thenReturn(User.builder().userId("testId").build());

    mockMvc.perform(get("/user/notifications")
        .header("Content-Type", "application/json")
    ).andExpect(MockMvcResultMatchers.status().isOk());
  }

}