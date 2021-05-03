package be.xplore.notifyme.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class Facility {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String description;
  private String location;
  private int minimalStaff;
  private int maximalStaff;

  @ManyToOne
  private Venue venue;
  @OneToMany
  private List<Line> lines;

  public Facility(String description, String location, int minimalStaff, int maximalStaff) {
    this.description = description;
    this.location = location;
    this.minimalStaff = minimalStaff;
    this.maximalStaff = maximalStaff;
  }
}
