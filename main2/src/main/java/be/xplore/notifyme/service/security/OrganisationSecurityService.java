package be.xplore.notifyme.service.security;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.UnauthorizedException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OrganisationSecurityService {

  public void checkUserIsOrgManager(User user, Organisation organisation) {
    Optional<OrganisationUser> orgUser = organisation.getUsers().stream()
        .filter(ou -> ou.getUser().getUserId().equals(user.getUserId())).findFirst();
    if (orgUser.isEmpty() || !orgUser.get().isOrganisationLeader()) {
      throw new UnauthorizedException("User is not part of the organization.");
    }
  }
}
