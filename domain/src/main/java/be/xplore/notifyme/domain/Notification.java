package be.xplore.notifyme.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
  private Object communicationAddress;
  private CommunicationPreference communicationPreference;
  private Message message;
}
