package be.xplore.notifyme.domain.communicationstrategies;

import be.xplore.notifyme.domain.Message;

public interface ISmsService {
  void send(Object phoneNumber, Message message);
}
