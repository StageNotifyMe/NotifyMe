package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaEvent;
import be.xplore.notifyme.jpaobjects.JpaLine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLineRepository extends JpaRepository<JpaLine, Long> {

  List<JpaLine> getAllByEvent(JpaEvent jpaEvent);

  @Query(value = "SELECT jl "
      + "FROM JpaLine jl "
      + "         JOIN jl.team jt"
      + "         JOIN jt.organisations jto"
      + "         JOIN jto.users jtuo"
      + "         JOIN jtuo.user u"
      + "         LEFT JOIN jt.teamMembers tm"
      + " WHERE (tm.userId <> :userId or tm.userId is null) and u.userId = :userId")
  List<JpaLine> getAllAvailableLinesForUser(String userId);

}
