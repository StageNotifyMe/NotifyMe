package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.domain.TeamApplicationStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public interface ITeamRepo {

  Team save(Team team);

  Optional<Team> findById(long teamId);

  void delete(long teamId);

  Team create(long lineId, long organisationId);

  Team addOrganisation(long teamId, long organisationId);

  Team addUser(long teamId, String userId);

  Team removeUser(long teamId, String userId);

  List<Organisation> getAvailableOrganisations(long teamId);

  void deleteOrganisationFromTeam(long teamId, long organisationId);

  void applyToTeam(long teamId, String userId);

  Set<Team> getTeamsForUser(String userId);

  Set<TeamApplication> getUserApplicationsForOrganisationManager(String userId);

  Team changeApplicationStatus(String userId, Long teamId,
      TeamApplicationStatus applicationStatus);
}
