package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.venue.GetVenueDto;
import be.xplore.notifyme.dto.event.CreateEventDto;
import be.xplore.notifyme.dto.event.PutEventDto;
import be.xplore.notifyme.dto.facility.CreateFacilityDto;
import be.xplore.notifyme.dto.line.CreateLineDto;
import be.xplore.notifyme.services.IEventService;
import be.xplore.notifyme.services.IFacilityService;
import be.xplore.notifyme.services.ILineService;
import be.xplore.notifyme.services.IUserService;
import be.xplore.notifyme.services.IVenueService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vmanager")
@RolesAllowed({"venue_manager", "admin"})
@RequiredArgsConstructor
@Validated
public class VenueManagerController {

  private final IEventService eventService;
  private final IVenueService venueService;
  private final IFacilityService facilityService;
  private final ILineService lineService;
  private final IUserService userService;

  /**
   * HTTP POST: creates a new Event.
   *
   * @param createEventDto contains all relevant information for domain event object.
   * @param principal      authorization header.
   * @return created event.
   */
  @PostMapping("/event")
  public ResponseEntity<Object> createEvent(@RequestBody @NotNull CreateEventDto createEventDto,
                                            Principal principal) {
    var result = eventService
        .createEvent(createEventDto.getTitle(), createEventDto.getDescription(),
            createEventDto.getArtist(), createEventDto.getDateTime(), createEventDto.getVenueId(),
            principal);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @GetMapping("/event")
  public ResponseEntity<Object> getEvent(@RequestParam long eventId) {
    var result = eventService.getEvent(eventId);
    return ResponseEntity.ok(result);
  }

  /**
   * Updates the status of an event.
   *
   * @param putEventDto needs to contain eventId and status.
   * @return updated event object.
   */
  @PutMapping("/event/status")
  public ResponseEntity<Object> updateEventStatus(@RequestBody PutEventDto putEventDto) {
    var updatedEvent =
        eventService.updateEventStatus(putEventDto.getEventId(),
            EventStatus.valueOf(putEventDto.getEventStatus()));
    return ResponseEntity.ok(updatedEvent);
  }

  /**
   * HTTP GET: gets all venues of which the user is venue manager.
   *
   * @param userId of the venue manager.
   * @return List of venue objects.
   */
  @GetMapping("/venues")
  public ResponseEntity<Object> getAllVenuesForUser(@RequestParam @NotBlank String userId) {
    var venues = venueService.getVenuesForUser(userId);
    var venueDtos = new ArrayList<GetVenueDto>();
    for (Venue venue : venues) {
      venueDtos.add(new GetVenueDto(venue.getId(), venue.getName(), venue.getDescription(),
          venue.getAddress()));
    }
    return ResponseEntity.ok(venueDtos);
  }

  @GetMapping("/venue")
  public ResponseEntity<Object> getVenue(@RequestParam long venueId) {
    var venue = venueService.getVenue(venueId);
    return ResponseEntity.ok(venue);
  }

  /**
   * HTTP POST: creates a new Line object.
   *
   * @param createLineDto all information need for domain Line object.
   * @param principal     authorization header.
   * @return the crated Line object.
   */
  @PostMapping("/line")
  public ResponseEntity<Object> createLine(@RequestBody @NotNull CreateLineDto createLineDto,
                                           Principal principal) {
    var line = lineService.createLine(createLineDto.getNote(), createLineDto.getRequiredStaff(),
        createLineDto.getFacilityId(), createLineDto.getEventId(), principal);
    return ResponseEntity.status(HttpStatus.CREATED).body(line);
  }

  /**
   * HTTP POST: creates a new facility object.
   *
   * @param createFacilityDto contains all information for domain facility object.
   * @return created facility.
   */
  @PostMapping("/facility")
  public ResponseEntity<Object> createFacility(
      @RequestBody @NotNull CreateFacilityDto createFacilityDto) {
    var facility = facilityService
        .createFacility(createFacilityDto.getDescription(), createFacilityDto.getLocation(),
            createFacilityDto.getMinimalStaff(), createFacilityDto.getMaximalStaff(),
            createFacilityDto.getVenueId());
    return ResponseEntity.status(HttpStatus.CREATED).body(facility);
  }

  @GetMapping("facilities")
  public ResponseEntity<Object> getAllFacilitiesForVenue(@RequestParam long venueId) {
    var facilities = facilityService.getAllFacilitesForVenue(venueId);
    return ResponseEntity.ok(facilities);
  }

  @PostMapping("/promoteToLineManager")
  public ResponseEntity<Object> promoteUserToLineManager(@RequestParam String userId,
                                                         @RequestParam long eventId) {
    eventService.promoteToLineManager(userId, eventId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/lineManagers")
  public ResponseEntity<Object> getLineManagersForEvent(@RequestParam long eventId) {
    var result = eventService.getEvent(eventId).getLineManagers();
    return ResponseEntity.ok(result);
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
}
