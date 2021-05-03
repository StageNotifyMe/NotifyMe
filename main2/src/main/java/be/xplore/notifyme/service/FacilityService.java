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

  public Facility getFacility(long facilityId) {
    var facility = facilityRepo.findById(facilityId);
    if (facility.isPresent()) {
      return facility.get();
    }
    throw new CrudException("Could not find facility for id " + facilityId);
  }

  public List<Facility> getAllFacilitesForVenue(long venueId) {
    try {
      var venue = venueService.getVenue(venueId);
      return facilityRepo.getAllByVenue(venue);
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }

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
