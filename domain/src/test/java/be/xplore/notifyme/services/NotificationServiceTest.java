package be.xplore.notifyme.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.EventStatus;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.persistence.IMessageRepo;
import be.xplore.notifyme.persistence.INotificationRepo;
import be.xplore.notifyme.services.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.IEmailService;
import be.xplore.notifyme.services.communicationstrategies.ISmsService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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
  @MockBean
  private OrganisationService organisationService;

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
  void notifyUserUrgent() {
    when(keycloakCommunicationService.getUserInfoUsername(anyString()))
        .thenReturn(getDummyUserRep());
    var mockComPref = mock(CommunicationPreference.class);
    when(mockComPref.getCommunicationStrategy())
        .thenReturn(new EmailCommunicationStrategy(emailService));
    var notification =
        new Notification(1L, "address", mockComPref, "emailservice", new Message("title", "text"),
            new User("userId", "username"));
    when(notificationRepo.createUrgent(anyLong(), anyString())).thenReturn(notification);

    when(notificationRepo.save(any())).thenAnswer(new Answer<Notification>() {
      @Override
      public Notification answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        return (Notification) args[0];
      }
    });

    assertDoesNotThrow(() -> {
      notificationService.notifyUserUrgent("userId", 1L);
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

  @Test
  void createCanceledEventMessage() {
    var event = new Event(1L, "title", "description", "artist", LocalDateTime.now(), EventStatus.OK,
        new Venue(), new ArrayList<>(), new HashSet<>());
    when(messageRepo.save(any())).thenAnswer(new Answer<Message>() {
      @Override
      public Message answer(InvocationOnMock invocation) throws Throwable {
        var args = invocation.getArguments();
        return (Message) args[0];
      }
    });

    var message = notificationService.createCanceledEventMessage(event);
    assertTrue(message.getText().contains("EventId: 1"));
    assertTrue(message.getText().contains("Title: title"));
    assertTrue(message.getText().contains("Description: description"));
  }

  @Test
  void notifyOrganisationsManagers() {
    mockNotifyOrgManagers();

    var orgIdList = new ArrayList<Long>();
    orgIdList.add(1L);
    orgIdList.add(2L);
    assertDoesNotThrow(() -> {
      notificationService.notifyOrganisationsManagers(orgIdList, 1L);
    });
  }

  private void mockNotifyOrgManagers() {
    var orgManager1 = new User("org1-man", "orgMan1");
    var orgManager2 = new User("org2-man", "orgMan2");
    this.mockGetOrganisationManagers(orgManager1, orgManager2);
    var message = new Message(1L, "title", "text");
    var comPref = new CommunicationPreference(1L, orgManager1, true, true, true,
        mockEmailCommunicationStrategy());
    this.mockCreateNotification(message, orgManager1, comPref);
    this.mockCreateNotification(message, orgManager2, comPref);
    this.mockGetUserInfoUsername();
    this.mockSave();
  }

  private EmailCommunicationStrategy mockEmailCommunicationStrategy() {
    var emailComStrat = mock(EmailCommunicationStrategy.class);
    doNothing().when(emailComStrat).send(any());
    return emailComStrat;
  }


  private void mockGetOrganisationManagers(User orgManager1, User orgManager2) {
    var org1managers = new ArrayList<User>();
    org1managers.add(orgManager1);
    var org2managers = new ArrayList<User>();
    org2managers.add(orgManager2);
    when(organisationService.getOrganisationManagers(1L)).thenReturn(org1managers);
    when(organisationService.getOrganisationManagers(2L)).thenReturn(org2managers);
  }

  private void mockCreateNotification(Message message, User user, CommunicationPreference comPref) {
    var notification =
        new Notification(1L, "address", comPref, "emailservice", message,
            user);
    when(notificationRepo.create(anyLong(), anyString())).thenReturn(notification);
  }

  private void mockGetUserInfoUsername() {
    when(keycloakCommunicationService.getUserInfoUsername(anyString())).thenAnswer(
        new Answer<UserRepresentation>() {
          @Override
          public UserRepresentation answer(InvocationOnMock invocation) throws Throwable {
            var args = invocation.getArguments();
            var userRep = getDummyUserRep();
            userRep.setUsername((String) args[0]);
            return userRep;
          }
        });
  }

  private void mockSave() {
    when(notificationRepo.save(any())).thenAnswer(new Answer<Notification>() {
      @Override
      public Notification answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(0);
      }
    });
    when(messageRepo.save(any())).thenAnswer(new Answer<Message>() {
      @Override
      public Message answer(InvocationOnMock invocation) throws Throwable {
        return invocation.getArgument(0);
      }
    });
  }

  @Test
  void notifyUsers() {
  }
}