package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.persistence.IVenueRepo;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Contains all functions and processes related to venues.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VenueService {

  private final TokenService tokenService;
  private final UserService userService;
  private final IVenueRepo venueRepo;

  /**
   * Creates a new venue.
   *
   * @param createVenueDto DTO containing all information abou the venue
   *                       (name, description, address).
   * @param principal      used to identify the user.
   * @return 203 if successful, 400 if unsuccessful.
   */
  public ResponseEntity<String> createVenue(CreateVenueDto createVenueDto, Principal principal) {
    try {
      var accessToken = tokenService.decodeToken(principal);
      User user = userService.getUser(accessToken.getSubject());
      Address address =
          new Address(createVenueDto.getStreetAndNumber(), createVenueDto.getPostalCode(),
              createVenueDto.getVillage(), createVenueDto.getCountry());
      Venue venue =
          new Venue(createVenueDto.getName(), createVenueDto.getDescription(), address, user);
      venueRepo.save(venue);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      log.error(String.format("Error while creating venue: %s",e.getMessage()));
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(String.format("{errorMessage:\"%s\"}", e.getMessage()));
    }

  }
}
