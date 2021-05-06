package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
  private String userId;
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<OrganisationUser> organisation;
  @OneToMany(mappedBy = "appliedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<UserOrgApplication> appliedOrganisations;
  @ManyToMany(cascade = CascadeType.ALL)
  private List<Venue> venues = new LinkedList<>();
  @ManyToMany
  private List<Team> teams;
  @ManyToMany
  private List<Event> events;

}
