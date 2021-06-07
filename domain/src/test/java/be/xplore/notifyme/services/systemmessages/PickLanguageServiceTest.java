package be.xplore.notifyme.services.systemmessages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import be.xplore.notifyme.domain.AvailableLanguages;
import be.xplore.notifyme.services.systemmessages.implementations.PickLanguageService;
import be.xplore.notifyme.services.systemmessages.implementations.SystemMessagesEn;
import be.xplore.notifyme.services.systemmessages.implementations.SystemMessagesNl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {PickLanguageService.class})
class PickLanguageServiceTest {
  @Autowired
  private PickLanguageService pickLanguageService;
  @MockBean
  private SystemMessagesNl systemMessagesNl;
  @MockBean
  private SystemMessagesEn systemMessagesEn;

  @Test
  void getLanguageService() {
    assertThat(pickLanguageService.getLanguageService(AvailableLanguages.EN),
        instanceOf(SystemMessagesEn.class));
    assertThat(pickLanguageService.getLanguageService(AvailableLanguages.NL),
        instanceOf(SystemMessagesNl.class));
  }
}