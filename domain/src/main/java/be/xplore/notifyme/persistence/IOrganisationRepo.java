package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrganisationRepo extends JpaRepository<Organisation, Long> {

}
