package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEventRepository extends JpaRepository<JpaEvent, Long> {
}
