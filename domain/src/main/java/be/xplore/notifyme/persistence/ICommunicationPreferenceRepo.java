package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommunicationPreferenceRepo {

  CommunicationPreference save(CommunicationPreference communicationPreference);

  CommunicationPreference findById(long communicationPreferenceId);

  CommunicationPreference create(String userId, boolean isActive, boolean isDefault,
      ICommunicationStrategy strategy);

  void delete(long communicationPreferenceId);

  Optional<CommunicationPreference> getDefaultCommunicationPreference(String userId);

  List<CommunicationPreference> getAllForUser(String userId);

  CommunicationPreference makeNewdefault(CommunicationPreference communicationPreference);
}
