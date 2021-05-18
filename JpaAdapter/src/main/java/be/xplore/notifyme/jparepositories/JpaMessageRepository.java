package be.xplore.notifyme.jparepositories;

import be.xplore.notifyme.jpaobjects.JpaMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMessageRepository extends JpaRepository<JpaMessage, Long> {
}
