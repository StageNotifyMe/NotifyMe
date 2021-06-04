package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.TeamApplicationStatus;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ITeamRepo;
import be.xplore.notifyme.services.systemmessages.SystemMessages;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService implements ITeamService {

  private final ITeamRepo teamRepo;
  private final IUserService userService;
  private final INotificationService notificationService;


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
  public Team removeUserFromTeam(long teamId, String userId) {
    var team = teamRepo.removeUser(teamId, userId);
    var user = userService.getUserIncOrganisations(userId);
    notificationService.notifyOrganisationManagersForUserEvent(team.getLine().getEvent(), user,
        SystemMessages.USER_CANCELLED_ATTENDANCE);
    return team;
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

  @Override
  public List<Organisation> getAllAvailableOrganisations(long teamId) {
    return teamRepo.getAvailableOrganisations(teamId);
  }

  @Override
  public Set<Team> getTeamsForUser(String userId) {
    return teamRepo.getTeamsForUser(userId);
  }

  @Override
  public void deleteOrganisationFromTeam(long teamId, long organisationId) {
    teamRepo.deleteOrganisationFromTeam(teamId, organisationId);
  }

  @Override
  public Team changeApplicationStatus(String userId, Long teamId,
      TeamApplicationStatus applicationStatus) {
    return teamRepo.changeApplicationStatus(userId, teamId, applicationStatus);
  }


}
