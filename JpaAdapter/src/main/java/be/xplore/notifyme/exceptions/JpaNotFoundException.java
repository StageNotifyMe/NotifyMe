package be.xplore.notifyme.exceptions;

public class JpaNotFoundException extends RuntimeException {
  public JpaNotFoundException(String message) {
    super(message);
  }
}
