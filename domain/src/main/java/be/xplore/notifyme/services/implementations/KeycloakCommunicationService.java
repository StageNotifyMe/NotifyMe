package be.xplore.notifyme.services.implementations;

import be.xplore.notifyme.dto.AdminTokenResponseDto;
import be.xplore.notifyme.dto.CredentialRepresentationDto;
import be.xplore.notifyme.dto.GiveClientRoleDto;
import be.xplore.notifyme.dto.RelevantClientInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.UserRepresentationDto;
import be.xplore.notifyme.exception.ChannelNotVerifiedException;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.services.IKeycloakCommunicationService;
import be.xplore.notifyme.services.ISmsVerificationSenderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
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
public class KeycloakCommunicationService implements IKeycloakCommunicationService {

  @Value("${keycloak.resource}")
  @Getter
  private String clientId;
  @Value("${keycloak.credentials.secret}")
  @Getter
  private String clientSecret;
  @Value("${userservice.login.url}")
  @Getter
  public String tokenUri;
  @Value("${userservice.register.url}")
  @Getter
  private String registerUri;
  @Value("${userservice.clients.url}")
  @Getter
  private String clientUri;
  @Value("${notifyme.link}")
  @Getter
  private String notifymeLink;
  final RestTemplate restTemplate;
  @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
  @Qualifier("xformRequest")
  @Autowired
  HttpHeaders httpXformHeader;
  private final Gson gson;
  private final CodeGeneratorService codeGeneratorService;
  private final ISmsVerificationSenderService ismsVerificationSenderService;
  private String requestVerificationCode = "";


  /**
   * Sends the user login request to the keycloak server.
   *
   * @param username from user account.
   * @param password of the user.
   * @return ResponseEntity received from keycloak that contains the access token when succesful.
   */
  @Override
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
  @Override
  public void register(UserRegistrationDto userRegistrationDto) {
    var request =
        createHttpEntityForUserRegistry(getAdminAccesstoken(), userRegistrationDto);

    var restResponse = restTemplate.postForEntity(registerUri, request, Void.class);
    if (restResponse.getStatusCode() != HttpStatus.CREATED) {
      throw new CrudException("Could not create new user in database");
    }
    if (userRegistrationDto.getPhoneNumber() != null
        && !userRegistrationDto.getPhoneNumber().equals("")) {
      sendSmsVerificationCode(requestVerificationCode, userRegistrationDto.getUsername(),
          userRegistrationDto.getPhoneNumber());
    }

  }

  private HttpEntity<String> createHttpEntityForUserRegistry(
      String accessToken, UserRegistrationDto userRegistrationDto) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(accessToken);

    var credentialRepresentation = new CredentialRepresentationDto("password",
        userRegistrationDto.getPassword(), false);
    var attributes = createAttributes(userRegistrationDto);
    var userRepresentation = new UserRepresentationDto(userRegistrationDto.getFirstname(),
        userRegistrationDto.getLastname(), userRegistrationDto.getEmail(),
        userRegistrationDto.getUsername(), true, attributes, List.of(credentialRepresentation));
    String parsedUserRepresentation = gson.toJson(userRepresentation);
    return new HttpEntity<>(parsedUserRepresentation, headers);
  }

  private Map<String, List<String>> createAttributes(UserRegistrationDto userRegistrationDto) {
    HashMap<String, List<String>> attributes = new HashMap<>();
    var phoneNumber = new ArrayList<String>();
    phoneNumber.add(userRegistrationDto.getPhoneNumber());
    attributes.put("phone_number", phoneNumber);
    attributes.put("phone_number_verified", List.of("false"));
    requestVerificationCode = codeGeneratorService.generatePhoneVerificationCode();
    attributes.put("phone_number_verification_code",
        List.of(requestVerificationCode));
    return attributes;
  }

  private void sendSmsVerificationCode(String code, String username, String phoneNo) {
    ismsVerificationSenderService.send("Please verify your phone no.",
        "You should verify your phone No. to use it in the notifyme application."
            + "\n follow this link to activate your phone: "
            + notifymeLink + "user/activatePhone?username=" + username + "&code=" + code, phoneNo);
  }

  @Override
  public void verifyPhoneNo(String username, String code) {
    var userRep = getUserInfoUsername(username);
    var foundCode = userRep.getAttributes().get("phone_number_verification_code").stream()
        .filter(ur -> ur.equals(code)).findFirst();
    if (foundCode.isPresent()) {
      userRep.getAttributes().replace("phone_number_verified", List.of("true"));
      var entity = createJsonHttpEntity(getAdminAccesstoken(), userRep);
      restTemplate.put(registerUri + "/" + userRep.getId(), entity, String.class);
    } else {
      throw new ChannelNotVerifiedException(
          "Could not verify phone no because the given code did not match the saved code.");
    }
  }

  @Override
  public void checkPhoneVerification(String userId) {
    var userRep = getUserInfoId(userId);
    var foundVerified = userRep.getAttributes().get("phone_number_verified").stream().findFirst();
    if (!(foundVerified.isPresent() && foundVerified.get().equals("true"))) {
      throw new ChannelNotVerifiedException("Phone no was not verified.");
    }
  }

  /**
   * Sends request to keycloak to send a verification email to new user.
   *
   * @param userId the id of the new user.
   */
  @Override
  public void sendEmailVerificationRequest(String userId) {
    var request = createJsonHttpEntity(getAdminAccesstoken());
    var uri = String.format("%s/%s/send-verify-email", registerUri, userId);
    try {
      restTemplate.put(uri, request);
    } catch (Exception e) {
      throw new RestClientException("Could not send verification email: " + e.getMessage());
    }
  }

  /**
   * Gets Keycloak Userrepresentation with all of a user's info based on the username.
   *
   * @param username The username of the user you want to get information from.
   * @return Keycloak Userrepresentation.
   */
  @Override
  public UserRepresentation getUserInfoUsername(String username) {
    var userinfoReturn = getUserInfoRest(getAdminAccesstoken(), username);
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    ArrayList<UserRepresentation> result = gson.fromJson(userinfoReturn, listType);
    if (result == null) {
      throw new CrudException("Result from GET on userinfo was null");
    }
    return result.get(0);
  }

  /**
   * Gets Keycloak Userrepresentation with all of a user's info based on the username.
   *
   * @param userId The userId of the user you want to get information from.
   * @return Keycloak Userrepresentation.
   */
  @Override
  public UserRepresentation getUserInfoId(String userId) {
    var userinfoReturn = getUserInfoRestById(getAdminAccesstoken(), userId);
    var result = gson.fromJson(userinfoReturn, UserRepresentation.class);
    if (result == null) {
      throw new CrudException("Result from GET on userinfo was null");
    }
    return result;
  }

  /**
   * Gets a service account admin access token so spring can execute management actions on
   * keycloak.
   *
   * @return ReponseEntity that if successful contains the accesstoken.
   */
  @Override
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
  @Override
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
  @Override
  public String getUserInfoRest(String accessToken, String username) {
    HttpEntity<String> request = createJsonHttpEntity(accessToken);
    var uri = String.format("%s?username=%s", registerUri, username);
    var restReturn = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
    if (restReturn.getStatusCode() != HttpStatus.OK) {
      throw new CrudException("Could not retrieve user for username " + username);
    }
    return restReturn.getBody();
  }

  @Override
  public String getUserInfoRestById(String accessToken, String userId) {
    HttpEntity<String> request = createJsonHttpEntity(accessToken);
    var uri = String.format("%s/%s", registerUri, userId);
    var restReturn = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
    if (restReturn.getStatusCode() != HttpStatus.OK) {
      throw new CrudException("Could not retrieve user for userId " + userId);
    }
    return restReturn.getBody();
  }

  /**
   * Creates an instance of HttpEntity(String) with content-type = application/json and a bearerAuth
   * with given accessToken.
   *
   * @param accessToken bearer access token.
   * @return HttpEntity(String).
   */
  @Override
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
  @Override
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
  @Override
  public List<UserRepresentation> getAllUserInfoRest(String accessToken) {
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
  }

  /**
   * Gives a given user a given Notifyme-Realm client-role of a given client.
   *
   * @param userId     of the user to be given a role.
   * @param roleToGive role to give to given user.
   * @param idOfClient id of the client where the role is defined.
   */
  @Override
  public void giveUserRole(String userId, RoleRepresentation roleToGive, String idOfClient) {
    var uri = registerUri + String.format("/%s/role-mappings/clients/%s", userId, idOfClient);
    var role = new GiveClientRoleDto(roleToGive.getId(), roleToGive.getName(), true);
    var body = new GiveClientRoleDto[] {role};
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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

  @Override
  public void updateUserInfo(UserRepresentation userRepresentation, boolean resendEmailVer,
                             boolean resendPhoneVerificationCode) {
    try {
      var entity = createJsonHttpEntity(getAdminAccesstoken(), userRepresentation);
      restTemplate.put(registerUri + "/" + userRepresentation.getId(), entity, String.class);
      String verificationCode = codeGeneratorService.generatePhoneVerificationCode();
      if (resendEmailVer) {
        sendEmailVerificationRequest(userRepresentation.getId());
      }
      if (resendPhoneVerificationCode) {
        sendSmsVerificationCode(verificationCode, userRepresentation.getUsername(),
            userRepresentation.getAttributes().get("phone_number").get(0));
        userRepresentation.getAttributes()
            .replace("phone_number_verification_code", List.of(verificationCode));
      }
      restTemplate.put(registerUri + "/" + userRepresentation.getId(), entity, String.class);
    } catch (Exception e) {
      throw new CrudException(
          "Could not update userrepresentation for user " + userRepresentation.getId());
    }

  }
}
