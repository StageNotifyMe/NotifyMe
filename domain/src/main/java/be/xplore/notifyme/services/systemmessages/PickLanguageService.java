package be.xplore.notifyme.services.systemmessages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PickLanguageService {
  private final SystemMessagesNl systemMessagesNl;
  private final SystemMessagesEn systemMessagesEn;

  public ILanguagePreferenceService getLanguageService(AvailableLanguages availableLanguages) {
    switch (availableLanguages) {
      case EN:
        return systemMessagesEn;
      case NL:
        return systemMessagesNl;
      default:
        return systemMessagesEn;
    }
  }
}
