package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaFacility;
import be.xplore.notifyme.jpaobjects.JpaVenue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaFacilityRepository extends JpaRepository<JpaFacility, Long> {
  List<JpaFacility> getAllByVenue(JpaVenue venue);
}
