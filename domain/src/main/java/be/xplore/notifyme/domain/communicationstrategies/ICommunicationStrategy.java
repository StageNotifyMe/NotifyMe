package be.xplore.notifyme.domain.communicationstrategies;

import be.xplore.notifyme.domain.Notification;

public interface ICommunicationStrategy {
  public void send(Notification notification);
}
