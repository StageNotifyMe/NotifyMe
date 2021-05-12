package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrganisationRepository extends JpaRepository<JpaOrganisation, Long> {

}
