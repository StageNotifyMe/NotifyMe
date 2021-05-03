package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILineRepo extends JpaRepository<Line, Long> {
}
