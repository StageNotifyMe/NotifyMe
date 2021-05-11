package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVenueRepo {
  Venue save(Venue venue);

  Optional<Venue> findById(long venueId);

  List<Venue> getAllByManagersIsContaining(User user);

}