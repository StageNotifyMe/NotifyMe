package be.xplore.notifyme.dto.facility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFacilityDto {
  private String description;
  private String location;
  private int minimalStaff;
  private int maximalStaff;
  private long venueId;
}
