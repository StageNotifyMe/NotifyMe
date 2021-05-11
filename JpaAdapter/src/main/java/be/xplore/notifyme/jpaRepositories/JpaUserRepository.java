package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<JpaUser, String> {
}
