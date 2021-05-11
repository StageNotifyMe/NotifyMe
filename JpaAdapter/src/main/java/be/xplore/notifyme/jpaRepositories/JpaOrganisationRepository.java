package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrganisationRepository extends JpaRepository<JpaOrganisation, Long> {

}
