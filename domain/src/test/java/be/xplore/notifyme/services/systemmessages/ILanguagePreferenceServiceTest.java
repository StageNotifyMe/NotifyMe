package be.xplore.notifyme.services.systemmessages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.xplore.notifyme.domain.AvailableLanguages;
import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.SystemMessages;
import be.xplore.notifyme.exception.SystemNotificationException;
import be.xplore.notifyme.services.systemmessages.implementations.SystemMessagesEn;
import be.xplore.notifyme.services.systemmessages.implementations.SystemMessagesNl;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {ILanguagePreferenceService.class, SystemMessagesEn.class,
    SystemMessagesNl.class})
@TestPropertySource(locations = {"classpath:testMessages.properties"})
@EnableConfigurationProperties({SystemMessagesNl.class, SystemMessagesEn.class})
@RequiredArgsConstructor
class ILanguagePreferenceServiceTest {

  @Autowired
  private SystemMessagesEn systemMessagesEn;

  @Autowired
  SystemMessagesNl systemMessagesNl;

  //Application approved
  private Object[] mockApplicationApproved() {
    var orgName = "orgName";
    return new Object[]{orgName};
  }

  private void testApplicationApproved() {
    var attributes = mockApplicationApproved();
    assertDoesNotThrow(() -> {
      var cancelMessageNl =
          systemMessagesNl.getSystemMessage(SystemMessages.APPLICATION_APPROVED, attributes);
    });
    assertDoesNotThrow(() -> {
      var cancelMessageEn =
          systemMessagesEn.getSystemMessage(SystemMessages.APPLICATION_APPROVED, attributes);
    });
    assertThrows(SystemNotificationException.class, () -> {
      systemMessagesEn.getSystemMessage(SystemMessages.APPLICATION_APPROVED, new Object[]{});
    });
  }

  private void testTeamApplicationApproved() {
    var attributes = mockApplicationApproved();
    assertDoesNotThrow(() -> {
      var cancelMessageNl =
          systemMessagesNl.getSystemMessage(SystemMessages.TEAM_APPLICATION_APPROVED, attributes);
    });
    assertDoesNotThrow(() -> {
      var cancelMessageEn =
          systemMessagesEn.getSystemMessage(SystemMessages.TEAM_APPLICATION_APPROVED, attributes);
    });
    assertThrows(SystemNotificationException.class, () -> {
      systemMessagesEn.getSystemMessage(SystemMessages.TEAM_APPLICATION_APPROVED, new Object[]{});
    });
  }

  //User application
  private Object[] mockUserApplication() {
    var orgName = "orgName";
    var userName = "userName";
    return new Object[]{userName, orgName};
  }

  private void testUserApplication() {
    var attributes = mockUserApplication();
    assertDoesNotThrow(() -> {
      var cancelMessageNl =
          systemMessagesNl.getSystemMessage(SystemMessages.USER_APPLICATION, attributes);
    });
    assertDoesNotThrow(() -> {
      var cancelMessageEn =
          systemMessagesEn.getSystemMessage(SystemMessages.USER_APPLICATION, attributes);
    });
    assertThrows(SystemNotificationException.class, () -> {
      systemMessagesEn.getSystemMessage(SystemMessages.USER_APPLICATION, new Object[]{});
    });
  }

  private void testUserAppliedToTeam() {
    var attributes = mockUserApplication();
    assertDoesNotThrow(() -> {
      var cancelMessageNl =
          systemMessagesNl.getSystemMessage(SystemMessages.USER_TEAM_APPLICATION, attributes);
    });
    assertDoesNotThrow(() -> {
      var cancelMessageEn =
          systemMessagesEn.getSystemMessage(SystemMessages.USER_TEAM_APPLICATION, attributes);
    });
    assertThrows(SystemNotificationException.class, () -> {
      systemMessagesEn.getSystemMessage(SystemMessages.USER_TEAM_APPLICATION, new Object[]{});
    });
  }

  private void testUserCancelledAttendance() {
    var attributes = mockCancelAttendance();
    assertDoesNotThrow(() -> {
      var cancelMessageNl =
          systemMessagesNl.getSystemMessage(SystemMessages.USER_CANCELLED_ATTENDANCE, attributes);
    });
    assertDoesNotThrow(() -> {
      var cancelMessageEn =
          systemMessagesEn.getSystemMessage(SystemMessages.USER_CANCELLED_ATTENDANCE, attributes);
    });
    assertThrows(SystemNotificationException.class, () -> {
      systemMessagesEn.getSystemMessage(SystemMessages.USER_CANCELLED_ATTENDANCE, new Object[]{});
    });
  }

  //Cancel event
  private Object[] mockCancelEvent() {
    var event =
        Event.builder().id(1L).title("title").artist("artist").description("description").dateTime(
            LocalDateTime.now()).build();
    return new Object[]{event};
  }

  //Cancel event attendance
  private Object[] mockCancelAttendance() {
    return new Object[]{"username", "eventname"};
  }

  private void testCancelEvent() {
    var attributes = mockCancelEvent();
    assertDoesNotThrow(() -> {
      var cancelMessageNl =
          systemMessagesNl.getSystemMessage(SystemMessages.CANCEL_EVENT, attributes);
    });
    assertDoesNotThrow(() -> {
      var cancelMessageEn =
          systemMessagesEn.getSystemMessage(SystemMessages.CANCEL_EVENT, attributes);
    });
    assertThrows(SystemNotificationException.class, () -> {
      systemMessagesEn.getSystemMessage(SystemMessages.CANCEL_EVENT, new Object[]{});
    });
  }

  @Test
  void getSystemMessage() {
    testCancelEvent();
    testUserApplication();
    testApplicationApproved();
    testTeamApplicationApproved();
    testUserAppliedToTeam();
    testUserCancelledAttendance();
  }

  @Test
  void getLanguagePreference() {
    assertEquals(AvailableLanguages.EN, systemMessagesEn.getLanguagePreference());
    assertEquals(AvailableLanguages.NL, systemMessagesNl.getLanguagePreference());
  }

  private void testPropsCancelEvent() {
    assertThat(systemMessagesEn.getCancelEventText(), instanceOf(String.class));
    assertThat(systemMessagesEn.getCancelEventTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getCancelEventText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getCancelEventTitle(), instanceOf(String.class));
  }

  private void testPropsUserApplication() {
    assertThat(systemMessagesEn.getUserApplicationText(), instanceOf(String.class));
    assertThat(systemMessagesEn.getUserApplicationTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationTitle(), instanceOf(String.class));
  }

  private void testPropsApplicationApproved() {
    assertThat(systemMessagesEn.getUserApplicationApprovedText(), instanceOf(String.class));
    assertThat(systemMessagesEn.getUserApplicationApprovedTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationApprovedText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationApprovedTitle(), instanceOf(String.class));
  }

  private void testPropsTeamsApplicationApproved() {
    assertThat(systemMessagesEn.getTeamApplicationApprovedTitle(), instanceOf(String.class));
    assertThat(systemMessagesEn.getTeamApplicationApprovedText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getTeamApplicationApprovedTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getTeamApplicationApprovedText(), instanceOf(String.class));
  }

  private void testPropsTeamApplication() {
    assertThat(systemMessagesEn.getUserTeamApplicationTitle(), instanceOf(String.class));
    assertThat(systemMessagesEn.getUserTeamApplicationText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserTeamApplicationTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserTeamApplicationText(), instanceOf(String.class));
  }

  private void testPropsUserAttendanceCancelled() {
    assertThat(systemMessagesEn.getUserAttendanceCancelledTitle(), instanceOf(String.class));
    assertThat(systemMessagesEn.getUserAttendanceCancelledText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserAttendanceCancelledTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserAttendanceCancelledText(), instanceOf(String.class));
  }

  @Test
  void testProperties() {
    testPropsCancelEvent();
    testPropsUserApplication();
    testPropsApplicationApproved();
    testPropsTeamsApplicationApproved();
    testPropsTeamApplication();
    testPropsUserAttendanceCancelled();
  }
}