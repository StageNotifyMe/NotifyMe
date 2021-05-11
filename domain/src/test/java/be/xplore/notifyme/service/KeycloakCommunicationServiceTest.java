package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.dto.AdminTokenResponseDto;
import be.xplore.notifyme.dto.RelevantClientInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.UserRepresentationDto;
import be.xplore.notifyme.exception.CrudException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = {KeycloakCommunicationService.class})
class KeycloakCommunicationServiceTest {

  @Autowired
  private KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  private RestTemplate restTemplate;
  @MockBean
  private Gson gson;

  final UserRegistrationDto userRegistrationDto =
      new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");


  @Test
  void loginSuccess() {
    String infoString = "userinfo";
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenReturn(ResponseEntity.ok(infoString));
    assertEquals(infoString, keycloakCommunicationService.login("user", "User123").getBody());
  }

  @Test
  void loginFail() {
    int statusCode = 401;
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenReturn(ResponseEntity.status(statusCode).build());
    assertEquals(statusCode,
        keycloakCommunicationService.login("user", "User123!").getStatusCode().value());
  }

  @Test
  void registerSuccess() {
    var arrayList = getTestUserRepresentation();
    final Type listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();

    when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("someResponseToken"));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("[{id:\"test-id\"}]"));

    when(gson.fromJson(anyString(), eq(AdminTokenResponseDto.class)))
        .thenReturn(new AdminTokenResponseDto("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentationDto.class)).thenReturn("User representation Json");

    when(gson.fromJson(anyString(), eq(listType))).thenReturn(arrayList);

    assertDoesNotThrow(() -> keycloakCommunicationService.register(userRegistrationDto));
  }

  @Test
  void registerFailOnCreation() {
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");
    when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("someResponseToken"));

    when(gson.fromJson(anyString(), eq(AdminTokenResponseDto.class)))
        .thenReturn(new AdminTokenResponseDto("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentationDto.class)).thenReturn("User representation Json");

    assertThrows(CrudException.class,
        () -> keycloakCommunicationService.register(userRegistrationDto));
  }

  @Test
  void getUserInfoRestSuccessful() {
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
        eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
    assertDoesNotThrow(() -> {
      keycloakCommunicationService.getUserInfoRest("token", "user.user");

    });
  }

  @Test
  void getUserInfoRestFail() {
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
        eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    assertThrows(Exception.class, () -> keycloakCommunicationService
        .getUserInfoRest("token", userRegistrationDto.getUsername()));
  }

  @Test
  void sendEmailVerificationRequestSuccessful() {
    this.mockGetAdminAccesstoken();
    assertDoesNotThrow(() -> keycloakCommunicationService.sendEmailVerificationRequest("user"));
  }

  @Test
  void sendEmailVerificationRequestPutFails() {
    this.mockGetAdminAccesstoken();
    doThrow(RuntimeException.class).when(restTemplate).put(anyString(), any());

    assertThrows(RestClientException.class,
        () -> keycloakCommunicationService.sendEmailVerificationRequest("user"));
  }

  @Test
  void getUserInfoSuccessful() {
    this.mockGetAdminAccesstoken();
    this.mockGetUserInfoRest();
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    final UserRepresentation mockUserRep = mock(UserRepresentation.class);

    when(gson.fromJson("LIST", listType)).thenReturn(mockList);
    when(mockList.get(anyInt())).thenReturn(mockUserRep);


    assertEquals(mockUserRep, keycloakCommunicationService.getUserInfo("user"));
  }

  @Test
  void getUserInfoNullFail() {
    this.mockGetAdminAccesstoken();
    this.mockGetUserInfoRest();
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();

    when(gson.fromJson("LIST", listType)).thenReturn(null);

    assertThrows(CrudException.class, () -> keycloakCommunicationService.getUserInfo("user"));
  }

  @Test
  void getUserInfoNoUserFound() {
    this.mockGetAdminAccesstoken();
    this.mockGetUserInfoRest();
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);

    when(gson.fromJson("LIST", listType)).thenReturn(mockList);
    doThrow(RuntimeException.class).when(mockList).get(anyInt());

    assertThrows(RuntimeException.class, () -> keycloakCommunicationService.getUserInfo("user"));
  }

  @Test
  void getAllUserInfoRestSuccessful() {
    final ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    when(mockResponseEntity.getBody()).thenReturn("LIST");
    when(gson.fromJson("LIST", listType)).thenReturn(mockList);


    assertEquals(mockList, keycloakCommunicationService.getAllUserInfoRest("token"));
  }

  @Test
  void getAllUserInfoRestStatusNotOK() {
    final ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(mockResponseEntity.getBody()).thenReturn("LIST");
    when(gson.fromJson("LIST", listType)).thenReturn(mockList);

    assertThrows(CrudException.class,
        () -> keycloakCommunicationService.getAllUserInfoRest("token"));
  }

  @Test
  void getAllUserInfoRestNullFail() {
    final ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    when(mockResponseEntity.getBody()).thenReturn("LIST");
    when(gson.fromJson("LIST", listType)).thenReturn(null);

    assertThrows(RestClientException.class,
        () -> keycloakCommunicationService.getAllUserInfoRest("token"));
  }

  @Test
  void giveUserRoleSuccesful() {
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    mockGetAdminAccesstoken();

    assertDoesNotThrow(() -> keycloakCommunicationService
        .giveUserRole("userid", getTestRoleRepresentation(), "clientid"));
  }

  @Test
  void giveUserRolePostingFails() {
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    mockGetAdminAccesstoken();
    var testRoleRep = getTestRoleRepresentation();

    assertThrows(CrudException.class, () -> keycloakCommunicationService
        .giveUserRole("userid", testRoleRep, "clientid"));
  }

  @Test
  void getClientRolesSuccessful() {
    this.mockGetClientRoles(true);

    var result = keycloakCommunicationService.getClientRoles("clientid");
    assertTrue(result.stream().anyMatch(role -> role.getId().equals("roleid")));
  }

  private void mockGetClientRoles(boolean isSuccesful) {
    final ResponseEntity<String> mockResponse = mock(ResponseEntity.class);
    mockGetAdminAccesstoken();
    when(restTemplate
        .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(mockResponse);
    when(mockResponse.getBody()).thenReturn("RoleArray");
    if (isSuccesful) {
      when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
    } else {
      when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    when(gson.fromJson("RoleArray", RoleRepresentation[].class))
        .thenReturn(new RoleRepresentation[] {getTestRoleRepresentation()});
  }

  @Test
  void getClientRolesGetFails() {
    mockGetClientRoles(false);

    assertThrows(CrudException.class,
        () -> keycloakCommunicationService.getClientRoles("clientid"));
  }

  @Test
  void getClientRoleSuccesful() {
    this.mockGetClientRoles(true);

    var result = keycloakCommunicationService.getClientRole("rolename", "clientid");
    assertEquals("roleid", result.getId());
  }

  @Test
  void getClientRoleRoleNotFound() {
    this.mockGetClientRoles(true);

    assertThrows(CrudException.class,
        () -> keycloakCommunicationService.getClientRole("invalidRoleName", "clientid"));
  }

  @Test
  void getAllClientsSuccessful() {
    mockGetAllClients(true);

    var result = keycloakCommunicationService.getAllClients();
    assertTrue(
        result.stream().anyMatch(ci -> ci.getId().equals(getTestRelevantClientInfo().getId())));
  }

  @Test
  void getAllClientsErrorOnGet() {
    mockGetAllClients(false);

    assertThrows(CrudException.class, () -> keycloakCommunicationService.getAllClients());
  }

  @Test
  void getClientSuccessful() {
    mockGetAllClients(true);

    var result = keycloakCommunicationService.getClient(getTestRelevantClientInfo().getClientId());
    assertEquals(getTestRelevantClientInfo().getId(), result.getId());
  }

  @Test
  void getClientNotFound() {
    mockGetAllClients(true);

    assertThrows(CrudException.class, () -> keycloakCommunicationService.getClient("invalidId"));
  }

  private void mockGetAllClients(boolean isSuccessful) {
    final ResponseEntity<String> mockResponse = mock(ResponseEntity.class);
    this.mockGetAdminAccesstoken();
    when(restTemplate
        .exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(mockResponse);
    when(mockResponse.getBody()).thenReturn("clientArray");
    if (isSuccessful) {
      when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
    } else {
      when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    when(gson.fromJson("clientArray", RelevantClientInfoDto[].class))
        .thenReturn(new RelevantClientInfoDto[] {getTestRelevantClientInfo()});
  }

  private RelevantClientInfoDto getTestRelevantClientInfo() {
    return new RelevantClientInfoDto("id", "clientid");
  }

  private RoleRepresentation getTestRoleRepresentation() {
    RoleRepresentation roleRepresentation = new RoleRepresentation();
    roleRepresentation.setName("rolename");
    roleRepresentation.setId("roleid");
    return roleRepresentation;
  }

  private void mockGetAdminAccesstoken() {
    AdminTokenResponseDto tokenResponse = mock(AdminTokenResponseDto.class);
    ResponseEntity restResponse = mock(ResponseEntity.class);
    when(restTemplate
        .postForEntity(eq(keycloakCommunicationService.tokenUri), any(), eq(String.class)))
        .thenReturn(restResponse);
    when(restResponse.getBody()).thenReturn("body");
    when(gson.fromJson(anyString(), eq(AdminTokenResponseDto.class))).thenReturn(tokenResponse);
    when(tokenResponse.getAccessToken()).thenReturn("token");
  }

  private void mockGetUserInfoRest() {
    final ResponseEntity restResponse = mock(ResponseEntity.class);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
        eq(String.class)))
        .thenReturn(restResponse);
    when(restResponse.getStatusCode()).thenReturn(HttpStatus.OK);
    when(restResponse.getBody()).thenReturn("LIST");
  }

  private ArrayList<UserRepresentation> getTestUserRepresentation() {
    ArrayList<UserRepresentation> arrayList = new ArrayList<>();
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setId("test-id");
    arrayList.add(userRepresentation);
    return arrayList;
  }
}
