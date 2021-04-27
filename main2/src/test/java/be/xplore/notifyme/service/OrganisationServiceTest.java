package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.persistence.OrganisationRepo;
import java.util.ArrayList;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class OrganisationServiceTest {

  @Autowired
  private OrganisationService organisationService;
  @MockBean
  private OrganisationRepo organisationRepo;

  @Test
  void createOrganisation() {
    String orgname = "testOrg";
    Organisation testOrg = new Organisation(1L, orgname, new ArrayList<>());
    when(organisationRepo.save(any())).thenReturn(testOrg);
    assertEquals(testOrg, organisationService.createOrganisation(orgname));
  }

  @Test
  void createOrganisationDbNotWorking() {
    String orgname = "testOrg";
    Organisation testOrg = new Organisation(1L, orgname, new ArrayList<>());
    when(organisationRepo.save(any())).thenThrow(new HibernateException("HBE"));
    assertThrows(HibernateException.class, () ->
        organisationService.createOrganisation(orgname));
  }
}