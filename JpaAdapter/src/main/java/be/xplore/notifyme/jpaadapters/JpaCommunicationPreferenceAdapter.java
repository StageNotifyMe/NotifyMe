package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaCommunicationPreference;
import be.xplore.notifyme.jparepositories.JpaCommunicationPreferenceRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.ICommunicationPreferenceRepo;
import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCommunicationPreferenceAdapter implements ICommunicationPreferenceRepo {

  private final JpaCommunicationPreferenceRepository jpaCommunicationPreferenceRepository;
  private final JpaUserRepository jpaUserRepository;

  @Override
  public CommunicationPreference save(CommunicationPreference communicationPreference) {
    if (communicationPreference.getUser().getUserId() != null) {
      var jpaUser =
          jpaUserRepository.findById(communicationPreference.getUser().getUserId()).orElseThrow(
              () -> new JpaNotFoundException(
                  "Could not find user for id " + communicationPreference.getUser().getUserId()));
      return jpaCommunicationPreferenceRepository
          .save(new JpaCommunicationPreference(communicationPreference, jpaUser)).toDomainBase();
    }
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
      boolean isUrgent,
      ICommunicationStrategy strategy) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    var jpaComPref = new JpaCommunicationPreference(jpaUser, isActive, isDefault, isUrgent,
        strategy);

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
    if (communicationPreference.isDefault()) {
      throw new ValidationException("Cannot delete default communication method");
    }
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

  @Override
  public List<CommunicationPreference> getAllForUser(String userId) {
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    return jpaCommunicationPreferenceRepository.findAllByUser(jpaUser).stream()
        .map(JpaCommunicationPreference::toDomainBase).collect(Collectors.toList());
  }

  @Override
  public CommunicationPreference makeNewdefault(CommunicationPreference communicationPreference) {
    var currentDefault = jpaCommunicationPreferenceRepository
        .findAllByUser_UserId(communicationPreference.getUser().getUserId()).stream()
        .filter(JpaCommunicationPreference::isDefault).findFirst()
        .orElseThrow(() -> new JpaNotFoundException(
            "Could not find default communication preference for user with id "
                + communicationPreference.getUser().getUserId()));
    currentDefault.setDefault(false);
    var newDefault = jpaCommunicationPreferenceRepository.findById(communicationPreference.getId())
        .orElseThrow(() -> new JpaNotFoundException(
            "Could not find communication preference for id " + communicationPreference.getId()));
    newDefault.setDefault(true);
    newDefault.setActive(true);
    jpaCommunicationPreferenceRepository.save(currentDefault);
    return jpaCommunicationPreferenceRepository.save(newDefault).toDomainBase();
  }
}
