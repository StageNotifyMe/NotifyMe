package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaCommunicationPreference;
import be.xplore.notifyme.jpaobjects.JpaUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCommunicationPreferenceRepository
    extends JpaRepository<JpaCommunicationPreference, Long> {
  List<JpaCommunicationPreference> findAllByUser(JpaUser jpaUser);
}
