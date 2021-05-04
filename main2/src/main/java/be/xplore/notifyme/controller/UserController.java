package be.xplore.notifyme.controller;

import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.service.UserService;
import java.security.Principal;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles user authentication related requests.
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping(value = "/token")
  public ResponseEntity<String> getAccessTokenForUser(String username, String password) {
    return userService.login(username, password);
  }

  /**
   * Allows unregistered users to create an account.
   */
  @PostMapping(value = "/register")
  public ResponseEntity<Void> registerNewUser(
      @RequestBody UserRegistrationDto registrationDto) {
    return userService
        .register(registrationDto);
  }

  @GetMapping(value = "/userInfo")
  public ResponseEntity<Object> getUserInfo(@RequestParam @NotBlank String username,
                                            Principal principal) {
    return ResponseEntity.ok(userService.getUserInfo(username, principal));
  }
}
