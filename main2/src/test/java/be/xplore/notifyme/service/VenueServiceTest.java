package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.TokenHandlerException;
import be.xplore.notifyme.persistence.IVenueRepo;
import java.security.Principal;
import org.hibernate.HibernateError;
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