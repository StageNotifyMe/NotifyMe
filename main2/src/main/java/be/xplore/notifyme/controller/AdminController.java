package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Testcontroller for now.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final OrganisationService organisationService;

  @GetMapping("/getAdminTest")
  public ResponseEntity<String> adminInfoTest() {
    return ResponseEntity.ok("Hello Admin.");
  }

  @PostMapping("createOrganisation")
  public ResponseEntity<Organisation> createOrganisation(@RequestParam String name) {
    try {
      return ResponseEntity.ok(organisationService.createOrganisation(name));
    } catch (RuntimeException e) {
      return ResponseEntity.status(500).build();
    }
  }
}
