package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaUser;
import be.xplore.notifyme.jpaobjects.JpaVenue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaVenueRepository extends JpaRepository<JpaVenue, Long> {

  List<JpaVenue> getAllByManagersIsContaining(JpaUser user);
}
