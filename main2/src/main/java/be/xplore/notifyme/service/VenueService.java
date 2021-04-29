package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.domain.VenueManager;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.TokenHandlerException;
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
  public ResponseEntity<Object> createVenue(CreateVenueDto createVenueDto, Principal principal) {
    try {
      var accessToken = tokenService.decodeToken(principal);
      var user = userService.getUser(accessToken.getSubject());
      var address =
          new Address(createVenueDto.getStreetAndNumber(), createVenueDto.getPostalCode(),
              createVenueDto.getVillage(), createVenueDto.getCountry());
      var venue =
          new Venue(createVenueDto.getName(), createVenueDto.getDescription(), address);
      venue = venueRepo.save(venue);
      //venue.getManagers().add();
      venue = venueRepo.save(venue);
      return new ResponseEntity<>(HttpStatus.CREATED);
    //} catch (TokenHandlerException | CrudException e){
    } catch (Exception e){
      log.error(e.getMessage());
      throw e;
    }
  }

  public Venue getVenue(long id){
    var venue = venueRepo.getOne(id);
    if (venue.getName().equals("")){
      throw new CrudException("Could not retrieve venue for id "+id);
    }
    return venue;
  }
}
