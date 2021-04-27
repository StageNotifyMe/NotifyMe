package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.dto.AdminTokenResponse;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.UserRepresentationDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class UserServiceTest {

  @MockBean
  private RestTemplate restTemplate;
  @MockBean
  private Gson gson;
  @Autowired
  private UserService userService;

  @Test
  void loginSuccess() {
    String infoString = "userinfo";
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenReturn(ResponseEntity.ok(infoString));
    assertEquals(infoString, userService.login("user", "User123!").getBody());
  }

  @Test
  void loginFail() {
    int statusCode = 401;
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenReturn(ResponseEntity.status(statusCode).build());
    assertEquals(statusCode, userService.login("user", "User123!").getStatusCode().value());
  }

  private ArrayList<UserRepresentation> getTestUserRepresentation(String id) {
    ArrayList<UserRepresentation> arrayList = new ArrayList<>();
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setId(id);
    arrayList.add(userRepresentation);
    return arrayList;
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

    when(gson.fromJson(anyString(), eq(AdminTokenResponse.class)))
        .thenReturn(new AdminTokenResponse("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentationDto.class)).thenReturn("User representation Json");

    when(gson.fromJson(anyString(), eq(listType))).thenReturn(arrayList);

    assertEquals(HttpStatus.CREATED,
        userService.register(userRegistrationDto)
            .getStatusCode());
  }

  @Test
  void registerFailOnCreation() {
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");
    when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("someResponseToken"));

    when(gson.fromJson(anyString(), eq(AdminTokenResponse.class)))
        .thenReturn(new AdminTokenResponse("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentationDto.class)).thenReturn("User representation Json");

    assertEquals(HttpStatus.BAD_REQUEST,
        userService.register(userRegistrationDto)
            .getStatusCode());
  }

  @Test
  void registerFailOnRetrieveUserInfo() {
    var arrayList = getTestUserRepresentation("test-id");
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");

    when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("someResponseToken"));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));

    when(gson.fromJson(anyString(), eq(AdminTokenResponse.class)))
        .thenReturn(new AdminTokenResponse("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentationDto.class)).thenReturn("User representation Json");

    Type listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    when(gson.fromJson(anyString(), eq(listType))).thenReturn(arrayList);

    assertEquals(HttpStatus.BAD_REQUEST,
        userService.register(userRegistrationDto)
            .getStatusCode());
  }

  @Test
  void registerFailNoUserFoundInDB() {
    var arrayList = getTestUserRepresentation("test-id");
    final UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user", "userlastname", "user@user.be", "user.user", "User123!");

    when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("someResponseToken"));
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("[]"));

    when(gson.fromJson(anyString(), eq(AdminTokenResponse.class)))
        .thenReturn(new AdminTokenResponse("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentationDto.class)).thenReturn("User representation Json");

    Type listType = new TypeToken<List<UserRepresentation>>() {
    }.getType();
    when(gson.fromJson(eq("[]"), eq(listType))).thenReturn(null);

    assertEquals(HttpStatus.BAD_REQUEST,
        userService.register(userRegistrationDto)
            .getStatusCode());
  }
}