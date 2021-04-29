package be.xplore.notifyme.domain;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "external_user")
@Getter
@Setter
@NoArgsConstructor
public class User {

  @Id
  private String externalOidcId;
  @OneToMany(mappedBy = "organisation")
  private List<OrganisationUser> organisation;
  @OneToMany(mappedBy = "id")
  private List<VenueManager> venues;

  public User(String externalOidcId) {
    this.externalOidcId = externalOidcId;
    this.organisation = new LinkedList<>();
    this.venues = new LinkedList<>();
  }
}
