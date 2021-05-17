package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.dto.CreateLineDto;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ILineRepo;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LineService implements ILineService {
  private final EventService eventService;
  private final FacilityService facilityService;
  private final ILineRepo lineRepo;

  /**
   * Gets all lines belonging to an event.
   *
   * @param eventId id of an event.
   * @return list of lines.
   */
  @Override
  public List<Line> getAllLinesByEvent(long eventId) {
    return eventService.getEvent(eventId).getLines();
  }

  /**
   * Gets a line for a line ID.
   *
   * @param lineId id of the line to get.
   * @return line object or CrudException if not found.
   */
  @Override
  public Line getLine(long lineId) {
    return lineRepo.findById(lineId)
        .orElseThrow(() -> new CrudException("Could not find line for id " + lineId));
  }

  /**
   * Creates a line based on a createLineDto.
   *
   * @param createLineDto contains all relevant properties of a line in a JSON-friendly format.
   * @return the created line.
   */
  @Override
  public Line createLine(CreateLineDto createLineDto, Principal principal) {
    var event = eventService
        .getEventAndVerifyLineManagerPermission(createLineDto.getEventId(), principal);
    var facility = facilityService.getFacility(createLineDto.getFacilityId());
    var line =
        new Line(createLineDto.getNote(), createLineDto.getRequiredStaff(), event, facility,
            new Team());
    return lineRepo.save(line);

  }
}
