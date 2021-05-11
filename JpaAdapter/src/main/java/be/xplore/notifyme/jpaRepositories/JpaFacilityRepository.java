package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaFacility;
import be.xplore.notifyme.jpaObjects.JpaVenue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaFacilityRepository extends JpaRepository<JpaFacility, Long> {
  List<JpaFacility> getAllByVenue(JpaVenue venue);
}
