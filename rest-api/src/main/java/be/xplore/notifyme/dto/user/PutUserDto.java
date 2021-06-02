package be.xplore.notifyme.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutUserDto {
  private String userId;
  private String username;
  private String preferedLanguage;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private boolean emailVerified;
  private boolean phoneVerified;
}
