package be.xplore.notifyme.domain.communicationstrategies;

import be.xplore.notifyme.domain.Message;

public interface IEmailService {
  public void send(Object emailAddress, Message message);
}