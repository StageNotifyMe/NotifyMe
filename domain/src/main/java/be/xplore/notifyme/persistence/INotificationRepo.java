package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Notification;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationRepo {
  public Notification save(Notification notification);

  public Notification findById(long notificationId);

  public Notification create(long messageId, String userId);
}
