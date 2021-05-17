package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IEventRepo;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService implements IEventService {

  private final VenueService venueService;
  private final UserService userService;
  private final IEventRepo eventRepo;
  private final TokenService tokenService;

  /**
   * Method to create an event from a createEventDTO (comes from API).
   *
   * @param createEventDto contains all the necessary data to create an event.
   * @return HTTP Response
   */
  @Override
  public Event createEvent(CreateEventDto createEventDto, Principal principal) {
    var venue = venueService.getVenue(createEventDto.getVenueId());
    var event = new Event(createEventDto.getTitle(), createEventDto.getDescription(),
        createEventDto.getArtist(), createEventDto.getDateTime(), venue);
    eventRepo.save(event);
    makeCreatorLineManager(event, principal);
    return event;
  }

  /**
   * Gets an event object based on an eventId.
   *
   * @param eventId id of the event.
   * @return event if successful, or CrudException if not found.
   */
  @Override
  public Event getEvent(long eventId) {
    return eventRepo.findById(eventId)
        .orElseThrow(() -> new CrudException("Could not retrieve event for id " + eventId));
  }

  /**
   * Checks whether a user is line manager of an event and returns the event if true.
   *
   * @param eventId   to get.
   * @param principal to check.
   * @return event if permission is ok.
   */
  @Override
  public Event getEventAndVerifyLineManagerPermission(long eventId, Principal principal) {
    var event = eventRepo.findById(eventId)
        .orElseThrow(() -> new CrudException("Could not retireve event for id " + eventId));
    var token = tokenService.getIdToken(principal);
    var user = userService.getUser(token.getSubject());
    if (event.getLineManagers().contains(user)) {
      return event;
    } else {
      throw new UnauthorizedException(String.format("User %s is not a line manager of event %d",
          token.getSubject(), eventId));
    }
  }

  /**
   * A line manager can edit lines for a specific event, this method returns all the events where
   * the line manager has access to the lines.
   *
   * @param userId of a line manager.
   * @return list of all events accessible to the line manager.
   */
  @Override
  public List<Event> getAllEventsForLineManager(String userId) {
    return userService.getUser(userId).getEvents();
  }

  /**
   * Adds given user to given event as line manager, also grants line manager permissions.
   *
   * @param userId  id of the user to add as line manager.
   * @param eventId id of the event to add the user to as line manager.
   */
  @Override
  public void promoteToLineManager(String userId, long eventId) {
    var user = userService.getUser(userId);
    var event = this.getEvent(eventId);
    if (!event.getLineManagers().contains(user)) {
      event.getLineManagers().add(user);
      eventRepo.save(event);
    } else {
      throw new SaveToDatabaseException("User is already line manager of this event!");
    }
    userService.grantUserRole(userId, "line_manager");
  }

  /**
   * Makes an event's creator, line manager of this event.
   *
   * @param event     to which the line manager should have access.
   * @param principal authentication token.
   */
  private void makeCreatorLineManager(Event event, Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    event.setLineManagers(new HashSet<>());
    event.getLineManagers().add(user);
    eventRepo.save(event);
  }
}
