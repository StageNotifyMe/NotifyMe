package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IFacilityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityService {
  private final IFacilityRepo facilityRepo;

  public Facility getFacility(long facilityId){
    var facility = facilityRepo.findById(facilityId);
    if (facility.isPresent()){
      return facility.get();
    }
    throw new CrudException("Could not find facility for id "+facilityId);
  }
}
