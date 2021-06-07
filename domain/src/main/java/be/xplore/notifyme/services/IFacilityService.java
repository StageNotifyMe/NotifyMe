package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Facility;
import java.util.List;

public interface IFacilityService {

  Facility getFacility(long facilityId);

  List<Facility> getAllFacilitesForVenue(long venueId);

  Facility createFacility(String description, String location, int minimalStaff,
                          int maximalStaff, long venueId);
}
