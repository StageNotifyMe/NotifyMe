package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILineRepo extends JpaRepository<Line, Long> {
  List<Line> getAllByEvent(Event event);
}
