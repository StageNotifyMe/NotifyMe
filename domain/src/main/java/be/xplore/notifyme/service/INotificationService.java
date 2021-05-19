package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Message;

public interface INotificationService {
  void notifyUser(String username, long messageId);

  Message createMessage(String title, String text);
}
