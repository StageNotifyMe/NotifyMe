package be.xplore.notifyme.domain;

import java.util.ArrayList;
import java.util.List;
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
public class Organisation {

  private Long id;
  private String name;
  private List<OrganisationUser> users;
  private List<UserOrgApplication> appliedUsers;
  private List<Team> teams;

  public Organisation(String name) {
    this.name = name;
    this.users = new ArrayList<>();
  }

  /**
   * Compatibility contructor, might be removed later.
   *
   * @param id                of the user, same as in keycloak db.
   * @param name              of the user.
   * @param organisationUsers used to map of which organisations the user is a member.
   */
  public Organisation(long id, String name, List<OrganisationUser> organisationUsers) {
    this.id = id;
    this.name = name;
    this.users = organisationUsers;
  }
}
