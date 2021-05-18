package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Event;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventRepo {

  Event save(Event event);

  Optional<Event> findById(long eventId);

  Event addLineManager(long eventId, String userId);

  Optional<Event> findByIdWithLineManagers(long eventId);

  List<Event> findAllForLineManager(String userId);
}
