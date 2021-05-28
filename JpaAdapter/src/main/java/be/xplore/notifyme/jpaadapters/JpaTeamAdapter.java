package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaTeam;
import be.xplore.notifyme.jparepositories.JpaLineRepository;
import be.xplore.notifyme.jparepositories.JpaOrganisationRepository;
import be.xplore.notifyme.jparepositories.JpaTeamRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.ITeamRepo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTeamAdapter implements ITeamRepo {
  private final JpaTeamRepository jpaTeamRepository;
  private final JpaLineRepository jpaLineRepository;
  private final JpaOrganisationRepository jpaOrganisationRepository;
  private final JpaUserRepository jpaUserRepository;

  private final String couldNotFindTeam = "Could not find team for id ";

  @Override
  public Team save(Team team) {
    return jpaTeamRepository.save(new JpaTeam(team)).toDomain();
  }

  @Override
  public Optional<Team> findById(long teamId) {
    return jpaTeamRepository.findById(teamId).map(JpaTeam::toDomainBaseIncOrganisations);
  }

  @Override
  public void delete(long teamId) {
    var team = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException(couldNotFindTeam + teamId));
    jpaTeamRepository.delete(team);
  }

  @Override
  public Team create(long lineId, long organisationId) {
    var jpaLine = jpaLineRepository.findById(lineId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find line for id " + lineId));
    var jpaOrg = jpaOrganisationRepository.findById(organisationId).orElseThrow(
        () -> new JpaNotFoundException("Could not find organisation for id " + organisationId));
    var jpaTeam = new JpaTeam(jpaLine, jpaOrg);
    return jpaTeamRepository.save(jpaTeam).toDomainBase();
  }

  @Override
  public Team addOrganisation(long teamId, long organisationId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException(couldNotFindTeam + teamId));
    var jpaOrg = jpaOrganisationRepository.findById(organisationId)
        .orElseThrow(
            () -> new JpaNotFoundException("Could not find organisation for id " + organisationId));
    jpaTeam.getOrganisations().add(jpaOrg);
    return jpaTeamRepository.save(jpaTeam).toDomainBaseIncOrganisations();
  }

  @Override
  public Team addUser(long teamId, String userId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException(couldNotFindTeam + teamId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    jpaTeam.getTeamMembers().add(jpaUser);
    return jpaTeamRepository.save(jpaTeam).toDomainBaseIncMembers();
  }

  @Override
  public List<Organisation> getAvailableOrganisations(long teamId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find team for id " + teamId));
    return jpaOrganisationRepository.findAllByTeamsNotContaining(jpaTeam).stream().map(
        JpaOrganisation::toDomainBase).collect(Collectors.toList());
  }

  @Override
  public void deleteOrganisationFromTeam(long teamId, long organisationId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find team for id " + teamId));
    var jpaOrg =
        jpaTeam.getOrganisations().stream().filter(o -> o.getId() == organisationId).findFirst()
            .orElseThrow(() -> new JpaNotFoundException(
                "Could not find organisation with id " + organisationId + " in team with id "
                    + teamId));
    jpaTeam.getOrganisations().remove(jpaOrg);
    jpaTeamRepository.save(jpaTeam);
  }
}
