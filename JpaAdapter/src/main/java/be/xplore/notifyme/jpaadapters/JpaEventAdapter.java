package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.jpaobjects.JpaEvent;
import be.xplore.notifyme.jparepositories.JpaEventRepository;
import be.xplore.notifyme.persistence.IEventRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaEventAdapter implements IEventRepo {

  private final JpaEventRepository jpaEventRepository;


  @Override
  public Event save(Event event) {
    return jpaEventRepository.save(new JpaEvent(event)).toDomain();
  }

  @Override
  public Optional<Event> findById(long eventId) {
    return jpaEventRepository.findById(eventId).map(JpaEvent::toDomain);
  }
}
