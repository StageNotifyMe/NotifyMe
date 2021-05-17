package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateVenueDto;
import be.xplore.notifyme.dto.GetVenueDto;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface IVenueService {

  ResponseEntity<Object> createVenue(CreateVenueDto createVenueDto, Principal principal);

  Venue getVenue(long id);

  List<GetVenueDto> getVenuesForUser(String userId);

  void makeUserVenueManager(String userId, Long venueId);
}
