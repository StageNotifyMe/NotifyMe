package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.dto.CreateLineDto;
import java.security.Principal;
import java.util.List;

public interface ILineService {

  List<Line> getAllLinesByEvent(long eventId);

  Line getLine(long lineId);

  Line createLine(CreateLineDto createLineDto, Principal principal);
}
