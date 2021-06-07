package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Line;
import java.security.Principal;
import java.util.List;

public interface ILineService {

  List<Line> getAllLinesByEvent(long eventId);

  Line getLine(long lineId);

  Line createLine(String note, int requiredStaff, long facilityId, long eventId,
                  Principal principal);

  List<Line> getAvailableLinesForUser(String userId);
}
