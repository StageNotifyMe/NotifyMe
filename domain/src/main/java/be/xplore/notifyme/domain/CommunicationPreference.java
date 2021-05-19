package be.xplore.notifyme.domain;

import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
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
  private ICommunicationStrategy communicationStrategy;

  public void sendNotification(Notification notification) {
    communicationStrategy.send(notification);
  }
}
