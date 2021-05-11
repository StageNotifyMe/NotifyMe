package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.Line;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class JpaLine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String note;
  private int requiredStaff;

  @ManyToOne(cascade = CascadeType.ALL)
  private JpaFacility facility;
  @ManyToOne(cascade = CascadeType.ALL)
  private JpaEvent event;
  @OneToOne(cascade = CascadeType.ALL)
  private JpaTeam team;

  public Line toDomain() {
    return Line.builder()
        .id(this.id)
        .note(this.note)
        .event(this.event.toDomain())
        .requiredStaff(this.requiredStaff)
        .facility(this.facility.toDomain())
        .team(this.team.toDomain())
        .build();
  }

  public JpaLine(Line line) {
    this.id = line.getId();
    this.note = line.getNote();
    this.requiredStaff = line.getRequiredStaff();
    this.facility = new JpaFacility(line.getFacility());
    this.event=new JpaEvent(line.getEvent());
    this.team=new JpaTeam(line.getTeam());
  }

}
