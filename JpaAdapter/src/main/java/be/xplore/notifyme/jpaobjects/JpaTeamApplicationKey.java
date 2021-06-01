package be.xplore.notifyme.jpaobjects;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class that represents a key for the many to many table between team and user.
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class JpaTeamApplicationKey implements Serializable {

  private String userId;
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
}
