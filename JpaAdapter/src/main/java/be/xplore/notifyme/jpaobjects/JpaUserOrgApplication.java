package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.UserOrgApplication;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class JpaUserOrgApplication {

  @EmbeddedId
  private JpaOrganisationUserKey organisationUserKey;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private JpaUser appliedUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("organisationId")
  @JoinColumn(name = "organisation_id")
  private JpaOrganisation appliedOrganisation;

  private OrgApplicationStatus applicationStatus;

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  public UserOrgApplication toDomain() {
    return UserOrgApplication.builder()
        .organisationUserKey(this.organisationUserKey.toDomain())
        .appliedUser(this.appliedUser.toDomain())
        .appliedOrganisation(this.appliedOrganisation.toDomain())
        .applicationStatus(this.applicationStatus).build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param userOrgApplication jpa version of the object.
   */
  public JpaUserOrgApplication(UserOrgApplication userOrgApplication) {
    this.organisationUserKey = new JpaOrganisationUserKey(
        userOrgApplication.getOrganisationUserKey());
    this.appliedUser = new JpaUser(userOrgApplication.getAppliedUser());
    this.appliedOrganisation = new JpaOrganisation(userOrgApplication.getAppliedOrganisation());
    this.applicationStatus = userOrgApplication.getApplicationStatus();
  }
}
