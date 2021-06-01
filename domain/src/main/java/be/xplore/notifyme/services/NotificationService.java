package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.INotificationRepo;
import be.xplore.notifyme.services.systemmessages.AvailableLanguages;
import be.xplore.notifyme.services.systemmessages.PickLanguageService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

  public void createAndSendSystemNotification(String userId, String messageName,
                                              Object[] attribute) {
    var user = userService.getUser(userId);
    var message = pickLanguageService.getLanguageService(user.getPreferedLanguage())
        .getSystemMessage(messageName, attribute);
    message = messageRepo.save(message);
    var notification = notificationRepo.create(message.getId(), userId);
    finishAndSendNotification(notification, user.getUserName());
  }

  /*@Override
  public Message createCanceledEventMessage(Event updatedEvent,
                                            AvailableLanguages preferedLanguage) {
    var message = getLanguageService(preferedLanguage).getCancelEvent(updatedEvent);
    return messageRepo.save(message);
  }*/


  @Override
  public void notifyOrganisationManagersForCancelEvent(Event event, String messageName) {
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
  public void notifyUsers(Collection<User> users, String systemMessageName, Object[] attributes) {
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

  @Override
  public Message testMessage(AvailableLanguages languageCode) {
    var languageService = pickLanguageService.getLanguageService(languageCode);
    var event = new Event(1L, "Title", "Description", "Artist",
        LocalDateTime.now(), EventStatus.OK, new Venue(), new ArrayList<>(), new HashSet<>());
    return languageService.getSystemMessage("cancelEvent", new Object[] {event});
  }
}
