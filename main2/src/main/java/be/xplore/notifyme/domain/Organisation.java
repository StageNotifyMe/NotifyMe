package be.xplore.notifyme.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(unique = true)
  @Size(min = 1, max = 500)
  @NotNull
  @NotEmpty
  private String name;
  @OneToMany(mappedBy = "organisation")
  private List<OrganisationUser> users;
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
