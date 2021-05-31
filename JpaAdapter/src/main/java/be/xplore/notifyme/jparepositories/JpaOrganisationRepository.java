package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaTeam;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrganisationRepository extends JpaRepository<JpaOrganisation, Long> {
  List<JpaOrganisation> findAllByTeamsNotContaining(JpaTeam team);

  @Query(value = "SELECT ju\n"
      + "FROM jpa_user ju\n"
      + "         JOIN jpa_organisation_user jou on ju.user_id = jou.user_id\n"
      + "         JOIN jpa_organisation jo on jou.organisation_id = jo.id\n"
      + "WHERE jo.id = :id AND jou.is_organisation_leader=true", nativeQuery = true)
  List<User> findAllUsersByOrg(@Param("id") long organisationId);
}
