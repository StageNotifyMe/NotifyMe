package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Facility;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
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


  /**
   * Converts a jpa-object to a domain variant with only primitive type attributes.
   *
   * @return domain version of the object.
   */
  public Facility toDomainBase() {
    return Facility.builder()
        .id(this.id)
        .description(this.description)
        .location(this.location)
        .minimalStaff(this.minimalStaff)
        .maximalStaff(this.maximalStaff)
        .build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param facility jpa version of the object.
   */
  public JpaFacility(Facility facility) {
    this.id = facility.getId();
    this.description = facility.getDescription();
    this.location = facility.getLocation();
    this.minimalStaff = facility.getMinimalStaff();
    this.maximalStaff = facility.getMaximalStaff();
    this.venue = new JpaVenue(facility.getVenue());
    this.lines = facility.getLines().stream().map(JpaLine::new)
        .collect(Collectors.toList());
  }

  /**
   * Creates jpa facility.
   *
   * @param facility domain rep of facility to create.
   * @param jpaVenue jpa venue to add facility to.
   */
  public JpaFacility(Facility facility, JpaVenue jpaVenue) {
    this.id = facility.getId();
    this.description = facility.getDescription();
    this.location = facility.getLocation();
    this.minimalStaff = facility.getMinimalStaff();
    this.maximalStaff = facility.getMaximalStaff();
    this.lines = facility.getLines().stream().map(JpaLine::new)
        .collect(Collectors.toList());
    this.venue = jpaVenue;
  }
}
