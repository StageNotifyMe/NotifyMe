package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaEvent;
import be.xplore.notifyme.jpaobjects.JpaUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEventRepository extends JpaRepository<JpaEvent, Long> {
  List<JpaEvent> findAllByLineManagersContains(JpaUser user);
}
