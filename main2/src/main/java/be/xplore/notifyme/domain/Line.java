package be.xplore.notifyme.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Line {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String note;
  private int requiredStaff;

  @ManyToOne(cascade = CascadeType.ALL)
  private Facility facility;
  @ManyToOne(cascade = CascadeType.ALL)
  private Event event;
  @OneToOne(cascade = CascadeType.ALL)
  private Team team;
  @ManyToMany
  private List<User> lineManagers;

  public Line(String note, int requiredStaff) {
    this.note = note;
    this.requiredStaff = requiredStaff;
  }

  /**
   * Constructor used to add an event, facility and team to a line with only base properties.
   *
   * @param line     the original line containing base properties.
   * @param event    event to which the line belongs.
   * @param facility for which the line is.
   * @param team     who will man the line.
   */
  public Line(Line line, Event event, Facility facility, Team team) {
    this.id = line.id;
    this.note = line.note;
    this.requiredStaff = line.requiredStaff;
    this.event = event;
    this.facility = facility;
    this.team = team;
  }
}
