package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaEvent;
import be.xplore.notifyme.jpaobjects.JpaLine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLineRepository extends JpaRepository<JpaLine, Long> {
  List<JpaLine> getAllByEvent(JpaEvent jpaEvent);
}
