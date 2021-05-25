package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that handles Organisation CRUD logic.
 */
@Service
@AllArgsConstructor
@Slf4j
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

  @Override
  public Organisation getOrganisationIncAppliedUsers(long orgId) {
    return organisationRepo.findByIdIncAppliedUsers(orgId).orElseThrow(
        () -> new CrudException("Organisation with id " + orgId + " does not exist in db"));
  }

  /**
   * Promotes a user to an organisation manager.
   *
   * @param username the username of the user to be promoted.
   * @param orgId    the id of the organisation to promote the user in.
   */
  @Override
  public Organisation promoteUserToOrgManager(String username, Long orgId, Principal principal) {
    var user = userService.getUserInfo(username, principal);
    userService.grantUserRole(user.getId(), "organisation_manager");
    return organisationRepo
        .addToOrgManagers(orgId, user.getId());
  }

  /**
   * Add a user to an organisation.
   *
   * @param userId        unique user Id.
   * @param oranisationId unique organisation id.
   * @return the updated organisation.
   */
  @Override
  public Organisation addUserToOrganisation(String userId, Long oranisationId) {
    return organisationRepo.addUserToOrganisation(userId, oranisationId);
  }

  /**
   * Changes the application status.
   *
   * @param userId            unique user id.
   * @param oranisationId     unique organisation id.
   * @param applicationStatus status to set.
   * @return organisation related to the application.
   */
  @Override
  public Organisation changeApplicationStatus(String userId, Long oranisationId,
      OrgApplicationStatus applicationStatus) {
    return organisationRepo.changeApplicationStatus(userId, oranisationId, applicationStatus);
  }
}
