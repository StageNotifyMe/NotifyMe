package be.xplore.notifyme.jpaObjects;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class JpaEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String title;
  private String description;
  private String artist;
  private LocalDateTime dateTime;

  @ManyToOne
  private JpaVenue venue;
  @OneToMany
  private List<JpaLine> lines;
  @ManyToMany
  private Set<JpaUser> lineManagers;

  public Event toDomain() {
    return Event.builder()
        .id(this.id)
        .title(this.title)
        .description(this.description)
        .artist(this.artist)
        .dateTime(this.dateTime)
        .venue(this.venue.toDomain())
        .lines(this.lines.stream().map(JpaLine::toDomain).collect(Collectors.toList()))
        .lineManagers(this.lineManagers.stream().map(JpaUser::toDomain).collect(Collectors.toSet()))
        .build();
  }
}
