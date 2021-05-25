package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.services.security.OrganisationSecurityService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserOrgService implements IUserOrgService {

  private final UserService userService;
  private final OrganisationSecurityService organisationSecurityService;
  private final OrganisationService organisationService;

  /**
   * Gets organisations where a user is organisation manager.
   *
   * @param principal injected by security.
   * @return a list of organisations where the user is manager.
   */
  @Override
  public List<Organisation> getOrgManagerOrganisations(Principal principal) {
    var user = userService.getUserFromprincipalIncOrganisations(principal);
    var organisations = new ArrayList<Organisation>();
    for (var orgUser : user.getOrganisations()) {
      if (orgUser.isOrganisationLeader()) {
        organisations.add(orgUser.getOrganisation());
      }
    }
    return organisations;
  }

  /**
   * Gets an organisation the principal is manager of.
   *
   * @param orgId     the unique id of the organisation.
   * @param principal representation of authorized user.
   * @return the organisation and its attached info.
   */
  @Override
  public Organisation getOrgInfoAsManager(Long orgId, Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    var organisation = organisationService.getOrganisation(orgId);
    organisationSecurityService.checkUserIsOrgManager(user, organisation);
    return organisation;
  }
}
