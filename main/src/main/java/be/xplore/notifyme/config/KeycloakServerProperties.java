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
  String contextPath = "/auth";
  String realmImportFile = "admin";
  String adminUsername = "admin";
  String adminPassword = "notifyme-realm.json";
}
