package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.dto.CreateFacilityDto;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.dto.GetLineDto;
import be.xplore.notifyme.service.EventService;
import be.xplore.notifyme.service.FacilityService;
import be.xplore.notifyme.service.LineService;
import be.xplore.notifyme.service.VenueService;
import java.security.Principal;
import java.util.LinkedList;
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
  public ResponseEntity<Object> createEvent(@RequestBody @NotNull CreateEventDto createEventDto,
      Principal principal) {
    return eventService.createEvent(createEventDto, principal);
  }

  @GetMapping("/event")
  public ResponseEntity<Object> getEvent(@RequestParam long eventId) {
    var result = eventService.getEvent(eventId);
    return ResponseEntity.ok(result);
  }

   /**
   * Gets all of the lines for a certain event.
   *
   * @param eventId the unique id of the event.
   * @return the lines related to this event.
   */
  @GetMapping("/lines")
  public ResponseEntity<Object> getAllLinesForEvent(@RequestParam long eventId) {
    var lines = lineService.getAllLinesByEvent(eventId);
    var dtoLines = new LinkedList<GetLineDto>();
    for (Line line : lines) {
      dtoLines.add(new GetLineDto(line));
    }
    return ResponseEntity.ok(dtoLines);
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
