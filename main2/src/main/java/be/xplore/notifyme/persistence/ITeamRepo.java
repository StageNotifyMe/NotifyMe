package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITeamRepo extends JpaRepository<Team, Long> {
}
