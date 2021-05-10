package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.dto.ApplicationUsernameDto;
import be.xplore.notifyme.dto.OrganisationDto;
import be.xplore.notifyme.dto.UserApplicationResponseDto;
import be.xplore.notifyme.service.UserOrgApplicationService;
import be.xplore.notifyme.service.UserOrgService;
import be.xplore.notifyme.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
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

  private final UserOrgApplicationService userOrgApplicationService;
  private final UserService userService;
  private final UserOrgService userOrgService;

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
   * Posts the response to a user application
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

  @GetMapping("organisations")
  public ResponseEntity<List<OrganisationDto>> organisations(Principal principal) {
    var orgDtos = new ArrayList<OrganisationDto>();
    for (var org : userOrgService.getOrgManagerOrganisations(principal)) {
      orgDtos.add(new OrganisationDto(org));
    }
    return ResponseEntity.ok(orgDtos);
  }

  @GetMapping("organisation")
  public ResponseEntity<Organisation> organisation(Long organisationId, Principal principal) {
    return ResponseEntity.ok(userOrgService.getOrgInfoAsManager(organisationId, principal));
  }
}
