package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.services.conversion.CommunicationStrategyConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaCommunicationPreference {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private boolean isActive;
  private boolean isDefault;
  @Convert(converter = CommunicationStrategyConverter.class)
  private ICommunicationStrategy communicationStrategy;
  @ManyToOne
  private JpaUser user;

  /**
   * Creates a jpa Com preference.
   *
   * @param jpaUser   jpa representation of the user.
   * @param isActive  is the prefence active.
   * @param isDefault is the preference the default preference?
   * @param strategy  the strategy to use to send notifications for this preference.
   */
  public JpaCommunicationPreference(JpaUser jpaUser, boolean isActive, boolean isDefault,
                                    ICommunicationStrategy strategy) {
    this.user = jpaUser;
    this.isDefault = isDefault;
    this.isActive = isActive;
    this.communicationStrategy = strategy;
  }

  /**
   * Converts the jpa-version of the object to a domain version with basic attributes.
   *
   * @return domain version of the object.
   */
  public CommunicationPreference toDomainBase() {
    return CommunicationPreference.builder()
        .id(this.id)
        .isActive(this.isActive)
        .isDefault(this.isDefault)
        .communicationStrategy(this.communicationStrategy)
        .user(this.user.toDomainBase())
        .build();
  }

  /**
   * Constructor to convert domain object to jpa-version.
   *
   * @param communicationPreference domain version of the object.
   */
  public JpaCommunicationPreference(CommunicationPreference communicationPreference) {
    this.id = communicationPreference.getId();
    this.isActive = communicationPreference.isActive();
    this.isDefault = communicationPreference.isDefault();
    this.communicationStrategy = communicationPreference.getCommunicationStrategy();
    this.user = new JpaUser(communicationPreference.getUser());
  }

  /**
   * Constructor used for updates.
   *
   * @param communicationPreference updated preferences.
   * @param jpaUser                 complete JpaUser object.
   */
  public JpaCommunicationPreference(CommunicationPreference communicationPreference,
                                    JpaUser jpaUser) {
    this.id = communicationPreference.getId();
    this.isActive = communicationPreference.isActive();
    this.isDefault = communicationPreference.isDefault();
    this.communicationStrategy = communicationPreference.getCommunicationStrategy();
    this.user = jpaUser;
  }
}
