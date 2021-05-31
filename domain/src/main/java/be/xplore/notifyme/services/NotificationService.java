package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.INotificationRepo;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

  private final INotificationRepo notificationRepo;
  private final IMessageRepo messageRepo;
  private final KeycloakCommunicationService keycloakCommunicationService;
  private final IOrganisationService organisationService;

  @Override
  public Message createMessage(String title, String text) {
    var message = new Message(title, text);
    return messageRepo.save(message);
  }

  @Override
  public void notifyUser(String username, long messageId) {
    var userInfo = keycloakCommunicationService.getUserInfoUsername(username);
    var notification = notificationRepo.create(messageId, userInfo.getId());
    notification.setCommunicationAddresAndUsedStrategy(userInfo);
    notificationRepo.save(notification);
    notification.send();
  }

  @Override
  public void notifyUserUrgent(String username, long messageId) {
    var userInfo = keycloakCommunicationService.getUserInfoUsername(username);
    var notification = notificationRepo.createUrgent(messageId, userInfo.getId());
    notification.setCommunicationAddresAndUsedStrategy(userInfo);
    notificationRepo.save(notification);
    notification.send();
  }

  @Override
  public List<Notification> getNotificationsForUser(String userId) {
    return notificationRepo.findByUser(userId);
  }

  @Override
  public Message createCanceledEventMessage(Event updatedEvent) {
    var message = new Message("Event has been canceled", "An event has been canceled, details:"
        + "\nEventId: " + updatedEvent.getId()
        + "\nTitle: " + updatedEvent.getTitle()
        + "\nDescription: " + updatedEvent.getDescription()
        + "\nArtist: " + updatedEvent.getArtist()
        + "\nDate and time: " + updatedEvent.getDateTime().toString());
    return messageRepo.save(message);
  }

  @Override
  public void notifyOrganisationManagersForCancelEvent(long eventId, long messageId) {
    List<User> orgManagers = organisationService.getOrganisationManagersForEvent(eventId);
    notifyUsers(orgManagers, messageId);
  }

  @Override
  public void notifyUsers(Collection<User> users, long messageId) {
    if (users != null) {
      for (User user : users) {
        var notification = notificationRepo.create(messageId, user.getUserId());
        finishAndSendNotification(notification, user.getUserName());
      }
    }
  }

  @Override
  public void notifyOrganisationManagers(long organisationId, String sender, String title,
                                         String text) {
    var message = this.createMessage(title, text);
    var orgManagers = organisationService.getOrganisationManagers(organisationId);
    for (User orgManager : orgManagers) {
      var notification = notificationRepo.create(message.getId(), orgManager.getUserId(), sender);
      finishAndSendNotification(notification, orgManager.getUserName());
    }
  }

  private void finishAndSendNotification(Notification notification, String receiverUserName) {
    var userInfo = keycloakCommunicationService.getUserInfoUsername(receiverUserName);
    notification.setCommunicationAddresAndUsedStrategy(userInfo);
    notificationRepo.save(notification);
    notification.send();
  }
}
