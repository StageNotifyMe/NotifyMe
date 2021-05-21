package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrganisationRepo {

  Organisation save(Organisation organisation);

  List<Organisation> findAll();

  Optional<Organisation> findById(Long id);

  Optional<Organisation> findByIdIncAppliedUsers(long orgId);

  Organisation addToOrgManagers(long organisationId, String userId);

  Organisation addUserToOrganisation(String userId, Long organisationId);

  Organisation changeApplicationStatus(String userId, Long oranisationId,
      OrgApplicationStatus applicationStatus);

  void applyToOrganisation(long orgId, String userId);
}
