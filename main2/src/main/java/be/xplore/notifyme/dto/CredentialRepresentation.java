package be.xplore.notifyme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CredentialRepresentation {

  private String type;
  private String value;
  private boolean temporary;
}
