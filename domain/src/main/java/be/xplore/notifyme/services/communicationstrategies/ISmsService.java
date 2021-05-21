package be.xplore.notifyme.communication.communicationstrategies;

import be.xplore.notifyme.domain.Message;

public interface ISmsService {
  void send(Object phoneNumber, Message message);
}
