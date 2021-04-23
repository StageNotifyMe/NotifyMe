package be.xplore.notifyme.controller;

import be.xplore.notifyme.dto.UserRegistrationDTO;
import be.xplore.notifyme.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles user authentication related requests.
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(value = "/getToken")
  public ResponseEntity<String> getAccessTokenForUser(String username, String password) {
    return userService.login(username, password);
  }

  @GetMapping(value = "/register")
  public ResponseEntity<String> resgisterNewUser(
      @RequestBody UserRegistrationDTO userRegistrationDTO) {
    return userService
        .register(userRegistrationDTO.getFirstname(), userRegistrationDTO.getLastname(),
            userRegistrationDTO.getEmail(), userRegistrationDTO.getUsername(),
            userRegistrationDTO.getPassword());
  }
}
