package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Notification;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaNotification;
import be.xplore.notifyme.jparepositories.JpaNotificationRepository;
import be.xplore.notifyme.persistence.INotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaNotificationAdapter implements INotificationRepo {
  private final JpaNotificationRepository jpaNotificationRepository;

  @Override
  public Notification save(Notification notification) {
    return jpaNotificationRepository.save(new JpaNotification(notification)).toDomainBase();
  }

  @Override
  public Notification findById(long notificationId) {
    return jpaNotificationRepository.findById(notificationId).orElseThrow(
        () -> new JpaNotFoundException("Could not find notification for id " + notificationId))
        .toDomainBase();
  }
}
