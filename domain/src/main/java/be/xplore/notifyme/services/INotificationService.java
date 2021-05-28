package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import java.util.List;

public interface INotificationService {

  void notifyUser(String username, long messageId);

  void notifyUserUrgent(String username, long messageId);

  Message createMessage(String title, String text);

  List<Notification> getNotificationsForUser(String userId);

  void notifyUserHidden(String username, long messageId);
}
