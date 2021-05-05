package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.dto.AdminTokenResponseDto;
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
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class KeycloakCommunicationServiceTest {

  @Autowired
  private KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  private RestTemplate restTemplate;
  @MockBean
  private Gson gson;

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
    var arrayList = getTestUserRepresentation("test-id");
    final Type listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");

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

    keycloakCommunicationService.register(userRegistrationDto);
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

    assertThrows(CrudException.class, () -> {
      keycloakCommunicationService.register(userRegistrationDto);
    });
  }

  @Test
  void getUserInfoRestSuccessful() {
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");
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

    assertThrows(Exception.class, () -> {
      keycloakCommunicationService.getUserInfoRest("token", userRegistrationDto.getUsername());
    });
  }

  @Test
  void sendEmailVerificationRequestSuccessful() {
    this.mockGetAdminAccesstoken();
    assertDoesNotThrow(() -> {
      keycloakCommunicationService.sendEmailVerificationRequest("user");
    });
  }

  @Test
  void sendEmailVerificationRequestPutFails() {
    this.mockGetAdminAccesstoken();
    doThrow(RuntimeException.class).when(restTemplate).put(anyString(), any());

    assertThrows(CrudException.class, () -> {
      keycloakCommunicationService.sendEmailVerificationRequest("user");
    });
  }

  @Test
  void getUserInfoSuccessful() {
    this.mockGetAdminAccesstoken();
    this.mockGetUserInfoRest();
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    final UserRepresentation mockUserRep = mock(UserRepresentation.class);

    when(gson.fromJson(eq("LIST"), eq(listType))).thenReturn(mockList);
    when(mockList.get(anyInt())).thenReturn(mockUserRep);


    assertEquals(mockUserRep, keycloakCommunicationService.getUserInfo("user"));
  }

  @Test
  void getUserInfoNullFail() {
    this.mockGetAdminAccesstoken();
    this.mockGetUserInfoRest();
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    final UserRepresentation mockUserRep = mock(UserRepresentation.class);

    when(gson.fromJson(eq("LIST"), eq(listType))).thenReturn(null);

    assertThrows(CrudException.class, () -> {
      keycloakCommunicationService.getUserInfo("user");
    });
  }

  @Test
  void getUserInfoNoUserFound() {
    this.mockGetAdminAccesstoken();
    this.mockGetUserInfoRest();
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    final UserRepresentation mockUserRep = mock(UserRepresentation.class);

    when(gson.fromJson(eq("LIST"), eq(listType))).thenReturn(mockList);
    doThrow(RuntimeException.class).when(mockList).get(anyInt());

    assertThrows(CrudException.class, () -> {
      keycloakCommunicationService.getUserInfo("user");
    });
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
    when(gson.fromJson(eq("LIST"),eq(listType))).thenReturn(mockList);


    assertEquals(mockList, keycloakCommunicationService.getAllUserInfoRest("token"));
  }

  @Test
  void getAllUserInfoRestStatusNotOK(){
    final ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(mockResponseEntity.getBody()).thenReturn("LIST");
    when(gson.fromJson(eq("LIST"),eq(listType))).thenReturn(mockList);

    assertThrows(CrudException.class, ()->{
      keycloakCommunicationService.getAllUserInfoRest("token");
    });
  }

  @Test
  void getAllUserInfoRestNullFail(){
    final ResponseEntity<String> mockResponseEntity = mock(ResponseEntity.class);
    final ArrayList<UserRepresentation> mockList = mock(ArrayList.class);
    var listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(mockResponseEntity);
    when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    when(mockResponseEntity.getBody()).thenReturn("LIST");
    when(gson.fromJson(eq("LIST"),eq(listType))).thenReturn(null);

    assertThrows(CrudException.class, ()->{
      keycloakCommunicationService.getAllUserInfoRest("token");
    });
  }

  private void mockGetAdminAccesstoken() {
    AdminTokenResponseDto tokenResponse = mock(AdminTokenResponseDto.class);
    ResponseEntity restResponse = mock(ResponseEntity.class);
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(restResponse);
    when(restResponse.getBody()).thenReturn("body");
    when(gson.fromJson(anyString(), eq(AdminTokenResponseDto.class))).thenReturn(tokenResponse);
    when(tokenResponse.getAccessToken()).thenReturn("token");
  }

  private void mockGetUserInfoRest() {
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");
    final ResponseEntity restResponse = mock(ResponseEntity.class);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
        eq(String.class)))
        .thenReturn(restResponse);
    when(restResponse.getStatusCode()).thenReturn(HttpStatus.OK);
    when(restResponse.getBody()).thenReturn("LIST");
  }

  private ArrayList<UserRepresentation> getTestUserRepresentation(String id) {
    ArrayList<UserRepresentation> arrayList = new ArrayList<>();
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setId(id);
    arrayList.add(userRepresentation);
    return arrayList;
  }
}
