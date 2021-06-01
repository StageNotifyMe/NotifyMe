package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import be.xplore.notifyme.services.security.OrganisationSecurityService;
import be.xplore.notifyme.services.systemmessages.SystemMessages;
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
  private final NotificationService notificationService;

  /**
   * Creates a user application for a certain organisation.
   *
   * @param organisationId the unique organisation id.
   * @param principal      from the request context.
   */
  @Override
  public void applyToOrganisation(Long organisationId, Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    organisationRepo
        .applyToOrganisation(organisationId, user.getUserId());
    sendUserApplicationNotificationToAllOrgManagers(organisationId, user);
  }

  private void sendUserApplicationNotificationToAllOrgManagers(Long organisationId,
                                                               User appliedUser) {
    var org = organisationService.getOrganisationIncAppliedUsers(organisationId);
    /*var message = notificationService.createMessage("User application.",
        appliedUser.getUserName() + " applied to join " + org.getName() + ".");
    org.getUsers()
        .forEach(ou -> notificationService.notifyUser(ou.getUser().getUserName(), message.getId()));*/

    org.getUsers().forEach(ou -> notificationService
        .createAndSendSystemNotification(ou.getUser().getUserId(),
            SystemMessages.USER_APPLICATION,
            new Object[] {ou.getUser().getUserName(), org.getName()}));
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
    if (accept) {
      var organisation = organisationService.addUserToOrganisation(organisationUserKey.getUserId(),
          organisationUserKey.getOrganisationId());
      organisationService.changeApplicationStatus(organisationUserKey.getUserId(),
          organisationUserKey.getOrganisationId(), OrgApplicationStatus.ACCEPTED);
      var user = userService.getUser(organisationUserKey.getUserId());
      sendUserApplicationApprovalNotification(user, organisation);
    } else {
      organisationService.changeApplicationStatus(organisationUserKey.getUserId(),
          organisationUserKey.getOrganisationId(), OrgApplicationStatus.REFUSED);
    }
  }

  private void sendUserApplicationApprovalNotification(User user, Organisation organisation) {
    /*var message = notificationService
        .createMessage("Application Approved",
            "A manager of " + organisation.getName() + " has approved your application.");
    notificationService.notifyUser(user.getUserName(), message.getId());*/

    notificationService
        .createAndSendSystemNotification(user.getUserId(), SystemMessages.APPLICATION_APPROVED,
            new Object[] {organisation.getName()});
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
