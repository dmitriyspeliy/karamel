package effective_mobile.com.utils.exception;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class CustomExceptionHandler {


    /**
     * 400
     */
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ErrorResponse> badRequestException(BadRequestException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .message(ex.getTextException())
                        .code(ex.getCode())
                        .build());
    }

    /**
     * 404
     */
    @ExceptionHandler(ElementNotFound.class)
    public final ResponseEntity<ErrorResponse> elemNotFound(ElementNotFound ex) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(ErrorResponse.builder()
                        .message(ex.getTextException())
                        .code(ex.getCode())
                        .build());
    }


    /**
     * 405
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public final ResponseEntity<ErrorResponse> methodNotAllowedException(MethodNotAllowedException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .code(ex.getStatusCode().toString())
                        .build());
    }

    /**
     * 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .code(ex.getStatusCode().toString())
                        .build());
    }

    /**
     * 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .code(String.valueOf(BAD_REQUEST.value()))
                        .build());
    }


    /**
     * 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .code(String.valueOf(BAD_REQUEST.value()))
                        .build());
    }

}