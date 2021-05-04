package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITeamRepo extends JpaRepository<Team, Long> {
}
