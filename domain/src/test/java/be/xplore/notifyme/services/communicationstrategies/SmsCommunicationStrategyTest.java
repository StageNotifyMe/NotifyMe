package be.xplore.notifyme.services.communicationstrategies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {SmsCommunicationStrategy.class})
class SmsCommunicationStrategyTest {

  @Autowired
  private SmsCommunicationStrategy smsCommunicationStrategy;
  @MockBean
  private ISmsService smsService;

  @Test
  void getName() {
    assertEquals("SMS", smsCommunicationStrategy.getName());
  }

  @Test
  void getSmsService() {
    assertThat(smsCommunicationStrategy.getSmsService(), instanceOf(ISmsService.class));
  }

  @Test
  void send() {
    var comPref = mock(CommunicationPreference.class);
    var message = new Message("title", "text");
    var user = mock(User.class);
    var notification =
        new Notification(1L, "+32123456789", comPref, "smscommunicationstrategy", message, user,
            false);

    doNothing().when(smsService).send("+32123456789", message);

    assertDoesNotThrow(() -> {
      smsCommunicationStrategy.send(notification);
    });
  }

  @Test
  void getCommunicationAddress() {
    var userRepresentation = mock(UserRepresentation.class);
    var attributes = new HashMap<String, List<String>>();
    var list = new ArrayList<String>();
    list.add("+32123456789");
    attributes.put("phone_number", list);
    when(userRepresentation.getAttributes()).thenReturn(attributes);

    assertEquals("+32123456789",
        smsCommunicationStrategy.getCommunicationAddress(userRepresentation));
  }
}