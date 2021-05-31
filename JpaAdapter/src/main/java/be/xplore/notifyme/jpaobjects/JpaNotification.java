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
  private String sender;
  boolean hidden;

  /**
   * Constructor used to create a new notification and wire message, user and
   * communicationPreference correctly.
   *
   * @param jpaMessage jpa version of the message.
   * @param jpaUser    jpa version of the receiver.
   */
  public JpaNotification(JpaMessage jpaMessage, JpaUser jpaUser,
      JpaCommunicationPreference jpaCommunicationPreference) {
    this.message = jpaMessage;
    this.receiver = jpaUser;
    this.communicationPreference = jpaCommunicationPreference;
    this.usedCommunicationStrategy =
        jpaCommunicationPreference.getCommunicationStrategy().toString();
  }

  /**
   * Constructor used for updates on an existing object.
   *
   * @param notification the domain version of the object to save.
   * @param jpaUser      already converted version of notification.getReceiver.
   */
  public JpaNotification(Notification notification, JpaUser jpaUser,
      JpaCommunicationPreference jpaCommunicationPreference) {
    this.id = notification.getId();
    this.communicationAddress = notification.getCommunicationAddress();
    this.communicationPreference = jpaCommunicationPreference;
    this.usedCommunicationStrategy = notification.getUsedCommunicationStrategy();
    this.message = new JpaMessage(notification.getMessage());
    this.receiver = jpaUser;
    this.sender = notification.getSender();
  }

  /**
   * Converts jpa-object to domain version with basic attributes.
   *
   * @return domain version object.
   */
  public Notification toDomainBase() {
    return Notification.builder()
        .id(this.id)
        .communicationAddress(this.communicationAddress)
        .usedCommunicationStrategy(this.usedCommunicationStrategy)
        .receiver(this.receiver.toDomainBase())
        .communicationPreference(this.communicationPreference.toDomainBase())
        .message(this.message.toDomainBase())
        .sender(this.sender)
        .build();
  }

  /**
   * Contructor to convert domain object to jpa-version.
   *
   * @param notification domain object.
   */
  public JpaNotification(Notification notification) {
    this.id = notification.getId();
    this.communicationAddress = notification.getCommunicationAddress();
    this.communicationPreference =
        new JpaCommunicationPreference(notification.getCommunicationPreference());
    this.usedCommunicationStrategy = notification.getUsedCommunicationStrategy();
    this.message = new JpaMessage(notification.getMessage());
    this.receiver = new JpaUser(notification.getReceiver());
    this.sender = notification.getSender();
  }
}
