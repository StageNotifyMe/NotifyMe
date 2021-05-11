package be.xplore.notifyme.jpaObjects;

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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@AllArgsConstructor
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
}
