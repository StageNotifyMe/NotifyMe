package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.OrganisationUserKey;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class that represents a key for the many to many table between organisation and user.
 */
@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class JpaOrganisationUserKey implements Serializable {

  @Column(name = "user_id")
  private String userId;
  @Column(name = "organisation_id")
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
    JpaOrganisationUserKey that = (JpaOrganisationUserKey) o;
    return organisationId.longValue() == that.organisationId.longValue()
        && userId.equals(that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(organisationId, userId);
  }

  public OrganisationUserKey toDomain() {
    return OrganisationUserKey.builder()
        .organisationId(this.organisationId)
        .userId(this.userId)
        .build();
  }

  public JpaOrganisationUserKey(OrganisationUserKey organisationUserKey) {
    this.organisationId = organisationUserKey.getOrganisationId();
    this.userId = organisationUserKey.getUserId();
  }
}
