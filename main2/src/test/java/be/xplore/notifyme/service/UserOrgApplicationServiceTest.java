package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.User;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
public class UserOrgApplicationServiceTest {

  @Autowired
  private UserOrgApplicationService userOrgApplicationService;
  @MockBean
  private UserService userService;
  @MockBean
  private OrganisationService organisationService;


  @Test
  void applyToOrganisation() {
    var user = new User();
    user.setAppliedOrganisations(new ArrayList<>());
    when(userService.getUserFromPrincipal(any())).thenReturn(user);
    when(organisationService.getOrganisation(anyLong())).thenReturn(new Organisation());
    when(userService.updateUser(any())).thenReturn(new User());

    userOrgApplicationService.applyToOrganisation(1L, getKeycloakPrincipal());
  }

  @Test
  void getUserOrgApplications() {
    var keycloakPrincipal = getKeycloakPrincipal();
    var user = new User();
    user.setAppliedOrganisations(new ArrayList<>());
    when(userService.getUserFromPrincipal(any())).thenReturn(user);
    assertEquals(user.getAppliedOrganisations(),
        userOrgApplicationService.getUserOrgApplications(keycloakPrincipal));
  }

  private KeycloakAuthenticationToken getKeycloakPrincipal() {
    return (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }
}
