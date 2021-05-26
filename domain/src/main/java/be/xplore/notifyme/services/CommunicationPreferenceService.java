package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.persistence.ICommunicationPreferenceRepo;
import be.xplore.notifyme.services.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.SmsCommunicationStrategy;
import java.util.List;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunicationPreferenceService implements ICommunicationPreferenceService {

  private final ICommunicationPreferenceRepo communicationPreferenceRepo;

  @Override
  public CommunicationPreference createCommunicationPreference(String userId, boolean isActive,
      boolean isDefault, boolean isUrgent, String strategy) {
    var strategyImpl = getStrategyImplementation(strategy);
    return communicationPreferenceRepo.create(userId, isActive, isDefault, isUrgent, strategyImpl);
  }

  @Override
  public CommunicationPreference getCommunicationPreference(long communicationPreferenceId) {
    return communicationPreferenceRepo.findById(communicationPreferenceId);
  }

  @Override
  public CommunicationPreference updateCommunicationPreference(long communicationPreferenceId,
      boolean isActive,
      boolean isDefault, boolean isUrgent) {
    var communicationPreference = communicationPreferenceRepo.findById(communicationPreferenceId);
    if (!communicationPreference.isDefault() && !communicationPreference.isUrgent() && !isDefault && !isUrgent) {
      //toggle active state
      communicationPreference.setActive(isActive);
      return communicationPreferenceRepo.save(communicationPreference);
    } else if (!communicationPreference.isDefault() && isDefault && !isUrgent) {
      //make new default
      return communicationPreferenceRepo.makeNewdefault(communicationPreference);
    } else if (isUrgent){
      //make new urgent
      return communicationPreferenceRepo.makeNewUrgent(communicationPreference);
    } else

      {
      throw new ValidationException("Cannot change default communication method");
    }
  }

  @Override
  public void deleteCommunicationPreference(long communicationPreferenceId) {
    communicationPreferenceRepo.delete(communicationPreferenceId);
  }

  public List<CommunicationPreference> getAllCommunicationPreferencesForUser(String userId) {
    return communicationPreferenceRepo.getAllForUser(userId);
  }

  private ICommunicationStrategy getStrategyImplementation(String strategy) {
    ICommunicationStrategy strategyImpl;
    switch (strategy) {
      case "smscommunicationstrategy":
        strategyImpl = new SmsCommunicationStrategy(null);
        break;
      case "emailcommunicationstrategy":
        strategyImpl = new EmailCommunicationStrategy(null);
        break;
      default:
        strategyImpl = null;
    }
    return strategyImpl;
  }
}
