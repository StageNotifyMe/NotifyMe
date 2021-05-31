package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaCommunicationPreference;
import be.xplore.notifyme.jpaobjects.JpaNotification;
import be.xplore.notifyme.jpaobjects.JpaUser;
import be.xplore.notifyme.jparepositories.JpaCommunicationPreferenceRepository;
import be.xplore.notifyme.jparepositories.JpaMessageRepository;
import be.xplore.notifyme.jparepositories.JpaNotificationRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.INotificationRepo;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaNotificationAdapter implements INotificationRepo {

  private final JpaNotificationRepository jpaNotificationRepository;
  private final JpaMessageRepository jpaMessageRepository;
  private final JpaUserRepository jpaUserRepository;
  private final JpaCommunicationPreferenceRepository jpaCommunicationPreferenceRepository;

  private static final String DEFAULT_COMPREF_NOT_FOUND_MESSAGE =
      "Could not find default communication preference";

  @Override
  public Notification save(Notification notification) {
    if (notification.getReceiver() != null) {
      var jpaUser = jpaUserRepository.findById(notification.getReceiver().getUserId()).orElseThrow(
          (() -> new JpaNotFoundException(
              "Could not find user for id " + notification.getReceiver().getUserId())));
      var defaultComPref = jpaCommunicationPreferenceRepository.findAllByUser(jpaUser).stream()
          .filter(JpaCommunicationPreference::isDefault).findFirst().orElseThrow(
              () -> new JpaNotFoundException(DEFAULT_COMPREF_NOT_FOUND_MESSAGE));
      return jpaNotificationRepository
          .save(new JpaNotification(notification, jpaUser, defaultComPref))
          .toDomainBase();
    }
    return jpaNotificationRepository.save(new JpaNotification(notification)).toDomainBase();
  }

  @Override
  public Notification findById(long notificationId) {
    return jpaNotificationRepository.findById(notificationId).orElseThrow(
        () -> new JpaNotFoundException("Could not find notification for id " + notificationId))
        .toDomainBase();
  }

  @Override
  public List<Notification> findByUser(String userId) {
    return jpaNotificationRepository.findByReceiver_UserIdAndHiddenIsFalse(userId).stream()
        .map(JpaNotification::toDomainBase).collect(Collectors.toList());
  }

  @Override
  public Notification create(long messageId, String userId) {
    return createNotification(messageId, userId, false, null);
  }

  @Override
  public Notification create(long messageId, String userId, String sender) {
    return createNotification(messageId, userId, false, sender);
  }

  @Override
  public Notification createUrgent(long messageId, String userId) {
    return createNotification(messageId, userId, true, null);
  }

  /**
   * Creates a notification that can be either urgent or default.
   *
   * @return created notification.
   */
  private Notification createNotification(long messageId, String userId, boolean isUrgent,
                                          String sender) {
    var jpaMessage = jpaMessageRepository.findById(messageId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find message for id " + messageId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    var defaultComPref = getCommunicationStrategyForUseCase(isUrgent, jpaUser);
    var notification = new JpaNotification(jpaMessage, jpaUser, defaultComPref);
    notification.setSender(Objects.requireNonNullElse(sender, "SYSTEM"));
    return jpaNotificationRepository.save(notification).toDomainBase();
  }

  /**
   * Gets communication strategy based on if notification is urgent or not.
   *
   * @return Corresponding com strategy.
   */
  private JpaCommunicationPreference getCommunicationStrategyForUseCase(boolean isUrgent,
                                                                        JpaUser jpaUser) {
    if (isUrgent) {
      return jpaCommunicationPreferenceRepository.findAllByUser(jpaUser).stream()
          .filter(JpaCommunicationPreference::isUrgent).findFirst().orElseThrow(
              () -> new JpaNotFoundException(DEFAULT_COMPREF_NOT_FOUND_MESSAGE));
    }
    return jpaCommunicationPreferenceRepository.findAllByUser(jpaUser).stream()
        .filter(JpaCommunicationPreference::isDefault).findFirst().orElseThrow(
            () -> new JpaNotFoundException(DEFAULT_COMPREF_NOT_FOUND_MESSAGE));
  }
}

