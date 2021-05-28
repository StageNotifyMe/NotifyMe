package be.xplore.notifyme.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
  private long id;
  private String title;
  private String description;
  private String artist;
  private LocalDateTime dateTime;
  private EventStatus eventStatus;

  private Venue venue;
  private List<Line> lines = new ArrayList<>();
  private Set<User> lineManagers = new HashSet<>();

  /**
   * Constructor for API POST methods. Converts a dateTime from ISO to LocalDateTime.
   *
   * @param title             event title.
   * @param description       event description.
   * @param artist            event artist.
   * @param isoDateTimeString ISO dateTime string value, is converted to LocalDateTime.
   * @param venue             the ID of the venue where the event is held.
   */
  public Event(String title, String description, String artist, String isoDateTimeString,
               Venue venue) {
    this.title = title;
    this.description = description;
    this.artist = artist;
    this.venue = venue;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        .withZone(ZoneId.of("Europe/Brussels"));
    this.dateTime = LocalDateTime.parse(isoDateTimeString, formatter);
    this.eventStatus = EventStatus.OK;

  }
}
