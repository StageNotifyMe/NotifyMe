package be.xplore.notifyme.services.communicationstrategies.implementations;

import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.IEmailService;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.keycloak.representations.account.UserRepresentation;

@RequiredArgsConstructor
@Getter
@Setter
@JsonSerialize
public class EmailCommunicationStrategy implements
    ICommunicationStrategy {

  private final IEmailService emailService;

  @Override
  public void send(Notification notification) {
    emailService.send(notification.getCommunicationAddress(), notification.getMessage());
  }

  @Override
  public String getCommunicationAddress(UserRepresentation userRepresentation) {
    return userRepresentation.getEmail();
  }

  @Override
  public String getName() {
    return "Email";
  }
}
