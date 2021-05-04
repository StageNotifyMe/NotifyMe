package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IEventRepo;
import be.xplore.notifyme.persistence.IVenueRepo;
import java.security.Principal;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class EventServiceTest {
  @Autowired
  private EventService eventService;
  @MockBean
  private VenueService venueService;
  @MockBean
  private UserService userService;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private IEventRepo eventRepo;
  @MockBean
  private IVenueRepo venueRepo;

  @Test
  void createEventSuccessful() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    when(venueService.getVenue(1)).thenReturn(getTestVenue());
    when(userService.getUserFromPrincipal(any(Principal.class))).thenReturn(testUser);

    var ced = getTestCreateEventDto();
    assertEquals(ResponseEntity.status(HttpStatus.CREATED).build(),
        eventService.createEvent(ced, principal));
  }

  @Test
  void createEventVenueNotFound() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    doThrow(CrudException.class).when(venueService).getVenue(1);

    var ced = getTestCreateEventDto();
    assertThrows(CrudException.class, () ->
        eventService.createEvent(ced, principal));
  }

  @Test
  void getEventSuccessful() {
    when(eventRepo.findById(1L)).thenReturn(Optional.of(testEvent));

    assertEquals(testEvent.getId(), eventService.getEvent(1L).getId());
  }

  @Test
  void getEventNotFound() {
    when(eventRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(CrudException.class, () -> {
      eventService.getEvent(1L);
    });
  }

  @Test
  void getEventAndVerifyLineManagerPermissionSuccessful() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    final IDToken token = Mockito.mock(IDToken.class);
    when(eventRepo.findById(1L)).thenReturn(Optional.of(getTestEventWithLineManager()));
    when(tokenService.getIdToken(any(Principal.class))).thenReturn(token);
    when(token.getPreferredUsername()).thenReturn("testUser");
    when(userService.getUser("testUser")).thenReturn(testUser);
    when(testUser.getUserId()).thenReturn("testUser");

    var result = eventService.getEventAndVerifyLineManagerPermission(1L, principal);
    assertEquals(getTestEventWithLineManager().getId(), result.getId());
    assertTrue(
        result.getLineManagers().stream()
            .anyMatch(user -> user.getUserId().equals(testUser.getUserId())));
  }

  @Test
  void getEventAndVerifyLineManagerPermissionUnauthorized() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    final IDToken token = Mockito.mock(IDToken.class);
    testEvent.setLineManagers(new LinkedList<>());
    when(eventRepo.findById(1L)).thenReturn(Optional.of(testEvent));
    when(tokenService.getIdToken(any(Principal.class))).thenReturn(token);
    when(token.getPreferredUsername()).thenReturn("testUser");
    when(userService.getUser("testUser")).thenReturn(testUser);
    when(testUser.getUserId()).thenReturn("testUser");

    assertThrows(UnauthorizedException.class, () -> {
      eventService.getEventAndVerifyLineManagerPermission(1L, principal);
    });

  }


  private CreateEventDto getTestCreateEventDto() {
    return new CreateEventDto("Evenement", "een evenement", "een artiest",
        "2021-04-30 06:45", 1);
  }

  private Venue getTestVenue() {
    Address address = new Address("Teststraat 10", "2000", "Antwerpen", "België");
    return new Venue(1, "Zaal", "een zaal", address, new LinkedList<>());
  }

  private User testUser = Mockito.mock(User.class);

  private final Event testEvent =
      new Event("Evenement", "een evenement", "een artiest", "2021-04-30 06:45", getTestVenue());

  private Event getTestEventWithLineManager() {
    var testEvent =
        new Event("Evenement", "een evenement", "een artiest", "2021-04-30 06:45", getTestVenue());
    testEvent.setLineManagers(new LinkedList<>());
    testEvent.getLineManagers().add(testUser);
    return testEvent;
  }
}