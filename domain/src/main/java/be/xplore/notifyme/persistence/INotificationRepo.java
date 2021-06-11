package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Notification;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationRepo {

  public Notification save(Notification notification);

  public Notification findById(long notificationId);

  public Notification create(long messageId, String userId);

  public Notification create(long messageId, String userId, String sender);

  public Notification createUrgent(long messageId, String userId);

  public List<Notification> findByUser(String userId);

  List<Notification> getAllNotifications();

  void hideNotification(long notificationId);
}
