package be.xplore.notifyme.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PostCommunicationPreferenceDto {
  private String userId;
  private Boolean isActive;
  private Boolean isDefault;
  private String communicationStrategy;

  public PostCommunicationPreferenceDto(String userId, boolean isActive, boolean isDefault,
                                        String communicationStrategy) {
    this.userId = userId;
    this.isActive = isActive;
    this.isDefault = isDefault;
    this.communicationStrategy = communicationStrategy;
  }

  public boolean isActive() {
    return this.isActive;
  }

  public boolean isDefault() {
    return this.isDefault;
  }
}
