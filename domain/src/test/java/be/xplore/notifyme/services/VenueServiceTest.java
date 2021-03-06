package be.xplore.notifyme.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.exception.TokenHandlerException;
import be.xplore.notifyme.persistence.IVenueRepo;
import be.xplore.notifyme.services.implementations.TokenService;
import be.xplore.notifyme.services.implementations.UserService;
import be.xplore.notifyme.services.implementations.VenueService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.IDToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {VenueService.class})
class VenueServiceTest {

  @Autowired
  private VenueService venueService;
  @MockBean
  private UserService userService;
  @MockBean
  private TokenService tokenService;
  @MockBean
  private IVenueRepo venueRepo;

  @Test
  void createVenueSuccessful() {
    Principal mockPrincipal = Mockito.mock(Principal.class);
    IDToken mockIdToken = getMockIdToken();
    when(tokenService.getIdToken(mockPrincipal)).thenReturn(mockIdToken);

    Venue venue = new Venue();
    venue.setId(1);
    User user = getTestUser();
    when(venueRepo.save(any())).thenReturn(venue);
    when(venueRepo.addVenueManager(anyLong(), anyString())).thenReturn(venue);
    when(userService.getUser("abcd")).thenReturn(user);

    assertEquals(venue,
        venueService
            .createVenue("ConcertHall", "A concerthall", "Concertway 10", "1000", "Brussels",
                "Belgium", mockPrincipal));
  }

  @Test
  void createVenueUserNotFound() {
    Principal mockPrincipal = Mockito.mock(Principal.class);
    IDToken mockIdToken = getMockIdToken();
    when(tokenService.getIdToken(mockPrincipal)).thenReturn(mockIdToken);

    doThrow(new CrudException(
        String.format("User with id %s could not be found", mockIdToken.getSubject())))
        .when(venueRepo).save(any());

    assertThrows(CrudException.class, () ->
        venueService
            .createVenue("ConcertHall", "A concerthall", "Concertway 10", "1000", "Brussels",
                "Belgium", mockPrincipal));
  }

  @Test
  void createVenueTokenDecodeFails() {
    Principal mockPrincipal = Mockito.mock(Principal.class);

    doThrow(new TokenHandlerException(String.format("Could not convert %s object to IDToken object",
        mockPrincipal.getClass().getName()))).when(tokenService).getIdToken(mockPrincipal);

    assertThrows(TokenHandlerException.class, () ->
        venueService
            .createVenue("ConcertHall", "A concerthall", "Concertway 10", "1000", "Brussels",
                "Belgium", mockPrincipal));
  }

  @Test
  void getVenueSuccessful() {
    when(venueRepo.findById(anyLong())).thenReturn(Optional.of(getTestVenue()));
    assertNotNull(venueService.getVenue(1L));
  }

  @Test
  void getVenueFail() {
    when(venueRepo.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(CrudException.class, () -> venueService.getVenue(1L));
  }

  @Test
  void getVenuesSuccessful() {
    var user = getTestUser();
    when(userService.getUser("abcd")).thenReturn(user);
    when(venueRepo.getAllByManagersIsContaining(user.getUserId())).thenReturn(getTestVenues());

    var result = venueService.getVenuesForUser("abcd");
    assertEquals(getTestVenues().size(), result.size());
    assertEquals(getTestVenues().get(0).getId(), result.get(0).getId());
  }

  @Test
  void getVenuesUserNotFound() {
    doThrow(CrudException.class).when(venueRepo).getAllByManagersIsContaining(anyString());

    assertThrows(CrudException.class, () -> venueService.getVenuesForUser("unknown"));
  }

  @Test
  void getVenuesNoVenuesFound() {
    var user = getTestUser();
    when(userService.getUser("abcd")).thenReturn(user);
    when(venueRepo.getAllByManagersIsContaining(user.getUserId())).thenReturn(new LinkedList<>());

    var result = venueService.getVenuesForUser("abcd");
    assertEquals(0, result.size());
  }

  @Test
  void makeUserVenueManagerSuccessful() {
    when(userService.getUser(anyString())).thenReturn(getTestUser());
    when(venueRepo.findById(anyLong())).thenReturn(Optional.of(getTestVenue()));
    when(venueRepo.save(any(Venue.class))).thenReturn(getTestVenue());
    doNothing().when(userService).grantUserRole(anyString(), anyString());

    assertDoesNotThrow(() -> venueService.makeUserVenueManager("userid", 1L));
  }

  @Test
  void makeUserVenueManagerFailA() {
    doThrow(CrudException.class).when(venueRepo).addVenueManager(anyLong(), anyString());

    assertThrows(SaveToDatabaseException.class,
        () -> venueService.makeUserVenueManager("userid", 1L));
  }

  @Test
  void makeUserVenueManagerFailB() {
    when(userService.getUser(anyString())).thenReturn(getTestUser());
    doThrow(CrudException.class).when(userService).grantUserRole(anyString(), anyString());

    assertThrows(SaveToDatabaseException.class,
        () -> venueService.makeUserVenueManager("userid", 1L));
  }

  @Test
  void makeUserVenueManagerFailD() {
    when(userService.getUser(anyString())).thenReturn(getTestUser());
    when(venueRepo.findById(anyLong())).thenReturn(Optional.of(getTestVenue()));
    when(venueRepo.save(any(Venue.class))).thenReturn(getTestVenue());
    doThrow(CrudException.class).when(userService).grantUserRole(anyString(), anyString());

    assertThrows(SaveToDatabaseException.class,
        () -> venueService.makeUserVenueManager("userid", 1L));
  }

  @Test
  void getAllVenueManagers() {
    var userList = new ArrayList<User>();
    userList.add(getTestUser());
    when(venueRepo.getAllVenueManagers(1L)).thenReturn(userList);

    assertTrue(venueService.getAllVenueManagers(1L).stream()
        .anyMatch(user -> user.getUserId().equals(getTestUser().getUserId())));
  }

  private List<Venue> getTestVenues() {
    LinkedList<Venue> venues = new LinkedList<>();
    venues.add(getTestVenue());
    return venues;
  }

  private Venue getTestVenue() {
    return new Venue(1L, "Venue", "venue", new Address(), new HashSet<>());
  }

  private IDToken getMockIdToken() {
    IDToken mockIdToken = Mockito.mock(IDToken.class);
    when(mockIdToken.getSubject()).thenReturn("abcd");
    return mockIdToken;
  }

  private User getTestUser() {
    User user = new User();
    user.setUserId("abcd");
    return user;
  }

}