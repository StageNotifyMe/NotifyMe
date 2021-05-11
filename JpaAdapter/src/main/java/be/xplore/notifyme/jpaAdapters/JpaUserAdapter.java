package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.jpaRepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.IUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUserAdapter implements IUserRepo {
  private final JpaUserRepository jpaUserRepository;
}
