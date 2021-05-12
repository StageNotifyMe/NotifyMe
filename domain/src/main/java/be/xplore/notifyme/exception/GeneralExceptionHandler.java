package be.xplore.notifyme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class GeneralExceptionHandler {

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleControllerException(Exception re) {
    log.error(String.format("And exception of type [%s] was thrown with message: %s",
        re.getClass().getSimpleName(), re.getMessage()));
    return ResponseEntity.badRequest().body(re.getMessage());
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  protected ResponseEntity<Object> handleAccessDeniedException(Exception re) {
    log.error(String.format("ACCESS DENIED %s %s",
        re.getClass().getSimpleName(), re.getMessage()));
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(re);
  }

  @ExceptionHandler(value = {CrudException.class})
  protected ResponseEntity<Object> handleCrudException(RuntimeException ex) {
    log.error(ex.getMessage());
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(value = {NullPointerException.class})
  protected ResponseEntity<Object> handleNpeException(RuntimeException ex) {
    log.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Server could not handle request correctly.");
  }

  @ExceptionHandler(value = {OrgApplicationNotFoundException.class})
  protected ResponseEntity<Object> handleApplicationNotFoundException(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("The requested organisation application does not exist.");
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = {SaveToDatabaseException.class})
  protected ResponseEntity<Object> handleConversionException(RuntimeException re) {
    log.error(re.getMessage());
    return ResponseEntity.badRequest().body(re.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = {TokenHandlerException.class})
  protected ResponseEntity<Object> handleTokenConversionException(RuntimeException re) {
    log.error(re.getMessage());
    return ResponseEntity.badRequest().body(re.getMessage());
  }
}
