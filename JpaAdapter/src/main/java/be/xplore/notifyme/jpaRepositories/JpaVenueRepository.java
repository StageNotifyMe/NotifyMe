package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.jpaObjects.JpaVenue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaVenueRepository extends JpaRepository<JpaVenue, Long> {
  public List<JpaVenue> getAllByManagersIsContaining(User user);
}
