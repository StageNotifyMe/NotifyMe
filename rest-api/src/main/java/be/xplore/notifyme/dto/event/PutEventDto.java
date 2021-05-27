package be.xplore.notifyme.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutEventDto {
  private long eventId;
  private String eventStatus;
  private String title;
  private String description;
  private String artist;
  private String isoDateTime;
}
