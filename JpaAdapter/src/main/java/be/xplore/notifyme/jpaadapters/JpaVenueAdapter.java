package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.jpaobjects.JpaUser;
import be.xplore.notifyme.jpaobjects.JpaVenue;
import be.xplore.notifyme.jparepositories.JpaUserRepository;
import be.xplore.notifyme.jparepositories.JpaVenueRepository;
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
  private final JpaUserRepository jpaUserRepository;

  @Override
  public Venue save(Venue venue) {
    return jpaVenueRepository.save(new JpaVenue(venue)).toDomain();
  }

  @Override
  public Optional<Venue> findById(long venueId) {
    return jpaVenueRepository.findById(venueId).map(JpaVenue::toDomainBase);
  }

  @Override
  public List<Venue> getAllByManagersIsContaining(String userId) {
    var jpaUser = jpaUserRepository.findById(userId).orElseThrow();
    var jpaVenues = jpaVenueRepository.getAllByManagersIsContaining(jpaUser);
    return jpaVenues.stream().map(JpaVenue::toDomainBase).collect(Collectors.toList());
  }

  @Override
  public Venue addVenueManager(long venueId, String userId) {
    var jpaVenue = jpaVenueRepository.findById(venueId).orElseThrow();
    var jpaUser = jpaUserRepository.findById(userId).orElseThrow();
    jpaVenue.getManagers().add(jpaUser);
    var returnVen = jpaVenueRepository.save(jpaVenue);
    return returnVen.toDomainBase();
  }

  @Override
  public List<User> getAllVenueManagers(long venueId) {
    var jpaVenue = jpaVenueRepository.findById(venueId).orElseThrow();
    return jpaVenue.getManagers().stream().map(JpaUser::toDomainBase).collect(Collectors.toList());
  }
}
