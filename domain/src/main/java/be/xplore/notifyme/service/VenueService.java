package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.dto.GetVenueDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.persistence.IVenueRepo;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

/**
 * Contains all functions and processes related to venues.
 */
@Service
@Slf4j
@RequiredArgsConstructor
//@Transactional
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
  public Venue createVenue(CreateVenueDto createVenueDto, Principal principal) {
    var accessToken = tokenService.getIdToken(principal);
    var address =
        new Address(createVenueDto.getStreetAndNumber(), createVenueDto.getPostalCode(),
            createVenueDto.getVillage(), createVenueDto.getCountry());
    var venue =
        new Venue(createVenueDto.getName(), createVenueDto.getDescription(), address);
    venue = venueRepo.save(venue);
    venue = venueRepo.addVenueManager(venue.getId(), accessToken.getSubject());
    return venue;
  }

  /**
   * Gets a venue based on ID.
   *
   * @param id of the venue to get.
   * @return the venue matching the ID or thorws error if no matches found.
   */
  public Venue getVenue(long id) {
    return venueRepo.findById(id)
        .orElseThrow(() -> new CrudException("Could not retrieve venue for id " + id));
  }

  /**
   * Gets all venuemanagers for a venue.
   *
   * @param venueId id of which you want to get the managers.
   * @return list of users.
   */
  public List<User> getAllVenueManagers(long venueId) {
    return venueRepo.getAllVenueManagers(venueId);
  }

  /**
   * Returns all venues of which the user is a manager.
   *
   * @param userId of the user
   * @return list object containing all venues.
   */
  public List<GetVenueDto> getVenuesForUser(String userId) {
    var venues = venueRepo.getAllByManagersIsContaining(userId);
    List<GetVenueDto> venueDtos = new LinkedList<>();
    for (Venue venue : venues) {
      venueDtos.add(new GetVenueDto(venue.getId(), venue.getName(), venue.getDescription(),
          venue.getAddress()));
    }
    return venueDtos;
  }

  /**
   * Makes a user venue manager over a certain venue,
   * also adds grants them venue_manager permissions.
   *
   * @param userId  id of user to grant permission.
   * @param venueId id of venue over which the user gets perimissions.
   */
  public void makeUserVenueManager(String userId, Long venueId) {
    try {
      venueRepo.addVenueManager(venueId, userId);
      userService.grantUserRole(userId, "venue_manager");
    } catch (Exception e) {
      throw new SaveToDatabaseException("Could not make user venue manager: " + e.getMessage());
    }
  }
}
