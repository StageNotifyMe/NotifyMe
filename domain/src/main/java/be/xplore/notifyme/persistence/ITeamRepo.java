package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ITeamRepo {
  Team save(Team team);

  Optional<Team> findById(long teamId);

  void delete(long teamId);

  Team create(long lineId, long organisationId);

  Team addOrganisation(long teamId, long organisationId);

  Team addUser(long teamId, String userId);

  List<Organisation> getAvailableOrganisations(long teamId);

  void deleteOrganisationFromTeam(long teamId, long organisationId);
}
