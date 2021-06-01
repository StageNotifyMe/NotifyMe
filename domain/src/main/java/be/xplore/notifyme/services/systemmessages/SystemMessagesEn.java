package be.xplore.notifyme.services.systemmessages;

import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "notification.en")
public class SystemMessagesEn implements ILanguagePreferenceService {
  private String cancelEventTitle;
  private String cancelEventText;

  private String userApplicationTitle;
  private String userApplicationText;
  private String userApplicationApprovedTitle;
  private String userApplicationApprovedText;

  @Override
  public AvailableLanguages getLanguagePreference() {
    return AvailableLanguages.EN;
  }

  @Override
  public Message getCancelEvent(Event event) {
    var text =
        String.format(cancelEventText, event.getId(), event.getTitle(), event.getDescription(),
            event.getArtist(), event.getDateTime().toString());
    var title = String.format(cancelEventTitle, event.getTitle());
    return new Message(title, text);
  }

  @Override
  public Message getUserApplication(String userName, String organisationName) {
    var text = String.format(userApplicationText, userName, organisationName);
    return new Message(userApplicationTitle, text);
  }

  @Override
  public Message getUserApplicationApproved(String organisationName) {
    var text = String.format(userApplicationApprovedText, organisationName);
    return new Message(userApplicationApprovedTitle, text);
  }
}
