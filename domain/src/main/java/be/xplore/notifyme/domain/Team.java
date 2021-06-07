package be.xplore.notifyme.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {
  private long id;

  private Line line;
  private List<Organisation> organisations = new ArrayList<>();
  private Set<User> teamMembers = new HashSet<>();
  private List<TeamApplication> appliedUsers = new ArrayList<>();

}
