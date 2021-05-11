package be.xplore.notifyme.jpaAdapters;

import be.xplore.notifyme.domain.Line;
import be.xplore.notifyme.jpaObjects.JpaLine;
import be.xplore.notifyme.jpaRepositories.JpaLineRepository;
import be.xplore.notifyme.persistence.ILineRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaLineAdapter implements ILineRepo {

  private final JpaLineRepository jpaLineRepository;

  @Override
  public Optional<Line> findById(long lineId) {
    return jpaLineRepository.findById(lineId).map(JpaLine::toDomain);
  }

  @Override
  public Line save(Line line) {
    return jpaLineRepository.save(new JpaLine(line)).toDomain();
  }
}
