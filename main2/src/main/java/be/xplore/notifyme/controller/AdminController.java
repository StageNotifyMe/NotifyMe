package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.service.OrganisationService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles administrator api requests.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

  private final OrganisationService organisationService;

  @GetMapping("/getAdminTest")
  public ResponseEntity<String> adminInfoTest() {
    return ResponseEntity.ok("Hello Admin.");
  }

  /**
   * API method that creates an organisation.
   *
   * @param name is the unique name of the organisation.
   * @return a newly created organisation.
   */
  @PostMapping("createOrganisation")
  public ResponseEntity<Organisation> createOrganisation(
      @RequestParam("name") @Valid @NotBlank @NotNull String name) {
    try {
      return ResponseEntity.ok(organisationService.createOrganisation(name));
    } catch (RuntimeException e) {
      return ResponseEntity.status(500).build();
    }
  }
}
