package be.xplore.notifyme.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.xplore.notifyme.domain.CommunicationPreference;
import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.domain.communicationstrategies.EmailCommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.domain.communicationstrategies.SmsCommunicationStrategy;
import be.xplore.notifyme.persistence.ICommunicationPreferenceRepo;
import java.util.ArrayList;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {CommunicationPreferenceService.class})
class CommunicationPreferenceServiceTest {
  @Autowired
  CommunicationPreferenceService communicationPreferenceService;
  @MockBean
  ICommunicationPreferenceRepo communicationPreferenceRepo;
  private CommunicationPreference mockComPref = mock(CommunicationPreference.class);

  @Test
  void createCommunicationPreference() {
    when(communicationPreferenceRepo.create(anyString(), anyBoolean(), anyBoolean(), any()))
        .thenReturn(mockComPref);

    assertEquals(mockComPref, communicationPreferenceService
        .createCommunicationPreference("userId", true, true, "emailcommunicationstrategy"));
  }

  @Test
  void getCommunicationPreference() {
    when(communicationPreferenceRepo.findById(anyLong())).thenReturn(mockComPref);

    assertEquals(mockComPref, communicationPreferenceService.getCommunicationPreference(1L));
  }

  @Test
  void updateCommunicationPreference() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, false,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);

    when(communicationPreferenceRepo.save(comPref))
        .thenAnswer(new Answer<CommunicationPreference>() {
          @Override
          public CommunicationPreference answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            return (CommunicationPreference) args[0];
          }
        });
    var result = communicationPreferenceService.updateCommunicationPreference(1L, false, false);
    assertEquals(1L, result.getId());
    assertFalse(result.isActive());
  }

  @Test
  void updateCommunicationPreferenceIsDefault() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, true,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);

    assertThrows(ValidationException.class, () -> {
      communicationPreferenceService.updateCommunicationPreference(1L, false, false);
    });
  }

  @Test
  void updateCommunicationPreferenceNewDefault() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, true,
        new EmailCommunicationStrategy(null));
    CommunicationPreference comPrefB = new CommunicationPreference(2L, mockUser, false, false,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);
    when(communicationPreferenceRepo.findById(2L)).thenReturn(comPrefB);
    this.mockMakeNewDefault();
    var newDefaultResult =
        communicationPreferenceService.updateCommunicationPreference(2L, true, true);
    assertEquals(2L, newDefaultResult.getId());
    assertTrue(newDefaultResult.isActive());
    assertTrue(newDefaultResult.isDefault());

  }

  private void mockMakeNewDefault() {
    when(communicationPreferenceRepo.makeNewdefault(any()))
        .thenAnswer(new Answer<CommunicationPreference>() {
          @Override
          public CommunicationPreference answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            var given = (CommunicationPreference) args[0];
            var comPref =
                new CommunicationPreference(given.getId(), given.getUser(), given.isActive(),
                    given.isDefault(), given.getCommunicationStrategy());
            comPref.setDefault(true);
            comPref.setActive(true);
            return comPref;
          }
        });
  }

  @Test
  void deleteCommunicationPreference() {
    doNothing().when(communicationPreferenceRepo).delete(1L);
    assertDoesNotThrow((() -> {
      communicationPreferenceService.deleteCommunicationPreference(1L);
    }));
  }

  @Test
  void getAllCommunicationPreferencesForUser() {
    var comPrefList = new ArrayList<CommunicationPreference>();
    when(communicationPreferenceRepo.getAllForUser("userId")).thenReturn(comPrefList);

    assertEquals(comPrefList,
        communicationPreferenceService.getAllCommunicationPreferencesForUser("userId"));
  }

  @Test
  void testStrategies() {
    when(communicationPreferenceRepo.create(anyString(), anyBoolean(), anyBoolean(), any()))
        .thenAnswer(new Answer<CommunicationPreference>() {
          @Override
          public CommunicationPreference answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            CommunicationPreference comPrefObj = new CommunicationPreference();
            comPrefObj.setId(1L);
            comPrefObj.setUser(mock(User.class));
            comPrefObj.setDefault((boolean) args[2]);
            comPrefObj.setActive((boolean) args[1]);
            comPrefObj.setCommunicationStrategy((ICommunicationStrategy) args[3]);
            return comPrefObj;
          }
        });

    assertEquals(EmailCommunicationStrategy.class, communicationPreferenceService
        .createCommunicationPreference("userId", true, true, "emailcommunicationstrategy")
        .getCommunicationStrategy().getClass());
    assertEquals(SmsCommunicationStrategy.class, communicationPreferenceService
        .createCommunicationPreference("userId", true, true, "smscommunicationstrategy")
        .getCommunicationStrategy().getClass());
    assertNull(communicationPreferenceService
        .createCommunicationPreference("userId", true, true, "invalid").getCommunicationStrategy());
  }
}