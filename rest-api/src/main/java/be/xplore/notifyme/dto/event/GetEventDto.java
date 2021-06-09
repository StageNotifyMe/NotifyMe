package be.xplore.notifyme.dto.event;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEventDto {

  private long id;
  private String title;
  private String description;
  private String artist;
  private String dateTime;
  private EventStatus eventStatus;

  private Venue venue;
  private List<Line> lines = new ArrayList<>();
  private Set<User> lineManagers = new HashSet<>();

  /**
   * Contructor to create a json-friendly DTO from an Event object.
   *
   * @param event object to convert.
   */
  public GetEventDto(Event event) {
    this.id = event.getId();
    this.title = event.getTitle();
    this.description = event.getDescription();
    this.artist = event.getArtist();
    this.dateTime = event.getDateTime().toString();
    this.eventStatus = event.getEventStatus();
    this.venue = event.getVenue();
  }
}
