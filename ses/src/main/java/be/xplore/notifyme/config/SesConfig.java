package be.xplore.notifyme.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class SesConfig {
  @Value("${sesconfig.from}")
  private String from;
  @Value("${sesconfig.fromname}")
  private String fromName;
  @Value("${sesconfig.smtpusername}")
  private String smtpUsername;
  @Value("${sesconfig.smtppassword}")
  private String smtpPassword;
  @Value("${sesconfig.configset}")
  private String configSet;
  @Value("${sesconfig.host}")
  private String host;
  @Value("${sesconfig.port}")
  private int port;
}
