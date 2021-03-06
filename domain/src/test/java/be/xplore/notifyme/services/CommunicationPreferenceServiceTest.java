package be.xplore.notifyme.services;

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
import be.xplore.notifyme.persistence.ICommunicationPreferenceRepo;
import be.xplore.notifyme.services.communicationstrategies.ICommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.implementations.EmailCommunicationStrategy;
import be.xplore.notifyme.services.communicationstrategies.implementations.SmsCommunicationStrategy;
import be.xplore.notifyme.services.implementations.CommunicationPreferenceService;
import be.xplore.notifyme.services.implementations.KeycloakCommunicationService;
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
  @MockBean
  KeycloakCommunicationService keycloakCommunicationService;
  private CommunicationPreference mockComPref = mock(CommunicationPreference.class);

  @Test
  void createCommunicationPreference() {
    when(communicationPreferenceRepo
        .create(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), any()))
        .thenReturn(mockComPref);

    assertEquals(mockComPref, communicationPreferenceService
        .createCommunicationPreference("userId", true, true, false, "emailcommunicationstrategy"));
  }

  @Test
  void getCommunicationPreference() {
    when(communicationPreferenceRepo.findById(anyLong())).thenReturn(mockComPref);

    assertEquals(mockComPref, communicationPreferenceService.getCommunicationPreference(1L));
  }

  @Test
  void updateCommunicationPreference() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, false, false,
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
    var result = communicationPreferenceService
        .updateCommunicationPreference(1L, false, false, false);
    assertEquals(1L, result.getId());
    assertFalse(result.isActive());
  }

  @Test
  void updateCommunicationPreferenceIsDefault() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, true, true,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);

    assertThrows(ValidationException.class, () -> {
      communicationPreferenceService.updateCommunicationPreference(1L, false, false, false);
    });
    assertThrows(ValidationException.class, () -> {
      communicationPreferenceService.updateCommunicationPreference(1L, false, true, false);
    });
  }

  @Test
  void updateCommunicationPreferenceNewDefault() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, true, false,
        new EmailCommunicationStrategy(null));
    CommunicationPreference comPrefB = new CommunicationPreference(2L, mockUser, false, false,
        false,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);
    when(communicationPreferenceRepo.findById(2L)).thenReturn(comPrefB);
    this.mockMakeNewDefault();
    var newDefaultResult =
        communicationPreferenceService.updateCommunicationPreference(2L, true, true, false);
    assertEquals(2L, newDefaultResult.getId());
    assertTrue(newDefaultResult.isActive());
    assertTrue(newDefaultResult.isDefault());

  }

  @Test
  void updateCommunicationPreferenceNewUrgent() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, false, true,
        new EmailCommunicationStrategy(null));
    CommunicationPreference comPrefB = new CommunicationPreference(2L, mockUser, false, false,
        false,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);
    when(communicationPreferenceRepo.findById(2L)).thenReturn(comPrefB);
    this.mockMakeNewUrgent();
    var newUrgentResult =
        communicationPreferenceService.updateCommunicationPreference(2L, true, true, true);
    assertEquals(2L, newUrgentResult.getId());
    assertTrue(newUrgentResult.isActive());
    assertTrue(newUrgentResult.isUrgent());
  }

  @Test
  void updateCommunicationPreferenceNewUrgentNotDefault() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, false, true,
        new EmailCommunicationStrategy(null));
    CommunicationPreference comPrefB = new CommunicationPreference(2L, mockUser, false, false,
        false,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);
    when(communicationPreferenceRepo.findById(2L)).thenReturn(comPrefB);
    this.mockMakeNewUrgent();
    var newUrgentResult =
        communicationPreferenceService.updateCommunicationPreference(2L, true, false, true);
    assertEquals(2L, newUrgentResult.getId());
    assertTrue(newUrgentResult.isActive());
    assertTrue(newUrgentResult.isUrgent());
  }

  @Test
  void updateCommunicationPreferenceActive() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, false, false, false,
        new EmailCommunicationStrategy(null));
    CommunicationPreference comPrefSaved = new CommunicationPreference(1L, mockUser, true, false,
        false,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);
    when(communicationPreferenceRepo.save(any())).thenReturn(comPrefSaved);
    this.mockMakeNewUrgent();
    var newActiveResult =
        communicationPreferenceService.updateCommunicationPreference(1L, true, false, false);
    assertTrue(newActiveResult.isActive());
  }

  @Test
  void updateCommunicationPreferenceNewDefaultChecksFail() {
    var mockUser = mock(User.class);
    CommunicationPreference comPref = new CommunicationPreference(1L, mockUser, true, false, false,
        new EmailCommunicationStrategy(null));
    CommunicationPreference comPrefB = new CommunicationPreference(2L, mockUser, false, true, false,
        new EmailCommunicationStrategy(null));
    when(communicationPreferenceRepo.findById(1L)).thenReturn(comPref);
    when(communicationPreferenceRepo.findById(2L)).thenReturn(comPrefB);
    this.mockMakeNewDefault();
    assertThrows(ValidationException.class,
        () -> communicationPreferenceService.updateCommunicationPreference(2L, true, true, false));

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
                    given.isDefault(), given.isUrgent(),
                    given.getCommunicationStrategy());
            comPref.setDefault(true);
            comPref.setActive(true);
            return comPref;
          }
        });
  }

  private void mockMakeNewUrgent() {
    when(communicationPreferenceRepo.makeNewUrgent(any()))
        .thenAnswer(new Answer<CommunicationPreference>() {
          @Override
          public CommunicationPreference answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            var given = (CommunicationPreference) args[0];
            var comPref =
                new CommunicationPreference(given.getId(), given.getUser(), given.isActive(),
                    given.isDefault(), given.isUrgent(),
                    given.getCommunicationStrategy());
            comPref.setUrgent(true);
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
    when(communicationPreferenceRepo
        .create(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), any()))
        .thenAnswer(new Answer<CommunicationPreference>() {
          @Override
          public CommunicationPreference answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            CommunicationPreference comPrefObj = new CommunicationPreference();
            comPrefObj.setId(1L);
            comPrefObj.setUser(mock(User.class));
            comPrefObj.setDefault((boolean) args[2]);
            comPrefObj.setActive((boolean) args[1]);
            comPrefObj.setCommunicationStrategy((ICommunicationStrategy) args[4]);
            return comPrefObj;
          }
        });

    assertEquals(EmailCommunicationStrategy.class, communicationPreferenceService
        .createCommunicationPreference("userId", true, true, false, "emailcommunicationstrategy")
        .getCommunicationStrategy().getClass());
    assertEquals(SmsCommunicationStrategy.class, communicationPreferenceService
        .createCommunicationPreference("userId", true, true, false, "smscommunicationstrategy")
        .getCommunicationStrategy().getClass());
    assertNull(communicationPreferenceService
        .createCommunicationPreference("userId", true, true, false, "invalid")
        .getCommunicationStrategy());
  }
}