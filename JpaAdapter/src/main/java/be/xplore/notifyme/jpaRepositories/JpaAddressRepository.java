package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAddressRepository extends JpaRepository<JpaAddress, Long> {
}
