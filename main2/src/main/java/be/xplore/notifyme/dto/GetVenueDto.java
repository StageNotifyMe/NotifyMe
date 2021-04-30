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

  public GetVenueDto(Long id, String name, String description, Address address) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.streetAndNumber = address.getStreetAndNumber();
    this.postalCode = address.getPostalCode();
    this.village = address.getVillage();
    this.country = address.getCountry();
  }

  public GetVenueDto(Long id, CreateVenueDto createVenueDto) {
    this.id = id;
    this.name = createVenueDto.getName();
    this.description = createVenueDto.getDescription();
    this.streetAndNumber = createVenueDto.getStreetAndNumber();
    this.postalCode = createVenueDto.getPostalCode();
    this.village = createVenueDto.getVillage();
    this.country = createVenueDto.getCountry();
  }
}
