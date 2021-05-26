package be.xplore.notifyme.dto.communicationpreference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCommunicationPreferenceDto {

  private long communicationPreferenceId;
  private Boolean isActive;
  private Boolean isDefault;
  private Boolean isUrgent;

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
