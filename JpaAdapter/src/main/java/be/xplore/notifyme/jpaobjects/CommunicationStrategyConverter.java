package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.SmsCommunicationStrategy;
import javax.persistence.AttributeConverter;

public class CommunicationStrategyConverter
    implements AttributeConverter<ICommunicationStrategy, String> {
  @Override
  public String convertToDatabaseColumn(ICommunicationStrategy attribute) {
    return attribute.getClass().getSimpleName().toLowerCase();
  }

  @Override
  public ICommunicationStrategy convertToEntityAttribute(String dbData) {
    if (dbData.equals("emailcommunicationstrategy")) {
      return new EmailCommunicationStrategy();
    } else if (dbData.equals("smscommunicationstrategy")) {
      return new SmsCommunicationStrategy();
    }
    return null;
  }
}
