package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IEventRepo;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

  private final VenueService venueService;
  private final UserService userService;
  private final IEventRepo eventRepo;

  /**
   * Method to create an event from a createEventDTO (comes from API).
   *
   * @param createEventDto contains all the necessary data to create an event.
   * @return HTTP Response
   */
  public ResponseEntity<Object> createEvent(CreateEventDto createEventDto) {
    try {
      var venue = venueService.getVenue(createEventDto.getVenueId());
      var event = new Event(createEventDto.getTitle(), createEventDto.getDescription(),
          createEventDto.getArtist(), createEventDto.getDateTime(), venue);
      eventRepo.save(event);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  /**
   * Gets an event object based on an eventId.
   *
   * @param eventId id of the event.
   * @return event if successful, or CrudException if not found.
   */
  public Event getEvent(long eventId) {
    var event = eventRepo.findById(eventId);
    if (event.isPresent()) {
      return event.get();
    }
    throw new CrudException("Could not retrieve event for id " + eventId);
  }

  public List<Event> getAllEventsForLineManager(String userId) {
    try {
      var user = userService.getUser(userId);
      return eventRepo.getAllByLineManagersContains(user);
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  public void makeUserLineManager(Event event, Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    event.getLineManagers().add(user);
    eventRepo.save(event);
  }
}
