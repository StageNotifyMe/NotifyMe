package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAddressRepository extends JpaRepository<JpaAddress, Long> {
}
