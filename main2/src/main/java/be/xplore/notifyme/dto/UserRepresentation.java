package be.xplore.notifyme.dto;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRepresentation {

  private String firstName;
  private String lastName;
  private String email;
  private String username;
  private Boolean enabled;
  private List<CredentialRepresentation> credentials = new LinkedList<>();

}
