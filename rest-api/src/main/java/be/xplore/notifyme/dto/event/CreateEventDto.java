package be.xplore.notifyme.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventDto {
  private String title;
  private String description;
  private String artist;
  private String dateTime;
  private long venueId;
}
