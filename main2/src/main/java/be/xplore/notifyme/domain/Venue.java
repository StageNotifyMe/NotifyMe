package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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

/**
 * Object representation of a venue where an event can be held.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Venue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String description;
  @OneToOne
  @Cascade(CascadeType.ALL)
  private Address address;
  @ManyToMany(cascade = javax.persistence.CascadeType.ALL)
  private Set<User> managers;
  @OneToMany
  private List<Facility> facilities;

  /**
   * Constructor for venue without auto generated ID.
   *
   * @param name        of the venue
   * @param description of the venue
   * @param address     of the venue
   * @param user        who made the venue, gets assigned manager
   */
  public Venue(String name, String description, Address address, User user) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.managers = new HashSet<>();
    this.managers.add(user);
  }

  /**
   * Constructor for all properties except facilities.
   *
   * @param id          of the venue.
   * @param name        of the venue.
   * @param description of the venue.
   * @param address     of the venue (street, postalcode, country).
   * @param users       venue managers.
   */
  public Venue(long id, String name, String description, Address address, Set<User> users) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.address = address;
    this.managers = users;
  }
}
