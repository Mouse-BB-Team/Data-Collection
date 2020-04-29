package pl.edu.agh.data_collection.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.edu.agh.data_collection.exception.BadCredentialsException;

@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @WarningLog
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Object> handleWarningException(Exception exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}