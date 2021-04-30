package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventRepo extends JpaRepository<Event, Long> {
}
