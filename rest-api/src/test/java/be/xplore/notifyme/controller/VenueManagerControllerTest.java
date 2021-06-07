package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import be.xplore.notifyme.config.KeycloakSecurityConfig;
import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.GeneralExceptionHandler;
import be.xplore.notifyme.services.IUserService;
import be.xplore.notifyme.services.implementations.EventService;
import be.xplore.notifyme.services.implementations.FacilityService;
import be.xplore.notifyme.services.implementations.LineService;
import be.xplore.notifyme.services.implementations.VenueService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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

@SpringBootTest(classes = {VenueManagerController.class})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {RestConfig.class, KeycloakSecurityConfig.class})
class VenueManagerControllerTest {

  @Autowired
  private VenueManagerController venueManagerController;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(venueManagerController)
        .setControllerAdvice(new GeneralExceptionHandler())
        .build();
  }

  private MockMvc mockMvc;
  @MockBean
  private EventService eventService;
  @MockBean
  private LineService lineService;
  @MockBean
  private VenueService venueService;
  @MockBean
  private FacilityService facilityService;
  @MockBean
  private IUserService userService;

  private final String body =
      "{\"title\": \"Evenement\",\n\"description\":\"beschrijving\",\n"
          + " \"artist\":\"artiest\",\n\"dateTime\":\"2021-04-30T06:45:30\",\n\"venueId\":1\n}";
  private final String lineBody =
      "{\"note\": \"Testline\",\n\"requiredStaff\":10,\n"
          + " \"facilityId\":1,\n\"eventId\":1}";
  private final String facilityBody =
      "{\"description\": \"TestFacility\",\n\"location\":\"TestLocation\",\n"
          + " \"minimalStaff\":10,\n\"maximalStaff\":15,\n\"maximalStaff\":1}";

  private final Event mockEvent = mock(Event.class);

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void createEventSuccessful() throws Exception {
    when(eventService.createEvent(anyString(), anyString(), anyString(), anyString(), anyLong(),
        any(Principal.class)))
        .thenReturn(mockEvent);

    mockMvc
        .perform(post("/vmanager/event").header("Content-Type", "application/json")
            .content(body))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void unauthorizedCreate() throws Exception {
    when(eventService
        .createEvent(anyString(), anyString(), anyString(), anyString(), anyLong(), any()))
        .thenReturn(mockEvent);

    mockMvc
        .perform(post("/vmanager/event").header("Content-Type", "application/json")
            .content(body))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void nullBodyCreate() throws Exception {
    when(eventService.createEvent(anyString(), anyString(), anyString(), anyString(), anyLong(),
        any((Principal.class))))
        .thenReturn(mockEvent);

    mockMvc.perform(post("/vmanager/event"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getEventSuccessful() throws Exception {
    when(eventService.getEvent(anyLong()))
        .thenReturn(new Event());

    mockMvc
        .perform(get("/vmanager/event?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getEventNonExisting() throws Exception {
    when(eventService.getEvent(anyLong())).thenThrow(new CrudException("Could not retrieve event"));

    mockMvc
        .perform(get("/vmanager/event?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  /*@Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getLinesSuccessful() throws Exception {
    when(lineService.getAllLinesByEvent(anyLong()))
        .thenReturn(new ArrayList<>());

    mockMvc
        .perform(get("/vmanager/lines?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }*/

  /*@Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getLinesParsingSuccessful() throws Exception {
    final Facility mockFacility = mock(Facility.class);
    final Line testLine = new Line("note", 5);
    testLine.setFacility(mockFacility);
    final List<Line> lineList = new ArrayList<>();
    lineList.add(testLine);

    when(lineService.getAllLinesByEvent(1L)).thenReturn(lineList);
    when(mockFacility.getId()).thenReturn(1L);
    when(mockFacility.getDescription()).thenReturn("description");

    mockMvc
        .perform(get("/vmanager/lines?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(
            "[{note:\"note\", requiredStaff:5, facilityId:1,"
                + " facilityDescription:\"description\"}]"));
  }*/

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getLinesUnsuccessful() throws Exception {
    when(lineService.getAllLinesByEvent(anyLong()))
        .thenThrow(new CrudException("Could not get lines"));

    mockMvc
        .perform(get("/vmanager/lines?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getVenuesForUserSuccessful() throws Exception {
    when(venueService.getVenuesForUser(anyString()))
        .thenReturn(new ArrayList<>());

    mockMvc
        .perform(get("/vmanager/venues?userId=test"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getVenuesForUserUnsuccesful() throws Exception {
    when(venueService.getVenuesForUser(anyString()))
        .thenThrow(new CrudException("Could not get users"));

    mockMvc
        .perform(get("/vmanager/venues?userId=test"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getVenueSuccessful() throws Exception {
    when(venueService.getVenue(anyLong()))
        .thenReturn(new Venue());

    mockMvc
        .perform(get("/vmanager/venue?venueId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getVenueUnsuccessful() throws Exception {
    when(venueService.getVenue(anyLong()))
        .thenThrow(new CrudException("Could not get venue."));

    mockMvc
        .perform(get("/vmanager/venue?venueId=1"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void postLineSuccessful() throws Exception {
    when(lineService.createLine(any(), any()))
        .thenReturn(new Line());

    mockMvc
        .perform(post("/vmanager/line").contentType(MediaType.APPLICATION_JSON)
            .content(lineBody))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void postLineUnsuccessful() throws Exception {
    when(lineService.createLine(any(), any()))
        .thenThrow(new CrudException("Could not create line"));

    mockMvc
        .perform(post("/vmanager/line").contentType(MediaType.APPLICATION_JSON)
            .content(lineBody))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void postFacilitySuccessful() throws Exception {
    when(facilityService.createFacility(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
        .thenReturn(new Facility());

    mockMvc
        .perform(post("/vmanager/facility").contentType(MediaType.APPLICATION_JSON)
            .content(facilityBody))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void postFacilityUnuccessful() throws Exception {
    when(facilityService.createFacility(anyString(), anyString(), anyInt(), anyInt(), anyLong()))
        .thenThrow(new CrudException("Could not create facility."));

    mockMvc
        .perform(post("/vmanager/facility").contentType(MediaType.APPLICATION_JSON)
            .content(facilityBody))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getFacilitiesSuccessful() throws Exception {
    when(facilityService.getAllFacilitesForVenue(anyLong()))
        .thenReturn(new ArrayList<>());

    mockMvc
        .perform(get("/vmanager/facilities?venueId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getFacilitiesUnuccessful() throws Exception {
    when(facilityService.getAllFacilitesForVenue(anyLong()))
        .thenThrow(new CrudException("Could not get facilities for venue."));

    mockMvc
        .perform(get("/vmanager/facilities?venueId=1"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void promoteUserToLineManagerSuccessful() throws Exception {
    doNothing().when(eventService).promoteToLineManager(anyString(), anyLong());

    mockMvc
        .perform(post("/vmanager/promoteToLineManager?userId=userid&eventId=1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getLineManagersForEventSuccessful() throws Exception {
    final User testUser = new User();
    testUser.setUserId("userid");
    final Set<User> testSet = new HashSet<>();
    testSet.add(testUser);
    final Event event = new Event();
    event.setLineManagers(testSet);

    when(eventService.getEvent(anyLong())).thenReturn(event);

    mockMvc.perform(get("/vmanager/lineManagers?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json((
            "[{userId:\"userid\"}]"
        )));
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void updateEventStatus() throws Exception {
    when(eventService.updateEventStatus(anyLong(), any())).thenAnswer(new Answer<Event>() {
      @Override
      public Event answer(InvocationOnMock invocation) throws Throwable {
        var args = invocation.getArguments();
        return new Event((long) args[0], "title", "description", "artist", LocalDateTime.now(),
            (EventStatus) args[1], new Venue(), new LinkedList<>(), new HashSet<>());
      }
    });

    mockMvc.perform(put("/vmanager/event/status").contentType(MediaType.APPLICATION_JSON)
        .content("{\"eventId\":1,\n\"eventStatus\":\"CANCELED\"\n}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content()
            .json("{\"id\":1,\n\"eventStatus\":\"CANCELED\"\n}"));
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"user", "venue_manager"})
  void getUsers() throws Exception {
    when(userService.getAllUserInfo()).thenReturn(List.of(new UserRepresentation()));

    mockMvc.perform(get("/vmanager/users"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}