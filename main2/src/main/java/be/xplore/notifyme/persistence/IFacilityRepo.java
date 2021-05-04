package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Venue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacilityRepo extends JpaRepository<Facility, Long> {
  List<Facility> getAllByVenue(Venue venue);
}
