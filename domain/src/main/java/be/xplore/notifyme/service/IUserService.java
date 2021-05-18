package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.dto.UserRegistrationDto;
import java.security.Principal;
import java.util.List;
import org.keycloak.representations.account.UserRepresentation;

public interface IUserService {

  List<UserRepresentation> getAllUserInfo();

  UserRepresentation getUserInfo(String username, Principal principal);

  User getUserFromPrincipal(Principal principal);

  User getUser(String id);

  void register(UserRegistrationDto userRegistrationDto);

  List<User> getUsers();

  User updateUser(User user);

  void grantUserRole(String userId, String roleName);
}