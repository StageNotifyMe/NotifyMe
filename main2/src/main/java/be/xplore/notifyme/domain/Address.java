package be.xplore.notifyme.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Address {
  private String streetAndNumber;
  private String postalCode;
  private String village;
  private String country;
}
