package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Team;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITeamRepo {
  Team save(Team team);
  Optional<Team> findById(long teamId);
}
