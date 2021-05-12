package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationUser {

  private OrganisationUserKey organisationUserKey;

  @JsonBackReference
  private User user;

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
