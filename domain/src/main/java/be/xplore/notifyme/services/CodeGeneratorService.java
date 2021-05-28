package be.xplore.notifyme.services;

import java.util.Random;
import lombok.RequiredArgsConstructor;
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
    var leftLimit = 48; // numeral '0'
    var rightLimit = 122; //letter z
    var targetStringLength = 10;

    return new Random().ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
