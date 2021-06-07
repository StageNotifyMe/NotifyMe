package be.xplore.notifyme.dto.organisationapplication;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.UserOrgApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ApplicationOrgNameDto {

  private OrganisationUserKey organisationUserKey;
  private OrgApplicationStatus applicationStatus;
  private String orgName;

  /**
   * Constructs the application for an organisation dto with only relevant data for user.
   *
   * @param userOrgApplication to convert.
   */
  public ApplicationOrgNameDto(UserOrgApplication userOrgApplication) {
    this.organisationUserKey = userOrgApplication.getOrganisationUserKey();
    this.applicationStatus = userOrgApplication.getApplicationStatus();
    this.orgName = userOrgApplication.getAppliedOrganisation().getName();
  }
}
