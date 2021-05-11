package be.xplore.notifyme.controller;

import be.xplore.notifyme.dto.ApplicationOrgNameDto;
import be.xplore.notifyme.dto.OrganisationsLimitedInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.service.KeycloakCommunicationService;
import be.xplore.notifyme.service.OrganisationService;
import be.xplore.notifyme.service.UserOrgApplicationService;
import be.xplore.notifyme.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
  private final OrganisationService organisationService;
  private final UserOrgApplicationService userOrgApplicationService;
  private final KeycloakCommunicationService keycloakCommunicationService;

  @GetMapping(value = "/token")
  public ResponseEntity<String> getAccessTokenForUser(String username, String password) {
    return keycloakCommunicationService.login(username, password);
  }

  /**
   * Allows unregistered users to create an account.
   */
  @PostMapping(value = "/register")
  public ResponseEntity<Void> registerNewUser(
      @RequestBody UserRegistrationDto registrationDto) {
    userService.register(registrationDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping(value = "/userInfo")
  public ResponseEntity<Object> getUserInfo(@RequestParam @NotBlank String username,
      Principal principal) {
    return ResponseEntity.ok(userService.getUserInfo(username, principal));
  }

  @GetMapping(value = "/organisations")
  public ResponseEntity<OrganisationsLimitedInfoDto> getOrganisations() {
    return ResponseEntity
        .ok(new OrganisationsLimitedInfoDto(organisationService.getOrganisations()));
  }

  @PostMapping(value = "orgApplication")
  public ResponseEntity<Void> applyToOrganisation(
      @RequestParam Long organisationId, Principal principal) {
    userOrgApplicationService.applyToOrganisation(organisationId, principal);
    return ResponseEntity.ok().build();
  }

  /**
   * Gets a list of organisationApplications for the calling user.
   *
   * @param principal injected by securitycontext.
   * @return Response entity containing the list of applications with relevant data.
   */
  @GetMapping(value = "orgApplications")
  public ResponseEntity<List<ApplicationOrgNameDto>> getUserApplications(Principal principal) {
    var applicationsDto = new ArrayList<ApplicationOrgNameDto>();
    for (var application : userOrgApplicationService.getUserOrgApplications(principal)) {
      applicationsDto.add(new ApplicationOrgNameDto(application));
    }
    return ResponseEntity.ok(applicationsDto);
  }
}
