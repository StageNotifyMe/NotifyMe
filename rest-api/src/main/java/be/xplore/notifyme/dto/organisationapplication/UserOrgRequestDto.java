package be.xplore.notifyme.dto.organisationapplication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOrgRequestDto {

  private String username;
  private Long organisationId;

}
