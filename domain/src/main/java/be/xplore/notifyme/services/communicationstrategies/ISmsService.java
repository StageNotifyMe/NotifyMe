package be.xplore.notifyme.services.communicationstrategies;

import be.xplore.notifyme.domain.Message;

public interface ISmsService {
  void send(Object phoneNumber, Message message);
}
