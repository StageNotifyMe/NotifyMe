package be.xplore.notifyme.services.communicationstrategies;

import be.xplore.notifyme.domain.Notification;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.keycloak.representations.account.UserRepresentation;

@RequiredArgsConstructor
@Getter
@Setter
@JsonSerialize
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

  @Override
  public String getName() {
    return "SMS";
  }
}
