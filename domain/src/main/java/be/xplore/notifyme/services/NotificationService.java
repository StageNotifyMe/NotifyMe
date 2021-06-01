package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.INotificationRepo;
import be.xplore.notifyme.services.systemmessages.PickLanguageService;
import be.xplore.notifyme.services.systemmessages.SystemMessages;
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
  private final IUserService userService;
  private final PickLanguageService pickLanguageService;

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
  public void notifyUserHidden(String username, long messageId) {
    var userInfo = keycloakCommunicationService.getUserInfoUsername(username);
    var notification = notificationRepo.create(messageId, userInfo.getId());
    notification.setCommunicationAddresAndUsedStrategy(userInfo);
    notification.setHidden(true);
    notificationRepo.save(notification);
    notification.send();
  }

  @Override
  public List<Notification> getNotificationsForUser(String userId) {
    return notificationRepo.findByUser(userId);
  }

  /**
   * Creates a system notification in the approriate language.
   *
   * @param userId      user for whom the notification is intended.
   * @param messageName identifier for the system message.
   * @param attribute   any attributes needed to create the message.
   */
  public void createAndSendSystemNotification(String userId, SystemMessages messageName,
                                              Object[] attribute) {
    var user = userService.getUser(userId);
    var message = pickLanguageService.getLanguageService(user.getPreferedLanguage())
        .getSystemMessage(messageName, attribute);
    message = messageRepo.save(message);
    var notification = notificationRepo.create(message.getId(), userId);
    finishAndSendNotification(notification, user.getUserName());
  }

  @Override
  public void notifyOrganisationManagersForCancelEvent(Event event, SystemMessages messageName) {
    List<User> orgManagers = organisationService.getOrganisationManagersForEvent(event.getId());
    notifyUsers(orgManagers, messageName, new Object[] {event});
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
  public void notifyUsers(Collection<User> users, SystemMessages systemMessageName,
                          Object[] attributes) {
    if (users != null) {
      for (User user : users) {
        createAndSendSystemNotification(user.getUserId(), systemMessageName, attributes);
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
