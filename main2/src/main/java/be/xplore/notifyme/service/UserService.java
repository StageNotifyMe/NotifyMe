package be.xplore.notifyme.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
  public ResponseEntity register(String firstname, String lastname, String email,
      String username, String password) {
    AdminTokenResponse response = gson
        .fromJson(getAdminAccesstoken().getBody(), AdminTokenResponse.class);
    httpJsonHeader.add("Authorization", "Bearer " + response.accessToken);
    var credentialRepresentation = new CredentialRepresentation("password",
        password, false);
    var userRepresentation = new UserRepresentation(firstname, lastname, email,
        username, true, List.of(credentialRepresentation));
    String parsedUserRepresentation = gson.toJson(userRepresentation);
    HttpEntity<String> request = new HttpEntity<>(parsedUserRepresentation, httpJsonHeader);

    return restTemplate.postForEntity(registerUri, request, void.class);
  }

  private ResponseEntity<String> getAdminAccesstoken() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }

  @Getter
  @Setter
  @AllArgsConstructor
  private class AdminTokenResponse {

    @SerializedName(value = "access_token")
    private String accessToken;
    @SerializedName(value = "expires_in")
    private int expiresIn;
    @SerializedName(value = "refresh_expires_in")
    private int refreshExpiresIn;
    @SerializedName(value = "token_type")
    private String tokenType;
    @SerializedName(value = "not-before-policy")
    private int notBeforePolicy;
    private String scope;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  private class UserRepresentation {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Boolean enabled;
    private List<CredentialRepresentation> credentials = new LinkedList<>();

  }

  @Getter
  @Setter
  @AllArgsConstructor
  private class CredentialRepresentation {

    private String type;
    private String value;
    private boolean temporary;
  }
}
