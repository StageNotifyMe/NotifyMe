package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.exceptions.JpaNotFoundException;
import be.xplore.notifyme.jpaObjects.JpaFacility;
import be.xplore.notifyme.jpaRepositories.JpaFacilityRepository;
import be.xplore.notifyme.jpaRepositories.JpaVenueRepository;
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
  public List<Facility> getAllByVenue(Venue venue) {
    var jpaVenue = jpaVenueRepository.findById(venue.getId()).orElseThrow(
        () -> new JpaNotFoundException("Could not find venue for id " + venue.getId()));
    var jpaFacilities = jpaFacilityRepository.getAllByVenue(jpaVenue);
    return jpaFacilities.stream().map(JpaFacility::toDomain).collect(Collectors.toList());
  }

  @Override
  public Optional<Facility> findById(long facilityId) {
    return jpaFacilityRepository.findById(facilityId).map(JpaFacility::toDomain);
  }

  @Override
  public Facility save(Facility facility) {
    return jpaFacilityRepository.save(new JpaFacility(facility)).toDomain();
  }
}
