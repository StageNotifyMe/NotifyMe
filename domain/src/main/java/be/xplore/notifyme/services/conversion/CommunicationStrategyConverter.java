package be.xplore.notifyme.services.conversion;

import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.IEmailService;
import be.xplore.notifyme.services.communicationstrategies.ISmsService;
import be.xplore.notifyme.services.communicationstrategies.implementations.EmailCommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.implementations.SmsCommunicationStrategy;
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
