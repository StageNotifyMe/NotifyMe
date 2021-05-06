package be.xplore.notifyme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GiveClientRoleDto {
  private String id;
  private String name;
  private Boolean clientRole;
}
