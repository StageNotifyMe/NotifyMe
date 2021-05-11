package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.jpaRepositories.JpaEventRepository;
import be.xplore.notifyme.persistence.IEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaEventAdapter implements IEventRepo {
  private final JpaEventRepository jpaEventRepository;
}
