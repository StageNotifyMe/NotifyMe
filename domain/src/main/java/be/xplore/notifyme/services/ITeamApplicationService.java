package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.domain.TeamApplicationKey;
import java.security.Principal;
import java.util.Set;

public interface ITeamApplicationService {

  void applyForEventLine(long teamId, Principal principal);

  Set<TeamApplication> getUserApplications(Principal principal);

  Set<TeamApplication> getUserApplicationsForOrgAdmin(Principal principal);

  void handleTeamApplication(TeamApplicationKey teamApplicationKey, boolean accept,
      Principal principal);
}
