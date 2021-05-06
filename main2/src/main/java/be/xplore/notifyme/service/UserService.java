package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IUserRepo;
import com.google.gson.Gson;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for communication with the keycloak server related to user accounts.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final Gson gson;
  private final IUserRepo userRepo;
  private final TokenService tokenService;
  private final KeycloakCommunicationService keycloakCommunicationService;
  @Value("${keycloak.resource}")
  private final String clientName;

  /**
   * Gets Keycloak Userrepresentation with info from all of the users.
   *
   * @param adminAccesstoken Admin service account token needed to authorize request.
   * @return list of Keycloak Userrepresentation.
   */
  public List<UserRepresentation> getAllUserInfo(
      String adminAccesstoken) {
    try {
      return keycloakCommunicationService.getAllUserInfoRest(adminAccesstoken);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new CrudException("Could not get users from keycloak server: " + e.getMessage());
    }
  }

  /**
   * Returns a keycloak userrepresentation object.
   *
   * @param username of the user to get info from.
   * @return Keycloak UserRepresentation object.
   */
  public UserRepresentation getUserInfo(String username, Principal principal) {
    var token = tokenService.getIdToken(principal);
    var securityContext = tokenService.getSecurityContext(principal);
    if (token.getPreferredUsername().equals(username)
        || securityContext.getAuthorizationContext().hasScopePermission("admin")) {
      return keycloakCommunicationService.getUserInfo(username);
    } else {
      throw new UnauthorizedException("User can only get info about themself");
    }
  }

  /**
   * Gets a user based on the authentication send with an API request (principal).
   *
   * @param principal of and API request.
   * @return user object.
   */
  public User getUserFromPrincipal(Principal principal) {
    try {
      var decodedToken = tokenService.getIdToken(principal);
      return this.getUser(decodedToken.getSubject());
    } catch (Exception e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  /**
   * Gets a user from the database based on ID.
   *
   * @param id of the user to get.
   * @return the user or throws exception if unable to find a user with given ID.
   */
  public User getUser(String id) {
    var user = userRepo.findById(id);
    if (user.isPresent()) {
      return user.get();
    } else {
      throw new CrudException(String.format("Could not retrieve user for id %s", id));
    }
  }

  /**
   * Gets a user's ID based on their username and sends them an email to verify their email. *
   *
   * @param username username of the user that needs to receive the email.
   */
  private void getUserInfoAndSendVerificationEmail(String username) {
    try {
      var userInfo = keycloakCommunicationService.getUserInfo(username);
      keycloakCommunicationService.sendEmailVerificationRequest(userInfo.getId());
      createUserInDatabase(userInfo.getId());
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  /**
   * Registers a new user by sending a service request to the keycloak server.
   */
  public void register(UserRegistrationDto userRegistrationDto) {
    //if creation was unsuccessful, don't get user info and send verification email
    try {
      keycloakCommunicationService.register(userRegistrationDto);
      getUserInfoAndSendVerificationEmail(userRegistrationDto.getUsername());
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new CrudException(
          "Could not register new user in system: " + userRegistrationDto.getUsername());
    }
  }

  private void createUserInDatabase(String id) {
    var user = new User();
    user.setUserId(id);
    userRepo.save(user);
  }

  /**
   * Gets a list of all users in user repository.
   *
   * @return list of our domain users.
   */
  public List<User> getUsers() {
    try {
      return userRepo.findAll();
    } catch (RuntimeException ex) {
      log.error(ex.getMessage());
      throw new CrudException("Could not retrieve the list of users.");
    }
  }

  /**
   * Grants a any user any notifyme client role.
   *
   * @param userId   user who gets granted the role.
   * @param roleName role to grant.
   */
  public void grantUserRole(String userId, String roleName) {
    try {
      var client = keycloakCommunicationService.getClient(this.clientName);
      var role = keycloakCommunicationService.getClientRole(roleName, client.getId());
      keycloakCommunicationService.giveUserRole(userId, role, client.getId());
    } catch (Exception e) {
      log.error(e.getMessage());
      throw e;
    }
  }
}
