package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.service.KeycloakCommunicationService;
import be.xplore.notifyme.service.OrganisationService;
import be.xplore.notifyme.service.UserOrgApplicationService;
import be.xplore.notifyme.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = {UserController.class})
@AutoConfigureMockMvc
@Import(RestConfig.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userService;
  @MockBean
  private KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  private OrganisationService organisationService;
  @MockBean
  private UserOrgApplicationService userOrgApplicationService;

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

}