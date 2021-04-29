package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEventRepo extends JpaRepository<Event, Long> {
}
