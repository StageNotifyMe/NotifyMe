package be.xplore.notifyme.domain.communicationstrategies;

import be.xplore.notifyme.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.account.UserRepresentation;

@RequiredArgsConstructor
public class SmsCommunicationStrategy implements ICommunicationStrategy {
  private final ISmsService smsService;

  @Override
  public void send(Notification notification) {
    smsService.send(notification.getCommunicationAddress(), notification.getMessage());
  }

  @Override
  public String getCommunicationAddress(UserRepresentation userRepresentation) {
    return userRepresentation.getAttributes().get("phone_number").get(0);
  }
}
