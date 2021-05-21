package be.xplore.notifyme.communication;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.dto.CreateEventDto;
import java.security.Principal;
import java.util.List;

public interface IEventService {

  Event createEvent(CreateEventDto createEventDto, Principal principal);

  Event getEvent(long eventId);

  Event getEventAndVerifyLineManagerPermission(long eventId, Principal principal);

  List<Event> getAllEventsForLineManager(String userId);

  void promoteToLineManager(String userId, long eventId);
}
