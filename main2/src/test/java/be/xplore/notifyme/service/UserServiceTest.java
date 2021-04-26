package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class UserServiceTest {

  @Autowired
  private UserService userService;
  @MockBean
  private RestTemplate restTemplate;

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
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
    assertEquals(HttpStatus.CREATED, userService.login("user", "User123!").getStatusCode());
  }
  @Test
  void registerFail() {
    when(restTemplate.postForEntity(anyString(), any(), any()))
        .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    assertEquals(HttpStatus.BAD_REQUEST, userService.login("user", "User123!").getStatusCode());
  }
}