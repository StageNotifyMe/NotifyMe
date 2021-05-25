package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Team;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaTeam {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne
  private JpaLine line;
  @ManyToMany(mappedBy = "teams")
  private List<JpaOrganisation> organisations;
  @ManyToMany(mappedBy = "teams")
  private Set<JpaUser> teamMembers;

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  public Team toDomain() {
    return Team.builder()
        .id(this.id)
        .line(this.line.toDomain())
        .teamMembers(
            this.teamMembers.stream().map(JpaUser::toDomainBase).collect(Collectors.toSet()))
        .organisations(
            this.organisations.stream().map(JpaOrganisation::toDomainBase)
                .collect(Collectors.toList()))
        .build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param team jpa version of the object.
   */
  public JpaTeam(Team team) {
    this.id = team.getId();
    this.line = new JpaLine(team.getLine());
    this.organisations = team.getOrganisations().stream().map(JpaOrganisation::new)
        .collect(Collectors.toList());
    this.teamMembers = team.getTeamMembers().stream().map(JpaUser::new).collect(
        Collectors.toSet());
  }

  /**
   * Converts a jpa-object to a domain variant with only primitive type attributes.
   *
   * @return domain version of the object.
   */
  public Team toDomainBase() {
    return Team.builder()
        .id(this.id)
        .build();
  }

  public JpaTeam(JpaLine jpaLine, JpaOrganisation jpaOrganisation) {
    this.line = jpaLine;
    this.organisations = new ArrayList<>();
    this.organisations.add(jpaOrganisation);
    this.teamMembers = new HashSet<>();
  }

  public Team toDomainBaseIncOrganisations() {
    return Team.builder()
        .id(this.id)
        .line(this.line.toDomainBase())
        .organisations(this.organisations.stream().map(JpaOrganisation::toDomainBase).collect(
            Collectors.toList()))
        .build();
  }

  public Team toDomainBaseIncMembers() {
    return Team.builder()
        .id(this.id)
        .line(this.line.toDomainBase())
        .teamMembers(
            this.teamMembers.stream().map(JpaUser::toDomainBase).collect(Collectors.toSet()))
        .build();
  }
}
