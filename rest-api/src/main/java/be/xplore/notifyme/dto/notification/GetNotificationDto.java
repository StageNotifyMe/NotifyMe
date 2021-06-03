package be.xplore.notifyme.dto.notification;

import be.xplore.notifyme.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetNotificationDto {
  private long id;
  private String sender;
  private String receiver;
  private String messageTitle;
  private String messageText;
  private String usedCommunicationStrategy;
  private String timestamp;

  /**
   * Constructor that converts a domain notification object to a json-friendly dto version.
   *
   * @param notification object to convert.
   */
  public GetNotificationDto(Notification notification) {
    this.id = notification.getId();
    this.sender = notification.getSender();
    this.receiver = notification.getReceiver().getUserName();
    this.messageTitle = notification.getMessage().getTitle();
    this.messageText = notification.getMessage().getText();
    this.usedCommunicationStrategy = notification.getUsedCommunicationStrategy();
    this.timestamp = notification.getTimestamp().toString();
  }
}
