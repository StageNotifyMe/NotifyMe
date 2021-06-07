package be.xplore.notifyme.dto.organisation;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OrganisationDto {

  private Long id;
  private String name;
  @JsonIgnoreProperties("organisations")
  private List<OrganisationUser> users;

  /**
   * Constructor that creates on organisation DTO based on an organisation.
   * @param organisation to convert to dto.
   */
  public OrganisationDto(Organisation organisation) {
    this.id = organisation.getId();
    this.name = organisation.getName();
    this.users = organisation.getUsers();
  }
}
