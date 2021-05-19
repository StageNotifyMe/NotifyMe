package be.xplore.notifyme.domain.communicationstrategies.dummyimplements;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.communicationstrategies.ISmsService;
import org.springframework.stereotype.Service;

@Service
public class DummySmsService implements ISmsService {
  @Override
  public void send(Object phoneNumber, Message message) {
    System.out
        .println(String.format("PHONE_NUMBER: %s\nMESSAGE: %s", phoneNumber, message.getText()));
  }
}
