package be.xplore.notifyme.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sesconfig")
public class SesConfig {

  private String from;
  private String fromName;
  private String smtpUsername;
  private String smtpPassword;
  private String configSet;
  private String host;
  private int port;
}
