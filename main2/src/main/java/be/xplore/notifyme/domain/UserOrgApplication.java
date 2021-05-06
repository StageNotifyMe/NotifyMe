package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class UserOrgApplication {

  @EmbeddedId
  private OrganisationUserKey organisationUserKey;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User appliedUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("organisationId")
  @JoinColumn(name = "organisation_id")
  @JsonBackReference
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
