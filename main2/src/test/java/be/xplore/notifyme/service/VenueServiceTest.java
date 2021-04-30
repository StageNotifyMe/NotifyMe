package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.dto.GetVenueDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.TokenHandlerException;
import be.xplore.notifyme.persistence.IVenueRepo;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.IDToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@SpringBootTest
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
    when(tokenService.decodeToken(mockPrincipal)).thenReturn(mockIdToken);

    User user = getTestUser();
    when(userService.getUser("abcd")).thenReturn(user);

    assertEquals(HttpStatus.CREATED,
        venueService.createVenue(getTestCreateVenueDto(), mockPrincipal).getStatusCode());
  }

  @Test
  void createVenueUserNotFound() {
    Principal mockPrincipal = Mockito.mock(Principal.class);
    IDToken mockIdToken = getMockIdToken();
    when(tokenService.decodeToken(mockPrincipal)).thenReturn(mockIdToken);
    CreateVenueDto cvdto = getTestCreateVenueDto();

    doThrow(new CrudException(
        String.format("User with id %s could not be found", mockIdToken.getSubject())))
        .when(userService).getUser("abcd");

    assertThrows(CrudException.class, () ->
        venueService.createVenue(cvdto, mockPrincipal));
  }

  @Test
  void createVenueTokenDecodeFails() {
    Principal mockPrincipal = Mockito.mock(Principal.class);
    CreateVenueDto cvdto = getTestCreateVenueDto();

    doThrow(new TokenHandlerException(String.format("Could not convert %s object to IDToken object",
        mockPrincipal.getClass().getName()))).when(tokenService).decodeToken(mockPrincipal);

    assertThrows(TokenHandlerException.class, () ->
        venueService.createVenue(cvdto, mockPrincipal));
  }

  @Test
  void getVenueSuccessful() {
    when(venueRepo.getOne(anyLong())).thenReturn(getTestVenue());
    assertNotNull(venueService.getVenue(1L));
  }

  @Test
  void getVenueFail() {
    when(venueRepo.getOne(anyLong())).thenReturn(new Venue());
    assertThrows(CrudException.class, () -> {
      venueService.getVenue(1L);
    });
  }

  @Test
  void getVenuesSuccessful() {
    var user = getTestUser();
    when(userService.getUser("abcd")).thenReturn(user);
    when(venueRepo.getAllByManagersIsContaining(user)).thenReturn(getTestVenues());

    var result = venueService.getVenuesForUser("abcd");
    assertEquals(getTestGetVenues().size(), result.size());
    assertEquals(getTestGetVenues().get(0).getId(), result.get(0).getId());
  }

  @Test
  void getVenuesUserNotFound() {
    doThrow(CrudException.class).when(userService).getUser("unknown");

    assertThrows(CrudException.class, () -> {
      venueService.getVenuesForUser("unknown");
    });
  }

  @Test
  void getVenuesNoVenuesFound() {
    var user = getTestUser();
    when(userService.getUser("abcd")).thenReturn(user);
    when(venueRepo.getAllByManagersIsContaining(user)).thenReturn(new LinkedList<>());

    var result = venueService.getVenuesForUser("abcd");
    assertEquals(0, result.size());
  }

  private List<GetVenueDto> getTestGetVenues() {
    LinkedList<GetVenueDto> venues = new LinkedList<>();
    venues.add(new GetVenueDto(getTestVenue().getId(), getTestVenue().getName(),
        getTestVenue().getDescription(), getTestVenue().getAddress()));
    return venues;
  }

  private List<Venue> getTestVenues() {
    LinkedList<Venue> venues = new LinkedList<>();
    venues.add(getTestVenue());
    return venues;
  }

  private Venue getTestVenue() {
    return new Venue(1L, "Venue", "venue", new Address(), new LinkedList<>());
  }

  private IDToken getMockIdToken() {
    IDToken mockIdToken = Mockito.mock(IDToken.class);
    when(mockIdToken.getSubject()).thenReturn("abcd");
    return mockIdToken;
  }

  private User getTestUser() {
    User user = new User();
    user.setExternalOidcId("abcd");
    return user;
  }

  private CreateVenueDto getTestCreateVenueDto() {
    CreateVenueDto createVenueDto = new CreateVenueDto();
    createVenueDto.setName("ConcertHall");
    createVenueDto.setDescription("A concerthall");
    createVenueDto.setCountry("Belgium");
    createVenueDto.setPostalCode("1000");
    createVenueDto.setVillage("Brussels");
    createVenueDto.setStreetAndNumber("Concertway 10");
    return createVenueDto;
  }

}