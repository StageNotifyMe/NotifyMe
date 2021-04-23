package be.xplore.notifyme.service;

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

  @Autowired
  private RestTemplate restTemplate;
  @Qualifier("jsonRequest")
  @Autowired
  private HttpHeaders httpJsonHeader;
  @Qualifier("xformRequest")
  @Autowired
  private HttpHeaders httpXformHeader;
  @Value("${userservice.login.url}")
  private String tokenUri;
  @Value("${keycloak.resource}")
  private String clientId;
  @Value("${keycloak.credentials.secret}")
  private String clientSecret;

  /**
   * Sends the user login request to the keycloak server.
   *
   * @param username from user account.
   * @param password of the user.
   * @return ResponseEntity received from keycloak that contains the access token when succesful.
   */
  public ResponseEntity<String> login(String username, String password) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", username);
    map.add("password", password);
    map.add("grant_type", "password");
    map.add("client_id", clientId);
    map.add("client_secret", clientSecret);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpXformHeader);

    return restTemplate.postForEntity(tokenUri, request, String.class);
  }
}
