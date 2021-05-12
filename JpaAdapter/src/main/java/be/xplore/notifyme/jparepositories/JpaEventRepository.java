package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEventRepository extends JpaRepository<JpaEvent, Long> {
}
