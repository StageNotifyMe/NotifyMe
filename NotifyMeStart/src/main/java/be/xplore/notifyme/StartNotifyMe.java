package be.xplore.notifyme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the notify me application.
 */
@SpringBootApplication(scanBasePackages = "be.xplore.notifyme")
public class StartNotifyMe {

  public static void main(String[] args) {
    SpringApplication.run(StartNotifyMe.class, args);
  }

}
