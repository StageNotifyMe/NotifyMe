package be.xplore.notifyme.domain.communicationStrategies;

import be.xplore.notifyme.domain.Message;

public interface ISmsService {
  public void send(Object phoneNumber, Message message);
}
