package be.xplore.notifyme.domain.communicationstrategies;

import be.xplore.notifyme.domain.Notification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailCommunicationStrategy implements ICommunicationStrategy {
  private IEmailService emailService;

  @Override
  public void send(Notification notification) {
    emailService.send(notification.getCommunicationAddress(), notification.getMessage());
  }
}
