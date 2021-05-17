package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.jpaobjects.JpaOrganisation;
import be.xplore.notifyme.jpaobjects.JpaOrganisationUser;
import be.xplore.notifyme.jparepositories.JpaOrganisationRepository;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.persistence.IOrganisationRepo;
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
    return jpaOrganisationRepository.save(new JpaOrganisation(organisation)).toDomain();
  }

  @Override
  public List<Organisation> findAll() {
    return jpaOrganisationRepository.findAll().stream().map(JpaOrganisation::toDomainBase).collect(
        Collectors.toList());
  }

  @Override
  public Optional<Organisation> findById(Long id) {
    return jpaOrganisationRepository.findById(id).map(JpaOrganisation::toDomain);
  }

  @Override
  public Organisation addToOrgManagers(long organisationId, String userId) {
    var jpaOrg = jpaOrganisationRepository.findById(organisationId).orElseThrow(
        () -> new CrudException("Could not find organisation for id " + organisationId));
    var jpaUser = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new CrudException("Could not find user for id " + userId));
    jpaOrg.getUsers().add(new JpaOrganisationUser(jpaUser, true));
    return jpaOrganisationRepository.save(jpaOrg).toDomainBase();
  }
}
