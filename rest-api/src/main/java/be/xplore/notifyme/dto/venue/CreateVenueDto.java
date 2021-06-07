package be.xplore.notifyme.dto.venue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateVenueDto {
  private String name;
  private String description;
  private String streetAndNumber;
  private String postalCode;
  private String village;
  private String country;
}
