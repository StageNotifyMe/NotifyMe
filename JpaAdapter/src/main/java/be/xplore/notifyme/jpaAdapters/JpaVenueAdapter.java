package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.jpaObjects.JpaVenue;
import be.xplore.notifyme.jpaRepositories.JpaVenueRepository;
import be.xplore.notifyme.persistence.IVenueRepo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaVenueAdapter implements IVenueRepo {
  private final JpaVenueRepository jpaVenueRepository;

  @Override
  public Venue save(Venue venue) {
    return jpaVenueRepository.save(new JpaVenue(venue)).toDomain();
  }

  @Override
  public Optional<Venue> findById(long venueId) {
    return jpaVenueRepository.findById(venueId).map(JpaVenue::toDomain);
  }

  @Override
  public List<Venue> getAllByManagersIsContaining(User user) {
    var jpaVenues = jpaVenueRepository.getAllByManagersIsContaining(user);
    return jpaVenues.stream().map(JpaVenue::toDomain).collect(Collectors.toList());
  }
}
