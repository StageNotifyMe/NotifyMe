package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import be.xplore.notifyme.dto.CreateEventDto;
import be.xplore.notifyme.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class VenueManagerControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private EventService eventService;
  private final String body =
      "{\"title\": \"Evenement\",\n\"description\":\"beschrijving\",\n"
          + " \"artist\":\"artiest\",\n\"dateTime\":\"2021-04-30T06:45:30\",\n\"venueId\":1\n}";

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void createEventSuccessful() throws Exception {
    when(eventService.createEvent(any(CreateEventDto.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

    mockMvc
        .perform(post("/vmanager/event").header("Content-Type", "application/json")
            .content(body))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithMockUser(username = "user", roles = {"user"})
  void unauthorizedCreate() throws Exception {
    when(eventService.createEvent(any(CreateEventDto.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

    mockMvc
        .perform(post("/vmanager/event").header("Content-Type", "application/json")
            .content(body))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "vmanager", roles = {"venue_manager"})
  void nullBodyCreate() throws Exception {
    when(eventService.createEvent(any(CreateEventDto.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

    mockMvc.perform(post("/vmanager/event"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }


}