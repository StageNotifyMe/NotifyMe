package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ITeamRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService implements ITeamService {
  private final ITeamRepo teamRepo;

  @Override
  public Team createTeam(long lineId, long organisationId) {
    return teamRepo.create(lineId, organisationId);
  }

  @Override
  public Team addOrganisationToTeam(long teamId, long organisationId) {
    return teamRepo.addOrganisation(teamId, organisationId);
  }

  @Override
  public Team addUserToTeam(long teamId, String userId) {
    return teamRepo.addUser(teamId, userId);
  }


  @Override
  public void deleteTeam(long teamId) {
    teamRepo.delete(teamId);
  }

  @Override
  public Team getTeam(long teamId) {
    return teamRepo.findById(teamId).orElseThrow(() ->
        new CrudException("Could not find team for id " + teamId)
    );
  }


}
