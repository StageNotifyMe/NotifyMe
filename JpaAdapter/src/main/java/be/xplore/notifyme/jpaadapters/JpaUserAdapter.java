package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaUser;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
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
    var jpaUser = jpaUserRepository.findById(user.getUserId()).orElseThrow(
        () -> new JpaNotFoundException("Could not find user for id " + user.getUserId()));
    jpaUser.setUserName(user.getUserName());
    jpaUser.setPreferedLanguage(user.getPreferedLanguage());
    return jpaUserRepository.save(jpaUser).toDomainBase();
  }

  @Override
  public Optional<User> findById(String userId) {
    return jpaUserRepository.findById(userId).map(JpaUser::toDomainBase);
  }

  @Override
  public List<User> findAll() {
    return jpaUserRepository.findAll().stream().map(JpaUser::toDomainBase)
        .collect(Collectors.toList());
  }

  @Override
  public User findByIdIncOrganisations(String userId) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new CrudException("Could not find user for id " + userId));
    return jpaUser.toDomainIncOrganisations();
  }

  @Override
  public User findByIdIncAppliedUsers(String userId) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new CrudException("Could not find user for id " + userId));
    return jpaUser.toDomainIncAppliedOrganisations();
  }

  @Override
  public User findByIdIncTeamApplications(String userId) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new CrudException("Could not find user for id " + userId));
    return jpaUser.toDomainIncTeamApplications();
  }

  @Override
  public User createUser(User user) {
    JpaUser jpaUser = new JpaUser(user);
    var savedUser = jpaUserRepository.save(jpaUser);
    return savedUser.toDomainBase();
  }
}
