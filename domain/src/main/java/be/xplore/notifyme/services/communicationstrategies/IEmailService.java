package be.xplore.notifyme.services.communicationstrategies;

import be.xplore.notifyme.domain.Message;

public interface IEmailService {
  void send(Object emailAddress, Message message);
}
