package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Notification;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationRepo {

  Notification save(Notification notification);

  Notification findById(long notificationId);

  Notification create(long messageId, String userId);

  Notification createUrgent(long messageId, String userId);

  List<Notification> findByUser(String userId);
}
