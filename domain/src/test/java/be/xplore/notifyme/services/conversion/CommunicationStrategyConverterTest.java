package be.xplore.notifyme.services.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import be.xplore.notifyme.services.communicationstrategies.IEmailService;
import be.xplore.notifyme.services.communicationstrategies.ISmsService;
import be.xplore.notifyme.services.communicationstrategies.implementations.EmailCommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.implementations.SmsCommunicationStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {CommunicationStrategyConverter.class})
class CommunicationStrategyConverterTest {
  @Autowired
  private CommunicationStrategyConverter communicationStrategyConverter;
  @MockBean
  private IEmailService emailService;
  @MockBean
  private ISmsService smsService;

  @Test
  void convertToDatabaseColumn() {
    assertEquals("emailcommunicationstrategy", communicationStrategyConverter
        .convertToDatabaseColumn(new EmailCommunicationStrategy(null)));
    assertEquals("smscommunicationstrategy",
        communicationStrategyConverter.convertToDatabaseColumn(new SmsCommunicationStrategy(null)));
  }

  @Test
  void convertToEntityAttribute() {
    assertThat(
        communicationStrategyConverter.convertToEntityAttribute("emailcommunicationstrategy"),
        instanceOf(EmailCommunicationStrategy.class));
    assertThat(
        communicationStrategyConverter.convertToEntityAttribute("smscommunicationstrategy"),
        instanceOf(SmsCommunicationStrategy.class));
    assertNull(communicationStrategyConverter.convertToEntityAttribute("invalid"));
  }
}