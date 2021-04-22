package be.xplore.notifyme.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "keycloak.server")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakServerProperties {
  @Value("${keycloak.server.contextPath}")
  String contextPath;
  @Value("${keycloak.server.realmImportFile}")
  String realmImportFile;
  @Value("${keycloak.server.adminUsername}")
  String adminUsername;
  @Value("${keycloak.server.adminPassword}")
  String adminPassword;
}
