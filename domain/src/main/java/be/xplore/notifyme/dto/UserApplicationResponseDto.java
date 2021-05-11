package be.xplore.notifyme.dto;

import be.xplore.notifyme.domain.OrganisationUserKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserApplicationResponseDto {

  private OrganisationUserKey organisationUserKey;
  private boolean accepted;
}
