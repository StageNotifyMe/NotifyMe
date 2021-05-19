package be.xplore.notifyme.domain;

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
public class Notification {
  private long id;
  private String communicationAddress;
  private CommunicationPreference communicationPreference;
  private String usedCommunicationStrategy;
  private Message message;
  private User receiver;

  public void send() {
    this.getCommunicationPreference().getCommunicationStrategy().send(this);
  }
}
