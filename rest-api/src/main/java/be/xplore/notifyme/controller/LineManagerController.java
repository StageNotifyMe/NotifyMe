package be.xplore.notifyme.controller;

import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.dto.team.PostTeamDto;
import be.xplore.notifyme.dto.team.PutTeamDto;
import be.xplore.notifyme.services.IEventService;
import be.xplore.notifyme.services.ITeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/lmanager")
@RequiredArgsConstructor
public class LineManagerController {

  private final ITeamService teamService;
  private final IEventService eventService;

  @GetMapping("/events")
  public ResponseEntity<Object> getAllEventsForLineManager(@RequestParam String userId) {
    var events = eventService.getAllEventsForLineManager(userId);
    return ResponseEntity.ok(events);
  }

  @GetMapping("/team")
  public ResponseEntity<Object> getTeam(@RequestParam long teamId) {
    var team = teamService.getTeam(teamId);
    return ResponseEntity.ok(team);
  }

  @PostMapping("/team")
  public ResponseEntity<Object> createTeam(@RequestBody PostTeamDto postTeamDto) {
    var team = teamService.createTeam(postTeamDto.getLineId(), postTeamDto.getOrganisationId());
    return ResponseEntity.status(HttpStatus.CREATED).body(team);
  }

  /**
   * Updates a team.
   *
   * @param putTeamDto Dto team representation.
   * @return The updated team.
   */
  @PutMapping("/team")
  public ResponseEntity<Object> updateTeam(@RequestBody PutTeamDto putTeamDto) {
    Team team;
    if (putTeamDto.getUserId() != null) {
      team = teamService.addUserToTeam(putTeamDto.getTeamId(), putTeamDto.getUserId());
    } else {
      team =
          teamService.addOrganisationToTeam(putTeamDto.getTeamId(), putTeamDto.getOrganisationId());
    }
    return ResponseEntity.ok(team);
  }

  @DeleteMapping("/team")
  public ResponseEntity<Object> deleteTeam(@RequestParam long teamId) {
    teamService.deleteTeam(teamId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
