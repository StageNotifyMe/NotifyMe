package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.service.OrganisationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private OrganisationService organisationService;

  @Test
  @WithMockUser
  void adminInfoTestNotAdmin() throws Exception {
    mockMvc.perform(get("/admin/getAdminTest"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void adminInfoTestIsAdmin() throws Exception {
    mockMvc.perform(get("/admin/getAdminTest"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Hello Admin."));
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void createOrganisation() throws Exception {
    when(organisationService.createOrganisation(anyString()))
        .thenReturn(new Organisation("testOrg"));

    mockMvc.perform(post("/admin/createOrganisation?name=TestOrg"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "adminUser", roles = {"user", "admin"})
  void createOrganisationWithoutName() throws Exception {
    mockMvc.perform(post("/admin/createOrganisation?name="))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
  }
}