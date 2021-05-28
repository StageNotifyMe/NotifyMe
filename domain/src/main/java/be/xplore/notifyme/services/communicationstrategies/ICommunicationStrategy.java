package be.xplore.notifyme.services.communicationstrategies;

import be.xplore.notifyme.domain.Notification;
import org.keycloak.representations.account.UserRepresentation;

public interface ICommunicationStrategy {

  void send(Notification notification);

  String getCommunicationAddress(UserRepresentation userRepresentation);

  String getName();
}
