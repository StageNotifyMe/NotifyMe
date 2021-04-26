package be.xplore.notifyme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

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
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}