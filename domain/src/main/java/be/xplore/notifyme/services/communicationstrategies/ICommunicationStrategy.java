package be.xplore.notifyme.communication.communicationstrategies;

import be.xplore.notifyme.domain.Notification;
import org.keycloak.representations.account.UserRepresentation;

public interface ICommunicationStrategy {
  public void send(Notification notification);

  public String getCommunicationAddress(UserRepresentation userRepresentation);

  public String getName();
}
