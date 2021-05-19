package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaCommunicationPreference;
import be.xplore.notifyme.jparepositories.JpaCommunicationPreferenceRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.ICommunicationPreferenceRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCommunicationPreferenceAdapter implements ICommunicationPreferenceRepo {
  private final JpaCommunicationPreferenceRepository jpaCommunicationPreferenceRepository;
  private final JpaUserRepository jpaUserRepository;

  @Override
  public CommunicationPreference save(CommunicationPreference communicationPreference) {
    return jpaCommunicationPreferenceRepository
        .save(new JpaCommunicationPreference(communicationPreference)).toDomainBase();
  }

  @Override
  public CommunicationPreference findById(long communicationPreferenceId) {
    return jpaCommunicationPreferenceRepository.findById(communicationPreferenceId).orElseThrow(
        () -> new JpaNotFoundException(
            "Could not find communicationPreference for id " + communicationPreferenceId))
        .toDomainBase();
  }

  @Override
  public CommunicationPreference create(String userId, boolean isActive, boolean isDefault,
                                        ICommunicationStrategy strategy) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    var jpaComPref = new JpaCommunicationPreference(jpaUser, isActive, isDefault, strategy);

    //removes old default if there is any
    var preferences = jpaCommunicationPreferenceRepository.findAllByUser(jpaUser);
    var currentDefault = preferences.stream()
        .filter(JpaCommunicationPreference::isDefault).findFirst();
    if (currentDefault.isPresent()) {
      var currentDefaultUpdated = currentDefault.get();
      currentDefaultUpdated.setDefault(false);
      jpaCommunicationPreferenceRepository.save(currentDefaultUpdated);
    }
    return jpaCommunicationPreferenceRepository.save(jpaComPref).toDomainBase();
  }

  @Override
  public void delete(long communicationPreferenceId) {
    var communicationPreference =
        jpaCommunicationPreferenceRepository.findById(communicationPreferenceId).orElseThrow(
            () -> new JpaNotFoundException(
                "Could not find communication preference for id " + communicationPreferenceId));
    jpaCommunicationPreferenceRepository.delete(communicationPreference);
  }

  @Override
  public Optional<CommunicationPreference> getDefaultCommunicationPreference(String userId) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    var preferences = jpaCommunicationPreferenceRepository.findAllByUser(jpaUser);
    return preferences.stream().map(JpaCommunicationPreference::toDomainBase)
        .filter(CommunicationPreference::isDefault).findFirst();
  }
}
