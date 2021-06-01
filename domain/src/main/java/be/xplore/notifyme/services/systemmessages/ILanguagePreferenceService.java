package be.xplore.notifyme.services.systemmessages;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.exception.SystemNotificationException;

public interface ILanguagePreferenceService {
  public AvailableLanguages getLanguagePreference();

  public Message getCancelEvent(Event event);

  /**
   * Returns the correct message based on the identifier.
   *
   * @param messageIdentifier identifier.
   * @param attribute         attributes needed to construct the message.
   * @return a message object.
   */
  default Message getSystemMessage(String messageIdentifier, Object[] attribute) {
    switch (messageIdentifier) {
      case "cancelEvent":
        if (attribute.length < 1) {
          throw new SystemNotificationException(
              "Expected 1 attribute of type Event, but found none");
        }
        return this.getCancelEvent((Event) attribute[0]);

      default:
        throw new SystemNotificationException(
            "Could not find message for identifier " + messageIdentifier);
    }
  }
}
