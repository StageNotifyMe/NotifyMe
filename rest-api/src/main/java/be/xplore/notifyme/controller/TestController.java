package be.xplore.notifyme.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

  @GetMapping("/")
  public ResponseEntity<String> basicAccessTest() {
    return ResponseEntity.ok("YOU HAVE REACHED THE SERVER");
  }
}
