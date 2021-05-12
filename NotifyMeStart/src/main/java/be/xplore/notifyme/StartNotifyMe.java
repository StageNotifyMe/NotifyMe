package be.xplore.notifyme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Starts the notify me application.
 */
@SpringBootApplication(scanBasePackages = "be.xplore.notifyme")
@EnableJpaRepositories(basePackages = "be.xplore.notifyme.jparepositories")
public class StartNotifyMe {

  public static void main(String[] args) {
    SpringApplication.run(StartNotifyMe.class, args);
  }

}
