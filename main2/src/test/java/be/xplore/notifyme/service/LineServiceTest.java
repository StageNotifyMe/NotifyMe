package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ILineRepo;
import java.time.LocalDateTime;
import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
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

  private final CreateLineDto createLineDto = new CreateLineDto("note", 10, 1L, 1L);
  private final Event event =
      new Event(1L, "titel", "descriptie", "artiest", LocalDateTime.now(), new Venue(),
          new LinkedList<>());
  private final Facility facility =
      new Facility(1L, "descriptie", "locatie", 1, 20, new Venue(), new LinkedList<>());

  @Test
  void createLineSuccessful() {
    when(eventService.getEvent(1L)).thenReturn(event);
    when(facilityService.getFacility(1L)).thenReturn(facility);
    when(lineRepo.save(any(Line.class))).thenAnswer(
        (Answer<Object>) invocation -> invocation.getArguments()[0]);

    var result = lineService.createLine(createLineDto);
    assertEquals("note", result.getNote());
    assertEquals(10, result.getRequiredStaff());
    assertEquals(event.getId(), result.getEvent().getId());
    assertEquals(facility.getId(), result.getFacility().getId());
  }

  @Test
  void createLineEventNotFound() {
    doThrow(new CrudException("Could not find event for id 1")).when(eventService).getEvent(1L);
    when(facilityService.getFacility(1L)).thenReturn(facility);
    when(lineRepo.save(any(Line.class))).thenAnswer(
        (Answer<Object>) invocation -> invocation.getArguments()[0]);

    assertThrows(CrudException.class, () -> {
      lineService.createLine(createLineDto);
    });
  }

  @Test
  void createLineFacilityNotFound() {
    when(eventService.getEvent(1L)).thenReturn(event);
    doThrow(new CrudException("Could not find facility for id 1")).when(facilityService)
        .getFacility(1L);
    when(lineRepo.save(any(Line.class))).thenAnswer(
        (Answer<Object>) invocation -> invocation.getArguments()[0]);

    assertThrows(CrudException.class, () -> {
      lineService.createLine(createLineDto);
    });
  }
}