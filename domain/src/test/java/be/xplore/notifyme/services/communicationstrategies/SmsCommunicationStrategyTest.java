package be.xplore.notifyme.services.communicationstrategies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
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
}