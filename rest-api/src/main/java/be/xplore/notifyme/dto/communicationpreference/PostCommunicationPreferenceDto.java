package be.xplore.notifyme.dto.communicationpreference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostCommunicationPreferenceDto {

  private String userId;
  private Boolean isActive;
  private Boolean isDefault;
  private Boolean isUrgent;
  private String communicationStrategy;

  public boolean isActive() {
    return this.isActive;
  }

  public boolean isDefault() {
    return this.isDefault;
  }

  public boolean isUrgent() {
    return this.isUrgent;
  }
}
