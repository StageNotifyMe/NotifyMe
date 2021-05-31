package be.xplore.notifyme.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostOrgNotificationDto {
  private String senderId;
  private long receivingOrgId;
  private String title;
  private String text;
}
