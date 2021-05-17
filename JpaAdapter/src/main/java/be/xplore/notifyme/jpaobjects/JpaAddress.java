package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Address;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class JpaAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String streetAndNumber;
  private String postalCode;
  private String village;
  private String country;

  /**
   * Converts a jpa-object to a domain variant.
   *
   * @return domain version of the object.
   */
  Address toDomain() {
    return Address.builder()
        .streetAndNumber(this.streetAndNumber)
        .postalCode(this.postalCode)
        .village(this.village)
        .country(this.country)
        .build();
  }

  /**
   * Constructor for conversion from domain object to jpa-object.
   *
   * @param address jpa version of the object.
   */
  public JpaAddress(Address address) {
    this.streetAndNumber = address.getStreetAndNumber();
    this.postalCode = address.getPostalCode();
    this.village = address.getVillage();
    this.country = address.getCountry();
  }

}
