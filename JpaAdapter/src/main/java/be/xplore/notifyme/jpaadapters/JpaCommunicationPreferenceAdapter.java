package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaCommunicationPreference;
import be.xplore.notifyme.jparepositories.JpaCommunicationPreferenceRepository;
import be.xplore.notifyme.persistence.ICommunicationPreferenceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCommunicationPreferenceAdapter implements ICommunicationPreferenceRepo {
  private final JpaCommunicationPreferenceRepository jpaCommunicationPreferenceRepository;

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
}
