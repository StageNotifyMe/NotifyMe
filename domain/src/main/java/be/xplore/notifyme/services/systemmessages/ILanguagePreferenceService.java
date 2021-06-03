package be.xplore.notifyme.services.systemmessages;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.exception.SystemNotificationException;

public interface ILanguagePreferenceService {

  public AvailableLanguages getLanguagePreference();

  public Message getCancelEvent(Event event);

  public Message getUserApplication(String userName, String organisationName);

  public Message getUserApplicationApproved(String organisationName);

  public Message getTeamApplication(String username, String eventName);

  public Message getTeamApplicationApproved(String eventName);

  /**
   * Returns the correct message based on the identifier. Expected contents of attributes: -
   * CANCEL_EVENT: Event object. - USER_APPLICATION: String userName, String organisationName. -
   * APPLICATION_APPROVED: String organisationName.
   *
   * @param messageIdentifier identifier.
   * @param attributes        attributes needed to construct the message.
   * @return a message object.
   */
  default Message getSystemMessage(SystemMessages messageIdentifier, Object[] attributes) {
    switch (messageIdentifier) {
      case CANCEL_EVENT:
        if (attributes.length < 1) {
          throw new SystemNotificationException(
              "Expected 1 attribute of type Event, but found none");
        }
        return this.getCancelEvent((Event) attributes[0]);
      case USER_APPLICATION:
        if (attributes.length < 2) {
          throw new SystemNotificationException(
              "Expected 2 attributes: String userName, String organisationName | but found none.");
        }
        return this.getUserApplication((String) attributes[0], (String) attributes[1]);
      case APPLICATION_APPROVED:
        if (attributes.length < 1) {
          throw new SystemNotificationException(
              "Expected 1 attribute: String organisationName | but found none.");
        }
        return this.getUserApplicationApproved((String) attributes[0]);
      case USER_TEAM_APPLICATION:
        if (attributes.length < 2) {
          throw new SystemNotificationException(
              "Expected 2 attributes: String userName, String eventName | but found none.");
        }
        return this.getTeamApplication((String) attributes[0], (String) attributes[1]);
      case TEAM_APPLICATION_APPROVED:
        return this.getTeamApplicationApproved((String) attributes[0]);
      default:
        throw new SystemNotificationException(
            "Could not find message for identifier " + messageIdentifier);
    }
  }
}
