package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLineRepository extends JpaRepository<JpaLine, Long> {
}
