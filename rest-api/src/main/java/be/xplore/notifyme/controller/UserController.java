package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.dto.ApplicationOrgNameDto;
import be.xplore.notifyme.dto.NotificationDto;
import be.xplore.notifyme.dto.OrganisationsLimitedInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.communicationpreference.GetCommunicationPreferenceDto;
import be.xplore.notifyme.dto.communicationpreference.PostCommunicationPreferenceDto;
import be.xplore.notifyme.dto.communicationpreference.UpdateCommunicationPreferenceDto;
import be.xplore.notifyme.service.ICommunicationPreferenceService;
import be.xplore.notifyme.service.IKeycloakCommunicationService;
import be.xplore.notifyme.service.IOrganisationService;
import be.xplore.notifyme.service.IUserOrgApplicationService;
import be.xplore.notifyme.service.IUserService;
import be.xplore.notifyme.service.NotificationService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  private final IUserService userService;
  private final IOrganisationService organisationService;
  private final IUserOrgApplicationService userOrgApplicationService;
  private final IKeycloakCommunicationService keycloakCommunicationService;
  private final ICommunicationPreferenceService communicationPreferenceService;
  private final NotificationService notificationService;

  @GetMapping(value = "/token")
  public ResponseEntity<String> getAccessTokenForUser(String username, String password) {
    return keycloakCommunicationService.login(username, password);
  }

  /**
   * Updates an existing communication preference object.
   *
   * @param dto contains updated values.
   * @return the updated object.
   */
  @CrossOrigin
  @PutMapping(value = "/communicationpreference")
  public ResponseEntity<Object> updateCommunicationPreference(
      @RequestBody UpdateCommunicationPreferenceDto dto) {
    var updatedPreference =
        communicationPreferenceService
            .updateCommunicationPreference(dto.getCommunicationPreferenceId(), dto.isActive(),
                dto.isDefault());
    return ResponseEntity.status(HttpStatus.OK).body(updatedPreference);
  }

  /**
   * Creates a new communication preference.
   *
   * @param postCommunicationPreferenceDto contains all values to create
   *                                       a new communication preference.
   * @return the created object.
   */
  @PostMapping(value = "/communicationpreference")
  public ResponseEntity<Object> postCommunicationPreference(
      @RequestBody PostCommunicationPreferenceDto postCommunicationPreferenceDto) {
    var createdPreference = communicationPreferenceService
        .createCommunicationPreference(postCommunicationPreferenceDto.getUserId(),
            postCommunicationPreferenceDto.isActive(), postCommunicationPreferenceDto.isDefault(),
            postCommunicationPreferenceDto.getCommunicationStrategy());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference);
  }

  @CrossOrigin
  @DeleteMapping(value = "/communicationpreference")
  public ResponseEntity<Object> deleteCommunicationPreference(
      @RequestParam long communicationPreferenceId) {
    communicationPreferenceService.deleteCommunicationPreference(communicationPreferenceId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /**
   * gets all communication preferences of a user.
   *
   * @param userId user ID.
   * @return list of communicationPreference.
   */
  @GetMapping(value = "/communicationpreferences")
  public ResponseEntity<List<GetCommunicationPreferenceDto>> getAllcommunicationPreferences(
      String userId) {
    var communicationPreferences =
        communicationPreferenceService.getAllCommunicationPreferencesForUser(userId);
    var parsedComPrefs = new ArrayList<GetCommunicationPreferenceDto>();
    for (CommunicationPreference comPref : communicationPreferences) {
      parsedComPrefs.add(new GetCommunicationPreferenceDto(comPref));
    }
    return ResponseEntity.ok(parsedComPrefs);
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

  /**
   * Gets a list of notifications for the calling user.
   *
   * @param principal injected by securitycontext.
   * @return Response entity containing the list of notifications of the user.
   */
  @GetMapping(value = "notifications")
  public ResponseEntity<List<NotificationDto>> getNotifications(Principal principal) {
    var notifications = notificationService
        .getNotificationsForUser(userService.getUserFromPrincipal(principal).getUserId());
    var notificationsDto = new ArrayList<NotificationDto>();
    notifications.forEach(n -> notificationsDto.add(new NotificationDto(n)));
    return ResponseEntity.ok(notificationsDto);
  }
}
