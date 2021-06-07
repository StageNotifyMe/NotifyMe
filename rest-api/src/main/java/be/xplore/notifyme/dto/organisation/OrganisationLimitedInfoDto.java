package be.xplore.notifyme.dto.organisation;

import be.xplore.notifyme.domain.Organisation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Send only the id and name of an organisation for basic user requests.
 */
@NoArgsConstructor
@Setter
@Getter
public class OrganisationLimitedInfoDto {

  private Long id;
  private String name;

  /**
   * Constructor that creates on organisation DTO based on an organisation.
   *
   * @param organisation to convert to dto.
   */
  public OrganisationLimitedInfoDto(Organisation organisation) {
    this.id = organisation.getId();
    this.name = organisation.getName();
  }
}
