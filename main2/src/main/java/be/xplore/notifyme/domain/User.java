package be.xplore.notifyme.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "external_user")
@Getter
@Setter
@NoArgsConstructor
public class User {

  @Id
  private String externalOidcId;
  @OneToMany(mappedBy = "organisation")
  private List<OrganisationUser> organisation;
}
