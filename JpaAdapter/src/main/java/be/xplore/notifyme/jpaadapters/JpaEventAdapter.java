package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.jpaobjects.JpaEvent;
import be.xplore.notifyme.jparepositories.JpaEventRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.IEventRepo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaEventAdapter implements IEventRepo {

  private final JpaEventRepository jpaEventRepository;
  private final JpaUserRepository jpaUserRepository;


  @Override
  public Event save(Event event) {
    return jpaEventRepository.save(new JpaEvent(event)).toDomainBase();
  }

  @Override
  public Optional<Event> findById(long eventId) {
    return jpaEventRepository.findById(eventId).map(JpaEvent::toDomainBase);
  }

  @Override
  public Event addLineManager(long eventId, String userId) {
    var jpaEvent = jpaEventRepository.findById(eventId).orElseThrow();
    var jpaUser = jpaUserRepository.findById(userId).orElseThrow();
    jpaEvent.getLineManagers().add(jpaUser);
    return jpaEventRepository.save(jpaEvent).toDomainBase();
  }

  @Override
  public Optional<Event> findByIdWithLineManagers(long eventId) {
    return jpaEventRepository.findById(eventId).map(JpaEvent::toDomainIncLineManagers);
  }

  @Override
  public List<Event> findAllForLineManager(String userId) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new CrudException("Could not find user for id " + userId));
    return jpaEventRepository.findAllByLineManagersContains(jpaUser).stream()
        .map(JpaEvent::toDomainBase).collect(Collectors.toList());
  }
}
