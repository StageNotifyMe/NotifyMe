package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Line;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
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

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
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

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param line jpa version of the object.
   */
  public JpaLine(Line line) {
    this.id = line.getId();
    this.note = line.getNote();
    this.requiredStaff = line.getRequiredStaff();
    this.facility = new JpaFacility(line.getFacility());
    this.event = new JpaEvent(line.getEvent());
    this.team = new JpaTeam(line.getTeam());
  }

}
