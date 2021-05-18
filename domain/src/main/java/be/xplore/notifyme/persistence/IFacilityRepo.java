package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Facility;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacilityRepo {

  Optional<Facility> findById(long facilityId);

  Facility save(Facility facility);

  Facility create(Facility facility, long venueId);

  List<Facility> getAllByVenue(long venueId);
}
