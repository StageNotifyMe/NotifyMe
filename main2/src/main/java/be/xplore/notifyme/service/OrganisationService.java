package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that handles Organisation CRUD logic.
 */
@Service
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class OrganisationService {

  private final IOrganisationRepo organisationRepo;

  /**
   * Constructs an organisation and saves it in the organisationRepository.
   *
   * @param name the unique name of the organisation
   * @return the newly created organisation with this name.
   */
  public Organisation createOrganisation(String name) {
    try {
      return organisationRepo.save(new Organisation(name));
    } catch (RuntimeException e) {
      log.error(e.getMessage());
      throw new CrudException(
          "Could not create new organisation. Make sure that the name does not exist.");
    }
  }
}
