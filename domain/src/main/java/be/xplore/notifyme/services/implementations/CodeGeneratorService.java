package be.xplore.notifyme.services.implementations;

import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.keycloak.common.util.RandomString;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CodeGeneratorService {

  /**
   * Generates a random alphanumeric string to use for phone no verification.
   *
   * @return random alphanumeric string. 0-9 & a-z.
   */
  public String generatePhoneVerificationCode() {
    var alphanum = RandomString.alphanum;
    var verificationCode = new RandomString(10, new SecureRandom(), alphanum);
    return verificationCode.nextString();
  }
}
