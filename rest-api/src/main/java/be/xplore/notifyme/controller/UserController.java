package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.dto.organisationapplication.ApplicationOrgNameDto;
import be.xplore.notifyme.dto.NotificationDto;
import be.xplore.notifyme.dto.OrganisationsLimitedInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import be.xplore.notifyme.dto.communicationpreference.GetCommunicationPreferenceDto;
import be.xplore.notifyme.dto.communicationpreference.PostCommunicationPreferenceDto;
import be.xplore.notifyme.dto.communicationpreference.UpdateCommunicationPreferenceDto;
import be.xplore.notifyme.dto.user.GetUserDto;
import be.xplore.notifyme.dto.user.PutUserDto;
import be.xplore.notifyme.services.ICommunicationPreferenceService;
import be.xplore.notifyme.services.IKeycloakCommunicationService;
import be.xplore.notifyme.services.ILineService;
import be.xplore.notifyme.services.IOrganisationService;
import be.xplore.notifyme.services.ITeamApplicationService;
import be.xplore.notifyme.services.ITeamService;
import be.xplore.notifyme.services.IUserOrgApplicationService;
import be.xplore.notifyme.services.IUserService;
import be.xplore.notifyme.services.implementations.NotificationService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
@CrossOrigin({"http://127.0.0.1:8080", "http://localhost:8080"})
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

  private final IUserService userService;
  private final ILineService lineService;
  private final IOrganisationService organisationService;
  private final IUserOrgApplicationService userOrgApplicationService;
  private final IKeycloakCommunicationService keycloakCommunicationService;
  private final ICommunicationPreferenceService communicationPreferenceService;
  private final NotificationService notificationService;
  private final ITeamApplicationService teamApplicationService;
  private final ITeamService teamService;

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
  @PutMapping(value = "/communicationpreference")
  public ResponseEntity<Object> updateCommunicationPreference(
      @RequestBody UpdateCommunicationPreferenceDto dto) {
    var updatedPreference =
        communicationPreferenceService
            .updateCommunicationPreference(dto.getCommunicationPreferenceId(), dto.isActive(),
                dto.isDefault(),
                dto.isUrgent());
    var resultDto = new GetCommunicationPreferenceDto(updatedPreference);
    return ResponseEntity.status(HttpStatus.OK).body(resultDto);
  }

  /**
   * Creates a new communication preference.
   *
   * @param postCommunicationPreferenceDto contains all values to create a new communication
   *                                       preference.
   * @return the created object.
   */
  @PostMapping(value = "/communicationpreference")
  public ResponseEntity<Object> postCommunicationPreference(
      @RequestBody PostCommunicationPreferenceDto postCommunicationPreferenceDto) {
    var createdPreference = communicationPreferenceService
        .createCommunicationPreference(postCommunicationPreferenceDto.getUserId(),
            postCommunicationPreferenceDto.isActive(), postCommunicationPreferenceDto.isDefault(),
            postCommunicationPreferenceDto.getIsUrgent(),
            postCommunicationPreferenceDto.getCommunicationStrategy());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference);
  }

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

  /**
   * HTTP GET: used to get user account information.
   *
   * @param username  of the user you want to retrieve information from.
   * @param principal Keycloak Authorization header.
   * @return relevant account information.
   */
  @GetMapping(value = "/account")
  public ResponseEntity<Object> getAccountSetting(@RequestParam String username,
      Principal principal) {
    var keycloakUser = userService.getUserInfo(username, principal);
    var dbUser = userService.getUser(keycloakUser.getId());
    var getUserDto = new GetUserDto(dbUser, keycloakUser);

    return ResponseEntity.ok(getUserDto);
  }

  /**
   * HTTP PUT: used to update user account information.
   *
   * @param putUserDto contains updated user account attributes.
   * @return 204 | no content
   */
  @PutMapping(value = "/account")
  public ResponseEntity<Object> updateAccountSettings(@RequestBody PutUserDto putUserDto) {
    userService.updateAccountInfo(putUserDto.getUserId(), putUserDto.getUsername(),
        putUserDto.getFirstName(), putUserDto.getLastName(), putUserDto.getEmail(),
        putUserDto.getPhoneNumber(), putUserDto.getPreferedLanguage());
    return ResponseEntity.noContent().build();
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

  @GetMapping(value = "activatePhone")
  public ResponseEntity<String> activatePhone(@RequestParam(name = "username") String username,
      @RequestParam(name = "code") String code) {
    keycloakCommunicationService.verifyPhoneNo(username, code);
    return ResponseEntity.ok("Your number is now ready to receive notifications through text.");
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

  /**
   * Gets a list of lines the calling user can apply to.
   *
   * @param principal injected by securitycontext.
   * @return Response entity containing the list of notifications of the user.
   */
  @GetMapping(value = "lines")
  public ResponseEntity<List<Line>> getAvailableLines(Principal principal) {
    return ResponseEntity
        .ok(lineService
            .getAvailableLinesForUser(userService.getUserFromPrincipal(principal).getUserId()));
  }


  /**
   * Gets a list of team applications for the calling user.
   *
   * @param principal injected by securitycontext.
   * @return Response entity containing the list of applications for a user.
   */
  @GetMapping(value = "teamApplications")
  public ResponseEntity<Set<TeamApplication>> getTeamApplications(Principal principal) {
    return ResponseEntity
        .ok(teamApplicationService.getUserApplications(principal));
  }

  /**
   * Sends out a user application for a team.
   *
   * @param principal injected by securitycontext.
   * @return Response entity containing the list of notifications of the user.
   */
  @PostMapping(value = "teamApplication")
  public ResponseEntity<Void> applyForTeam(@RequestParam(name = "teamId") Long teamId,
      Principal principal) {
    teamApplicationService.applyForEventLine(teamId, principal);
    return ResponseEntity.ok().build();
  }

  /**
   * Gets the teams that the user calling the method was added to.
   *
   * @param principal injected by securitycontext.
   * @return Response entity containing a set of teams the user was added to.
   */
  @GetMapping(value = "teams")
  public ResponseEntity<Set<Team>> getTeams(Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    return ResponseEntity.ok(teamService.getTeamsForUser(user.getUserId()));
  }

  /**
   * Remove a user from the team they are assigned to.
   *
   * @param principal injected by securitycontext.
   * @param teamId    unique id of the team.
   * @return Response entity containing the list of notifications of the user.
   */
  @DeleteMapping(value = "team")
  public ResponseEntity<Team> removeFromTeam(@RequestParam(name = "teamId") Long teamId,
      Principal principal) {
    var user = userService.getUserFromPrincipal(principal);
    return ResponseEntity.ok(teamService.removeUserFromTeam(teamId, user.getUserId()));
  }
}
