package be.xplore.notifyme.services;

import java.security.Principal;

public interface ITeamApplicationService {

  void applyForEventLine(long teamId, Principal principal);
}
