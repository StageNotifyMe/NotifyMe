package be.xplore.notifyme.services;

import be.xplore.notifyme.dto.RelevantClientInfoDto;
import be.xplore.notifyme.dto.UserRegistrationDto;
import java.util.List;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface IKeycloakCommunicationService {

  ResponseEntity<String> login(String username, String password);

  void register(UserRegistrationDto userRegistrationDto);

  void sendEmailVerificationRequest(String userId);

  UserRepresentation getUserInfoUsername(String username);

  UserRepresentation getUserInfoId(String userId);

  String getAdminAccesstoken();

  MultiValueMap<String, String> addAuthorization(MultiValueMap<String, String> map);

  String getUserInfoRest(String accessToken, String username);

  String getUserInfoRestById(String accessToken, String userId);

  HttpEntity<String> createJsonHttpEntity(String accessToken);

  HttpEntity<String> createJsonHttpEntity(String accessToken, Object body);

  List<UserRepresentation> getAllUserInfoRest(String accessToken);

  void giveUserRole(String userId, RoleRepresentation roleToGive, String idOfClient);

  List<RoleRepresentation> getClientRoles(String idOfClient);

  RoleRepresentation getClientRole(String roleName, String idOfClient);

  RelevantClientInfoDto getClient(String clientId);

  List<RelevantClientInfoDto> getAllClients();

  void verifyPhoneNo(String username, String code);

  void checkPhoneVerification(String username);

  void updateUserInfo(UserRepresentation userRep, boolean resendEmailVerification,
                      boolean resendPhoneVerification);
}
