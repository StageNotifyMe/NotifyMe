package be.xplore.notifyme.services.implementations;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.domain.Team;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.persistence.ILineRepo;
import be.xplore.notifyme.services.ILineService;
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
    return lineRepo.getAllByEventId(eventId);
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
  public Line createLine(String note, int requiredStaff, long facilityId, long eventId,
                         Principal principal) {
    var line =
        new Line(note, requiredStaff, new Team());
    return lineRepo.create(line, eventId, facilityId);
  }

  /**
   * Gets all of the lines a member of one or multiple orgs can apply to.
   *
   * @param userId the unique id of the user.
   * @return the lines that a user can apply for to be part of a team.
   */
  @Override
  public List<Line> getAvailableLinesForUser(String userId) {
    return lineRepo.getAvailableLinesForUser(userId);
  }
}
