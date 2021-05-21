package be.xplore.notifyme.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio.account")
@Getter
@Setter
public class SmsConfiguration {

  private String phoneNo;
  private String sid;
  private String key;
}
