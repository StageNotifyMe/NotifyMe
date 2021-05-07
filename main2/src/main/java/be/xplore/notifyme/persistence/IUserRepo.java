package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepo extends JpaRepository<User, String> {
  List<User> getAllByVenuesContains(Venue venue);

  List<User> getAllByEventsContaining(Event event);
}
