package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVenueRepo extends JpaRepository<Venue, Long> {

}