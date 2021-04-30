package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IEventRepo;
import be.xplore.notifyme.persistence.IVenueRepo;
import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class EventServiceTest {
  @Autowired
  private EventService eventService;
  @MockBean
  private VenueService venueService;
  @MockBean
  private IEventRepo eventRepo;
  @MockBean
  private IVenueRepo venueRepo;

  @Test
  void createEventSuccessful() {
    when(venueService.getVenue(1)).thenReturn(getTestVenue());

    var ced = getTestCreateEventDto();
    assertEquals(ResponseEntity.status(HttpStatus.CREATED).build(), eventService.createEvent(ced));
  }

  @Test
  void createEventVenueNotFound() {
    doThrow(CrudException.class).when(venueService).getVenue(1);

    var ced = getTestCreateEventDto();
    assertThrows(CrudException.class, () ->
        eventService.createEvent(ced));
  }

  private CreateEventDto getTestCreateEventDto() {
    return new CreateEventDto("Evenement", "een evenement", "een artiest",
        "2021-04-30T06:45:30", 1);
  }

  private Venue getTestVenue() {
    Address address = new Address("Teststraat 10", "2000", "Antwerpen", "België");
    return new Venue(1, "Zaal", "een zaal", address, new LinkedList<>());
  }

}