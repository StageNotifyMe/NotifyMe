package be.xplore.notifyme.communication;

import be.xplore.notifyme.config.SmsConfiguration;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.communication.communicationstrategies.ISmsService;
import com.twilio.Twilio;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class SmsCommunicationService implements ISmsService {

  private final SmsConfiguration smsConfiguration;

  @Override
  public void send(Object phoneNumber, Message message) {
    Twilio.init(smsConfiguration.getSid(), smsConfiguration.getKey());
    var phoneNo = (String) phoneNumber;
    var msg = com.twilio.rest.api.v2010.account.Message.creator(
        new com.twilio.type.PhoneNumber(phoneNo),
        new com.twilio.type.PhoneNumber(smsConfiguration.getPhoneNo()),
        buildSms(message))
        .create();
    log.info(msg.getStatus().toString());
  }

  private String buildSms(Message message) {
    return message.getTitle() + "\n\n" + message.getText();
  }
}
