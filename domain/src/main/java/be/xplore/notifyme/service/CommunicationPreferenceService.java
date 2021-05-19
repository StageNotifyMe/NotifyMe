package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.SmsCommunicationStrategy;
import be.xplore.notifyme.persistence.ICommunicationPreferenceRepo;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunicationPreferenceService {
  private final ICommunicationPreferenceRepo communicationPreferenceRepo;

  public CommunicationPreference createCommunicationPreference(String userId, boolean isActive,
                                                               boolean isDefault, String strategy) {
    var strategyImpl = getStrategyImplementation(strategy);
    return communicationPreferenceRepo.create(userId, isActive, isDefault, strategyImpl);
  }

  public CommunicationPreference getCommunicationPreference(long communicationPreferenceId) {
    return communicationPreferenceRepo.findById(communicationPreferenceId);
  }

  public CommunicationPreference updateCommunicationPreference(long communicationPreferenceId,
                                                               boolean isActive) {
    var communicationPreference = communicationPreferenceRepo.findById(communicationPreferenceId);
    if (!communicationPreference.isDefault()) {
      communicationPreference.setActive(isActive);
      return communicationPreferenceRepo.save(communicationPreference);
    } else {
      throw new ValidationException("Cannot change default communication method");
    }
  }

  public void deleteCommunicationPreference(long communicationPreferenceId) {
    communicationPreferenceRepo.delete(communicationPreferenceId);
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
