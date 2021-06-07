package be.xplore.notifyme.dto.organisationapplication;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.UserOrgApplication;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationUsernameDto {

  private OrganisationUserKey organisationUserKey;
  private OrgApplicationStatus applicationStatus;
  private String userName;

  /**
   * Constructs the application for an organisation dto with only relevant data for organisation.
   *
   * @param userOrgApplication to convert.
   */
  public ApplicationUsernameDto(UserOrgApplication userOrgApplication) {
    this.organisationUserKey = userOrgApplication.getOrganisationUserKey();
    this.applicationStatus = userOrgApplication.getApplicationStatus();
  }
}
