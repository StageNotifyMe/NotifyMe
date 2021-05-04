package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.dto.AdminTokenResponseDto;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.dto.OrganisationDto;
import be.xplore.notifyme.dto.UserOrgPromotionDto;
import be.xplore.notifyme.dto.UserRepresentationDto;
import be.xplore.notifyme.service.OrganisationService;
import be.xplore.notifyme.service.TokenService;
import be.xplore.notifyme.service.UserService;
import be.xplore.notifyme.service.VenueService;
import com.google.gson.Gson;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles administrator api requests.
 */
@RestController
@RequestMapping("/admin")
@RolesAllowed("admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

  private final OrganisationService organisationService;
  private final VenueService venueService;
  private final UserService userService;
  private final TokenService tokenService;
  private final Gson gson;


  @GetMapping("/adminTest")
  public ResponseEntity<String> adminInfoTest() {
    return ResponseEntity.ok("Hello Admin.");
  }

  /**
   * API method that creates an organisation.
   *
   * @param name is the unique name of the organisation.
   * @return a newly created organisation.
   */
  @PostMapping("/organisation")
  public ResponseEntity<Organisation> createOrganisation(
      @RequestParam("name") @Valid @NotBlank @NotNull String name) {
    return ResponseEntity.ok(organisationService.createOrganisation(name));
  }

  /**
   * API method that gets an organisation by id.
   *
   * @param id is the unique id of the organisation.
   * @return a newly created organisation.
   */
  @GetMapping("/organisation")
  public ResponseEntity<Organisation> getOrganisation(
      @RequestParam("id") Long id) {
    return ResponseEntity.ok(organisationService.getOrganisation(id));
  }

  /**
   * Gets a list of all the organisations in the system.
   *
   * @return a list of organisation DTO
   */
  @GetMapping("/organisations")
  public ResponseEntity<List<OrganisationDto>> getOrganisations() {
    var organisations = new ArrayList<OrganisationDto>();
    for (var organisation : organisationService.getOrganisations()) {
      organisations.add(new OrganisationDto(organisation));
    }
    return ResponseEntity.ok(organisations);
  }

  @PostMapping("/venue")
  public ResponseEntity<Object> createVenue(
      @RequestBody @NotNull CreateVenueDto createVenueDto,
      Principal principal) {
    return venueService.createVenue(createVenueDto, principal);
  }

  /**
   * Promotes a user to manager of a certain organisation.
   *
   * @param userOrgPromotionDto which contains the username and org id.
   * @return the organisation object containing the newly added manager.
   */
  @PostMapping("/promoteUserToOrgMgr")
  public ResponseEntity<OrganisationDto> promoteUserToOrgMgr(@RequestBody @NotNull
      UserOrgPromotionDto userOrgPromotionDto, Principal principal) {
    return ResponseEntity.ok(new OrganisationDto(
        organisationService.promoteUserToOrgManager(userOrgPromotionDto.getUsername(),
            userOrgPromotionDto.getOrganisationId(), principal)));
  }

  /**
   * Gets a list of all keycloak users.
   *
   * @return a list of keycloak user representations in dto format.
   */
  @GetMapping("/users")
  public ResponseEntity<List<UserRepresentationDto>> getUsers() {
    var userRepListDto = new ArrayList<UserRepresentationDto>();
    AdminTokenResponseDto response = gson
        .fromJson(tokenService.getAdminAccesstoken().getBody(), AdminTokenResponseDto.class);
    var userRepresentations = userService
        .getAllUserInfo(response.getAccessToken());
    for (var userRep : userRepresentations) {
      userRepListDto.add(new UserRepresentationDto(userRep));
    }
    return ResponseEntity.ok(userRepListDto);
  }

}
