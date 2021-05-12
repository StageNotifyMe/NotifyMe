package be.xplore.notifyme.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Line {

  private long id;
  private String note;
  private int requiredStaff;

  private Facility facility;
  private Event event;
  private Team team;

  public Line(String note, int requiredStaff) {
    this.note = note;
    this.requiredStaff = requiredStaff;
  }

  /**
   * Constructor used to create a new line.
   *
   * @param event    event to which the line belongs.
   * @param facility for which the line is.
   * @param team     who will man the line.
   */
  public Line(String note, int requiredStaff, Event event, Facility facility, Team team) {
    this.note = note;
    this.requiredStaff = requiredStaff;
    this.event = event;
    this.facility = facility;
    this.team = team;
  }
}
