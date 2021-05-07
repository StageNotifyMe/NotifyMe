package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVenueRepo extends JpaRepository<Venue, Long> {
  List<Venue> getAllByManagersIsContaining(User user);

}