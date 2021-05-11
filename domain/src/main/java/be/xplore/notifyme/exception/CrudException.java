package be.xplore.notifyme.exception;

import javax.persistence.PersistenceException;

public class CrudException extends PersistenceException {

  public CrudException(String message) {
    super(message);
  }
}
