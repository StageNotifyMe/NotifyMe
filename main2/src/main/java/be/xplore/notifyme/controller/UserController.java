package be.xplore.notifyme.controller;

import be.xplore.notifyme.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping(value = "/getToken")
  public ResponseEntity<String> getAccessTokenForUser(String username, String password){
   return userService.login(username,password);
  }
}
