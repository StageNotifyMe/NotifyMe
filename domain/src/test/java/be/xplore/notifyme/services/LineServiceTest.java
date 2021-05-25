package be.xplore.notifyme.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ILineRepo;
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
  private final Line line = new Line("note", 10);

  @Test
  void createLineSuccessful() {
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    when(lineRepo.create(any(Line.class), anyLong(), anyLong())).thenReturn(line);

    var result = lineService.createLine(createLineDto, principal);
    assertEquals(line,result);
  }

  @Test
  void createLineEventNotFound() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    doThrow(new CrudException("Could not find event for id 1")).when(lineRepo)
        .create(any(), anyLong(), anyLong());

    assertThrows(CrudException.class, () -> lineService.createLine(createLineDto, principal));
  }

  @Test
  void createLineFacilityNotFound() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    doThrow(new CrudException("Could not find facility for id 1")).when(lineRepo)
        .create(any(), anyLong(), anyLong());

    assertThrows(CrudException.class, () -> lineService.createLine(createLineDto, principal));
  }

  @Test
  void getAllLinesByEventSuccessful() {
    List<Line> lineList = new LinkedList<>();
    lineList.add(line);
    when(lineRepo.getAllByEventId(1L)).thenReturn(lineList);

    assertEquals(lineList, lineService.getAllLinesByEvent(1L));
  }

  @Test
  void getAllLinesByEventNotFound() {
    doThrow(CrudException.class).when(lineRepo).getAllByEventId(1L);

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