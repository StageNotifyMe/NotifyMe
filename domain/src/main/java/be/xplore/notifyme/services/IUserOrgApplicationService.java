package be.xplore.notifyme.communication;

import be.xplore.notifyme.domain.OrganisationUserKey;
import be.xplore.notifyme.domain.UserOrgApplication;
import java.security.Principal;
import java.util.List;

public interface IUserOrgApplicationService {

  void applyToOrganisation(Long organisationId, Principal principal);

  List<UserOrgApplication> getUserOrgApplications(Principal principal);

  List<UserOrgApplication> getOrgApplications(Long organisationId, Principal principal);

  void respondToApplication(OrganisationUserKey organisationUserKey, boolean accept,
      Principal principal);
}
