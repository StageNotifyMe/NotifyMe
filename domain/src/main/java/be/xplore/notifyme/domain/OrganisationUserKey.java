package be.xplore.notifyme.domain;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class that represents a key for the many to many table between organisation and user.
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrganisationUserKey implements Serializable {

  private String userId;
  private Long organisationId;
  private static final long serialVersionUID = 1L;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganisationUserKey that = (OrganisationUserKey) o;
    return organisationId.longValue() == that.organisationId.longValue()
        && userId.equals(that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(organisationId, userId);
  }

}
