package be.xplore.notifyme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Starts the notify me application.
 */
@SpringBootApplication(scanBasePackages = "be.xplore.notifyme")
@PropertySource("classpath:application.properties")
@PropertySource("classpath:messages.properties")
public class StartNotifyMe {

  public static void main(String[] args) {
    SpringApplication.run(StartNotifyMe.class, args);
  }

}
