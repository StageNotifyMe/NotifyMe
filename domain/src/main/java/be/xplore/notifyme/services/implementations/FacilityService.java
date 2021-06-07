package be.xplore.notifyme.services.implementations;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IFacilityRepo;
import be.xplore.notifyme.services.IFacilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityService implements IFacilityService {
  private final IFacilityRepo facilityRepo;

  /**
   * Gets a facility based on a facilityId.
   *
   * @param facilityId id of the facility to get.
   * @return a facility object if successful, CrudException if not found.
   */
  @Override
  public Facility getFacility(long facilityId) {
    return facilityRepo.findById(facilityId)
        .orElseThrow(() -> new CrudException("Could not find facility for id" + facilityId));
  }


  /**
   * Gets all facilities belonging to a venue.
   *
   * @param venueId of which you want to get the facilities.
   * @return a list of facilities.
   */
  @Override
  public List<Facility> getAllFacilitesForVenue(long venueId) {
    return facilityRepo.getAllByVenue(venueId);
  }

  /**
   * Creates a facility based on a createFacilityDto object.
   *
   * @return the created facility.
   */
  @Override
  public Facility createFacility(String description, String location, int minimalStaff,
                                 int maximalStaff, long venueId) {
    var facility =
        new Facility(description, location,
            minimalStaff, maximalStaff);
    facility = facilityRepo.create(facility, venueId);
    return facility;
  }
}
