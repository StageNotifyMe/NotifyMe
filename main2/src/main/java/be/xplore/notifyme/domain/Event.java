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

  public Event(String title, String description, String artist, LocalDateTime dateTime,
               Venue venue) {
    this.title = title;
    this.description = description;
    this.artist = artist;
    this.dateTime = dateTime;
    this.venue = venue;
  }

  public Event(String title, String description, String artist, String isoDateTimeString, Venue venue) {
    this.title = title;
    this.description = description;
    this.artist = artist;
    this.venue = venue;
    this.dateTime = LocalDateTime.parse(isoDateTimeString);
  }
}
