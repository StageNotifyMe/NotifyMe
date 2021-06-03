package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.jpaobjects.JpaTeam;
import be.xplore.notifyme.jpaobjects.JpaTeamApplication;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTeamRepository extends JpaRepository<JpaTeam, Long> {

  @Query(value = "SELECT teamApps "
      + "FROM JpaTeamApplication teamApps "
      + "         JOIN teamApps.appliedTeam teams"
      + "         JOIN teams.organisations organisations"
      + "         JOIN organisations.users organisationUsers"
      + "         JOIN organisationUsers.user u"
      + " WHERE u.userId = :orgManagerId")
  Set<JpaTeamApplication> getTeamApplicationsForOrgManager(String orgManagerId);

  @Query(value = "SELECT teams "
      + "FROM JpaTeam teams "
      + "         JOIN teams.teamMembers members"
      + " WHERE members.userId = :userId")
  Set<JpaTeam> getTeamApplicationsForUser(String userId);
}
