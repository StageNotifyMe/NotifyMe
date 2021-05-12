package be.xplore.notifyme.service.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.UnauthorizedException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OrganisationSecurityService.class)
class OrganisationSecurityServiceTest {

  @Autowired
  private OrganisationSecurityService organisationSecurityService;

  @Test
  void checkUserIsOrgManager() {
    var org = new Organisation();
    org.setId(1L);
    var user = new User();
    user.setUserId("testId");
    var orgUser = new OrganisationUser(org, user, true);
    org.setUsers(List.of(orgUser));
    assertDoesNotThrow(() -> {
      organisationSecurityService.checkUserIsOrgManager(user, org);
    });
  }

  @Test
  void checkUserIsNotOrgManager() {
    var org = new Organisation();
    org.setId(1L);
    var user = new User();
    user.setUserId("testId");
    var orgUser = new OrganisationUser(org, user, false);
    org.setUsers(List.of(orgUser));
    assertThrows(UnauthorizedException.class, () -> {
      organisationSecurityService.checkUserIsOrgManager(user, org);
    });
  }

  @Test
  void checkUserIsNotInOrg() {
    var org = new Organisation();
    org.setId(1L);
    var user = new User();
    user.setUserId("testId");
    org.setUsers(new ArrayList<>());
    assertThrows(UnauthorizedException.class, () -> {
      organisationSecurityService.checkUserIsOrgManager(user, org);
    });
  }
}