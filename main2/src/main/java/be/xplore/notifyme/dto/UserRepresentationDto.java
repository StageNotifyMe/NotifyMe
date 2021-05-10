package be.xplore.notifyme.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRepresentationDto {

  private String firstName;
  private String lastName;
  private String email;
  private String username;
  private Boolean enabled;
  private List<CredentialRepresentationDto> credentials;
}
