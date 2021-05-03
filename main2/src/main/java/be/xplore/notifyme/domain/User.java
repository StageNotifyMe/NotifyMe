package be.xplore.notifyme.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
  private String userId;
  @OneToMany(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  private List<OrganisationUser> organisation;
}
