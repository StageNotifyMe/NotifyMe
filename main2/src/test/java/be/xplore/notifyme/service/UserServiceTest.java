package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.dto.AdminTokenResponse;
import be.xplore.notifyme.dto.UserRepresentation;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  @Test
  void registerSuccess() {
    when(restTemplate.postForEntity(anyString(), any(), eq(void.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("someResponseToken"));

    when(gson.fromJson(anyString(), eq(AdminTokenResponse.class)))
        .thenReturn(new AdminTokenResponse("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentation.class)).thenReturn("User representation Json");

    assertEquals(HttpStatus.CREATED,
        userService.register("user", "userlastname", "user@user.be", "user.user", "User123!")
            .getStatusCode());
  }

  @Test
  void registerFail() {
    when(restTemplate.postForEntity(anyString(), any(), eq(void.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.OK).body("someResponseToken"));

    when(gson.fromJson(anyString(), eq(AdminTokenResponse.class)))
        .thenReturn(new AdminTokenResponse("a", 10, 10, "Access", 5, "scopes"));
    when(gson.toJson(UserRepresentation.class)).thenReturn("User representation Json");
    assertEquals(HttpStatus.BAD_REQUEST,
        userService.register("user", "userlastname", "user@user.be", "user.user", "User123!")
            .getStatusCode());
  }
}