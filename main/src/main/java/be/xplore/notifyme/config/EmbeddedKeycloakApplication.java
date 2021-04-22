package be.xplore.notifyme.config;

import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

  private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

  static KeycloakServerProperties keycloakServerProperties;

  protected void loadConfig() {
    JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
    Config.init(factory.create()
        .orElseThrow(() -> new NoSuchElementException("No value present")));
  }

  /**
   * Creates admin-user and realm.
   */
  public EmbeddedKeycloakApplication() {

    super();

    createMasterRealmAdminUser();

    createNotifymeRealm();
  }

  private void createMasterRealmAdminUser() {

    @Getter
    @Setter
    class AdminUser{
      String username;
      String password;

      public AdminUser(String username, String password) {
        this.username = username;
        this.password = password;
      }
    }

    KeycloakSession session = getSessionFactory().create();

    ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);

    AdminUser admin = new AdminUser(keycloakServerProperties.getAdminUsername(), keycloakServerProperties.getAdminPassword());

    try {
      session.getTransaction().begin();
      applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
      session.getTransaction().commit();
    } catch (Exception ex) {
      LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
      session.getTransaction().rollback();
    }

    session.close();
  }

  private void createNotifymeRealm() {
    KeycloakSession session = getSessionFactory().create();

    try {
      session.getTransaction().begin();

      RealmManager manager = new RealmManager(session);
      Resource lessonRealmImportFile =
          new ClassPathResource(keycloakServerProperties.getRealmImportFile());

      manager.importRealm(
          JsonSerialization
              .readValue(lessonRealmImportFile.getInputStream(), RealmRepresentation.class));

      session.getTransaction().commit();
    } catch (Exception ex) {
      LOG.warn("Failed to import Realm json file: {}", ex.getMessage());
      session.getTransaction().rollback();
    }

    session.close();
  }
}
