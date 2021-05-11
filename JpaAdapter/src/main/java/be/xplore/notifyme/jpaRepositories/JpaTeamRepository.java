package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTeamRepository extends JpaRepository<JpaTeam, Long> {
}
