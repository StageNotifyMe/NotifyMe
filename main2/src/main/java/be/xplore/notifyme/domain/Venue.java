package be.xplore.notifyme.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
public class Venue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String description;
  @OneToOne
  @Cascade(CascadeType.ALL)
  private Address address;
  @ManyToOne
  private User manager;

  /**
   * Default constructor without autogenerated ID.
   *
   * @param name        name
   * @param description description
   * @param address     Address object
   * @param manager     User object
   */
  public Venue(String name, String description, Address address,
               User manager) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.manager = manager;
  }
}