package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.jpaRepositories.JpaLineRepository;
import be.xplore.notifyme.persistence.ILineRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaLineAdapter implements ILineRepo {
  private final JpaLineRepository jpaLineRepository;

}
