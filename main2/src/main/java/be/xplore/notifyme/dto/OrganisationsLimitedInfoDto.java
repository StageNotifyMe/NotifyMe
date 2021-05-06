package be.xplore.notifyme.dto;

import be.xplore.notifyme.domain.Organisation;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto for list of organisations containing limited data of organisation
 */
@NoArgsConstructor
@Getter
@Setter
public class OrganisationsLimitedInfoDto {

  private List<OrganisationLimitedInfoDto> organisations;

  public OrganisationsLimitedInfoDto(List<Organisation> organisations) {
    this.organisations = new ArrayList<>();
    for (var organisation : organisations) {
      this.organisations.add(new OrganisationLimitedInfoDto(organisation));
    }
  }
}
