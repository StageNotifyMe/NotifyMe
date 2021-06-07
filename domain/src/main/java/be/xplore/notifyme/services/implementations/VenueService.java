package be.xplore.notifyme.services.implementations;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.SaveToDatabaseException;
import be.xplore.notifyme.persistence.IVenueRepo;
import be.xplore.notifyme.services.IVenueService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Contains all functions and processes related to venues.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VenueService implements IVenueService {

  private final TokenService tokenService;
  private final UserService userService;
  private final IVenueRepo venueRepo;

  /**
   * Creates a new venue.
   *
   * @param principal      used to identify the user.
   * @return 203 if successful, 400 if unsuccessful.
   */
  @Override
  public Venue createVenue(String name, String description, String streetAndNumber,
                           String postalCode, String village, String country, Principal principal) {
    var accessToken = tokenService.getIdToken(principal);
    var address = new Address(streetAndNumber, postalCode, village, country);
    var venue =
        new Venue(name, description, address);
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
  @Override
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
  @Override
  public List<User> getAllVenueManagers(long venueId) {
    return venueRepo.getAllVenueManagers(venueId);
  }

  /**
   * Returns all venues of which the user is a manager.
   *
   * @param userId of the user
   * @return list object containing all venues.
   */
  @Override
  public List<Venue> getVenuesForUser(String userId) {
    return venueRepo.getAllByManagersIsContaining(userId);
  }

  /**
   * Makes a user venue manager over a certain venue, also adds grants them venue_manager
   * permissions.
   *
   * @param userId  id of user to grant permission.
   * @param venueId id of venue over which the user gets perimissions.
   */
  @Override
  public void makeUserVenueManager(String userId, Long venueId) {
    try {
      venueRepo.addVenueManager(venueId, userId);
      userService.grantUserRole(userId, "venue_manager");
    } catch (Exception e) {
      throw new SaveToDatabaseException("Could not make user venue manager: " + e.getMessage());
    }
  }
}
