package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.dto.UserRegistrationDto;
import java.security.Principal;
import java.util.List;
import org.keycloak.representations.account.UserRepresentation;

public interface IUserService {

  List<UserRepresentation> getAllUserInfo();

  UserRepresentation getUserInfo(String username, Principal principal);

  User getUserFromPrincipal(Principal principal);

  User getUserFromprincipalIncOrganisations(Principal principal);

  User getUserFromPrincipalIncAppliedUsers(Principal principal);

  User getUserFromPrincipalIncTeamApplications(Principal principal);

  User getUser(String id);
  User getUserIncOrganisations(String id);

  void register(UserRegistrationDto userRegistrationDto);

  List<User> getUsers();

  User updateUser(User user);

  void grantUserRole(String userId, String roleName);

  void updateAccountInfo(String userId, String username, String firstName, String lastName,
                         String email, String phoneNumber, String preferedLanguage);
}
