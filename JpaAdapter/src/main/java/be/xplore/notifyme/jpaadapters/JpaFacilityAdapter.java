package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.jpaobjects.JpaFacility;
import be.xplore.notifyme.jparepositories.JpaFacilityRepository;
import be.xplore.notifyme.jparepositories.JpaVenueRepository;
import be.xplore.notifyme.persistence.IFacilityRepo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JpaFacilityAdapter implements IFacilityRepo {

  private final JpaFacilityRepository jpaFacilityRepository;
  private final JpaVenueRepository jpaVenueRepository;

  @Override
  public Optional<Facility> findById(long facilityId) {
    return jpaFacilityRepository.findById(facilityId).map(JpaFacility::toDomain);
  }

  @Override
  public Facility save(Facility facility) {
    return jpaFacilityRepository.save(new JpaFacility(facility)).toDomain();
  }

  @Override
  public Facility create(Facility facility, long venueId) {
    var jpaVenue = jpaVenueRepository.findById(venueId)
        .orElseThrow(() -> new CrudException("Could not find venue for id " + venueId));
    var jpaFacility = jpaFacilityRepository.save(new JpaFacility(facility, jpaVenue));
    return jpaFacilityRepository.save(jpaFacility).toDomainBase();
  }

  @Override
  public List<Facility> getAllByVenue(long venueId) {
    var jpaVenue = jpaVenueRepository.findById(venueId)
        .orElseThrow(() -> new CrudException("Could not get venue for Id " + venueId));
    return jpaFacilityRepository.getAllByVenue(jpaVenue).stream().map(JpaFacility::toDomainBase)
        .collect(Collectors.toList());
  }
}
