package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.UserOrgApplication;
import be.xplore.notifyme.domain.Venue;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class JpaUser {
  @Id
  private String userId;
  private String userName;

  //TODO: JWZ
  /*@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<OrganisationUser> organisation;
  @OneToMany(mappedBy = "appliedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<UserOrgApplication> appliedOrganisations;*/
  @ManyToMany(cascade = CascadeType.ALL)
  private List<JpaVenue> venues;
  /*@ManyToMany
  private Set<Team> teams;
  */
  @ManyToMany
  private List<JpaEvent> events;

  public User toDomain() {
    return User.builder()
        .userId(this.userId)
        .userName(this.userName)
        .venues(this.venues.stream().map(JpaVenue::toDomain).collect(Collectors.toList()))
        .events(this.events.stream().map(JpaEvent::toDomain).collect(Collectors.toList()))
        .build();
  }
}
