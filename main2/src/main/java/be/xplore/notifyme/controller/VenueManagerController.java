package be.xplore.notifyme.controller;

import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.service.EventService;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vmanager")
@RequiredArgsConstructor
@Validated
public class VenueManagerController {
  @Autowired
  private EventService eventService;

  @PostMapping("/event")
  public ResponseEntity<Object> createEvent(@RequestBody @NotNull CreateEventDto createEventDto) {
    return eventService.createEvent(createEventDto);
  }
}
