package be.xplore.notifyme.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Venue {
  private String name;
  private String description;
  private Address address;
  private User manager;

}
