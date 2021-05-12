package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.User;
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

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<JpaOrganisationUser> organisations;
  @OneToMany(mappedBy = "appliedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<JpaUserOrgApplication> appliedOrganisations;
  @ManyToMany(cascade = CascadeType.ALL)
  private List<JpaVenue> venues;
  @ManyToMany
  private Set<JpaTeam> teams;
  @ManyToMany
  private List<JpaEvent> events;

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  public User toDomain() {
    return User.builder()
        .userId(this.userId)
        .userName(this.userName)
        .venues(this.venues.stream().map(JpaVenue::toDomain).collect(Collectors.toList()))
        .events(this.events.stream().map(JpaEvent::toDomain).collect(Collectors.toList()))
        .organisations(this.organisations.stream().map(JpaOrganisationUser::toDomain).collect(
            Collectors.toList()))
        .appliedOrganisations(
            this.appliedOrganisations.stream().map(JpaUserOrgApplication::toDomain).collect(
                Collectors.toList()))
        .teams(this.teams.stream().map(JpaTeam::toDomain).collect(Collectors.toSet()))
        .build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param user jpa version of the object.
   */
  public JpaUser(User user) {
    this.userId = user.getUserId();
    this.userName = user.getUserName();
    this.organisations = user.getOrganisations().stream().map(JpaOrganisationUser::new).collect(
        Collectors.toList());
    this.appliedOrganisations = user.getAppliedOrganisations().stream()
        .map(JpaUserOrgApplication::new).collect(
            Collectors.toList());
    this.venues = user.getVenues().stream().map(JpaVenue::new).collect(Collectors.toList());
    this.teams = user.getTeams().stream().map(JpaTeam::new).collect(Collectors.toSet());
    this.events = user.getEvents().stream().map(JpaEvent::new).collect(Collectors.toList());
  }
}
