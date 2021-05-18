package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface IMessageRepo {
  Message save(Message message);

  Message findById(long messageId);
}
