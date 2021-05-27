package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<JpaUser, String> {
  List<User> findAllByOrganisationsContaining(JpaOrganisation organisation);
}
