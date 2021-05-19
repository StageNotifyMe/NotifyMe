package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class JpaCommunicationPreference {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Convert(converter = CommunicationStrategyConverter.class)
  private ICommunicationStrategy communicationStrategy;
  @OneToOne
  private JpaUser user;

  /**
   * Converts the jpa-version of the object to a domain version with basic attributes.
   *
   * @return domain version of the object.
   */
  public CommunicationPreference toDomainBase() {
    return CommunicationPreference.builder()
        .id(this.id)
        .communicationStrategy(this.communicationStrategy)
        .build();
  }

  /**
   * Constructor to convert domain object to jpa-version.
   *
   * @param communicationPreference domain version of the object.
   */
  public JpaCommunicationPreference(CommunicationPreference communicationPreference) {
    this.id = communicationPreference.getId();
    this.communicationStrategy = communicationPreference.getCommunicationStrategy();
  }
}
