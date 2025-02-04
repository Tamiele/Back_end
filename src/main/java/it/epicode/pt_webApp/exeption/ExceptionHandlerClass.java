package it.epicode.pt_webApp.exeption;



import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerClass extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = EntityNotFoundException.class)

    protected ResponseEntity<Object> entityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>("Error: "+ex.getMessage(), HttpStatus.NOT_FOUND);
    }


}
