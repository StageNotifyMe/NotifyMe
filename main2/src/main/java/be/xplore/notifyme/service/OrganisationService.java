package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that handles Organisation CRUD logic.
 */
@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class OrganisationService {

  private final IOrganisationRepo organisationRepo;
  private final UserService userService;

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

  /**
   * Gets all of the organisations.
   *
   * @return a list of organisations.
   */
  public List<Organisation> getOrganistions() {
    try {
      return organisationRepo.findAll();
    } catch (RuntimeException e) {
      log.error(e.getMessage());
      throw new CrudException("Could not get a list of organisations.");
    }
  }

  /**
   * Gets an organisation by id.
   *
   * @param id of the organisation to get.
   * @return the Organisation object.
   */
  public Organisation getOrganisation(Long id) {
    try {
      var organisation = organisationRepo.findById(id);
      if (organisation.isPresent()) {
        return organisation.get();
      }
      throw new CrudException("Organisation with id " + id + " does not exist in db");
    } catch (RuntimeException ex) {
      log.error(ex.getMessage());
      throw new CrudException("Could not get organisation with Id: " + id);
    }
  }

  /**
   * Promotes a user to an organisation manager.
   *
   * @param userId the id of the user to be promoted.
   * @param orgId  the id of the organisation to promote the user in.
   */
  public Organisation promoteUserToOrgManager(String userId, Long orgId) {
    try {
      var organisation = getOrganisation(orgId);
      organisation.getUsers()
          .add(new OrganisationUser(organisation, userService.getUser(userId), true));
      return organisationRepo.save(organisation);
    } catch (RuntimeException e) {
      log.error(e.getMessage());
      throw new CrudException("Could not promote user to organisation manager.");
    }
  }
}
