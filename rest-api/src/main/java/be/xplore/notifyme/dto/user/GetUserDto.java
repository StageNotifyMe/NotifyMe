package be.xplore.notifyme.dto.user;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.services.systemmessages.AvailableLanguages;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.keycloak.representations.account.UserRepresentation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDto {
  private String userId;
  private String username;
  private String preferedLanguage;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber = null;
  private boolean emailVerified = false;
  private boolean phoneVerified = false;
  private List<String> availableLanguages =
      Arrays.stream(AvailableLanguages.EN.getDeclaringClass().getEnumConstants()).map(
          Enum::toString).collect(Collectors.toList());

  /**
   * Domain to DTO conversion.
   *
   * @param user               domain user object.
   * @param userRepresentation Keycloak userrepresentation object.
   */
  public GetUserDto(User user, UserRepresentation userRepresentation) {
    this.userId = user.getUserId();
    this.username = user.getUserName();
    this.preferedLanguage = user.getPreferedLanguage().toString();
    this.firstName = userRepresentation.getFirstName();
    this.lastName = userRepresentation.getLastName();
    this.email = userRepresentation.getEmail();
    this.emailVerified = userRepresentation.isEmailVerified();

    if (userRepresentation.getAttributes().containsKey("phone_number")
        && userRepresentation.getAttributes().containsKey("phone_number_verified")) {

      this.phoneNumber = userRepresentation.getAttributes().get("phone_number").get(0);
      this.phoneVerified = Boolean
          .parseBoolean(userRepresentation.getAttributes().get("phone_number_verified").get(0));
    }
  }
}
