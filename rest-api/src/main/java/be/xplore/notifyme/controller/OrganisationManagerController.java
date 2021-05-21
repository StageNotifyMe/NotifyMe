package be.xplore.notifyme.controller;

import be.xplore.notifyme.services.IUserOrgApplicationService;
import be.xplore.notifyme.services.IUserOrgService;
import be.xplore.notifyme.services.IUserService;
import be.xplore.notifyme.dto.ApplicationUsernameDto;
import be.xplore.notifyme.dto.OrganisationDto;
import be.xplore.notifyme.dto.OrganisationLimitedInfoDto;
import be.xplore.notifyme.dto.UserApplicationResponseDto;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/omanager")
@AllArgsConstructor
public class OrganisationManagerController {

  private final IUserOrgApplicationService userOrgApplicationService;
  private final IUserService userService;
  private final IUserOrgService userOrgService;

  /**
   * Gets the user applications for a certain organisation and maps the usernames on users.
   *
   * @param principal injected from security.
   * @return List of applications including the username.
   */
  @GetMapping("userApplications")
  public ResponseEntity<List<ApplicationUsernameDto>> getUserApplications(
      @RequestParam Long organisationId, Principal principal) {
    var userApplications =
        userOrgApplicationService.getOrgApplications(organisationId, principal);
    var userInfo = userService.getAllUserInfo();
    var userApplicationsDto = new ArrayList<ApplicationUsernameDto>();
    for (var application : userApplications) {
      var newApplicationDto = new ApplicationUsernameDto(application);
      var user = userInfo.stream()
          .filter(rep -> rep.getId().equals(application.getAppliedUser().getUserId())).findFirst();
      user.ifPresent(
          userRepresentation -> newApplicationDto.setUserName(userRepresentation.getUsername()));
      userApplicationsDto.add(newApplicationDto);
    }
    return ResponseEntity.ok(userApplicationsDto);
  }

  /**
   * Posts the response to a user application.
   *
   * @param principal injected from security.
   * @return List of applications including the username.
   */
  @PostMapping("userApplication")
  public ResponseEntity<Void> respondToUserApplication(
      @RequestBody UserApplicationResponseDto applicationResponseDto, Principal principal) {
    userOrgApplicationService.respondToApplication(applicationResponseDto.getOrganisationUserKey(),
        applicationResponseDto.isAccepted(), principal);
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieve a list of organisations the user is manager of.
   *
   * @param principal representation of authorized user.
   * @return a list of organisation dto's with relevant info for this request.
   */
  @GetMapping("organisations")
  public ResponseEntity<List<OrganisationDto>> organisations(Principal principal) {
    var orgDtos = new ArrayList<OrganisationDto>();
    for (var org : userOrgService.getOrgManagerOrganisations(principal)) {
      orgDtos.add(new OrganisationDto(org));
    }
    return ResponseEntity.ok(orgDtos);
  }

  /**
   * Gets an organisation with limited info for showing name and making further requests.
   *
   * @param organisationId unique id of the organisation the user is manager of.
   * @param principal      representation of authorized user.
   * @return an organisation dto with limited info.
   */
  @GetMapping("organisation")
  public ResponseEntity<Object> organisation(Long organisationId, Principal principal) {
    var result = userOrgService.getOrgInfoAsManager(organisationId, principal);
    var response = new OrganisationLimitedInfoDto(result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
