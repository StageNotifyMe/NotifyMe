package be.xplore.notifyme.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity(name = "external_user")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private String userId;
  private String userName;

  @JsonManagedReference
  private List<OrganisationUser> organisations;
  @JsonManagedReference
  private List<UserOrgApplication> appliedOrganisations;
  private List<Venue> venues;
  private Set<Team> teams;
  private List<Event> events;

}
