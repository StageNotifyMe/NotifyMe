package be.xplore.notifyme.service;

import be.xplore.notifyme.dto.AdminTokenResponseDto;
import be.xplore.notifyme.dto.CredentialRepresentationDto;
import be.xplore.notifyme.dto.GiveClientRoleDto;
import be.xplore.notifyme.dto.RelevantClientInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.UserRepresentationDto;
import be.xplore.notifyme.exception.CrudException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
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
@Slf4j
public class KeycloakCommunicationService {

  @Value("${keycloak.resource}")
  String clientId;
  @Value("${keycloak.credentials.secret}")
  String clientSecret;
  @Value("${userservice.login.url}")
  String tokenUri;
  @Value("${userservice.register.url}")
  private String registerUri;
  @Value("${userservice.clients.url}")
  private String clientUri;
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
    try {
      var request = createJsonHttpEntity(getAdminAccesstoken());
      var uri = String.format("%s/%s/send-verify-email", registerUri, userId);
      restTemplate.put(uri, request);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new CrudException("Could not send verification email: " + e.getMessage());
    }
  }

  /**
   * Gets Keycloak Userrepresentation with all of a user's info based on the username.
   *
   * @param username The username of the user you want to get information from.
   * @return Keycloak Userrepresentation.
   */
  public UserRepresentation getUserInfo(String username) {
    try {
      var userinfoReturn = getUserInfoRest(getAdminAccesstoken(), username);
      var listType = new TypeToken<List<UserRepresentation>>() {
      }.getType();
      ArrayList<UserRepresentation> result = gson.fromJson(userinfoReturn, listType);
      if (result == null) {
        throw new CrudException("Result from GET on userinfo was null");
      }
      return result.get(0);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new CrudException(String
          .format("Could not retrieve user [%s] from database: %s", username, e.getMessage()));
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
  public String getUserInfoRest(String accessToken, String username) {
    try {
      HttpEntity<String> request = createJsonHttpEntity(accessToken);
      var uri = String.format("%s?username=%s", registerUri, username);
      var restReturn = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
      if (restReturn.getStatusCode() != HttpStatus.OK) {
        throw new CrudException("Could not retrieve user for username " + username);
      }
      return restReturn.getBody();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  /**
   * Creates an instance of HttpEntity(String) with content-type = application/json and a bearerAuth
   * with given accessToken.
   *
   * @param accessToken bearer access token.
   * @return HttpEntity(String).
   */
  public HttpEntity<String> createJsonHttpEntity(String accessToken) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);
    return new HttpEntity<>(headers);
  }

  /**
   * Creates an instance of HttpEntity(String) with content-type = application/json and a bearerAuth
   * with given accestoken. Includes an object as body.
   *
   * @param accessToken bearer access token.
   * @param body        object to include as body.
   * @return HttpEntity(String).
   */
  public HttpEntity<String> createJsonHttpEntity(String accessToken, Object body) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);
    return new HttpEntity<>(gson.toJson(body), headers);
  }


  /**
   * Gets all of the user info from keycloak.
   *
   * @param accessToken admin access token
   * @return response entity containing all the user info as a json string.
   */
  public List<UserRepresentation> getAllUserInfoRest(String accessToken) {
    try {
      var request = createJsonHttpEntity(accessToken);
      var userinfoReturn =
          restTemplate.exchange(registerUri, HttpMethod.GET, request, String.class);
      if (userinfoReturn.getStatusCode() != HttpStatus.OK) {
        throw new CrudException("");
      }
      List<UserRepresentation> result = parseUserInfo(userinfoReturn.getBody());
      if (result == null) {
        throw new RestClientException("Result from GET on userinfo was null");
      }
      return result;
    } catch (Exception e) {
      throw new CrudException(
          "Something went wrong trying to GET from keycloakServer: " + e.getMessage());
    }
  }

  /**
   * Gives a given user a given Notifyme-Realm client-role of a given client.
   *
   * @param userId     of the user to be given a role.
   * @param roleToGive role to give to given user.
   * @param idOfClient id of the client where the role is defined.
   */
  public void giveUserRole(String userId, RoleRepresentation roleToGive, String idOfClient) {
    var uri = registerUri + String.format("/%s/role-mappings/clients/%s", userId, idOfClient);
    var role = new GiveClientRoleDto(roleToGive.getId(), roleToGive.getName(), true);
    var body = new GiveClientRoleDto[]{role};
    var entity = createJsonHttpEntity(getAdminAccesstoken(), body);
    var restResult = restTemplate.postForEntity(uri, entity, String.class);
    if (restResult.getStatusCode() != HttpStatus.NO_CONTENT) {
      throw new CrudException("Could not give role to user. Response: " + restResult.getBody());
    }
  }

  /**
   * Gets all client-roles of a Notifyme-Realm client.
   *
   * @param idOfClient id of a client of the Notifyme-Realm.
   * @return list of keycloak RoleRepresentations.
   */
  public List<RoleRepresentation> getClientRoles(String idOfClient) {
    var entity = createJsonHttpEntity(getAdminAccesstoken());
    var uri = clientUri + String.format("/%s/roles", idOfClient);
    var roles = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    if (roles.getStatusCode() != HttpStatus.OK) {
      throw new CrudException("Could not retrieve roles: " + roles.getBody());
    }
    return Arrays.asList(parseClientRoles(roles.getBody()));
  }

  /**
   * Gets a defined client-role of a Notifyme-Realm client.
   *
   * @param roleName   name of the role to get.
   * @param idOfClient id of a client of the Notifyme-Realm.
   * @return a keycloak RoleRepresentation.
   */
  public RoleRepresentation getClientRole(String roleName, String idOfClient) {
    var roles = getClientRoles(idOfClient);
    var role = roles.stream().filter(r -> r.getName().equals(roleName)).findFirst();
    if (role.isPresent()) {
      return role.get();
    } else {
      throw new CrudException("Could not find role for roleName " + roleName);
    }
  }

  private RoleRepresentation[] parseClientRoles(String jsonString) {
    return gson.fromJson(jsonString, RoleRepresentation[].class);
  }

  /**
   * Gets client's relevant information.
   *
   * @param clientId id of a client of the Notifyme-Realm.
   * @return RelevantClientInfoDto.
   */
  public RelevantClientInfoDto getClient(String clientId) {
    var clients = getAllClients();
    var client = clients.stream().filter(c -> c.getClientId().equals(clientId)).findFirst();
    if (client.isPresent()) {
      return client.get();
    } else {
      throw new CrudException("Could not find client for id " + clientId);
    }
  }

  /**
   * Gets all clients' relevant information from the Notifyme-Realm.
   *
   * @return list of RelevantClientInfoDto.
   */
  public List<RelevantClientInfoDto> getAllClients() {
    var entity = createJsonHttpEntity(getAdminAccesstoken());
    var clients = restTemplate.exchange(clientUri, HttpMethod.GET, entity, String.class);
    if (clients.getStatusCode() != HttpStatus.OK) {
      throw new CrudException("Could not retrieve clients: " + clients.getBody());
    }
    return Arrays.asList(parseClients(clients.getBody()));
  }

  private RelevantClientInfoDto[] parseClients(String jsonString) {
    return gson.fromJson(jsonString, RelevantClientInfoDto[].class);
  }


  private List<UserRepresentation> parseUserInfo(String bodyToParse) {
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    return gson.fromJson(bodyToParse, listType);
  }
}
