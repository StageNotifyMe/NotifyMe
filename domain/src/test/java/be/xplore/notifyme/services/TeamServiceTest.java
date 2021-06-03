package be.xplore.notifyme.services;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.domain.TeamApplicationStatus;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ITeamRepo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {TeamService.class})
class TeamServiceTest {

  @Autowired
  private TeamService teamService;
  @MockBean
  private ITeamRepo teamRepo;

  private void setupTeamRepo() {
    final var dummyLine = new Line("note", 10);
    dummyLine.setId(1L);
    final var dummyOrgList = new ArrayList<Organisation>();
    final var dummyMemberSet = new HashSet<User>();
    final var dummyTeamApps = new ArrayList<TeamApplication>();
    final var dummyTeam = new Team(1L, dummyLine, dummyOrgList, dummyMemberSet, dummyTeamApps);
    final var dummyOrg = new Organisation(1L, "organisation", new ArrayList<>());
    final var dummyUsr = new User("userId", "username");

    setUpRepoMockito(dummyTeam, dummyOrg, dummyUsr, dummyOrgList);
  }

  private void setUpRepoMockito(Team dummyTeam, Organisation dummyOrg, User dummyUsr,
      List<Organisation> dummyOrglist) {
    when(teamRepo.findById(1L)).thenReturn(Optional.of(dummyTeam));
    when(teamRepo.create(1L, 1L)).thenReturn(dummyTeam);
    when(teamRepo.addOrganisation(1L, 1L)).thenReturn(addOrgToTeam(dummyTeam, dummyOrg));
    when(teamRepo.addUser(1L, "userId")).thenReturn(addUsrToTeam(dummyTeam, dummyUsr));
    when(teamRepo.findById(999L)).thenReturn(Optional.empty());
    when(teamRepo.getAvailableOrganisations(1L)).thenReturn(dummyOrglist);
    doNothing().when(teamRepo).delete(1L);
    doNothing().when(teamRepo).deleteOrganisationFromTeam(1L, 1L);
  }

  private Team addOrgToTeam(Team team, Organisation org) {
    team.getOrganisations().add(org);
    return team;
  }

  private Team addUsrToTeam(Team team, User user) {
    team.getTeamMembers().add(user);
    return team;
  }

  @Test
  void createTeam() {
    setupTeamRepo();
    var result = teamService.createTeam(1L, 1L);

    assertThat(result, instanceOf(Team.class));
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getLine().getId());
  }

  @Test
  void addOrganisationToTeam() {
    setupTeamRepo();

    var result = teamService.addOrganisationToTeam(1L, 1L);
    assertThat(result, instanceOf(Team.class));
    assertEquals(1L, result.getId());
    assertTrue(result.getOrganisations().stream().anyMatch(o -> o.getId() == 1L));
  }

  @Test
  void addUserToTeam() {
    setupTeamRepo();

    var result = teamService.addUserToTeam(1L, "userId");
    assertThat(result, instanceOf(Team.class));
    assertEquals(1L, result.getId());
    assertTrue(result.getTeamMembers().stream().anyMatch(m -> m.getUserId().equals("userId")));
  }

  @Test
  void deleteTeam() {
    setupTeamRepo();

    //trying valid teamId
    assertDoesNotThrow(() -> {
      teamService.deleteTeam(1L);
    });
  }

  @Test
  void getTeam() {
    setupTeamRepo();

    var result = teamService.getTeam(1L);
    assertThat(result, instanceOf(Team.class));
    assertEquals(1L, result.getId());

    assertThrows(CrudException.class, () -> {
      teamService.getTeam(999L);
    });
  }

  @Test
  void getAllAvailableOrganisations() {
    setupTeamRepo();

    var result = teamService.getAllAvailableOrganisations(1L);
    assertTrue(result.stream().anyMatch(o -> o.getId() == 1L));
  }

  @Test
  void deleteOrganisationFromTeam() {
    setupTeamRepo();

    assertDoesNotThrow(() -> {
      teamService.deleteOrganisationFromTeam(1L, 1L);
    });
  }

  @Test
  void changeApplicationStatus() {
    var team = new Team();
    when(teamRepo.changeApplicationStatus(anyString(), anyLong(), any())).thenReturn(team);
    assertEquals(team,
        teamService.changeApplicationStatus("test", 1L, TeamApplicationStatus.ACCEPTED));
  }
}