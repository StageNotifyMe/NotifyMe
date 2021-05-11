package be.xplore.notifyme.jpaRepositories;

import be.xplore.notifyme.jpaObjects.JpaLine;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLineRepository extends JpaRepository<JpaLine, Long> {
}
