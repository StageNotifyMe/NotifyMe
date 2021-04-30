package be.xplore.notifyme.controller;

import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.service.EventService;
import be.xplore.notifyme.service.VenueService;
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

@RestController
@RequestMapping("/vmanager")
@RequiredArgsConstructor
@Validated
public class VenueManagerController {
  private final EventService eventService;
  private final VenueService venueService;


  @PostMapping("/event")
  public ResponseEntity<Object> createEvent(@RequestBody @NotNull CreateEventDto createEventDto) {
    return eventService.createEvent(createEventDto);
  }

  @GetMapping("/venues")
  public ResponseEntity<Object> getAllVenuesForUser(@RequestParam @NotBlank String userId) {
    return ResponseEntity.ok(venueService.getVenuesForUser(userId));
  }
}
