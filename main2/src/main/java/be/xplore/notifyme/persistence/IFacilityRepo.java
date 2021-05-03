package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFacilityRepo extends JpaRepository<Facility, Long> {
}
