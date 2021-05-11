package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.jpaObjects.JpaUser;
import be.xplore.notifyme.jpaRepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.IUserRepo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUserAdapter implements IUserRepo {
  private final JpaUserRepository jpaUserRepository;

  @Override
  public User save(User user) {
    return jpaUserRepository.save(new JpaUser(user)).toDomain();
  }

  @Override
  public Optional<User> findById(String userId) {
    return jpaUserRepository.findById(userId).map(JpaUser::toDomain);
  }

  @Override
  public List<User> findAll() {
    return jpaUserRepository.findAll().stream().map(JpaUser::toDomain).collect(Collectors.toList());
  }
}
