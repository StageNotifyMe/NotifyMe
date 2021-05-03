package be.xplore.notifyme.domain;

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
  @JoinColumn(name="user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("organisationId")
  @JoinColumn(name = "organisation_id")
  private Organisation organisation;

  private boolean isOrganisationLeader;

  public OrganisationUser(Organisation organisation,User user,  boolean isOrganisationLeader) {
    this.user = user;
    this.organisation = organisation;
    this.isOrganisationLeader = isOrganisationLeader;
  }
}
