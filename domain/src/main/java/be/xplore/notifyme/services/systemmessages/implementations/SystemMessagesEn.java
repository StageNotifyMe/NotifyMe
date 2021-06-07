package be.xplore.notifyme.services.systemmessages.implementations;

import be.xplore.notifyme.domain.AvailableLanguages;
import be.xplore.notifyme.domain.Event;
import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.services.systemmessages.ILanguagePreferenceService;
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

  private String userTeamApplicationTitle;
  private String userTeamApplicationText;

  private String teamApplicationApprovedTitle;
  private String teamApplicationApprovedText;

  private String userAttendanceCancelledTitle;
  private String userAttendanceCancelledText;

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

  @Override
  public Message getTeamApplication(String username, String eventName) {
    var text = String.format(userTeamApplicationText, username, eventName);
    return new Message(userTeamApplicationTitle, text);
  }

  @Override
  public Message getTeamApplicationApproved(String eventName) {
    var text = String.format(teamApplicationApprovedText, eventName);
    return new Message(teamApplicationApprovedTitle, text);
  }

  @Override
  public Message getUserAttendanceCancelled(String username, String eventname) {
    var text = String.format(userAttendanceCancelledText, username, eventname);
    return new Message(userAttendanceCancelledTitle, text);
  }
}
