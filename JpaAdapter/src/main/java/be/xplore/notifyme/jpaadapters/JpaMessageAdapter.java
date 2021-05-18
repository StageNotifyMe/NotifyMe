package be.xplore.notifyme.jpaadapters;

import be.xplore.notifyme.domain.Message;
import be.xplore.notifyme.exception.CrudException;
import be.xplore.notifyme.jpaobjects.JpaMessage;
import be.xplore.notifyme.jparepositories.JpaMessageRepository;
import be.xplore.notifyme.persistence.IMessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaMessageAdapter implements IMessageRepo {
  private final JpaMessageRepository jpaMessageRepository;

  @Override
  public Message save(Message message) {
    return jpaMessageRepository.save(new JpaMessage(message)).toDomainBase();
  }

  @Override
  public Message findById(long messageId) {
    return jpaMessageRepository.findById(messageId)
        .orElseThrow(() -> new CrudException("Could not find message for id " + messageId))
        .toDomainBase();
  }
}
