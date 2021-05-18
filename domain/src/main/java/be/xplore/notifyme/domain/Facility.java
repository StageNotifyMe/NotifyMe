package be.xplore.notifyme.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Entity
@Builder
public class Facility {
  private long id;
  private String description;
  private String location;
  private int minimalStaff;
  private int maximalStaff;

  private Venue venue;
  private List<Line> lines = new ArrayList<>();

  /**
   * Contructor used to created a facility without relational properties.
   *
   * @param description  of the facility.
   * @param location     of the facility within the venue.
   * @param minimalStaff required to man the facility.
   * @param maximalStaff to man the facility.
   */
  public Facility(String description, String location, int minimalStaff, int maximalStaff) {
    this.description = description;
    this.location = location;
    this.minimalStaff = minimalStaff;
    this.maximalStaff = maximalStaff;
  }
}
