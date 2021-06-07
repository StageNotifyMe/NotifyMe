package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import java.security.Principal;
import java.util.List;

public interface IEventService {

  Event createEvent(String title, String description, String artist, String dateTime,
                    long venueId, Principal principal);

  Event getEvent(long eventId);

  Event getEventAndVerifyLineManagerPermission(long eventId, Principal principal);

  List<Event> getAllEventsForLineManager(String userId);

  void promoteToLineManager(String userId, long eventId);

  Event updateEventStatus(long eventId, EventStatus eventStatus);
}
