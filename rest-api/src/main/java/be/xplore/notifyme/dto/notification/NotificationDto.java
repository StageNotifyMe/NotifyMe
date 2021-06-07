package be.xplore.notifyme.dto.notification;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.domain.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NotificationDto {

  private long id;
  private Message message;

  public NotificationDto(Notification notification) {
    this.id = notification.getId();
    this.message = notification.getMessage();
  }
}
