package be.xplore.notifyme.service;

import be.xplore.notifyme.dto.AdminTokenResponseDto;
import be.xplore.notifyme.dto.CredentialRepresentationDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.UserRepresentationDto;
import be.xplore.notifyme.exception.CrudException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Service
public class KeycloakCommunicationService {

  @Value("${keycloak.resource}")
  String clientId;
  @Value("${keycloak.credentials.secret}")
  String clientSecret;
  @Value("${userservice.login.url}")
  String tokenUri;
  @Value("${userservice.register.url}")
  private String registerUri;
  final RestTemplate restTemplate;
  @Qualifier("xformRequest")
  @Autowired
  HttpHeaders httpXformHeader;
  private final Gson gson;


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
    map = addAuthorization(map);
    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }

  /**
   * Registers a new user by sending a service request to the keycloak server.
   */
  public void register(UserRegistrationDto userRegistrationDto) {
    var request =
        createHttpEntityForUserRegistry(getAdminAccesstoken(), userRegistrationDto);

    var restResponse = restTemplate.postForEntity(registerUri, request, Void.class);
    if (restResponse.getStatusCode() != HttpStatus.CREATED) {
      throw new CrudException("Could not create new user in database");
    }
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
   * Sends request to keycloak to send a verification email to new user.
   *
   * @param userId the id of the new user.
   */
  public void sendEmailVerificationRequest(String userId) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(getAdminAccesstoken());

    var uri = String.format("%s/%s/send-verify-email", registerUri, userId);

    HttpEntity<String> request = new HttpEntity<>(headers);
    restTemplate.put(uri, request);
  }

  /**
   * Gets Keycloak Userrepresentation with all of a user's info based on the username.
   *
   * @param username The username of the user you want to get information from.
   * @return Keycloak Userrepresentation.
   */
  public UserRepresentation getUserInfo(String username) {
    var userinfoReturn = getUserInfoRest(getAdminAccesstoken(), username);
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
   * Gets a service account admin access token so spring can execute management actions on
   * keycloak.
   *
   * @return ReponseEntity that if successful contains the accesstoken.
   */
  public String getAdminAccesstoken() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");
    map = addAuthorization(map);
    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<>(map, httpXformHeader);

    var restResponse =
        restTemplate.postForEntity(tokenUri, request, String.class);
    AdminTokenResponseDto responseDto = gson
        .fromJson(restResponse.getBody(), AdminTokenResponseDto.class);
    return responseDto.getAccessToken();
  }

  /**
   * Methods takes a multimap and adds client_id and client_secret to it.
   *
   * @param map containing values for a body.
   * @return the map with the client_id and client_secret values added.
   */
  public MultiValueMap<String, String> addAuthorization(MultiValueMap<String, String> map) {
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    return map;
  }

  /**
   * Gets user info from keycloak server.
   *
   * @param accessToken the admin access token.
   * @param username    to get the info from.
   * @return response entity containing the user info as a json string.
   */
  public ResponseEntity<String> getUserInfoRest(String accessToken, String username) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> request = new HttpEntity<>(headers);
    var uri = String.format("%s?username=%s", registerUri, username);
    return restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
  }

  /**
   * Gets all of the user info from keycloak.
   *
   * @param accessToken admin access token
   * @return response entity containing all the user info as a json string.
   */
  public ResponseEntity<String> getAllUserInfoRest(String accessToken) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> request = new HttpEntity<>(headers);
    return restTemplate.exchange(registerUri, HttpMethod.GET, request, String.class);
  }
}