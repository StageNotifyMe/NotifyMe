package be.xplore.notifyme.persistence;

import be.xplore.notifyme.domain.User;
import be.xplore.notifyme.services.systemmessages.AvailableLanguages;
import java.util.List;
import java.util.Optional;

public interface IUserRepo {
  User save(User user);

  Optional<User> findById(String userId);

  List<User> findAll();

  User findByIdIncOrganisations(String userId);

  User findByIdIncAppliedUsers(String userId);

  AvailableLanguages getUserPreferedLanguage(String userId);
}
