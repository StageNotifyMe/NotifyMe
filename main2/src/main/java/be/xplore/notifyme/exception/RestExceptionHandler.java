package be.xplore.notifyme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

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


}
