package be.xplore.notifyme.jpaobjects;

import be.xplore.notifyme.domain.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.IEmailService;
import be.xplore.notifyme.domain.communicationstrategies.ISmsService;
import be.xplore.notifyme.domain.communicationstrategies.SmsCommunicationStrategy;
import javax.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommunicationStrategyConverter
    implements AttributeConverter<ICommunicationStrategy, String> {
  private final IEmailService emailService;
  private final ISmsService smsService;

  @Override
  public String convertToDatabaseColumn(ICommunicationStrategy attribute) {
    return attribute.getClass().getSimpleName().toLowerCase();
  }

  @Override
  public ICommunicationStrategy convertToEntityAttribute(String dbData) {
    if (dbData.equals("emailcommunicationstrategy")) {
      return new EmailCommunicationStrategy(emailService);
    } else if (dbData.equals("smscommunicationstrategy")) {
      return new SmsCommunicationStrategy(smsService);
    }
    return null;
  }
}