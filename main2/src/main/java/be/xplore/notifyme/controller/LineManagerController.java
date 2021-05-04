package be.xplore.notifyme.controller;

import be.xplore.notifyme.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lmanager")
@RequiredArgsConstructor
public class LineManagerController {
  private final EventService eventService;

  @GetMapping("/events")
  public ResponseEntity<Object> getAllEventsForLineManager(@RequestParam String userId) {
    var events = eventService.getAllEventsForLineManager(userId);
    return ResponseEntity.ok(events);
  }
}
