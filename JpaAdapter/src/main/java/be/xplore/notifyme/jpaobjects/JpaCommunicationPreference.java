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

  public CommunicationPreference toDomainBase() {
    return CommunicationPreference.builder()
        .id(this.id)
        .communicationStrategy(this.communicationStrategy)
        .build();
  }

  public JpaCommunicationPreference(CommunicationPreference communicationPreference) {
    this.id = communicationPreference.getId();
    this.communicationStrategy = communicationPreference.getCommunicationStrategy();
    //this.user = new JpaUser(communicationPreference.getUser());
  }
}
