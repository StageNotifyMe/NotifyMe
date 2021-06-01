package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.persistence.ITeamRepo;
import java.security.Principal;
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
    notifyOrganisationManagersOfApplication(teamId, user);
  }

  private void notifyOrganisationManagersOfApplication(long teamId, User user) {
    var team = teamService.getTeam(teamId);
    OrganisationUser foundOrgUser =
        user.getOrganisations().stream().filter(ou -> team.getOrganisations().stream()
            .anyMatch(organisation -> organisation.getId().equals(ou.getOrganisation().getId())))
            .findFirst().orElseThrow();

    var message =
        notificationService
            .createMessage("User applied for event team.",
                user.getUserName() + " has applied to a team for the event " + team.getLine()
                    .getEvent().getTitle());
    //notificationService.notifyOrganisationManagers(foundOrgUser.getOrganisation().getId(),);
  }

}
