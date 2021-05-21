package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.dto.OrganisationDto;
import be.xplore.notifyme.dto.UserOrgRequestDto;
import be.xplore.notifyme.services.IOrganisationService;
import be.xplore.notifyme.services.IUserService;
import be.xplore.notifyme.services.IVenueService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.http.HttpStatus;
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

  private final IOrganisationService organisationService;
  private final IVenueService venueService;
  private final IUserService userService;

  @GetMapping("/adminTest")
  public ResponseEntity<String> adminInfoTest() {
    return ResponseEntity.ok("Well hello there, admin!");
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

  @PostMapping("/promoteUserToVmanager")
  public ResponseEntity<Object> promoteUserToVenueManager(@RequestParam String userId,
                                                          long venueId) {
    venueService.makeUserVenueManager(userId, venueId);
    return ResponseEntity.noContent().build();
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
    var venue = venueService.createVenue(createVenueDto, principal);
    return ResponseEntity.status(HttpStatus.CREATED).body(venue);
  }

  /**
   * Promotes a user to manager of a certain organisation.
   *
   * @param userOrgRequestDto which contains the username and org id.
   * @return the organisation object containing the newly added manager.
   */
  @PostMapping("/promoteUserToOrgMgr")
  public ResponseEntity<OrganisationDto> promoteUserToOrgMgr(
      @RequestBody @NotNull UserOrgRequestDto userOrgRequestDto, Principal principal) {
    return ResponseEntity.ok(new OrganisationDto(
        organisationService.promoteUserToOrgManager(userOrgRequestDto.getUsername(),
            userOrgRequestDto.getOrganisationId(), principal)));
  }

  /**
   * Gets a list of all keycloak users.
   *
   * @return a list of keycloak user representations in dto format.
   */
  @GetMapping("/users")
  public ResponseEntity<List<UserRepresentation>> getUsers() {
    var userRepresentations = userService
        .getAllUserInfo();
    return ResponseEntity.ok(userRepresentations);
  }

  /**
   * Gets a list of all venue managers of given venue.
   *
   * @param venueId id of venue to get managers from.
   * @return list of users.
   */
  @GetMapping("/venueManagers")
  public ResponseEntity<Object> getAllVenueManagers(@RequestParam long venueId) {
    var managers = venueService.getAllVenueManagers(venueId);
    return ResponseEntity.ok(managers);
  }

}
