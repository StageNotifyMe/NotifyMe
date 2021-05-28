package be.xplore.notifyme.services;

import be.xplore.notifyme.config.SmsConfiguration;
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
public class SmsVerificationSendingService implements ISmsVerificationSenderService {

  private final SmsConfiguration smsConfiguration;

  @Override
  public void send(String title, String body, String phoneNo) {
    Twilio.init(smsConfiguration.getSid(), smsConfiguration.getKey());
    com.twilio.rest.api.v2010.account.Message.creator(
        new com.twilio.type.PhoneNumber(phoneNo),
        new com.twilio.type.PhoneNumber(smsConfiguration.getPhoneNo()),
        buildSms(title, body))
        .create();
  }

  private String buildSms(String title, String body) {
    return title + "\n\n" + body;
  }
}
