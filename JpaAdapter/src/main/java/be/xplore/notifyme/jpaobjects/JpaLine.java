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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
  @OneToOne(cascade = CascadeType.ALL,mappedBy = "line")
  private JpaTeam team;

  /**
   * Create a jpa line.
   *
   * @param line        domain rep of line to create
   * @param jpaEvent    jpa event to add line to.
   * @param jpaFacility jpa facility to add line to.
   */
  public JpaLine(Line line, JpaEvent jpaEvent, JpaFacility jpaFacility) {
    this.id = line.getId();
    this.note = line.getNote();
    this.requiredStaff = line.getRequiredStaff();
    this.facility = jpaFacility;
    this.event = jpaEvent;
    this.team = new JpaTeam();
  }

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
   * Converts a jpa-object to a domain variant with only primitive type attributes.
   *
   * @return domain version of the object.
   */
  public Line toDomainBase() {
    return Line.builder()
        .id(this.id)
        .note(this.note)
        .event(this.event.toDomainBase())
        //.facility(this.facility.toDomainBase())
        .team(this.team.toDomainBaseOnlyId())
        .requiredStaff(this.requiredStaff)
        .build();
  }

  /**
   * Converts a jpa-object to a domain variant with only primitive type attributes and facility.
   *
   * @return domain version of the object.
   */
  public Line toDomainBaseIncFacility() {
    return Line.builder()
        .id(this.id)
        .note(this.note)
        //.event(this.event.toDomainBase())
        .facility(this.facility.toDomainBase())
        //.team(this.team.toDomainBase())
        .requiredStaff(this.requiredStaff)
        .build();
  }
  /**
   * Converts a jpa-object to a domain variant with only primitive type attributes and event.
   *
   * @return domain version of the object.
   */
  public Line toDomainBaseIncEvent() {
    return Line.builder()
        .id(this.id)
        .note(this.note)
        .event(this.event.toDomainBase())
        .facility(this.facility.toDomainBase())
        .team(this.team.toDomainBase())
        .requiredStaff(this.requiredStaff)
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
