package be.xplore.notifyme.services;

import be.xplore.notifyme.domain.CommunicationPreference;
import java.util.List;

public interface ICommunicationPreferenceService {
  CommunicationPreference createCommunicationPreference(String userId, boolean isActive,
                                                        boolean isDefault, String strategy);

  CommunicationPreference getCommunicationPreference(long communicationPreferenceId);

  CommunicationPreference updateCommunicationPreference(long communicationPreferenceId,
                                                        boolean isActive, boolean isDefault);

  void deleteCommunicationPreference(long communicationPreferenceId);

  List<CommunicationPreference> getAllCommunicationPreferencesForUser(String userId);
}
