package be.xplore.notifyme.dto;

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

  public ApplicationOrgNameDto(UserOrgApplication userOrgApplication) {
    this.organisationUserKey = userOrgApplication.getOrganisationUserKey();
    this.applicationStatus = userOrgApplication.getApplicationStatus();
    this.orgName = userOrgApplication.getAppliedOrganisation().getName();
  }
}
