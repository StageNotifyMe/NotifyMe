package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.jpaRepositories.JpaOrganisationRepository;
import be.xplore.notifyme.persistence.IOrganisationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaOrganisationAdapter implements IOrganisationRepo {

  private final JpaOrganisationRepository jpaOrganisationRepository;
}
