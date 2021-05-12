package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTeamRepository extends JpaRepository<JpaTeam, Long> {
}
