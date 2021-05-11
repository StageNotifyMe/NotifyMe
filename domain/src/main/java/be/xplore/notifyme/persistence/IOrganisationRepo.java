package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Organisation;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrganisationRepo {

  Organisation save(Organisation organisation);

  List<Organisation> findAll();

  Optional<Organisation> findById(Long id);
}
