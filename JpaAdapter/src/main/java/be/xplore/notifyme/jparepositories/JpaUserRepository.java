package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<JpaUser, String> {
  @Query(nativeQuery = true, value = "select ju.user_id from jpa_user ju "
      + "join jpa_organisation_user jou on ju.user_id = jou.user_id "
      + "join jpa_organisation jo on jou.organisation_id = jo.id "
      + "join jpa_team_organisations jto on jo.id = jto.organisations_id "
      + "join jpa_team jt on jto.teams_id = jt.id "
      + "join jpa_line jl on jt.line_id = jl.id "
      + "join jpa_event je on jl.event_id = je.id "
      + "where je.id = :eventId and is_organisation_leader = true")
  public List<String> findByEvent(@Param("eventId") long eventId);
}
