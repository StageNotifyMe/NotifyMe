package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ILineRepo;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest(classes = {LineService.class})
class LineServiceTest {
  @Autowired
  private LineService lineService;
  @MockBean
  @Autowired
  private EventService eventService;
  @MockBean
  @Autowired
  private FacilityService facilityService;
  @MockBean
  private ILineRepo lineRepo;
  @MockBean
  @Autowired
  private UserService userService;

  private final CreateLineDto createLineDto = new CreateLineDto("note", 10, 1L, 1L);
  private final Event event =
      new Event(1L, "titel", "descriptie", "artiest", LocalDateTime.now(), new Venue(),
          new LinkedList<>(), new HashSet<>());
  private final Facility facility =
      new Facility(1L, "descriptie", "locatie", 1, 20, new Venue(), new LinkedList<>());
  private final User user = new User();
  private final Line line = new Line("note", 5);

  @Test
  void createLineSuccessful() {
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    when(eventService.getEventAndVerifyLineManagerPermission(anyLong(), any(Principal.class)))
        .thenReturn(event);
    when(facilityService.getFacility(1L)).thenReturn(facility);
    when(lineRepo.save(any(Line.class))).thenAnswer(
        invocation -> invocation.getArguments()[0]);
    when(userService.getUserFromPrincipal(principal)).thenReturn(user);

    var result = lineService.createLine(createLineDto, principal);
    assertEquals("note", result.getNote());
    assertEquals(10, result.getRequiredStaff());
    assertEquals(event.getId(), result.getEvent().getId());
    assertEquals(facility.getId(), result.getFacility().getId());
  }

  @Test
  void createLineEventNotFound() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    doThrow(new CrudException("Could not find event for id 1")).when(eventService)
        .getEventAndVerifyLineManagerPermission(anyLong(), any(Principal.class));
    when(facilityService.getFacility(1L)).thenReturn(facility);
    when(lineRepo.save(any(Line.class))).thenAnswer(
        invocation -> invocation.getArguments()[0]);

    assertThrows(CrudException.class, () -> lineService.createLine(createLineDto, principal));
  }

  @Test
  void createLineFacilityNotFound() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    when(eventService.getEvent(1L)).thenReturn(event);
    doThrow(new CrudException("Could not find facility for id 1")).when(facilityService)
        .getFacility(1L);
    when(lineRepo.save(any(Line.class))).thenAnswer(
        invocation -> invocation.getArguments()[0]);

    assertThrows(CrudException.class, () -> lineService.createLine(createLineDto, principal));
  }

  @Test
  void getAllLinesByEventSuccessful() {
    var mockEvent = mock(Event.class);
    List<Line> lineList = new LinkedList<>();
    lineList.add(line);
    when(eventService.getEvent(1L)).thenReturn(mockEvent);
    when(mockEvent.getLines()).thenReturn(lineList);

    assertEquals(lineList, lineService.getAllLinesByEvent(1L));
  }

  @Test
  void getAllLinesByEventNotFound() {
    doThrow(CrudException.class).when(eventService).getEvent(1L);

    assertThrows(CrudException.class, () -> lineService.getAllLinesByEvent(1L));
  }

  @Test
  void getLineSuccessful() {
    when(lineRepo.findById(anyLong())).thenReturn(Optional.of(line));

    assertEquals(line, lineService.getLine(1L));
  }

  @Test
  void getLineNotFound() {
    when(lineRepo.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(CrudException.class, () -> lineService.getLine(1L));
  }
}