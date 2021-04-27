package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepo extends JpaRepository<Organisation, Long> {

}
