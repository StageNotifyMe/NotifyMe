package be.xplore.notifyme.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.Address;
import be.xplore.notifyme.domain.Facility;
import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Venue;
import be.xplore.notifyme.dto.CreateFacilityDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.IFacilityRepo;
import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class FacilityServiceTest {
  @Autowired
  private FacilityService facilityService;
  @MockBean
  private IFacilityRepo facilityRepo;
  @MockBean
  @Autowired
  private VenueService venueService;

  private final CreateFacilityDto createFacilityDto =
      new CreateFacilityDto("descriptie", "locatie", 1, 10, 1L);
  private final Venue venue =
      new Venue(1L, "venue", "descriptie", new Address(), new LinkedList<>(), new LinkedList<>());

  @Test
  void createFacilitySuccessful() {
    when(venueService.getVenue(1L)).thenReturn(venue);
    when(facilityRepo.save(any(Facility.class))).thenAnswer(
        (Answer<Object>) invocation -> invocation.getArguments()[0]);

    var result = facilityService.createFacility(createFacilityDto);
    assertEquals(createFacilityDto.getDescription(), result.getDescription());
    assertEquals(createFacilityDto.getLocation(), result.getLocation());
    assertEquals(venue.getId(), result.getVenue().getId());
  }

  @Test
  void createFacilityVenueNotFound() {
    doThrow(new CrudException("Could not find venue for id 1")).when(venueService).getVenue(1L);
    when(facilityRepo.save(any(Facility.class))).thenAnswer(
        (Answer<Object>) invocation -> invocation.getArguments()[0]);

    assertThrows(CrudException.class, () -> {
      facilityService.createFacility(createFacilityDto);
    });
  }
}