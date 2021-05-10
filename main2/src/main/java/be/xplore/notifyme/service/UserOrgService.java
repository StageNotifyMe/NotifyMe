package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.service.security.OrganisationSecurityService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserOrgService {

  private final UserService userService;
  private final OrganisationSecurityService organisationSecurityService;
  private final OrganisationService organisationService;

  /**
   * Gets organisations where a user is organisation manager.
   *
   * @param principal injected by security.
   * @return a list of organisations where the user is manager.
   */
  public List<Organisation> getOrgManagerOrganisations(Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    var organisations = new ArrayList<Organisation>();
    for (var orgUser : user.getOrganisation()) {
      if (orgUser.isOrganisationLeader()) {
        organisations.add(orgUser.getOrganisation());
      }
    }
    return organisations;
  }

  public Organisation getOrgInfoAsManager(Long orgId, Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    var organisation = organisationService.getOrganisation(orgId);
    organisationSecurityService.checkUserIsOrgManager(user, organisation);
    return organisation;
  }
}
