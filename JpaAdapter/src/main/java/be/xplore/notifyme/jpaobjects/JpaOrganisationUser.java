package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.OrganisationUser;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class JpaOrganisationUser {

  @EmbeddedId
  private JpaOrganisationUserKey organisationUserKey;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private JpaUser user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("organisationId")
  @JoinColumn(name = "organisation_id")
  private JpaOrganisation organisation;

  private boolean isOrganisationLeader;

  /**
   * Constructs a Jpa organistation user based on the jpa user,org and if the user is a manager.
   *
   * @param jpaUser         the jpa User.
   * @param jpaOrganisation the jpa organisation.
   * @param isManager       if the user is a manager.
   */
  public JpaOrganisationUser(JpaUser jpaUser, JpaOrganisation jpaOrganisation, boolean isManager) {
    this.isOrganisationLeader = isManager;
    this.user = jpaUser;
    this.organisation = jpaOrganisation;
    this.organisationUserKey =
        new JpaOrganisationUserKey(jpaUser.getUserId(), jpaOrganisation.getId());
  }

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  public OrganisationUser toDomain() {
    return OrganisationUser.builder()
        .organisationUserKey(this.organisationUserKey.toDomain())
        .user(this.user.toDomain())
        .organisation(this.organisation.toDomain())
        .isOrganisationLeader(this.isOrganisationLeader).build();
  }

  /**
   * Creates domain orguser from jpa orguser. Includes the organisations data.
   *
   * @return domain representation of orguser.
   */
  public OrganisationUser toDomainBaseIncOrganisations() {
    return OrganisationUser.builder()
        .organisationUserKey(this.organisationUserKey.toDomain())
        .organisation(this.organisation.toDomainBase())
        .user(this.user.toDomainBase())
        .isOrganisationLeader(this.isOrganisationLeader).build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param organisationUser jpa version of the object.
   */
  public JpaOrganisationUser(OrganisationUser organisationUser) {
    this.organisationUserKey = new JpaOrganisationUserKey(
        organisationUser.getOrganisationUserKey());
    this.user = new JpaUser(organisationUser.getUser());
    this.organisation = new JpaOrganisation(organisationUser.getOrganisation());
    this.isOrganisationLeader = organisationUser.isOrganisationLeader();
  }
}
