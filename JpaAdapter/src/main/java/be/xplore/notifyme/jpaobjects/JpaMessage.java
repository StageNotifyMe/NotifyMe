package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.Message;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JpaMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String title;
  private String text;

  /**
   * Converts jpa-object to domain object with only simple attributes.
   *
   * @return a domain object.
   */
  public Message toDomainBase() {
    return Message.builder()
        .id(this.id)
        .title(this.title)
        .text(this.text)
        .build();
  }

  /**
   * Creates a jpa-version of the domain object.
   *
   * @param message a message object.
   */
  public JpaMessage(Message message) {
    this.id = message.getId();
    this.title = message.getTitle();
    this.text = message.getText();
  }
}
