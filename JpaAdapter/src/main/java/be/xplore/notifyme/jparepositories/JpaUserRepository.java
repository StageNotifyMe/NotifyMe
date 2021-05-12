package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<JpaUser, String> {
}
