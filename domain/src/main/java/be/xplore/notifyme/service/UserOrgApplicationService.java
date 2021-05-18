package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.exception.OrgApplicationNotFoundException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
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
public class UserOrgApplicationService implements IUserOrgApplicationService {

  private final UserService userService;
  private final OrganisationService organisationService;
  private final OrganisationSecurityService organisationSecurityService;
  private final IOrganisationRepo organisationRepo;

  /**
   * Creates a user application for a certain organisation.
   *
   * @param organisationId the unique organisation id.
   * @param principal      from the request context.
   */
  @Override
  public void applyToOrganisation(Long organisationId, Principal principal) {
    /*var user = userService.getUserFromPrincipal(principal);
    var organisation = organisationService.getOrganisation(organisationId);
    user.getAppliedOrganisations().add(new UserOrgApplication(organisation, user,
        OrgApplicationStatus.APPLIED));
    userService.updateUser(user);*/
    organisationRepo
        .applyToOrganisation(organisationId, userService.getUserIdFromPrincipal(principal));
  }

  @Override
  public List<UserOrgApplication> getUserOrgApplications(Principal principal) {
    var user = userService.getUserFromPrincipalIncAppliedUsers(principal);
    return user.getAppliedOrganisations();
  }

  /**
   * Get a list of user org applications for an organisation the principal is manager of.
   *
   * @param organisationId the unique id of the organisation.
   * @param principal      representation of authorized user.
   * @return a list of user organisation applications.
   */
  @Override
  public List<UserOrgApplication> getOrgApplications(Long organisationId, Principal principal) {
    var organisation = organisationService.getOrganisationIncAppliedUsers(organisationId);
    secureOrgManagerRequestFromPrincipal(organisation, principal);
    return organisation.getAppliedUsers();
  }

  /**
   * Accept or deny a users organisation application.
   *
   * @param organisationUserKey the applications unique key.
   * @param accept              if the request should be accepted or denied.
   * @param principal           representation of the authorized organisation admin.
   */
  @Override
  public void respondToApplication(OrganisationUserKey organisationUserKey, boolean accept,
                                   Principal principal) {
    var organisation =
        organisationService.getOrganisationIncAppliedUsers(organisationUserKey.getOrganisationId());
    secureOrgManagerRequestFromPrincipal(organisation, principal);
    organisation.getAppliedUsers().stream().filter(
        application -> application.getAppliedUser().getUserId()
            .equals(organisationUserKey.getUserId())).findFirst()
        .ifPresentOrElse(userOrgApplication -> {
          if (accept) {
            userOrgApplication.setApplicationStatus(OrgApplicationStatus.ACCEPTED);
            var user = userService.getUser(organisationUserKey.getUserId());
            organisation.getUsers().add(new OrganisationUser(organisation, user, false));
          } else {
            userOrgApplication.setApplicationStatus(OrgApplicationStatus.REFUSED);
          }
        }, () -> {
          throw new OrgApplicationNotFoundException("Application does not exist.");
        });
    organisationService.save(organisation);
  }

  /**
   * Secure a request for an organisation manager of an organisation. Gets the domain user from the
   * user principal and passes user and org to security service.
   *
   * @param organisation that user should be manager of.
   * @param principal    representation of the authenticated user.
   */
  private void secureOrgManagerRequestFromPrincipal(Organisation organisation,
                                                    Principal principal) {
    var user = userService.getUserFromprincipalIncOrganisations(principal);
    organisationSecurityService.checkUserIsOrgManager(user, organisation);
  }
}
