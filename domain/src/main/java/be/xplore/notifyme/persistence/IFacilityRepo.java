package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Venue;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacilityRepo {
  List<Facility> getAllByVenue(Venue venue);

  Optional<Facility> findById(long facilityId);

  Facility save(Facility facility);
}
