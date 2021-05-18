package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.jpaobjects.JpaTeam;
import be.xplore.notifyme.jparepositories.JpaTeamRepository;
import be.xplore.notifyme.persistence.ITeamRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTeamAdapter implements ITeamRepo {
  private final JpaTeamRepository jpaTeamRepository;

  @Override
  public Team save(Team team) {
    return jpaTeamRepository.save(new JpaTeam(team)).toDomain();
  }

  @Override
  public Optional<Team> findById(long teamId) {
    return jpaTeamRepository.findById(teamId).map(JpaTeam::toDomain);
  }
}
