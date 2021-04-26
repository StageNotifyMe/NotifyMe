package be.xplore.notifyme.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import be.xplore.notifyme.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userService;

  @Test
  void getAccessTokenForUserValid() throws Exception {
    when(userService.login(anyString(), anyString())).thenReturn(ResponseEntity.ok("userinfo"));
    mockMvc.perform(get("/user/getToken?username=test&password=test"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("userinfo"));
  }

  @Test
  void getAccessTokenForUserInvalid() throws Exception {
    when(userService.login(anyString(), anyString()))
        .thenReturn(ResponseEntity.status(401).build());
    mockMvc.perform(get("/user/getToken?username=test&password=test"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void registerNewUser() throws Exception {
    when(userService.register(anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(ResponseEntity.ok("User created."));

    mockMvc.perform(post("/user/register").content("{\"firstname\":\"Arthur\",\n"
        + "    \"lastname\":\"De Craemer\",\n"
        + "    \"email\":\"adc@adc.be\",\n"
        + "    \"username\":\"arthur.decraemer\",\n"
        + "    \"password\":\"arthur123!\"}").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}