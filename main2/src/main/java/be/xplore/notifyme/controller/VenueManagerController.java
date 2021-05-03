package be.xplore.notifyme.controller;

import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.dto.CreateFacilityDto;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.service.EventService;
import be.xplore.notifyme.service.FacilityService;
import be.xplore.notifyme.service.LineService;
import be.xplore.notifyme.service.VenueService;
import java.security.Principal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vmanager")
@RequiredArgsConstructor
@Validated
public class VenueManagerController {
  private final EventService eventService;
  private final VenueService venueService;
  private final FacilityService facilityService;
  private final LineService lineService;


  @PostMapping("/event")
  public ResponseEntity<Object> createEvent(@RequestBody @NotNull CreateEventDto createEventDto) {
    return eventService.createEvent(createEventDto);
  }

  @GetMapping("/event")
  public ResponseEntity<Object> getEvent(@RequestParam long eventId) {
    var result = eventService.getEvent(eventId);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/lines")
  public ResponseEntity<Object> getAllLinesForEvent(@RequestParam long eventId) {
    var result = lineService.getAllLinesByEvent(eventId);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/venues")
  public ResponseEntity<Object> getAllVenuesForUser(@RequestParam @NotBlank String userId) {
    return ResponseEntity.ok(venueService.getVenuesForUser(userId));
  }

  @GetMapping("/venue")
  public ResponseEntity<Object> getVenue(@RequestParam long venueId) {
    var venue = venueService.getVenue(venueId);
    return ResponseEntity.ok(venue);
  }

  @PostMapping("/line")
  public ResponseEntity<Object> createLine(@RequestBody @NotNull CreateLineDto createLineDto,
                                           Principal principal) {
    var line = lineService.createLine(createLineDto, principal);
    return ResponseEntity.status(HttpStatus.CREATED).body(line);
  }

  @PostMapping("/facility")
  public ResponseEntity<Object> createFacility(
      @RequestBody @NotNull CreateFacilityDto createFacilityDto) {
    var facility = facilityService.createFacility(createFacilityDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(facility);
  }

  @GetMapping("facilities")
  public ResponseEntity<Object> getAllFacilitiesForVenue(@RequestParam long venueId) {
    var facilities = facilityService.getAllFacilitesForVenue(venueId);
    return ResponseEntity.ok(facilities);
  }
}
