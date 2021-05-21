package be.xplore.notifyme.communication;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.dto.GetVenueDto;
import java.security.Principal;
import java.util.List;

public interface IVenueService {

  Venue createVenue(CreateVenueDto createVenueDto, Principal principal);

  Venue getVenue(long id);

  List<GetVenueDto> getVenuesForUser(String userId);

  void makeUserVenueManager(String userId, Long venueId);

  List<User> getAllVenueManagers(long venueId);
}
