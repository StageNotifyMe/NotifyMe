package be.xplore.notifyme.domain.communicationstrategies.dummyimplements;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.communicationstrategies.IEmailService;
import org.springframework.stereotype.Service;

@Service
public class DummyEmailService implements IEmailService {
  @Override
  public void send(Object emailAddress, Message message) {
    System.out.println(String
        .format("EMAIL_ADDRESS: %s\nTITLE: %s\nMESSAGE: %s", emailAddress, message.getTitle(),
            message.getText()));
  }
}
