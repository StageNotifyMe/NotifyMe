package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import java.util.Collection;
import java.util.List;

public interface INotificationService {

  void notifyUser(String username, long messageId);

  void notifyUserUrgent(String username, long messageId);

  Message createMessage(String title, String text);

  List<Notification> getNotificationsForUser(String userId);

  Message createCanceledEventMessage(Event updatedEvent);

  void notifyOrganisationManagersForCancelEvent(long eventId, long messageId);

  void notifyUsers(Collection<User> users, long messageId);

  void notifyOrganisationManagers(long organisationId, String sender, String
      title, String text);
}
