package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import be.xplore.notifyme.config.KeycloakSecurityConfig;
import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.GeneralExceptionHandler;
import be.xplore.notifyme.services.IEventService;
import be.xplore.notifyme.services.ILineService;
import be.xplore.notifyme.services.ITeamService;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@SpringBootTest(classes = {LineManagerController.class})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {RestConfig.class, KeycloakSecurityConfig.class})
class LineManagerControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private LineManagerController lineManagerController;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(lineManagerController)
        .setControllerAdvice(new GeneralExceptionHandler())
        .build();
  }

  @MockBean
  private IEventService eventService;
  @MockBean
  private ITeamService teamService;
  @MockBean
  private ILineService lineService;

  @Test
  @WithMockUser(username = "test", roles = {"line_manager"})
  void getAllEventsForLineManagerSuccess() throws Exception {
    when(eventService.getAllEventsForLineManager(anyString())).thenReturn(new ArrayList<>());

    mockMvc
        .perform(get("/lmanager/events?userId=testUser"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void getAllEventsForLineManagerFail() throws Exception {
    when(eventService.getAllEventsForLineManager(anyString()))
        .thenThrow(new CrudException("Could not get events for line manager."));

    mockMvc
        .perform(get("/lmanager/events?userId=testUser"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void getAllLinesForEvent() throws Exception {
    mockGetAllLinesForEvent();

    mockMvc.perform(get("/lmanager/lines?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json("[\n"
            + "{\n"
            + "\"id\":1,\n"
            + "\"note\":\"note\",\n"
            + "\"requiredStaff\":5,\n"
            + "\"facilityId\":1,\n"
            + "\"facilityDescription\":\"description\"\n"
            + "}\n"
            + "]"));
  }

  private void mockGetAllLinesForEvent() {
    var dummyLines = new ArrayList<Line>();
    var dummyLine = new Line("note", 5);
    dummyLine.setId(1L);
    dummyLines.add(dummyLine);
    var dummyFacility = new Facility("description", "location", 0, 5);
    dummyFacility.setId(1L);
    dummyLine.setFacility(dummyFacility);
    when(lineService.getAllLinesByEvent(1L)).thenReturn(dummyLines);
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void getTeam() throws Exception {
    mockGetCreateTeam();

    mockMvc.perform(get("/lmanager/team?teamId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json("{\"id\":1}"));
  }

  private void mockGetCreateTeam() {
    var dummyTeam = new Team(1L, new Line(), new ArrayList<>(), new HashSet<>());
    when(teamService.getTeam(1L)).thenReturn(dummyTeam);
    when(teamService.createTeam(1L, 1L)).thenReturn(dummyTeam);
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void createTeam() throws Exception {
    mockGetCreateTeam();

    mockMvc.perform(post("/lmanager/team").contentType(MediaType.APPLICATION_JSON)
        .content("{\"lineId\":1,\"organisationId\":1}"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().json("{\"id\":1}"));
  }

  private void mockAddOrganisation() {
    var dummyOrg = new Organisation("organisation");
    dummyOrg.setId(1L);
    var dummyList = new ArrayList<Organisation>();
    dummyList.add(dummyOrg);
    var teamWithOrg = new Team();
    teamWithOrg.setId(1L);
    teamWithOrg.setOrganisations(dummyList);
    when(teamService.addOrganisationToTeam(1L, 1L)).thenReturn(teamWithOrg);
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void updateTeam() throws Exception {
    mockAddTeamMember();
    mockAddOrganisation();

    mockMvc.perform(put("/lmanager/team").contentType(MediaType.APPLICATION_JSON)
        .content("{\"teamId\":1,\"organisationId\":1}"))
        .andExpect(MockMvcResultMatchers.status().isOk());

    mockMvc.perform(put("/lmanager/team").contentType(MediaType.APPLICATION_JSON)
        .content("{\"teamId\":1,\"userId\":\"userId\"}"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  private void mockAddTeamMember() {
    var dummyUser = new User("userId", "userName");
    var dummySet = new HashSet<User>();
    dummySet.add(dummyUser);
    var teamWithUsr = new Team();
    teamWithUsr.setId(1L);
    teamWithUsr.setTeamMembers(dummySet);
    when(teamService.addUserToTeam(1L, "userId")).thenReturn(teamWithUsr);
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void deleteTeam() throws Exception {
    doNothing().when(teamService).deleteTeam(1L);

    mockMvc.perform(delete("/lmanager/team?teamId=1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void getAllAvailableOrganisations() throws Exception {
    mockGetAllAvailableOrganisations();

    mockMvc.perform(get("/lmanager/team/organisations/available?teamId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  private void mockGetAllAvailableOrganisations() {
    //since an empty list is a valid return value in this scenario, I won't add dummy organisations
    //to the test
    var dummyList = new ArrayList<Organisation>();
    when(teamService.getAllAvailableOrganisations(1L)).thenReturn(dummyList);
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void getTeamFromLine() throws Exception {
    mockGetTeamFromLine();

    mockMvc.perform(get("/lmanager/line/team?lineId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json("1"));

  }

  private void mockGetTeamFromLine() {
    var dummyLine = new Line("note", 5);
    var dummyTeam = new Team();
    dummyTeam.setId(1L);
    dummyLine.setTeam(dummyTeam);
    when(lineService.getLine(1L)).thenReturn(dummyLine);
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void deleteOrgFromTeam() throws Exception {
    doNothing().when(teamService).deleteOrganisationFromTeam(1L, 1L);

    mockMvc.perform(delete("/lmanager/team/organisation?teamId=1&organisationId=1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

}