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

  @Override
  public AvailableLanguages getLanguagePreference() {
    return AvailableLanguages.EN;
  }

  @Override
  public Message getCancelEvent(Event event) {
    var text = String.format(cancelEventText, event.getId(), event.getTitle(), event.getDescription(),
        event.getArtist(), event.getDateTime().toString());
    var title = String.format(cancelEventTitle, event.getTitle());
    return new Message(title, text);
  }
}
