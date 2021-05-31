package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.OrgApplicationStatus;
import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaOrganisationUser;
import be.xplore.notifyme.jpaobjects.JpaUser;
import be.xplore.notifyme.jpaobjects.JpaUserOrgApplication;
import be.xplore.notifyme.jparepositories.JpaOrganisationRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaOrganisationAdapter implements IOrganisationRepo {

  private final JpaOrganisationRepository jpaOrganisationRepository;
  private final JpaUserRepository jpaUserRepository;


  @Override
  public Organisation save(Organisation organisation) {
    return jpaOrganisationRepository.save(new JpaOrganisation(organisation)).toDomainBase();
  }

  @Override
  public Organisation addUserToOrganisation(String userId, Long organisationId) {
    var jpaUser = jpaUserRepository.findById(userId).orElseThrow();
    var jpaorg = jpaOrganisationRepository.findById(organisationId).orElseThrow();
    jpaorg.getUsers().add(new JpaOrganisationUser(jpaUser, jpaorg, false));
    return jpaOrganisationRepository.save(jpaorg).toDomainBase();
  }

  @Override
  public Organisation changeApplicationStatus(String userId, Long organisationId,
                                              OrgApplicationStatus applicationStatus) {
    var jpaorg = jpaOrganisationRepository.findById(organisationId).orElseThrow();
    jpaorg.getAppliedUsers().stream()
        .filter(application -> application.getOrganisationUserKey().getUserId().equals(userId)
            && application.getOrganisationUserKey().getOrganisationId().equals(organisationId))
        .findFirst()
        .ifPresentOrElse(
            userOrgApplication -> userOrgApplication.setApplicationStatus(applicationStatus),
            () -> {
              throw new CrudException("Could not find application to set status.");
            });
    return jpaOrganisationRepository.save(jpaorg).toDomainBase();
  }

  @Override
  public List<Organisation> findAll() {
    return jpaOrganisationRepository.findAll().stream().map(JpaOrganisation::toDomainBase).collect(
        Collectors.toList());
  }

  @Override
  public Optional<Organisation> findById(Long id) {
    return jpaOrganisationRepository.findById(id).map(JpaOrganisation::toDomainBaseIncUsers);
  }

  @Override
  public Optional<Organisation> findByIdIncAppliedUsers(long orgId) {
    return jpaOrganisationRepository.findById(orgId)
        .map(JpaOrganisation::toDomainBaseIncAppliedUsers);
  }

  @Override
  public Organisation addToOrgManagers(long organisationId, String userId) {
    var jpaOrg = jpaOrganisationRepository.findById(organisationId).orElseThrow(
        () -> new CrudException("Could not find organisation for id " + organisationId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new CrudException("Could not find user for id " + userId));
    jpaOrg.getUsers().add(new JpaOrganisationUser(jpaUser, jpaOrg, true));
    return jpaOrganisationRepository.save(jpaOrg).toDomainBase();
  }

  @Override
  public void applyToOrganisation(long orgId, String userId) {
    var jpaOrg = jpaOrganisationRepository.findById(orgId)
        .orElseThrow(() -> new CrudException("Could not find org for id " + orgId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new CrudException("Could not find user for id " + userId));
    jpaUser.getAppliedOrganisations().add(new JpaUserOrgApplication(jpaOrg, jpaUser,
        OrgApplicationStatus.APPLIED));
    jpaUserRepository.save(jpaUser);
  }

  @Override
  public List<User> getAllOrganisationManagers(Long organisationId) {
    /*return jpaUserRepository.getAllOrgManagersFromOrgId(organisationId).stream()
        .map(JpaUser::toDomainBase).collect(
            Collectors.toList());*/
    var userIds = jpaUserRepository.getAllOrgManagersFromOrgId(organisationId);
    var users = new ArrayList<User>();
    for (String userId : userIds) {
      users.add(jpaUserRepository.findById(userId)
          .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + userId
          )).toDomainBase());
    }
    return users;
  }

  @Override
  public List<User> getAllOrganisationManagersForEvent(Long eventId) {
    var jpaUserIds = jpaUserRepository.findByEvent(eventId);
    var jpaUsers = new ArrayList<JpaUser>();
    for (String jpaUserId : jpaUserIds) {
      var jpaUser = jpaUserRepository.findById(jpaUserId)
          .orElseThrow(() -> new JpaNotFoundException("Could not find user for id " + jpaUserId));
      jpaUsers.add(jpaUser);
    }
    return jpaUsers.stream().map(JpaUser::toDomainBase).collect(Collectors.toList());
  }
}
