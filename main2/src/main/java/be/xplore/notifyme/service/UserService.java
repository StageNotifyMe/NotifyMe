package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.dto.AdminTokenResponseDto;
import be.xplore.notifyme.dto.CredentialRepresentationDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.UserRepresentationDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IUserRepo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for communication with the keycloak server related to user accounts.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final RestTemplate restTemplate;
  @Qualifier("jsonRequest")
  @Autowired
  private HttpHeaders httpJsonHeader;
  @Qualifier("xformRequest")
  @Autowired
  private HttpHeaders httpXformHeader;
  @Value("${userservice.login.url}")
  private String tokenUri;
  @Value("${userservice.register.url}")
  private String registerUri;
  private final Gson gson;
  private final IUserRepo userRepo;
  private final TokenService tokenService;

  /**
   * Sends the user login request to the keycloak server.
   *
   * @param username from user account.
   * @param password of the user.
   * @return ResponseEntity received from keycloak that contains the access token when succesful.
   */
  public ResponseEntity<String> login(String username, String password) {
    final var passwordConst = "password";
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", username);
    map.add(passwordConst, password);
    map.add("grant_type", passwordConst);
    map = tokenService.addAuthorization(map);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }

  /**
   * Registers a new user by sending a service request to the keycloak server.
   */
  public ResponseEntity register(UserRegistrationDto userRegistrationDto) {
    AdminTokenResponseDto response = gson
        .fromJson(tokenService.getAdminAccesstoken().getBody(), AdminTokenResponseDto.class);

    var request = createHttpEntityForUserRegistry(response.getAccessToken(), userRegistrationDto);

    var registerReturn = restTemplate.postForEntity(registerUri, request, Void.class);
    //if creation was unsuccessful, don't get user info and send verification email
    if (registerReturn.getStatusCode() == HttpStatus.CREATED) {
      try {
        getUserInfoAndSendVerificationEmail(response.getAccessToken(),
            userRegistrationDto.getUsername());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("{errorMessage:\"" + e.getMessage() + "\"}");
      }
    }
    return registerReturn;
  }

  private HttpEntity<String> createHttpEntityForUserRegistry(
      String accessToken, UserRegistrationDto userRegistrationDto) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);

    var credentialRepresentation = new CredentialRepresentationDto("password",
        userRegistrationDto.getPassword(), false);
    var userRepresentation = new UserRepresentationDto(userRegistrationDto.getFirstname(),
        userRegistrationDto.getLastname(), userRegistrationDto.getEmail(),
        userRegistrationDto.getUsername(), true, List.of(credentialRepresentation));
    String parsedUserRepresentation = gson.toJson(userRepresentation);
    return new HttpEntity<>(parsedUserRepresentation, headers);
  }

  /**
   * Gets a user's ID based on their username and sends them an email to verify their email.
   *
   * @param accessToken Service account with management role over user accounts.
   * @param username    username of the user that needs to receive the email.
   */
  private void getUserInfoAndSendVerificationEmail(String accessToken, String username) {
    try {
      var userInfo = getUserInfo(accessToken, username);
      sendEmailVerificationRequest(accessToken, userInfo.getId());
      createUserInDatabase(userInfo.getId());
    } catch (Exception e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  private void createUserInDatabase(String id) {
    var user = new User();
    user.setExternalOidcId(id);
    userRepo.save(user);
  }


  private void sendEmailVerificationRequest(String adminAccessToken, String userId) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(adminAccessToken);

    var uri = String.format("%s/%s/send-verify-email", registerUri, userId);

    HttpEntity<String> request = new HttpEntity<>(headers);
    restTemplate.put(uri, request);
  }

  /**
   * Gets Keycloak Userrepresentation with all of a user's info based on the username.
   *
   * @param adminAccesstoken Admin service account token needed to authorize request.
   * @param username         The username of the user you want to get information from.
   * @return Keycloak Userrepresentation.
   */
  public UserRepresentation getUserInfo(
      String adminAccesstoken, String username) {
    var userinfoReturn = getUserInfoRest(adminAccesstoken, username);
    if (userinfoReturn.getStatusCode() == HttpStatus.OK) {
      var listType = new TypeToken<List<UserRepresentation>>() {
      }.getType();
      try {
        ArrayList<UserRepresentation> result =
            gson.fromJson(userinfoReturn.getBody(), listType);
        if (result == null) {
          throw new RestClientException("Result from GET on userinfo was null");
        }
        return result.get(0);
      } catch (Exception e) {
        throw new RestClientException(String
            .format("Could not retrieve user [%s] from database: %s", username, e.getMessage()));
      }
    } else {
      throw new RestClientException(String
          .format("Something went wrong retrieving user [%s], statuscode: [%s]", username,
              userinfoReturn.getStatusCodeValue()));
    }
  }

  /**
   * Returns a keycloak userrepresentation object.
   *
   * @param username of the user to get info from.
   * @return Keycloak UserRepresentation object.
   */
  public UserRepresentation getUserInfo(String username) {
    AdminTokenResponseDto response = gson
        .fromJson(tokenService.getAdminAccesstoken().getBody(), AdminTokenResponseDto.class);
    return getUserInfo(response.getAccessToken(), username);
  }

  private ResponseEntity<String> getUserInfoRest(String accessToken, String username) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> request = new HttpEntity<>(headers);
    var uri = String.format("%s?username=%s", registerUri, username);
    return restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
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
}
