package be.xplore.notifyme.dto;

import be.xplore.notifyme.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetVenueDto {
  private Long id;
  private String name;
  private String description;
  private String streetAndNumber;
  private String postalCode;
  private String village;
  private String country;

  /**
   * This dto is used to export a venue to a front end application.
   *
   * @param id          of venue
   * @param name        of venue
   * @param description of venue
   * @param address     of venue
   */
  public GetVenueDto(Long id, String name, String description, Address address) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.streetAndNumber = address.getStreetAndNumber();
    this.postalCode = address.getPostalCode();
    this.village = address.getVillage();
    this.country = address.getCountry();
  }
}
