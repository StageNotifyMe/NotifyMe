package be.xplore.notifyme.services.systemmessages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.exception.SystemNotificationException;
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
    return new Object[] {orgName};
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
      systemMessagesEn.getSystemMessage(SystemMessages.APPLICATION_APPROVED, new Object[] {});
    });
  }

  //User application
  private Object[] mockUserApplication() {
    var orgName = "orgName";
    var userName = "userName";
    return new Object[] {userName, orgName};
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
      systemMessagesEn.getSystemMessage(SystemMessages.USER_APPLICATION, new Object[] {});
    });
  }

  //Cancel event
  private Object[] mockCancelEvent() {
    var event =
        Event.builder().id(1L).title("title").artist("artist").description("description").dateTime(
            LocalDateTime.now()).build();
    return new Object[] {event};
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
      systemMessagesEn.getSystemMessage(SystemMessages.CANCEL_EVENT, new Object[] {});
    });
  }

  @Test
  void getSystemMessage() {
    System.out.println("Testing cancel event...");
    testCancelEvent();
    System.out.println("Testing user application...");
    testUserApplication();
    System.out.println("Testing application approved...");
    testApplicationApproved();


    System.out.println("fin");
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

  private void testPropsApplicationApproved() {
    assertThat(systemMessagesEn.getUserApplicationApprovedText(), instanceOf(String.class));
    assertThat(systemMessagesEn.getUserApplicationApprovedTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationApprovedText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationApprovedTitle(), instanceOf(String.class));
  }

  private void testPropsUserApplication() {
    assertThat(systemMessagesEn.getUserApplicationApprovedText(), instanceOf(String.class));
    assertThat(systemMessagesEn.getUserApplicationApprovedTitle(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationApprovedText(), instanceOf(String.class));
    assertThat(systemMessagesNl.getUserApplicationApprovedTitle(), instanceOf(String.class));
  }

  @Test
  void testProperties() {
    testPropsCancelEvent();
    testPropsApplicationApproved();
    testPropsUserApplication();
  }
}