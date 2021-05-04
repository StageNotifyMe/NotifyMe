package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.service.EventService;
import java.util.ArrayList;
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
class LineManagerControllerTest {

  @Autowired
  private MockMvc mockMvc;
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