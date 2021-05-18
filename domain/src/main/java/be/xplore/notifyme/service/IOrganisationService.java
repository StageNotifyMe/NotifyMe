package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Organisation;
import java.security.Principal;
import java.util.List;

public interface IOrganisationService {

  Organisation createOrganisation(String name);

  Organisation save(Organisation organisation);

  List<Organisation> getOrganisations();

  Organisation getOrganisation(Long id);

  Organisation getOrganisationIncAppliedUsers(long orgId);

  Organisation promoteUserToOrgManager(String username, Long orgId, Principal principal);
}
