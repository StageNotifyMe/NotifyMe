package be.xplore.notifyme.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class OrganisationUserKey implements Serializable {

  @Column(name = "organisation_id")
  private long organisationId;
  @Column(name = "user_id")
  private String userId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganisationUserKey that = (OrganisationUserKey) o;
    return organisationId == that.organisationId &&
        userId.equals(that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(organisationId, userId);
  }
}
