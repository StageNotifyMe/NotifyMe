package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.User;
import java.security.Principal;
import java.util.List;

public interface IOrganisationService {

  Organisation createOrganisation(String name);

  Organisation save(Organisation organisation);

  List<Organisation> getOrganisations();

  Organisation getOrganisation(Long id);

  Organisation getOrganisationIncAppliedUsers(long orgId);

  Organisation promoteUserToOrgManager(String username, Long orgId, Principal principal);

  Organisation addUserToOrganisation(String userId, Long oranisationId);

  Organisation changeApplicationStatus(String userId, Long oranisationId,
      OrgApplicationStatus applicationStatus);

  List<User> getOrganisationManagers(Long organisationId);
}
