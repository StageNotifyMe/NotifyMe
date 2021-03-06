package be.xplore.notifyme.domain;

import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunicationPreference {
  private long id;
  private User user;
  private boolean isActive = true;
  private boolean isDefault;
  private boolean isUrgent;
  private ICommunicationStrategy communicationStrategy;

  public void sendNotification(Notification notification) {
    communicationStrategy.send(notification);
  }
}
