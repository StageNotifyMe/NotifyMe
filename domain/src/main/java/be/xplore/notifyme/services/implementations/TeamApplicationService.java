package be.xplore.notifyme.services.implementations;

import be.xplore.notifyme.domain.SystemMessages;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.domain.TeamApplicationKey;
import be.xplore.notifyme.domain.TeamApplicationStatus;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.persistence.ITeamRepo;
import be.xplore.notifyme.services.INotificationService;
import be.xplore.notifyme.services.ITeamApplicationService;
import be.xplore.notifyme.services.ITeamService;
import be.xplore.notifyme.services.IUserService;
import java.security.Principal;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class TeamApplicationService implements ITeamApplicationService {

  private final ITeamService teamService;
  private final ITeamRepo teamRepo;
  private final IUserService userService;
  private final INotificationService notificationService;

  @Override
  public void applyForEventLine(long teamId, Principal principal) {
    var user = userService.getUserFromprincipalIncOrganisations(principal);
    teamRepo.applyToTeam(teamId, user.getUserId());
    var team = teamService.getTeam(teamId);
    notificationService.notifyOrganisationManagersForUserEvent(team.getLine().getEvent(), user,
        SystemMessages.USER_TEAM_APPLICATION);
  }

  @Override
  public Set<TeamApplication> getUserApplications(Principal principal) {
    var user = userService.getUserFromPrincipalIncTeamApplications(principal);
    return user.getTeamApplications();
  }

  @Override
  public Set<TeamApplication> getUserApplicationsForOrgAdmin(Principal principal) {
    var user = userService.getUserFromprincipalIncOrganisations(principal);
    return teamRepo.getUserApplicationsForOrganisationManager(user.getUserId());
  }

  @Override
  public void handleTeamApplication(TeamApplicationKey teamApplicationKey, boolean accept,
                                    Principal principal) {
    if (accept) {
      var team = teamService.addUserToTeam(teamApplicationKey.getTeamId(),
          teamApplicationKey.getUserId());
      teamService.changeApplicationStatus(teamApplicationKey.getUserId(),
          teamApplicationKey.getTeamId(), TeamApplicationStatus.ACCEPTED);
      var user = userService.getUser(teamApplicationKey.getUserId());
      sendUserApplicationApprovalNotification(user, team);
    } else {
      teamService.changeApplicationStatus(teamApplicationKey.getUserId(),
          teamApplicationKey.getTeamId(), TeamApplicationStatus.REFUSED);
    }
  }

  private void sendUserApplicationApprovalNotification(User user, Team team) {
    notificationService
        .createAndSendSystemNotification(user.getUserId(), SystemMessages.TEAM_APPLICATION_APPROVED,
            new Object[] {team.getLine().getEvent().getTitle()});
  }

}
