package be.xplore.notifyme.service;

import be.xplore.notifyme.dto.AdminTokenResponse;
import be.xplore.notifyme.dto.CredentialRepresentation;
import be.xplore.notifyme.dto.UserRepresentation;
import com.google.gson.Gson;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
    httpJsonHeader.add("Authorization", "Bearer " + response.getAccessToken());
    var credentialRepresentation = new CredentialRepresentation("password",
        password, false);
    var userRepresentation = new UserRepresentation(firstname, lastname, email,
        username, true, List.of(credentialRepresentation));
    String parsedUserRepresentation = gson.toJson(userRepresentation);
    HttpEntity<String> request = new HttpEntity<>(parsedUserRepresentation, httpJsonHeader);

    return restTemplate.postForEntity(registerUri, request, Void.class);
  }

  private ResponseEntity<String> getAdminAccesstoken() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }
}
