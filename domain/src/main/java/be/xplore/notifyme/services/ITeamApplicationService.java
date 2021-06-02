package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.TeamApplication;
import java.security.Principal;
import java.util.Set;

public interface ITeamApplicationService {

  void applyForEventLine(long teamId, Principal principal);

  Set<TeamApplication> getUserApplications(Principal principal);

  Set<TeamApplication> getUserApplicationsForOrgAdmin(Principal principal);
}
