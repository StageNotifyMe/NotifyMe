package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacilityRepo extends JpaRepository<Facility, Long> {
}
