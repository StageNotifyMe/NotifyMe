package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.dto.GetLineDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ILineRepo;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
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
   * Gets all lines belonging to an event.
   *
   * @param eventId id of an event.
   * @return list of lines.
   */
  public List<GetLineDto> getAllLinesByEvent(long eventId) {
    try {
      var event = eventService.getEvent(eventId);
      var lines = lineRepo.getAllByEvent(event);
      var dtoLines = new LinkedList<GetLineDto>();
      for (Line line : lines) {
        dtoLines.add(new GetLineDto(line));
      }
      return dtoLines;
    } catch (CrudException e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  /**
   * Gets a line for a line ID.
   *
   * @param lineId id of the line to get.
   * @return line object or CrudException if not found.
   */
  public Line getLine(long lineId) {
    var line = lineRepo.findById(lineId);
    if (line.isPresent()) {
      return line.get();
    }
    throw new CrudException("Could not find line for id " + lineId);
  }

  /**
   * Creates a line based on a createLineDto.
   *
   * @param createLineDto contains all relevant properties of a line in a JSON-friendly format.
   * @return the created line.
   */
  public Line createLine(CreateLineDto createLineDto, Principal principal) {
    try {
      var line = new Line(createLineDto.getNote(), createLineDto.getRequiredStaff());
      var event = eventService
          .getEventAndVerifyLineManagerPermission(createLineDto.getEventId(), principal);
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
