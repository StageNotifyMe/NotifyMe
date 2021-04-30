package be.xplore.notifyme.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String title;
  private String description;
  private String artist;
  private LocalDateTime dateTime;
  @ManyToOne
  private Venue venue;

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
    this.dateTime = LocalDateTime.parse(isoDateTimeString);
  }
}