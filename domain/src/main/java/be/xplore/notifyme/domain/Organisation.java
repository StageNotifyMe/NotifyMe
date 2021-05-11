package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  @Column(unique = true)
  private String name;
  @OneToMany(mappedBy = "organisation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<OrganisationUser> users;
  @OneToMany(mappedBy = "appliedOrganisation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<UserOrgApplication> appliedUsers;
  @ManyToMany
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
