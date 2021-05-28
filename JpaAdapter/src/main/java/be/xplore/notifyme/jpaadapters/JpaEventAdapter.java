package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaEvent;
import be.xplore.notifyme.jpaobjects.JpaLine;
import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaUser;
import be.xplore.notifyme.jparepositories.JpaEventRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.jparepositories.JpaVenueRepository;
import be.xplore.notifyme.persistence.IEventRepo;
import java.util.ArrayList;
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
  private final JpaVenueRepository jpaVenueRepository;

  private static final String EVENT_NOT_FOUND_MESSAGE = "Could not find event for id ";

  @Override
  public Event save(Event event) {
    var jpaVenue = jpaVenueRepository.findById(event.getVenue().getId()).orElseThrow(
        () -> new SaveToDatabaseException(
            "Could not retrieve venue to which the event was supposed to be linked"));
    return jpaEventRepository.save(new JpaEvent(event, jpaVenue)).toDomainBase();
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

  @Override
  public Event updateEventStatus(long eventId, EventStatus eventStatus) {
    var jpaEvent = jpaEventRepository.findById(eventId)
        .orElseThrow(() -> new JpaNotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId));
    jpaEvent.setEventStatus(eventStatus);
    return jpaEventRepository.save(jpaEvent).toDomainBase();
  }

  @Override
  public List<Long> getAllOrganisationIds(long eventId) {
    var jpaEvent = jpaEventRepository.findById(eventId)
        .orElseThrow(() -> new JpaNotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId));
    List<Long> orgIds = new ArrayList<>();
    for (JpaLine line : jpaEvent.getLines()) {
      for (JpaOrganisation organisation : line.getTeam().getOrganisations()) {
        orgIds.add(organisation.getId());
      }
    }
    return orgIds;
  }


  @Override
  public List<User> getAttendingMembers(long eventId) {
    var jpaEvent = jpaEventRepository.findById(eventId)
        .orElseThrow(() -> new JpaNotFoundException(EVENT_NOT_FOUND_MESSAGE + eventId));
    var attendingMembers = new ArrayList<JpaUser>();
    for (JpaLine line : jpaEvent.getLines()) {
      attendingMembers.addAll(line.getTeam().getTeamMembers());
    }
    return attendingMembers.stream().map(JpaUser::toDomainBase).collect(Collectors.toList());
  }
}
