package be.xplore.notifyme.domain.communicationStrategies;

import be.xplore.notifyme.domain.Notification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SmsCommunicationStrategy implements ICommunicationStrategy {
  private ISmsService smsService;

  @Override
  public void send(Notification notification) {
    smsService.send(notification.getCommunicationAddress(), notification.getMessage());
  }
}
