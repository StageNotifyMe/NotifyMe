package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.persistence.OrganisationRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class OrganisationService {
  private final OrganisationRepo organisationRepo;

  public Organisation createOrganisation(String name){
    try {
      return organisationRepo.save(new Organisation(name));
    }catch (HibernateException e){
      log.error(e.getMessage());
      throw e;
    }
  }
}
