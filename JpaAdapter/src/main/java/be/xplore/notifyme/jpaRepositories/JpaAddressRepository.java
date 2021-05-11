package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAddressRepository extends JpaRepository<JpaAddress, Long> {
}
