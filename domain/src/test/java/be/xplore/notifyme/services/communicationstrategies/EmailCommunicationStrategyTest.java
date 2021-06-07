package be.xplore.notifyme.services.communicationstrategies;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import be.xplore.notifyme.services.communicationstrategies.implementations.EmailCommunicationStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {EmailCommunicationStrategy.class})
class EmailCommunicationStrategyTest {
  @Autowired
  private EmailCommunicationStrategy emailCommunicationStrategy;
  @MockBean
  private IEmailService emailService;

  @Test
  void getName() {
    assertEquals("Email", emailCommunicationStrategy.getName());
  }

  @Test
  void getEmailService() {
    assertThat(emailCommunicationStrategy.getEmailService(), instanceOf(IEmailService.class));
  }
}