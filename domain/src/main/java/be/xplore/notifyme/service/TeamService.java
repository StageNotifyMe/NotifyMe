package be.xplore.notifyme.service;

import be.xplore.notifyme.persistence.ITeamRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService implements ITeamService {
  private final ITeamRepo teamRepo;

}