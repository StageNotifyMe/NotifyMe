package be.xplore.notifyme.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutTeamDto {
  private long teamId;
  private long organisationId;
  private String userId;
}
