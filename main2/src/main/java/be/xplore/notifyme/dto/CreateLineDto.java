package be.xplore.notifyme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateLineDto {

  private String note;
  private int requiredStaff;
  private long facilityId;
  private long eventId;

  //team is not yet created
  //private Team team;
}
