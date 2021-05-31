package be.xplore.notifyme.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.keycloak.representations.account.UserRepresentation;

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
  private String sender;
  private boolean hidden;

  public void send() {
    this.getCommunicationPreference().getCommunicationStrategy().send(this);
  }

  /**
   * Uses instance of User in this object in combination with a UserRepresentation object to set the
   * communicationaddress and used strategy.
   *
   * @param userInfo UserRepresentation object.
   */
  public void setCommunicationAddresAndUsedStrategy(UserRepresentation userInfo) {
    this.setCommunicationAddress(this.getCommunicationPreference().getCommunicationStrategy()
        .getCommunicationAddress(userInfo));
    this.setUsedCommunicationStrategy(
        this.getCommunicationPreference().getCommunicationStrategy().toString());
  }
}
