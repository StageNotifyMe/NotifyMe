package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Notification;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaNotification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String communicationAddress;
  @ManyToOne(cascade = CascadeType.ALL)
  private JpaCommunicationPreference communicationPreference;
  private String usedCommunicationStrategy;
  @ManyToOne(cascade = CascadeType.ALL)
  private JpaMessage message;
  @OneToOne(cascade = CascadeType.ALL)
  private JpaUser receiver;

  public Notification toDomainBase() {
    return Notification.builder()
        .id(this.id)
        .communicationAddress(this.communicationAddress)
        .usedCommunicationStrategy(this.usedCommunicationStrategy)
        .receiver(this.receiver.toDomainBase())
        .build();
  }

  public JpaNotification(Notification notification) {
    this.id = notification.getId();
    this.communicationAddress = notification.getCommunicationAddress();
    this.communicationPreference =
        new JpaCommunicationPreference(notification.getCommunicationPreference());
    this.usedCommunicationStrategy = notification.getUsedCommunicationStrategy();
    this.message = new JpaMessage(notification.getMessage());
    this.receiver = new JpaUser(notification.getReceiver());
  }
}
