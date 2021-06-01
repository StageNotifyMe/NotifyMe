package be.xplore.notifyme.services.systemmessages;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;

public interface ILanguagePreferenceService {
  public AvailableLanguages getLanguagePreference();

  public Message getCancelEvent(Event event);

  default Message getSystemMessage(String messageIdentifier, Object[] attribute) {
    switch (messageIdentifier) {
      case "cancelEvent":
        return this.getCancelEvent((Event) attribute[0]);

      default:
        return null;
    }
  }
}
