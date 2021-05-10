package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.service.security.OrganisationSecurityService;
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
  private final OrganisationSecurityService organisationSecurityService;

  /**
   * Creates a user application for a certain organisation.
   *
   * @param organisationId the unique organisation id.
   * @param principal      from the request context.
   */
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

  public List<UserOrgApplication> getOrgApplications(Long organisationId, Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    var organisation = organisationService.getOrganisation(organisationId);
    organisationSecurityService.checkUserIsOrgManager(user,organisation);
    return organisation.getAppliedUsers();
  }
}
