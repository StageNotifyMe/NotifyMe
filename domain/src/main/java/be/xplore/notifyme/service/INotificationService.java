package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import java.util.List;

public interface INotificationService {
  void notifyUser(String username, long messageId);

  Message createMessage(String title, String text);

  List<Notification> getNotificationsForUser(String userId);
}
