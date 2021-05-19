package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaNotification;
import be.xplore.notifyme.jparepositories.JpaMessageRepository;
import be.xplore.notifyme.jparepositories.JpaNotificationRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.INotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaNotificationAdapter implements INotificationRepo {
  private final JpaNotificationRepository jpaNotificationRepository;
  private final JpaMessageRepository jpaMessageRepository;
  private final JpaUserRepository jpaUserRepository;

  @Override
  public Notification save(Notification notification) {
    if (notification.getReceiver() != null) {
      var jpaUser = jpaUserRepository.findById(notification.getReceiver().getUserId()).orElseThrow(
          (() -> new JpaNotFoundException(
              "Could not find user for id " + notification.getReceiver().getUserId())));
      return jpaNotificationRepository.save(new JpaNotification(notification, jpaUser))
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
  public Notification create(long messageId, String userId) {
    var jpaMessage = jpaMessageRepository.findById(messageId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find message for id " + messageId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId));
    var notification = new JpaNotification(jpaMessage, jpaUser);
    return jpaNotificationRepository.save(notification).toDomainBase();
  }
}
