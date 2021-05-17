package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.dto.CreateFacilityDto;
import java.util.List;

public interface IFacilityService {

  Facility getFacility(long facilityId);

  List<Facility> getAllFacilitesForVenue(long venueId);

  Facility createFacility(CreateFacilityDto createFacilityDto);
}
