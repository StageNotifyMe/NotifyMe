package be.xplore.notifyme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GeneralExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleControllerException(Exception re) {
    log.error(String.format("And exception of type [%s] was thrown with message: %s",
        re.getClass().getSimpleName(), re.getMessage()));
    return ResponseEntity.badRequest().body(re.getMessage());
  }

  @ExceptionHandler(value = {Exception.class})
  protected Object handleGeneralException(Exception e){
    log.error(String.format("And exception of type [%s] was thrown with message: %s",
        e.getClass().getSimpleName(), e.getMessage()));
    return null;
  }

  @ExceptionHandler(value = {Exception.class})
  protected void handleVoidException(Exception e){
    log.error(String.format("And exception of type [%s] was thrown with message: %s",
        e.getClass().getSimpleName(), e.getMessage()));
  }
}
