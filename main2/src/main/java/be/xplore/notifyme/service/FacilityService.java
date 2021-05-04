package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.dto.CreateFacilityDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IFacilityRepo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityService {
  private final IFacilityRepo facilityRepo;
  private final VenueService venueService;

  /**
   * Gets a facility based on a facilityId.
   *
   * @param facilityId id of the facility to get.
   * @return a facility object if successful, CrudException if not found.
   */
  public Facility getFacility(long facilityId) {
    var facility = facilityRepo.findById(facilityId);
    if (facility.isPresent()) {
      return facility.get();
    }
    throw new CrudException("Could not find facility for id " + facilityId);
  }


  /**
   * Gets all facilities belonging to a venue.
   *
   * @param venueId of which you want to get the facilities.
   * @return a list of facilities.
   */
  public List<Facility> getAllFacilitesForVenue(long venueId) {
    try {
      var venue = venueService.getVenue(venueId);
      return facilityRepo.getAllByVenue(venue);
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  /**
   * Creates a facility based on a createFacilityDto object.
   *
   * @param createFacilityDto dto with all relevant properties in a JSON-friendly format.
   * @return the created facility.
   */
  public Facility createFacility(CreateFacilityDto createFacilityDto) {
    try {
      var facility =
          new Facility(createFacilityDto.getDescription(), createFacilityDto.getLocation(),
              createFacilityDto.getMinimalStaff(), createFacilityDto.getMaximalStaff());
      var venue = venueService.getVenue(createFacilityDto.getVenueId());
      facility.setVenue(venue);
      facility = facilityRepo.save(facility);
      return facility;
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }
}
