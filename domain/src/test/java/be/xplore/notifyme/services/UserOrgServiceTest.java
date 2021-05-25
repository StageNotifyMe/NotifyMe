package be.xplore.notifyme.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.UnauthorizedException;
import be.xplore.notifyme.services.security.OrganisationSecurityService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = {UserOrgService.class})
class UserOrgServiceTest {

  @Autowired
  private UserOrgService userOrgService;
  @MockBean
  private UserService userService;
  @MockBean
  private OrganisationService organisationService;
  @MockBean
  private OrganisationSecurityService organisationSecurityService;

  @Test
  void getOrgManagerOrganisations() {
    var org = new Organisation();
    org.setId(1L);
    var user = new User();
    user.setUserId("testId");
    user.setOrganisations(List.of(new OrganisationUser(org, user, true)));
    when(userService.getUserFromprincipalIncOrganisations(any())).thenReturn(user);
    Optional<Organisation> retrievedOrg = userOrgService
        .getOrgManagerOrganisations(getKeycloakPrincipal()).stream().findFirst();
    if (retrievedOrg.isEmpty()) {
      fail();
    }
    assertEquals(org.getId(), retrievedOrg.get().getId());
  }

  @Test
  void getOrgManagerOrganisationsNoOrgs() {
    var org = new Organisation();
    org.setId(1L);
    var user = new User();
    user.setUserId("testId");
    user.setOrganisations(List.of(new OrganisationUser(org, user, false)));
    when(userService.getUserFromprincipalIncOrganisations(any())).thenReturn(user);
    assertEquals(0, userOrgService
        .getOrgManagerOrganisations(getKeycloakPrincipal()).size());
  }

  @Test
  void getOrgInfoAsManager() {
    var user = new User();
    user.setUserId("testId");
    var org = new Organisation();
    when(organisationService.getOrganisation(anyLong())).thenReturn(org);
    when(userService.getUserFromPrincipal(any())).thenReturn(user);
    assertEquals(org, userOrgService.getOrgInfoAsManager(1L, getKeycloakPrincipal()));
  }

  @Test
  void getOrgInfoNotManager() {
    var user = new User();
    user.setUserId("testId");
    var org = new Organisation();
    when(organisationService.getOrganisation(anyLong())).thenReturn(org);
    when(userService.getUserFromPrincipal(any())).thenReturn(user);
    doThrow(new UnauthorizedException("User is not manager.")).when(organisationSecurityService)
        .checkUserIsOrgManager(any(), any());
    var principal = getKeycloakPrincipal();
    assertThrows(UnauthorizedException.class,
        () -> userOrgService.getOrgInfoAsManager(1L, principal));
  }

  private KeycloakAuthenticationToken getKeycloakPrincipal() {
    return (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }
}