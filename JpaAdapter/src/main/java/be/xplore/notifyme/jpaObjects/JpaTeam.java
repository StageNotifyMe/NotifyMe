package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.Team;
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

@Entity
@AllArgsConstructor
public class JpaTeam {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne
  private JpaLine line;
  @ManyToMany
  private List<JpaOrganisation> organisations;
  @ManyToMany
  private Set<JpaUser> teamMembers;

  public Team toDomain() {
    return Team.builder()
        .id(this.id)
        .line(this.line.toDomain())
        .teamMembers(this.teamMembers.stream().map(JpaUser::toDomain).collect(Collectors.toSet()))
        .organisations(
            this.organisations.stream().map(JpaOrganisation::toDomain).collect(Collectors.toList()))
        .build();
  }

  public JpaTeam(Team team) {
    this.id = team.getId();
    this.line = new JpaLine(team.getLine());
    this.organisations = team.getOrganisations().stream().map(JpaOrganisation::new)
        .collect(Collectors.toList());
    this.teamMembers = team.getTeamMembers().stream().map(JpaUser::new).collect(
        Collectors.toSet());
  }
}
