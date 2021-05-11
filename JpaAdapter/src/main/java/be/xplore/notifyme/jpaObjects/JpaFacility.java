package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Venue;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class JpaFacility {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String description;
  private String location;
  private int minimalStaff;
  private int maximalStaff;

  @ManyToOne
  private JpaVenue venue;
  @OneToMany
  private List<JpaLine> lines;


  public Facility toDomain() {
    return Facility.builder()
        .id(this.id)
        .description(this.description)
        .location(this.location)
        .minimalStaff(this.minimalStaff)
        .maximalStaff(this.maximalStaff)
        .venue(this.venue.toDomain())
        .lines(this.lines.stream().map(JpaLine::toDomain).collect(Collectors.toList()))
        .build();
  }
}
