package be.xplore.notifyme.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TeamApplication {

  private TeamApplicationKey teamApplicationKey;
  private User appliedUser;
  private Team appliedTeam;
  private TeamApplicationStatus applicationStatus;

  /**
   * Constructor that creates a new OrganisationUser. It automatically fills the key by the user and
   * org data. A boolean should be passed that represents if the user is an organisation manager or
   * not.
   *
   * @param team              the team that the user wants to join.
   * @param user              the user that wants to join the team.
   * @param applicationStatus the status of the users application.
   */
  public TeamApplication(Team team, User user,
      TeamApplicationStatus applicationStatus) {
    this.teamApplicationKey = new TeamApplicationKey(user.getUserId(), team.getId());
    this.appliedUser = user;
    this.appliedTeam = team;
    this.applicationStatus = applicationStatus;
  }
}
