package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.User;
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
  //TODO
  /*@ManyToMany
  private List<Organisation> organisations;*/
  @ManyToMany
  private Set<JpaUser> teamMembers;

  public Team toDomain() {
    return Team.builder()
        .id(this.id)
        .line(this.line.toDomain())
        .teamMembers(this.teamMembers.stream().map(JpaUser::toDomain).collect(Collectors.toSet()))
        .build();
  }
}
