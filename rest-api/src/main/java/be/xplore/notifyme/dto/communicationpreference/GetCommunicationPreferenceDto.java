package be.xplore.notifyme.dto.communicationpreference;

import be.xplore.notifyme.domain.CommunicationPreference;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetCommunicationPreferenceDto {
  private long id;
  private String communicationStrategy;
  private boolean active;
  @JsonAlias("default")
  private boolean defaultt;

  public GetCommunicationPreferenceDto(CommunicationPreference communicationPreference) {
    this.id = communicationPreference.getId();
    this.communicationStrategy =
        communicationPreference.getCommunicationStrategy().getName();
    this.active = communicationPreference.isActive();
    this.defaultt = communicationPreference.isDefault();
  }
}
