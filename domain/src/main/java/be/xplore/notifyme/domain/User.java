package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private String userId;
  private String userName;
  private AvailableLanguages preferedLanguage = AvailableLanguages.EN;

  @JsonManagedReference
  private List<OrganisationUser> organisations = new ArrayList<>();
  @JsonManagedReference
  private List<UserOrgApplication> appliedOrganisations = new ArrayList<>();
  private List<Venue> venues = new ArrayList<>();
  private Set<Team> teams = new HashSet<>();
  private Set<TeamApplication> teamApplications = new HashSet<>();
  private List<Event> events = new ArrayList<>();
  private List<Notification> notifications = new ArrayList<>();
  private List<CommunicationPreference> communicationPreferences;

  /**
   * Creates a user.
   *
   * @param userId   id from external server.
   * @param userName from external server. Redundant but avoids a lot of additional calls to api.
   */
  public User(String userId, String userName) {
    this.userId = userId;
    this.userName = userName;
    this.organisations = new ArrayList<>();
    this.appliedOrganisations = new ArrayList<>();
    this.venues = new ArrayList<>();
    this.teams = new HashSet<>();
    this.teamApplications = new HashSet<>();
    this.events = new ArrayList<>();
  }
}
