package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.OrganisationUser;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

/**
 * Service that handles Organisation CRUD logic.
 */
@Service
@AllArgsConstructor
@Slf4j
//@Transactional
public class OrganisationService implements IOrganisationService {

  private final IOrganisationRepo organisationRepo;
  private final UserService userService;

  /**
   * Constructs an organisation and saves it in the organisationRepository.
   *
   * @param name the unique name of the organisation
   * @return the newly created organisation with this name.
   */
  @Override
  public Organisation createOrganisation(String name) {
    return organisationRepo.save(new Organisation(name));
  }

  /**
   * Saves an organisation object with updated data.
   *
   * @param organisation the organisation to update or create.
   * @return the newly created organisation with this name.
   */
  @Override
  public Organisation save(Organisation organisation) {
    try {
      return organisationRepo.save(organisation);
    } catch (RuntimeException e) {
      throw new CrudException(
          "Could not create new organisation. Make sure that the name does not exist.");
    }
  }

  /**
   * Gets all of the organisations.
   *
   * @return a list of organisations.
   */
  @Override
  public List<Organisation> getOrganisations() {
    return organisationRepo.findAll();
  }

  /**
   * Gets an organisation by id.
   *
   * @param id of the organisation to get.
   * @return the Organisation object.
   */
  @Override
  public Organisation getOrganisation(Long id) {
    return organisationRepo.findById(id).orElseThrow(
        () -> new CrudException("Organisation with id " + id + " does not exist in db"));
  }

  /**
   * Promotes a user to an organisation manager.
   *
   * @param username the username of the user to be promoted.
   * @param orgId    the id of the organisation to promote the user in.
   */
  @Override
  public Organisation promoteUserToOrgManager(String username, Long orgId, Principal principal) {
    var user = userService.getUser(userService.getUserInfo(username, principal).getId());
    var organisation = getOrganisation(orgId);
    organisation.getUsers()
        .add(new OrganisationUser(organisation, user, true));

    return organisationRepo
        .addToOrgManagers(orgId, userService.getUserInfo(username, principal).getId());
  }
}
