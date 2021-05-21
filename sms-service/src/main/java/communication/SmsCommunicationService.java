package communication;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.communicationstrategies.ISmsService;
import com.twilio.Twilio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class SmsCommunicationService implements ISmsService {

  @Value("${twilio.phone.no}")
  private final String twilioPhoneNo;
  @Value("${twilio.account.sid}")
  private final String twilioAccountSid;
  @Value("${twilio.account.key}")
  private final String twilioAuthToken;

  @Override
  public void send(Object phoneNumber, Message message) {
    Twilio.init(twilioAccountSid, twilioAuthToken);
    var phoneNo = (String) phoneNumber;
    com.twilio.rest.api.v2010.account.Message.creator(
        new com.twilio.type.PhoneNumber(twilioPhoneNo),
        new com.twilio.type.PhoneNumber(phoneNo),
        buildSms(message))
        .create();
  }

  private String buildSms(Message message) {
    return message.getTitle() + "\n\n" + message.getText();
  }
}
