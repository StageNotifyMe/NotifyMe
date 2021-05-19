package be.xplore.notifyme.service;

import be.xplore.notifyme.domain.CommunicationPreference;

public interface ICommunicationPreferenceService {
  CommunicationPreference createCommunicationPreference(String userId, boolean isActive,
                                                        boolean isDefault, String strategy);

  CommunicationPreference getCommunicationPreference(long communicationPreferenceId);

  CommunicationPreference updateCommunicationPreference(long communicationPreferenceId,
                                                        boolean isActive);

  void deleteCommunicationPreference(long communicationPreferenceId);
}
