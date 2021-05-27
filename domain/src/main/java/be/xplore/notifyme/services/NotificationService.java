package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.INotificationRepo;
import java.util.ArrayList;
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
  public void notifyOrganisationsManagers(List<Long> organisationIds, long messageId) {
    List<User> managers = new ArrayList<>();
    for (Long organisationId : organisationIds) {
      managers.addAll(organisationService.getOrganisationManagers(organisationId));
    }
    for (User manager : managers) {
      var notification = notificationRepo.create(messageId, manager.getUserId());
      var userInfo = keycloakCommunicationService.getUserInfoUsername(manager.getUserName());
      notification.setCommunicationAddresAndUsedStrategy(userInfo);
      notificationRepo.save(notification);
      notification.send();
    }
  }

  @Override
  public void notifyUsers(Collection<User> users, long messageId) {
    for (User user : users) {
      var notification = notificationRepo.create(messageId, user.getUserId());
      var userInfo = keycloakCommunicationService.getUserInfoUsername(user.getUserName());
      notification.setCommunicationAddresAndUsedStrategy(userInfo);
      notificationRepo.save(notification);
      notification.send();
    }
  }
}
