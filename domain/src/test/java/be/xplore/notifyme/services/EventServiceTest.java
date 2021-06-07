package be.xplore.notifyme.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.persistence.IEventRepo;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.IVenueRepo;
import be.xplore.notifyme.services.implementations.EventService;
import be.xplore.notifyme.services.implementations.NotificationService;
import be.xplore.notifyme.services.implementations.TokenService;
import be.xplore.notifyme.services.implementations.UserService;
import be.xplore.notifyme.services.implementations.VenueService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {EventService.class})
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
  private NotificationService notificationService;
  @MockBean
  private IEventRepo eventRepo;
  @MockBean
  private IVenueRepo venueRepo;
  @MockBean
  private IMessageRepo messageRepo;

  @Test
  void createEventSuccessful() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    when(venueService.getVenue(1)).thenReturn(getTestVenue());
    when(userService.getUserFromPrincipal(any(Principal.class))).thenReturn(testUser);

    var ced = getTestCreateEventDto();
    var event = new Event();
    event.setId(1L);
    when(eventRepo.save(any())).thenReturn(event);
    assertThat(eventService.createEvent(ced, principal), instanceOf(Event.class));
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
    when(eventRepo.findByIdWithLineManagersAndVenue(1L)).thenReturn(Optional.of(testEvent));

    assertEquals(testEvent.getId(), eventService.getEvent(1L).getId());
  }

  @Test
  void getEventNotFound() {
    when(eventRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(CrudException.class, () -> eventService.getEvent(1L));
  }

  @Test
  void getEventAndVerifyLineManagerPermissionSuccessful() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    final IDToken token = Mockito.mock(IDToken.class);
    mocktestEvent();

    when(tokenService.getIdToken(any(Principal.class))).thenReturn(token);
    when(token.getSubject()).thenReturn("testUser");
    when(userService.getUser("testUser")).thenReturn(testUser);
    when(testUser.getUserId()).thenReturn("testUser");

    var result = eventService.getEventAndVerifyLineManagerPermission(1L, principal);
    assertEquals(getTestEventWithLineManager().getId(), result.getId());
    assertTrue(
        result.getLineManagers().stream()
            .anyMatch(user -> user.getUserId().equals(testUser.getUserId())));
  }

  private void mocktestEvent() {
    testEvent.setLineManagers(new HashSet<>());
    testEvent.setId(0);
    testEvent.getLineManagers().add(User.builder().userId("testUser").build());
    when(eventRepo.findByIdWithLineManagersAndVenue(anyLong())).thenReturn(Optional.of(testEvent));
  }

  @Test
  void getEventAndVerifyLineManagerPermissionUnauthorized() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    final IDToken token = Mockito.mock(IDToken.class);
    testEvent.setLineManagers(new HashSet<>());
    when(eventRepo.findByIdWithLineManagersAndVenue(1L)).thenReturn(Optional.of(testEvent));
    when(tokenService.getIdToken(any(Principal.class))).thenReturn(token);
    when(token.getPreferredUsername()).thenReturn("testUser");
    when(userService.getUser("testUser")).thenReturn(testUser);
    when(testUser.getUserId()).thenReturn("testUser");

    assertThrows(UnauthorizedException.class,
        () -> eventService.getEventAndVerifyLineManagerPermission(1L, principal));

  }

  @Test
  void getEventAndVerifyLineManagerPermissionNotfound() {
    final KeycloakAuthenticationToken principal = Mockito.mock(KeycloakAuthenticationToken.class);
    when(eventRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(CrudException.class,
        () -> eventService.getEventAndVerifyLineManagerPermission(1L, principal));

  }

  @Test
  void getAllEventsForLineManagerSuccessful() {
    List<Event> eventList = new LinkedList<>();
    eventList.add(testEvent);
    when(eventRepo.findAllForLineManager(anyString())).thenReturn(eventList);

    assertEquals(eventList, eventService.getAllEventsForLineManager("testUser"));
  }

  @Test
  void getAllEventsForLineManagerNotFound() {
    doThrow(CrudException.class).when(eventRepo).findAllForLineManager(anyString());

    assertThrows(CrudException.class, () -> eventService.getAllEventsForLineManager("testUser"));
  }

  @Test
  void promoteToLineManagerSuccessful() {
    when(userService.getUser(anyString())).thenReturn(testUser);
    when(eventRepo.findByIdWithLineManagersAndVenue(anyLong())).thenReturn(Optional.of(testEvent));
    when(eventRepo.save(any(Event.class))).thenReturn(testEvent);
    doNothing().when(userService).grantUserRole(anyString(), anyString());

    assertDoesNotThrow(() -> eventService.promoteToLineManager("userid", 1L));
  }

  @Test
  void promoteToLineManagerAlreadyLineManager() {
    var testEventB = testEvent;
    testEventB.getLineManagers().add(testUser);
    when(userService.getUser(anyString())).thenReturn(testUser);
    when(eventRepo.findByIdWithLineManagersAndVenue(anyLong())).thenReturn(Optional.of(testEventB));

    assertThrows(SaveToDatabaseException.class,
        () -> eventService.promoteToLineManager("userid", 1L));
  }

  @Test
  void promoteToLineManagerUserNotFound() {
    doThrow(CrudException.class).when(userService).getUser(anyString());

    assertThrows(CrudException.class,
        () -> eventService.promoteToLineManager("userid", 1L));
  }

  @Test
  void promoteToLineManagerEventNotFound() {
    when(userService.getUser(anyString())).thenReturn(testUser);
    when(eventRepo.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(CrudException.class,
        () -> eventService.promoteToLineManager("userid", 1L));
  }


  private CreateEventDto getTestCreateEventDto() {
    return new CreateEventDto("Evenement", "een evenement", "een artiest",
        "2021-04-30 06:45", 1);
  }

  private Venue getTestVenue() {
    Address address = new Address("Teststraat 10", "2000", "Antwerpen", "België");
    return new Venue(1, "Zaal", "een zaal", address, new HashSet<>());
  }

  private final User testUser = Mockito.mock(User.class);

  private final Event testEvent =
      new Event(1, "Evenement", "een evenement", "een artiest",
          LocalDateTime.now(), EventStatus.OK, getTestVenue(), new LinkedList<>(), new HashSet<>());

  private Event getTestEventWithLineManager() {
    var testEvent =
        new Event("Evenement", "een evenement", "een artiest", "2021-04-30 06:45", getTestVenue());
    testEvent.setLineManagers(new HashSet<>());
    testEvent.getLineManagers().add(testUser);
    return testEvent;
  }

  @Test
  void updateEventStatus() {
    mockUpdateEventStatus();

    var okEvent = eventService.updateEventStatus(1L, EventStatus.OK);
    assertEquals(EventStatus.OK, okEvent.getEventStatus());

    var cancelEvent = eventService.updateEventStatus(2L, EventStatus.CANCELED);
    assertEquals(EventStatus.CANCELED, cancelEvent.getEventStatus());
  }

  private void mockUpdateEventStatus() {
    when(eventRepo.updateEventStatus(anyLong(), any())).thenAnswer(new Answer<Event>() {
      @Override
      public Event answer(InvocationOnMock invocation) throws Throwable {
        var args = invocation.getArguments();
        return new Event((long) args[0], "title", "description", "artist", LocalDateTime.now(),
            (EventStatus) args[1], new Venue(), new LinkedList<>(), new HashSet<>());
      }
    });
    doNothing()
        .when(notificationService).createAndSendSystemNotification(anyString(), any(), any());
    this.mockGetAllOrganisationIds(2);
    doNothing().when(notificationService)
        .notifyOrganisationManagersForCancelEvent(any(), any());
    this.mockGetAttendingMembers(4);
    doNothing().when(notificationService).notifyUsers(anyList(), anyLong());
  }

  private void mockGetAllOrganisationIds(long amount) {
    var orgIds = new ArrayList<Long>();
    for (long i = 1L; i <= amount; i++) {
      orgIds.add(i);
    }
    when(eventRepo.getAllOrganisationIds(anyLong())).thenReturn(orgIds);
  }

  private void mockGetAttendingMembers(int amount) {
    var attendingMembers = new ArrayList<User>();
    for (int i = 0; i < amount; i++) {
      attendingMembers.add(new User(String.format("userId%s", i), String.format("username%s", i)));
    }
    when(eventRepo.getAttendingMembers(anyLong())).thenReturn(attendingMembers);
  }
}