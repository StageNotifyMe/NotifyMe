package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ILineRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LineService {
  private final EventService eventService;
  private final FacilityService facilityService;
  private final ILineRepo lineRepo;

  /**
   * Creates a line based on a createLineDto.
   *
   * @param createLineDto contains all relevant properties of a line in a JSON-friendly format.
   * @return the created line.
   */
  public Line createLine(CreateLineDto createLineDto) {
    try {
      var line = new Line(createLineDto.getNote(), createLineDto.getRequiredStaff());
      var event = eventService.getEvent(createLineDto.getEventId());
      var facility = facilityService.getFacility(createLineDto.getFacilityId());
      line = new Line(line, event, facility, new Team());
      line = lineRepo.save(line);

      return line;
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }
}
