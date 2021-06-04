package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.jpaobjects.JpaLine;
import be.xplore.notifyme.jparepositories.JpaEventRepository;
import be.xplore.notifyme.jparepositories.JpaFacilityRepository;
import be.xplore.notifyme.jparepositories.JpaLineRepository;
import be.xplore.notifyme.jparepositories.JpaTeamRepository;
import be.xplore.notifyme.persistence.ILineRepo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaLineAdapter implements ILineRepo {

  private final JpaLineRepository jpaLineRepository;
  private final JpaEventRepository jpaEventRepository;
  private final JpaFacilityRepository jpaFacilityRepository;
  private final JpaTeamRepository jpaTeamRepository;

  @Override
  public Optional<Line> findById(long lineId) {
    return jpaLineRepository.findById(lineId)
        .map(JpaLine::toDomainBase);
  }

  @Override
  public Line save(Line line) {
    return jpaLineRepository.save(new JpaLine(line)).toDomainBase();
  }

  @Override
  public List<Line> getAllByEventId(long eventId) {
    var jpaEvent = jpaEventRepository.findById(eventId)
        .orElseThrow(() -> new CrudException("Could not find event for id " + eventId));
    return jpaLineRepository.getAllByEvent(jpaEvent).stream().map(JpaLine::toDomainBaseIncFacility)
        .collect(Collectors.toList());
  }

  @Override
  public Line create(Line line, long eventId, long facilityId) {
    var jpaEvent = jpaEventRepository.findById(eventId)
        .orElseThrow(() -> new CrudException("Could not find event for id " + eventId));
    var jpaFacility = jpaFacilityRepository.findById(facilityId)
        .orElseThrow(() -> new CrudException("Could not find facility for id " + facilityId));
    var jpaLine =  jpaLineRepository.save(new JpaLine(line, jpaEvent, jpaFacility));
    jpaTeamRepository.updateTeamLineMapping(jpaLine.getId(), jpaLine.getTeam().getId());
    return jpaLine.toDomainBase();
  }

  @Override
  public List<Line> getAvailableLinesForUser(String userId) {
    return jpaLineRepository.getAllAvailableLinesForUser(userId).stream()
        .map(JpaLine::toDomainBaseIncEvent).collect(Collectors.toList());
  }

}
