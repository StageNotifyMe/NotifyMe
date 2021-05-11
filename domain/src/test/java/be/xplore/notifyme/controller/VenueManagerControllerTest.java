package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.service.EventService;
import be.xplore.notifyme.service.FacilityService;
import be.xplore.notifyme.service.LineService;
import be.xplore.notifyme.service.VenueService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = {VenueManagerController.class})
@AutoConfigureMockMvc
class VenueManagerControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private EventService eventService;
  @MockBean
  private LineService lineService;
  @MockBean
  private VenueService venueService;
  @MockBean
  private FacilityService facilityService;
  private final String body =
      "{\"title\": \"Evenement\",\n\"description\":\"beschrijving\",\n"
          + " \"artist\":\"artiest\",\n\"dateTime\":\"2021-04-30T06:45:30\",\n\"venueId\":1\n}";
  private final String lineBody =
      "{\"note\": \"Testline\",\n\"requiredStaff\":10,\n"
          + " \"facilityId\":1,\n\"eventId\":1}";
  private final String facilityBody =
      "{\"description\": \"TestFacility\",\n\"location\":\"TestLocation\",\n"
          + " \"minimalStaff\":10,\n\"maximalStaff\":15,\n\"maximalStaff\":1}";

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void createEventSuccessful() throws Exception {
    when(eventService.createEvent(any(CreateEventDto.class), any(Principal.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

    mockMvc
        .perform(post("/vmanager/event").header("Content-Type", "application/json")
            .content(body))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void unauthorizedCreate() throws Exception {
    when(eventService.createEvent(any(CreateEventDto.class), any(Principal.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

    mockMvc
        .perform(post("/vmanager/event").header("Content-Type", "application/json")
            .content(body))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void nullBodyCreate() throws Exception {
    when(eventService.createEvent(any(CreateEventDto.class), any((Principal.class))))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

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

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void getLinesSuccessful() throws Exception {
    when(lineService.getAllLinesByEvent(anyLong()))
        .thenReturn(new ArrayList<>());

    mockMvc
        .perform(get("/vmanager/lines?eventId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
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
  }

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
    when(facilityService.createFacility(any()))
        .thenReturn(new Facility());

    mockMvc
        .perform(post("/vmanager/facility").contentType(MediaType.APPLICATION_JSON)
            .content(facilityBody))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void postFacilityUnuccessful() throws Exception {
    when(facilityService.createFacility(any()))
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
}