package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.IEmailService;
import be.xplore.notifyme.domain.communicationstrategies.ISmsService;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.INotificationRepo;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.account.UserRepresentation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {NotificationService.class})
class NotificationServiceTest {

  @Autowired
  private NotificationService notificationService;
  @MockBean
  INotificationRepo notificationRepo;
  @MockBean
  IMessageRepo messageRepo;
  @MockBean
  KeycloakCommunicationService keycloakCommunicationService;
  @MockBean
  IEmailService emailService;
  @MockBean
  ISmsService smsService;

  @Test
  void createMessage() {
    when(messageRepo.save(any())).thenAnswer(new Answer<Message>() {
      @Override
      public Message answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        return (Message) args[0];
      }
    });
    var result = notificationService.createMessage("title", "text");
    assertEquals("title", result.getTitle());
    assertEquals("text", result.getText());
  }

  @Test
  void notifyUser() {
    when(keycloakCommunicationService.getUserInfoUsername(anyString()))
        .thenReturn(getDummyUserRep());
    var mockComPref = mock(CommunicationPreference.class);
    when(mockComPref.getCommunicationStrategy())
        .thenReturn(new EmailCommunicationStrategy(emailService));
    var notification =
        new Notification(1L, "address", mockComPref, "emailservice", new Message("title", "text"),
            new User("userId", "username"));
    when(notificationRepo.create(anyLong(), anyString())).thenReturn(notification);

    when(notificationRepo.save(any())).thenAnswer(new Answer<Notification>() {
      @Override
      public Notification answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        return (Notification) args[0];
      }
    });

    assertDoesNotThrow(() -> {
      notificationService.notifyUser("userId", 1L);
    });
  }


  @Test
  void getNotificationsForUser() {
    var notifications = new ArrayList<Notification>();
    when(notificationRepo.findByUser(anyString())).thenReturn(notifications);
    assertEquals(notifications, notificationService.getNotificationsForUser("testUser"));
  }

  private UserRepresentation getDummyUserRep() {
    var userRep = new UserRepresentation();
    userRep.setEmail("mail@mail.com");
    userRep.setUsername("username");
    userRep.setFirstName("firstname");
    userRep.setLastName("lastname");
    userRep.setId("userId");
    userRep.setEmailVerified(true);
    return userRep;
  }
}