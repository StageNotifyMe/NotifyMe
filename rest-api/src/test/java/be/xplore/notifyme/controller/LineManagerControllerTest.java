package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import be.xplore.notifyme.config.KeycloakSecurityConfig;
import be.xplore.notifyme.config.RestConfig;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exception.GeneralExceptionHandler;
import be.xplore.notifyme.services.EventService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = {LineManagerController.class})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {RestConfig.class, KeycloakSecurityConfig.class})
class LineManagerControllerTest {
  private MockMvc mockMvc;

  @Autowired
  private LineManagerController lineManagerController;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(lineManagerController)
        .setControllerAdvice(new GeneralExceptionHandler())
        .build();
  }

  @MockBean
  private EventService eventService;

  @Test
  @WithMockUser(username = "test", roles = {"line_manager"})
  void getAllEventsForLineManagerSuccess() throws Exception {
    when(eventService.getAllEventsForLineManager(anyString())).thenReturn(new ArrayList<>());

    mockMvc
        .perform(get("/lmanager/events?userId=testUser"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "lmanager", roles = {"user", "line_manager"})
  void getAllEventsForLineManagerFail() throws Exception {
    when(eventService.getAllEventsForLineManager(anyString()))
        .thenThrow(new CrudException("Could not get events for line manager."));

    mockMvc
        .perform(get("/lmanager/events?userId=testUser"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }
}