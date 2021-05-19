package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaCommunicationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCommunicationPreferenceRepository
    extends JpaRepository<JpaCommunicationPreference, Long> {
}
