package be.xplore.notifyme.services.implementations;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.SystemMessages;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IEventRepo;
import be.xplore.notifyme.services.IEventService;
import be.xplore.notifyme.services.INotificationService;
import java.security.Principal;
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
  private final INotificationService notificationService;

  /**
   * Method to create an event from a createEventDTO (comes from API).
   *
   * @return HTTP Response
   */
  @Override
  public Event createEvent(String title, String description, String artist, String dateTime,
                           long venueId, Principal principal) {
    var venue = venueService.getVenue(venueId);
    var event = new Event(title, description,
        artist, dateTime, venue);
    event = eventRepo.save(event);
    makeCreatorLineManager(event.getId(), principal);
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
    return eventRepo.findByIdWithLineManagersAndVenue(eventId)
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
    var event = eventRepo.findByIdWithLineManagersAndVenue(eventId)
        .orElseThrow(() -> new CrudException("Could not retrieve event for id " + eventId));
    var token = tokenService.getIdToken(principal);
    if (event.getLineManagers().stream().anyMatch(u -> u.getUserId().equals(token.getSubject()))) {
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
    return eventRepo.findAllForLineManager(userId);
  }

  /**
   * Makes an event's creator, line manager of this event.
   *
   * @param eventId   to which the line manager should have access.
   * @param principal authentication token.
   */
  private void makeCreatorLineManager(long eventId, Principal principal) {
    var userId = userService.getUserIdFromPrincipal(principal);
    eventRepo.addLineManager(eventId, userId);
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
      eventRepo.addLineManager(eventId, userId);
    } else {
      throw new SaveToDatabaseException("User is already line manager of this event!");
    }
    userService.grantUserRole(userId, "line_manager");
  }

  @Override
  public Event updateEventStatus(long eventId, EventStatus eventStatus) {
    var updatedEvent = eventRepo.updateEventStatus(eventId, eventStatus);
    if (eventStatus == EventStatus.CANCELED) {
      notifyForCanceledEvent(updatedEvent);
    }
    return updatedEvent;
  }

  private void notifyForCanceledEvent(Event updatedEvent) {
    var message = SystemMessages.CANCEL_EVENT;
    //notify organisation managers
    notificationService
        .notifyOrganisationManagersForCancelEvent(updatedEvent, message);
    //notify attending members
    var attendingMembers = eventRepo.getAttendingMembers(updatedEvent.getId());
    notificationService.notifyUsers(attendingMembers, message, new Object[] {updatedEvent});
    //notify line managers
    notificationService
        .notifyUsers(updatedEvent.getLineManagers(), message, new Object[] {updatedEvent});
  }
}
