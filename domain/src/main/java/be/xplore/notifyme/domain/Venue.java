package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Object representation of a venue where an event can be held.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Venue {
  private long id;
  private String name;
  private String description;
  private Address address;
  private Set<User> managers = new HashSet<>();
  private List<Facility> facilities = new ArrayList<>();

  /**
   * Constructor for venue without auto generated ID.
   *
   * @param name        of the venue
   * @param description of the venue
   * @param address     of the venue
   */
  public Venue(String name, String description, Address address) {
    this.name = name;
    this.description = description;
    this.address = address;
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
