package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNotificationRepository extends JpaRepository<JpaNotification, Long> {
}
