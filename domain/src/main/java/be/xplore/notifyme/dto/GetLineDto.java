package be.xplore.notifyme.dto;

import be.xplore.notifyme.domain.Line;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetLineDto {
  private long id;
  private String note;
  private int requiredStaff;
  private long facilityId;
  private String facilityDescription;

  /**
   * Converts a line into a GetLineDto.
   * This DTO is used to display a line's relevant properties in front end.
   *
   * @param line the line to convert.
   */
  public GetLineDto(Line line) {
    this.id = line.getId();
    this.note = line.getNote();
    this.requiredStaff = line.getRequiredStaff();
    this.facilityId = line.getFacility().getId();
    this.facilityDescription = line.getFacility().getDescription();
  }
}
