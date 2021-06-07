package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.GetVenueDto;
import java.security.Principal;
import java.util.List;

public interface IVenueService {

  Venue createVenue(String name, String description, String streetAndNumber,
                    String postalCode, String village, String country, Principal principal);

  Venue getVenue(long id);

  List<GetVenueDto> getVenuesForUser(String userId);

  void makeUserVenueManager(String userId, Long venueId);

  List<User> getAllVenueManagers(long venueId);
}
