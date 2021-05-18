package be.xplore.notifyme.domain;

import be.xplore.notifyme.domain.communicationStrategies.ICommunicationStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunicationPreference {
  private ICommunicationStrategy communicationStrategy;

  public void sendNotification(Notification notification) {
    communicationStrategy.send(notification);
  }
}
