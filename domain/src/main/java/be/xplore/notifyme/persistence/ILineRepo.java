package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Line;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ILineRepo {

  Optional<Line> findById(long lineId);

  Line save(Line line);
}
