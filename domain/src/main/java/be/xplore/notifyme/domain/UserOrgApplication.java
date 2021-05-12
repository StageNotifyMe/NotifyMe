package be.xplore.notifyme.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserOrgApplication {

  private OrganisationUserKey organisationUserKey;
  private User appliedUser;
  private Organisation appliedOrganisation;
  private OrgApplicationStatus applicationStatus;

  /**
   * Constructor that creates a new OrganisationUser. It automatically fills the key by the user and
   * org data. A boolean should be passed that represents if the user is an organisation manager or
   * not.
   *
   * @param organisation      the organisation the user should be added to.
   * @param user              the user that should be added to an organisation
   * @param applicationStatus the status of the users application.
   */
  public UserOrgApplication(Organisation organisation, User user,
      OrgApplicationStatus applicationStatus) {
    this.organisationUserKey = new OrganisationUserKey(user.getUserId(), organisation.getId());
    this.appliedUser = user;
    this.appliedOrganisation = organisation;
    this.applicationStatus = applicationStatus;
  }
}
