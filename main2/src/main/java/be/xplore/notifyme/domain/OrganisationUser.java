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
public class OrganisationUser {

  @EmbeddedId
  private OrganisationUserKey organisationUserKey;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("organisationId")
  @JoinColumn(name = "organisation_id")
  @JsonBackReference
  private Organisation organisation;

  private boolean isOrganisationLeader;

  /**
   * Constructor that creates a new OrganisationUser. It automatically fills the key by the user and
   * org data. A boolean should be passed that represents if the user is an organisation manager or
   * not.
   *
   * @param organisation         the organisation the user should be added to.
   * @param user                 the user that should be added to an organisation
   * @param isOrganisationLeader is the user an org manager.
   */
  public OrganisationUser(Organisation organisation, User user, boolean isOrganisationLeader) {
    this.organisationUserKey = new OrganisationUserKey(user.getUserId(), organisation.getId());
    this.user = user;
    this.organisation = organisation;
    this.isOrganisationLeader = isOrganisationLeader;
  }
}
