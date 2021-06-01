package be.xplore.notifyme.services.systemmessages;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import be.xplore.notifyme.domain.Event;
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
}