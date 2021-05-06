package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.UserOrgApplication;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles CRUD logic for the user applications for an organisation.
 */
@Service
@AllArgsConstructor
public class UserOrgApplicationService {

  private final UserService userService;
  private final OrganisationService organisationService;

  public void applyToOrganisation(Long organisationId, Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    var organisation = organisationService.getOrganisation(organisationId);
    user.getAppliedOrganisations().add(new UserOrgApplication(organisation, user,
        OrgApplicationStatus.APPLIED));
    userService.updateUser(user);
  }

  public List<UserOrgApplication> getUserOrgApplications(Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    return user.getAppliedOrganisations();
  }
}
