package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.INotificationRepo;
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
}
