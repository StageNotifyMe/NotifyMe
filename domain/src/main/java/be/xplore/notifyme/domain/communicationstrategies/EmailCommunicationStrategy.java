package be.xplore.notifyme.domain.communicationstrategies;

import be.xplore.notifyme.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.account.UserRepresentation;

@RequiredArgsConstructor
public class EmailCommunicationStrategy implements ICommunicationStrategy {
  private final IEmailService emailService;

  @Override
  public void send(Notification notification) {
    emailService.send(notification.getCommunicationAddress(), notification.getMessage());
  }

  @Override
  public String getCommunicationAddress(UserRepresentation userRepresentation) {
    return userRepresentation.getEmail();
  }
}
