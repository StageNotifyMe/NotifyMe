package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.TeamApplicationKey;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class that represents a key for the many to many table between team and user.
 */
@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class JpaTeamApplicationKey implements Serializable {

  @Column(name = "user_id")
  private String userId;
  @Column(name = "team_id")
  private Long teamId;
  private static final long serialVersionUID = 1L;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JpaTeamApplicationKey that = (JpaTeamApplicationKey) o;
    return teamId.longValue() == that.teamId.longValue()
        && userId.equals(that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamId, userId);
  }

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  public TeamApplicationKey toDomain() {
    return TeamApplicationKey.builder()
        .teamId(this.teamId)
        .userId(this.userId)
        .build();
  }
}
