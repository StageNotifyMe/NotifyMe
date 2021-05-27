package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaTeam;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrganisationRepository extends JpaRepository<JpaOrganisation, Long> {
  List<JpaOrganisation> findAllByTeamsNotContaining(JpaTeam team);
}
