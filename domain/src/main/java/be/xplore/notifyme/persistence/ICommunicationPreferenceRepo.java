package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.CommunicationPreference;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommunicationPreferenceRepo {
  public CommunicationPreference save(CommunicationPreference communicationPreference);

  public CommunicationPreference findById(long communicationPreferenceId);
}
