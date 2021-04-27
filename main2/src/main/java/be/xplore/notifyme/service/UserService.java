package be.xplore.notifyme.service;

import be.xplore.notifyme.dto.AdminTokenResponse;
import be.xplore.notifyme.dto.CredentialRepresentation;
import be.xplore.notifyme.dto.UserRepresentationDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Service for communication with the keycloak server related to user accounts.
 */
@Service
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
  @Value("${keycloak.resource}")
  private String clientId;
  @Value("${keycloak.credentials.secret}")
  private String clientSecret;
  private final Gson gson;

  public UserService(RestTemplate restTemplate, Gson gson) {
    this.restTemplate = restTemplate;
    this.gson = gson;
  }

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
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }

  /**
   * Registers a new user by sending a service request to the keycloak server.
   */
  public ResponseEntity<Void> register(String firstname, String lastname, String email,
                                       String username, String password) {
    AdminTokenResponse response = gson
        .fromJson(getAdminAccesstoken().getBody(), AdminTokenResponse.class);

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(response.getAccessToken());

    var credentialRepresentation = new CredentialRepresentation("password",
        password, false);
    var userRepresentation = new UserRepresentationDto(firstname, lastname, email,
        username, true, List.of(credentialRepresentation));
    String parsedUserRepresentation = gson.toJson(userRepresentation);
    HttpEntity<String> request = new HttpEntity<>(parsedUserRepresentation, headers);

    var registerReturn = restTemplate.postForEntity(registerUri, request, Void.class);
    var userInfo = getUserInfo(response.getAccessToken(), username);
    sendEmailVerificationRequest(response.getAccessToken(), userInfo.getId());
    return registerReturn;
  }

  /**
   * Gets a service account admin access token so spring can execute management actions on keycloak.
   *
   * @return ReponseEntity that if successful contains the accesstoken.
   */
  public ResponseEntity<String> getAdminAccesstoken() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }

  private void sendEmailVerificationRequest(String adminAccessToken, String userId) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(adminAccessToken);

    String uri = String.format("%s/%s/send-verify-email", registerUri, userId);

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
    var headers = new HttpHeaders();
    headers.setBearerAuth(adminAccesstoken);

    String uri = String.format("%s?username=%s", registerUri, username);

    HttpEntity<String> request = new HttpEntity<>(headers);

    var userinfoReturn =
        restTemplate.exchange(uri, HttpMethod.GET, request, String.class).getBody();

    Type listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    ArrayList<UserRepresentation> result =
        gson.fromJson(userinfoReturn, listType);
    assert result != null;
    return result.get(0);
  }
}
