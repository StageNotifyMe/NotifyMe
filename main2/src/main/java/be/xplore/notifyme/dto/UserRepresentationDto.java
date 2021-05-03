package be.xplore.notifyme.dto;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.account.UserRepresentation;

@Getter
@Setter
@AllArgsConstructor
public class UserRepresentationDto {

  private String firstName;
  private String lastName;
  private String email;
  private String username;
  private Boolean enabled;
  private List<CredentialRepresentationDto> credentials = new LinkedList<>();

  public UserRepresentationDto(UserRepresentation userRepresentation){
    this.firstName=userRepresentation.getFirstName();
    this.lastName=userRepresentation.getLastName();
    this.email=userRepresentation.getEmail();
    this.username=userRepresentation.getUsername();
    this.enabled=userRepresentation.isEmailVerified();
  }
}
