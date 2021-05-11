package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.domain.Organisation;
import be.xplore.notifyme.jpaObjects.JpaOrganisation;
import be.xplore.notifyme.jpaRepositories.JpaOrganisationRepository;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaOrganisationAdapter implements IOrganisationRepo {

  private final JpaOrganisationRepository jpaOrganisationRepository;


  @Override
  public Organisation save(Organisation organisation) {
    return jpaOrganisationRepository.save(new JpaOrganisation(organisation)).toDomain();
  }

  @Override
  public List<Organisation> findAll() {
    return jpaOrganisationRepository.findAll().stream().map(JpaOrganisation::toDomain).collect(
        Collectors.toList());
  }

  @Override
  public Optional<Organisation> findById(Long id) {
    return jpaOrganisationRepository.findById(id).map(JpaOrganisation::toDomain);
  }
}
