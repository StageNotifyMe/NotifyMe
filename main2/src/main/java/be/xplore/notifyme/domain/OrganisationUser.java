package be.xplore.notifyme.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrganisationUser {

  @EmbeddedId
  private OrganisationUserKey organisationUserKey;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  User user;

  @ManyToOne
  @MapsId("organisationId")
  @JoinColumn(name = "organisation_id")
  Organisation organisation;

  private boolean isUserAdmin;
}
