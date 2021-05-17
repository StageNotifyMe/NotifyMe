package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Venue;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaVenue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String description;
  @OneToOne
  @Cascade(CascadeType.ALL)
  private JpaAddress address;

  @ManyToMany(cascade = javax.persistence.CascadeType.ALL)
  private Set<JpaUser> managers;
  @OneToMany
  private List<JpaFacility> facilities;

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  public Venue toDomain() {
    return Venue.builder()
        .id(this.id)
        .name(this.name)
        .description(this.description)
        .address(this.address.toDomain())
        .managers(this.managers.stream().map(JpaUser::toDomain).collect(Collectors.toSet()))
        .facilities(this.facilities.stream().map(JpaFacility::toDomain).collect(
            Collectors.toList()))
        .build();
  }

  /**
   * Makes a domain object with all primitive type attributes (no lists).
   *
   * @return venue object.
   */
  public Venue toDomainBase() {
    return Venue.builder()
        .id(this.id)
        .name(this.name)
        .description(this.description)
        .address(this.address.toDomain())
        .build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param venue jpa version of the object.
   */
  public JpaVenue(Venue venue) {
    this.id = venue.getId();
    this.name = venue.getName();
    this.description = venue.getDescription();
    this.address = new JpaAddress(venue.getAddress());
    this.managers = venue.getManagers().stream().map(JpaUser::new).collect(Collectors.toSet());
    this.facilities = venue.getFacilities().stream().map(JpaFacility::new)
        .collect(Collectors.toList());
  }
}
