package be.xplore.notifyme.domain.communicationStrategies;

import be.xplore.notifyme.domain.Message;

public interface IEmailService {
  public void send(Object emailAddress, Message message);
}
