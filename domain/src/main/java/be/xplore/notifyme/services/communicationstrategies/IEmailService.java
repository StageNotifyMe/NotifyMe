package be.xplore.notifyme.communication.communicationstrategies;

import be.xplore.notifyme.domain.Message;

public interface IEmailService {
  void send(Object emailAddress, Message message);
}
