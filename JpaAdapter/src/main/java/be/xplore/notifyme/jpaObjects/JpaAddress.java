package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.Address;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class JpaAddress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String streetAndNumber;
  private String postalCode;
  private String village;
  private String country;

  Address toDomain() {
    return Address.builder()
        .streetAndNumber(this.streetAndNumber)
        .postalCode(this.postalCode)
        .village(this.village)
        .country(this.country)
        .build();
  }

}
