package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepo{
  User save(User user);
  Optional<User> findById(String userId);
}
