package be.xplore.notifyme.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
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
