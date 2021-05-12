package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Organisation;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class JpaOrganisation {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  @Column(unique = true)
  private String name;
  @OneToMany(mappedBy = "organisation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<JpaOrganisationUser> users;
  @OneToMany(mappedBy = "appliedOrganisation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<JpaUserOrgApplication> appliedUsers;
  @ManyToMany
  private List<JpaTeam> teams;

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  public Organisation toDomain() {
    return Organisation.builder()
        .id(this.id)
        .name(this.name)
        .users(this.users.stream().map(JpaOrganisationUser::toDomain).collect(Collectors.toList()))
        .appliedUsers(this.appliedUsers.stream().map(JpaUserOrgApplication::toDomain).collect(
            Collectors.toList()))
        .teams(this.teams.stream().map(JpaTeam::toDomain).collect(Collectors.toList()))
        .build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param organisation jpa version of the object.
   */
  public JpaOrganisation(Organisation organisation) {
    this.id = organisation.getId();
    this.name = organisation.getName();
    this.users = organisation.getUsers().stream().map(JpaOrganisationUser::new)
        .collect(
            Collectors.toList());
    this.appliedUsers = organisation.getAppliedUsers().stream()
        .map(JpaUserOrgApplication::new).collect(
            Collectors.toList());
    this.teams = organisation.getTeams().stream().map(JpaTeam::new).collect(Collectors.toList());
  }

}
