package be.xplore.notifyme;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "be.xplore.notifyme.jparepositories")
public class JpaConfiguration {
}
