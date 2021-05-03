package be.xplore.notifyme.dto;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
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
  private List<OrganisationUser> users;

  public OrganisationDto(Organisation organisation){
    this.id=organisation.getId();
    this.name=organisation.getName();
    this.users=organisation.getUsers();
  }
}
