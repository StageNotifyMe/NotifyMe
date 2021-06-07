package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.domain.TeamApplicationStatus;
import be.xplore.notifyme.domain.UserOrgApplication;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
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
@Entity
public class JpaTeamApplication {

  @EmbeddedId
  private JpaTeamApplicationKey jpaTeamApplicationKey;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private JpaUser appliedUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("teamId")
  @JoinColumn(name = "team_id")
  private JpaTeam appliedTeam;
  private TeamApplicationStatus applicationStatus;

  /**
   * Constructor that creates a new TeamApplication. It automatically fills the key by the user and
   * team data. An application status should be passed that represents the status of the
   * application.
   *
   * @param team              the team that the user wants to join.
   * @param user              the user that wants to join the team.
   * @param applicationStatus the status of the users application.
   */
  public JpaTeamApplication(JpaTeam team, JpaUser user,
      TeamApplicationStatus applicationStatus) {
    this.jpaTeamApplicationKey = new JpaTeamApplicationKey(user.getUserId(), team.getId());
    this.appliedUser = user;
    this.appliedTeam = team;
    this.applicationStatus = applicationStatus;
  }

  /**
   * Converts jpa userOrgApplication to domain representation.
   *
   * @return user org application.
   */
  public TeamApplication toDomainBase() {
    return TeamApplication.builder()
        .teamApplicationKey(this.jpaTeamApplicationKey.toDomain())
        .appliedUser(this.appliedUser.toDomainBase())
        .appliedTeam(this.appliedTeam.toDomainBase())
        .applicationStatus(this.applicationStatus)
        .build();
  }
}
