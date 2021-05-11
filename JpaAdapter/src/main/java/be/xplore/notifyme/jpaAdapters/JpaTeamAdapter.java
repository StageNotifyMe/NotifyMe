package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.jpaRepositories.JpaTeamRepository;
import be.xplore.notifyme.persistence.ITeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTeamAdapter implements ITeamRepo {
  private final JpaTeamRepository jpaTeamRepository;
}
