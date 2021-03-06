package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.domain.TeamApplicationStatus;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaTeam;
import be.xplore.notifyme.jpaobjects.JpaTeamApplication;
import be.xplore.notifyme.jparepositories.JpaLineRepository;
import be.xplore.notifyme.jparepositories.JpaOrganisationRepository;
import be.xplore.notifyme.jparepositories.JpaTeamRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.ITeamRepo;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  private static final String TEAM_NOT_FOUND_MESSAGE = "Could not find team for id ";

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
        .orElseThrow(() -> new JpaNotFoundException(TEAM_NOT_FOUND_MESSAGE + teamId));
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
        .orElseThrow(() -> new JpaNotFoundException(TEAM_NOT_FOUND_MESSAGE + teamId));
    var jpaOrg = jpaOrganisationRepository.findById(organisationId)
        .orElseThrow(
            () -> new JpaNotFoundException("Could not find organisation for id " + organisationId));
    jpaTeam.getOrganisations().add(jpaOrg);
    return jpaTeamRepository.save(jpaTeam).toDomainBaseIncOrganisations();
  }

  @Override
  public Team addUser(long teamId, String userId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException(TEAM_NOT_FOUND_MESSAGE + teamId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    jpaTeam.getTeamMembers().add(jpaUser);
    return jpaTeamRepository.save(jpaTeam).toDomainBaseIncMembers();
  }

  @Override
  public Team removeUser(long teamId, String userId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException(TEAM_NOT_FOUND_MESSAGE + teamId));
    jpaTeam.getUserApplications()
        .removeIf(ua -> ua.getJpaTeamApplicationKey().getUserId().equals(userId));
    jpaTeam.getTeamMembers().removeIf(user -> user.getUserId().equals(userId));
    return jpaTeamRepository.save(jpaTeam).toDomainBaseIncOrganisations();
  }

  @Override
  public List<Organisation> getAvailableOrganisations(long teamId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException(TEAM_NOT_FOUND_MESSAGE + teamId));
    return jpaOrganisationRepository.findAllByTeamsNotContaining(jpaTeam).stream().map(
        JpaOrganisation::toDomainBase).collect(Collectors.toList());
  }

  @Override
  public void deleteOrganisationFromTeam(long teamId, long organisationId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException(TEAM_NOT_FOUND_MESSAGE + teamId));
    var jpaOrg =
        jpaTeam.getOrganisations().stream().filter(o -> o.getId() == organisationId).findFirst()
            .orElseThrow(() -> new JpaNotFoundException(
                "Could not find organisation with id " + organisationId + " in team with id "
                    + teamId));
    jpaTeam.getOrganisations().remove(jpaOrg);
    jpaTeamRepository.save(jpaTeam);
  }

  @Override
  public void applyToTeam(long teamId, String userId) {
    var jpaTeam = jpaTeamRepository.findById(teamId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find team with id: " + teamId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user with id: " + userId));
    jpaTeam.getUserApplications().add(new JpaTeamApplication(jpaTeam, jpaUser,
        TeamApplicationStatus.APPLIED));
    jpaTeamRepository.save(jpaTeam);
  }

  @Override
  public Set<TeamApplication> getUserApplicationsForOrganisationManager(String userId) {
    return jpaTeamRepository.getTeamApplicationsForOrgManager(userId).stream()
        .map(JpaTeamApplication::toDomainBase).collect(Collectors.toSet());
  }

  @Override
  public Set<Team> getTeamsForUser(String userId) {
    return jpaTeamRepository.getTeamApplicationsForUser(userId).stream()
        .map(JpaTeam::toDomainBase).collect(Collectors.toSet());
  }

  @Override
  public Team changeApplicationStatus(String userId, Long teamId,
      TeamApplicationStatus applicationStatus) {
    var jpaTeam = jpaTeamRepository.findById(teamId).orElseThrow();
    jpaTeam.getUserApplications().stream()
        .filter(application -> application.getJpaTeamApplicationKey().getUserId().equals(userId)
            && application.getJpaTeamApplicationKey().getTeamId().equals(teamId))
        .findFirst()
        .ifPresentOrElse(
            teamApplication -> teamApplication.setApplicationStatus(applicationStatus),
            () -> {
              throw new CrudException("Could not find application to set status.");
            });
    return jpaTeamRepository.save(jpaTeam).toDomainBase();
  }
}
