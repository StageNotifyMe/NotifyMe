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
import be.xplore.notifyme.services.systemmessages.AvailableLanguages;
import be.xplore.notifyme.services.systemmessages.PickLanguageService;
import be.xplore.notifyme.services.systemmessages.SystemMessages;
import be.xplore.notifyme.services.systemmessages.SystemMessagesEn;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
  @MockBean
  private PickLanguageService pickLanguageService;
  @MockBean
  private IUserService userService;

  @BeforeEach
  private void setUp() {
    mockSave();
  }

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
        Notification.builder()
            .id(1L).communicationAddress("+32123456789").communicationPreference(mockComPref)
            .usedCommunicationStrategy("smscommunicationstrategy")
            .message(new Message("title", "text"))
            .receiver(new User("userId", "username")).timestamp(LocalDateTime.now()).hidden(false)
            .sender("SYSTEM").build();

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
            new User("userId", "username"), null, false, LocalDateTime.now());
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
  void notifyUserHidden() {
    when(keycloakCommunicationService.getUserInfoUsername(anyString()))
        .thenReturn(getDummyUserRep());
    var mockComPref = mock(CommunicationPreference.class);
    when(mockComPref.getCommunicationStrategy())
        .thenReturn(new EmailCommunicationStrategy(emailService));
    var notification =
        new Notification(1L, "address", mockComPref, "emailservice", new Message("title", "text"),
            new User("userId", "username"), null, false, LocalDateTime.now());
    when(notificationRepo.create(anyLong(), anyString())).thenReturn(notification);

    when(notificationRepo.save(any())).thenAnswer(new Answer<Notification>() {
      @Override
      public Notification answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        return (Notification) args[0];
      }
    });

    assertDoesNotThrow(() -> {
      notificationService.notifyUserHidden("userId", 1L);
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

  /*@Test
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
  }*/

  @Test
  void notifyOrganisationsManagers() {
    mockNotifyOrgManagersFromEvent();

    var orgIdList = new ArrayList<Long>();
    orgIdList.add(1L);
    orgIdList.add(2L);
    var event = new Event(1L, "title", "description", "artist", LocalDateTime.now(), EventStatus.OK,
        new Venue(), new ArrayList<>(), new HashSet<>());

    assertDoesNotThrow(() -> {
      notificationService
          .notifyOrganisationManagersForCancelEvent(event, SystemMessages.CANCEL_EVENT);
    });
  }

  private void mockNotifyOrgManagersFromEvent() {
    var orgManager1 = new User("org1-man", "orgMan1");
    var orgManager2 = new User("org2-man", "orgMan2");
    //this.mockGetOrganisationManagers(orgManager1, orgManager2);
    var message = new Message(1L, "title", "text");
    var comPref = new CommunicationPreference(1L, orgManager1, true, true, true,
        mockEmailCommunicationStrategy());
    this.mockCreateNotification(message, orgManager1, comPref);
    this.mockCreateNotification(message, orgManager2, comPref);
    this.mockGetUserInfoUsername();
  }

  private EmailCommunicationStrategy mockEmailCommunicationStrategy() {
    var emailComStrat = mock(EmailCommunicationStrategy.class);
    doNothing().when(emailComStrat).send(any());
    return emailComStrat;
  }

  private void mockCreateNotification(Message message, User user, CommunicationPreference comPref) {
    var notification =
        new Notification(1L, "address", comPref, "emailservice", message,
            user, null, false, LocalDateTime.now());
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
    var user1 = new User("user1", "username1");
    var user2 = new User("user2", "username2");
    var users = new ArrayList<User>();
    users.add(user1);
    users.add(user2);
    mockNotifyUsers(users);

    assertDoesNotThrow(() -> {
      notificationService.notifyUsers(users, 1L);
      notificationService.notifyUsers(null, 1L);
    });
  }

  private void mockNotifyUsers(Collection<User> users) {
    var message = new Message(1L, "title", "text");
    long iter = 1L;
    for (User user : users) {
      var comPref = new CommunicationPreference(iter++, user, true, true, true,
          mockEmailCommunicationStrategy());
      mockCreateNotification(message, user, comPref);
    }
    mockGetUserInfoUsername();
  }

  @Test
  void notifyOrganisationManagers() {
    mockNotifyOrgManagers();

    assertDoesNotThrow(() -> {
      notificationService.notifyOrganisationManagers(1L, "userId", "title", "text");
    });
  }

  private void mockNotifyOrgManagers() {
    var message = new Message(1L, "title", "text");

    when(messageRepo.save(any())).thenReturn(message);
    var users = new ArrayList<User>();
    users.add(new User("userId", "username"));
    mockGetUserInfoUsername();
    when(organisationService.getOrganisationManagers(1L)).thenReturn(users);
    var comPref = new CommunicationPreference(1L, users.get(0), true, true, true,
        mockEmailCommunicationStrategy());
    mockCreateNotification(message, users.get(0), comPref);
    when(notificationRepo.create(anyLong(), anyString(), anyString())).thenReturn(
        new Notification(1L, "mail@mailadres.com", comPref, "emailcommunicationstrategy", message,
            users.get(0), "userId", false, LocalDateTime.now()));
    mockEmailCommunicationStrategy();
  }

  @Test
  void createAndSendSystemNotification() {
    mockCreateAndSendSystemNotification();
    assertDoesNotThrow(() -> {
      notificationService.createAndSendSystemNotification("userId", SystemMessages.CANCEL_EVENT,
          new Object[] {Event.builder().id(1L).build()});
    });
  }

  private void mockCreateAndSendSystemNotification() {
    final var messageServiceEn = mock(SystemMessagesEn.class);
    final var message = Message.builder().id(1L).text("text").title("title").build();
    final var user = User.builder().userId("userId").userName("username").preferedLanguage(
        AvailableLanguages.EN).build();
    final var comPref = CommunicationPreference.builder().id(1L)
        .communicationStrategy(mockEmailCommunicationStrategy()).isActive(true).isDefault(true)
        .isUrgent(true).user(user).build();
    when(userService.getUser(anyString()))
        .thenReturn(user);
    when(pickLanguageService.getLanguageService(AvailableLanguages.EN))
        .thenReturn(messageServiceEn);
    when(messageServiceEn.getSystemMessage(any(), any()))
        .thenReturn(message);
    mockSave();
    mockCreateNotification(message, user, comPref);
  }

  @Test
  void testNotifyUsersSystemMessage() {
    mockCreateAndSendSystemNotification();
    var userList = new ArrayList<User>();
    userList.add(User.builder().userId("userId").build());
    assertDoesNotThrow(() -> {
      notificationService.notifyUsers(userList, SystemMessages.CANCEL_EVENT,
          new Object[] {Event.builder().id(1L).build()});
    });
    assertDoesNotThrow(() -> {
      notificationService.notifyUsers(null, SystemMessages.CANCEL_EVENT,
          new Object[] {Event.builder().id(1L).build()});
    });
  }

  @Test
  void testGetAllNotifications() {
    when(notificationRepo.getAllNotifications())
        .thenReturn(List.of(Notification.builder().id(1L).build()));
    assertTrue(notificationService.getAllNotifications().stream().anyMatch(n -> n.getId() == 1L));
  }
}