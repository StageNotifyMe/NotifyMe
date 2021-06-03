package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IUserRepo;
import be.xplore.notifyme.services.systemmessages.AvailableLanguages;
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
public class UserService implements IUserService {

  private final IUserRepo userRepo;
  private final TokenService tokenService;
  private final KeycloakCommunicationService keycloakCommunicationService;
  private final CommunicationPreferenceService communicationPreferenceService;
  @Value("${keycloak.resource}")
  private String clientName;


  /**
   * Gets Keycloak Userrepresentation with info from all of the users.
   *
   * @return list of Keycloak Userrepresentation.
   */
  @Override
  public List<UserRepresentation> getAllUserInfo() {
    return keycloakCommunicationService
        .getAllUserInfoRest(keycloakCommunicationService.getAdminAccesstoken());
  }

  /**
   * Returns a keycloak userrepresentation object.
   *
   * @param username of the user to get info from.
   * @return Keycloak UserRepresentation object.
   */
  @Override
  public UserRepresentation getUserInfo(String username, Principal principal) {
    var token = tokenService.getIdToken(principal);
    if (token.getPreferredUsername().equals(username)
        || tokenService.hasRole(principal, "admin")) {
      return keycloakCommunicationService.getUserInfoUsername(username);
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
  @Override
  public User getUserFromPrincipal(Principal principal) {
    var decodedToken = tokenService.getIdToken(principal);
    return this.getUser(decodedToken.getSubject());
  }

  /**
   * Gets a user including their organisations.
   *
   * @param principal of an API request.
   * @return user object including organisations.
   */
  @Override
  public User getUserFromprincipalIncOrganisations(Principal principal) {
    var decodedToken = tokenService.getIdToken(principal);
    return userRepo.findByIdIncOrganisations(decodedToken.getSubject());
  }

  @Override
  public User getUserFromPrincipalIncAppliedUsers(Principal principal) {
    var decodedToken = tokenService.getIdToken(principal);
    return userRepo.findByIdIncAppliedUsers(decodedToken.getSubject());
  }

  @Override
  public User getUserFromPrincipalIncTeamApplications(Principal principal) {
    var decodedToken = tokenService.getIdToken(principal);
    return userRepo.findByIdIncTeamApplications(decodedToken.getSubject());
  }

  /**
   * Gets a userId from a principal.
   *
   * @param principal authorization principal.
   * @return String userId.
   */
  public String getUserIdFromPrincipal(Principal principal) {
    return tokenService.getIdToken(principal).getSubject();
  }

  /**
   * Gets a user from the database based on ID.
   *
   * @param id of the user to get.
   * @return the user or throws exception if unable to find a user with given ID.
   */
  @Override
  public User getUser(String id) {
    return userRepo.findById(id).orElseThrow(
        () -> new CrudException(String.format("Could not retrieve user for id %s", id)));
  }

  /**
   * Gets a user's ID based on their username and sends them an email to verify their email. *
   *
   * @param username username of the user that needs to receive the email.
   */
  private void getUserInfoAndSendVerificationEmail(String username) {
    var userInfo = keycloakCommunicationService.getUserInfoUsername(username);
    keycloakCommunicationService.sendEmailVerificationRequest(userInfo.getId());
    createUserInDatabase(userInfo.getId(), username);
    createDefaultCommunicationPreference(userInfo.getId());
  }

  /**
   * Registers a new user by sending a service request to the keycloak server.
   */
  @Override
  public void register(UserRegistrationDto userRegistrationDto) {
    //if creation was unsuccessful, don't get user info and send verification email
    try {
      keycloakCommunicationService.register(userRegistrationDto);
      getUserInfoAndSendVerificationEmail(userRegistrationDto.getUsername());
    } catch (Exception e) {
      throw new SaveToDatabaseException(
          "Could not register new user in system: " + userRegistrationDto.getUsername());
    }
  }

  private void createDefaultCommunicationPreference(String userId) {
    communicationPreferenceService
        .createCommunicationPreference(userId, true, true, true, "emailcommunicationstrategy");
  }

  private void createUserInDatabase(String id, String username) {
    var user = new User(id, username);
    user.setUserId(id);
    userRepo.save(user);
  }

  /**
   * Gets a list of all users in user repository.
   *
   * @return list of our domain users.
   */
  @Override
  public List<User> getUsers() {
    try {
      return userRepo.findAll();
    } catch (RuntimeException ex) {
      log.error(ex.getMessage());
      throw new CrudException("Could not retrieve the list of users.");
    }
  }

  /**
   * Updates a user in the database.
   *
   * @param user to update.
   * @return an updated user.
   */
  @Override
  public User updateUser(User user) {
    try {
      return userRepo.save(user);
    } catch (RuntimeException ex) {
      log.error(ex.getMessage());
      throw new CrudException("Could not update the user data.");
    }
  }

  private void updateUser(String userId, String preferedLanguage) {
    var user = getUser(userId);
    if (!user.getPreferedLanguage().toString().equals(preferedLanguage)) {
      user.setPreferedLanguage(AvailableLanguages.valueOf(preferedLanguage));
      userRepo.save(user);
    }
  }

  /**
   * Grants a any user any notifyme client role.
   *
   * @param userId   user who gets granted the role.
   * @param roleName role to grant.
   */
  @Override
  public void grantUserRole(String userId, String roleName) {
    var client = keycloakCommunicationService.getClient(this.clientName);
    var role = keycloakCommunicationService.getClientRole(roleName, client.getId());
    keycloakCommunicationService.giveUserRole(userId, role, client.getId());
  }

  @Override
  public void updateAccountInfo(String userId, String username, String firstName, String lastName,
                                String email, String phoneNumber, String preferedLanguage) {
    updateUserRepresentation(userId, username, firstName, lastName, email, phoneNumber);
    updateUser(userId, preferedLanguage);
  }


  private void updateUserRepresentation(String userId, String username, String firstName,
                                        String lastName,
                                        String email, String phoneNumber) {
    var userRep = keycloakCommunicationService.getUserInfoId(userId);
    userRep.setUsername(username);
    userRep.setId(userId);
    userRep.setFirstName(firstName);
    userRep.setLastName(lastName);

    var updateEmailAndPhoneResult = checkUpdateEmailAndPhone(userRep, email, phoneNumber);
    userRep = (UserRepresentation) updateEmailAndPhoneResult[0];
    var resendEmailVerification = (Boolean) updateEmailAndPhoneResult[1];
    var resendPhoneVerification = (Boolean) updateEmailAndPhoneResult[2];

    keycloakCommunicationService
        .updateUserInfo(userRep, resendEmailVerification, resendPhoneVerification);
  }

  private Object[] checkUpdateEmailAndPhone(UserRepresentation userRep,
                                            String email, String phoneNumber) {
    var resendEmailVerification = false;
    var resendPhoneVerification = false;
    if (!email.equals(userRep.getEmail())) {
      userRep.setEmail(email);
      userRep.setEmailVerified(false);
      resendEmailVerification = true;
    }
    if (!userRep.getAttributes().get("phone_number").get(0).equals(phoneNumber)) {
      userRep.getAttributes().replace("phone_number", List.of(phoneNumber));
      userRep.getAttributes().replace("phone_number_verified", List.of("false"));
      resendPhoneVerification = true;
    }
    return new Object[] {userRep, resendEmailVerification, resendPhoneVerification};
  }
}
