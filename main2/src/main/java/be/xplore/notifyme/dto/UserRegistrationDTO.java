package be.xplore.notifyme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class UserRegistrationDTO {

  private String firstname;
  private String lastname;
  private String email;
  private String username;
  private String password;
}
