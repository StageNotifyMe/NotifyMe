package be.xplore.notifyme.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.domain.TeamApplication;
import be.xplore.notifyme.domain.TeamApplicationKey;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.persistence.ITeamRepo;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = TeamApplicationService.class)
class TeamApplicationServiceTest {

  @Autowired
  private ITeamApplicationService teamApplicationService;
  @MockBean
  private ITeamService teamService;
  @MockBean
  private ITeamRepo teamRepo;
  @MockBean
  private IUserService userService;
  @MockBean
  private INotificationService notificationService;

  @Test
  void applyForEventLine() {
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    var user = User.builder().userId("testUser").userName("test.user")
        .organisations(
            List.of(OrganisationUser.builder().isOrganisationLeader(false)
                .organisation(Organisation.builder().id(1L).build())
                .user(User.builder().userId("testUser").build())
                .build())).build();
    var team = Team.builder().id(1L)
        .organisations(List.of(
            Organisation.builder()
                .users(List.of(OrganisationUser.builder().isOrganisationLeader(true)
                    .user(User.builder().userId("testUser").userName("test.user").build()).build()))
                .id(1L).build()))
        .line(Line.builder().id(1L).event(Event.builder().title("anEvent").build()).build())
        .build();
    when(userService.getUserFromprincipalIncOrganisations(any())).thenReturn(user);
    when(teamService.getTeam(anyLong())).thenReturn(team);

    assertDoesNotThrow(() -> teamApplicationService.applyForEventLine(1L, principal));
  }

  @Test
  void getUserTeamApplications() {
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    var applications = Set.of(new TeamApplication());
    var user = User.builder().userId("testUser").teamApplications(applications).build();

    when(userService.getUserFromPrincipalIncTeamApplications(any())).thenReturn(user);
    assertEquals(applications, teamApplicationService.getUserApplications(principal));
  }

  @Test
  void getUserTeamApplicationsForOrgAdmin() {
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    var applications = Set.of(new TeamApplication());
    var user = User.builder().userId("testUser").teamApplications(applications).build();
    when(userService.getUserFromprincipalIncOrganisations(any())).thenReturn(user);
    when(teamRepo.getUserApplicationsForOrganisationManager(any())).thenReturn(applications);
    assertEquals(applications, teamApplicationService.getUserApplicationsForOrgAdmin(principal));
  }

  @Test
  void acceptTeamApplications() {
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    var team = Team.builder()
        .line(Line.builder().event(Event.builder().title("An Event").build()).build()).build();
    when(teamService.addUserToTeam(anyLong(), anyString())).thenReturn(team);
    when(userService.getUser(anyString())).thenReturn(new User());

    assertDoesNotThrow(() -> teamApplicationService
        .handleTeamApplication(TeamApplicationKey.builder().teamId(1L).userId("testUserId").build(),
            true, principal));
  }

  @Test
  void refuseTeamApplications() {
    KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);

    assertDoesNotThrow(() -> teamApplicationService
        .handleTeamApplication(TeamApplicationKey.builder().teamId(1L).userId("testUserId").build(),
            false, principal));
  }

}