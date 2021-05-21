package be.xplore.notifyme.communication;

import be.xplore.notifyme.domain.Organisation;
import java.security.Principal;
import java.util.List;

public interface IUserOrgService {

  List<Organisation> getOrgManagerOrganisations(Principal principal);

  Organisation getOrgInfoAsManager(Long orgId, Principal principal);
}
