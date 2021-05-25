package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Team;

public interface ITeamService {
  Team createTeam(long lineId, long organisationId);

  Team addOrganisationToTeam(long teamId, long organisationId);

  Team addUserToTeam(long teamId, String userId);

  void deleteTeam(long teamId);

  Team getTeam(long teamId);
}
