package jewoospring.splearn.adapter;

import jewoospring.splearn.domain.member.DuplicateEmailException;
import jewoospring.splearn.domain.member.DuplicateProfileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DuplicateEmailException.class, DuplicateProfileException.class})
    public ProblemDetail handleDuplicateEmailException(RuntimeException e) {
        return getProblemDetail(CONFLICT, e);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        return getProblemDetail(INTERNAL_SERVER_ERROR, e);
    }

    private static ProblemDetail getProblemDetail(HttpStatus httpStatus, Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, e.getMessage());

        problemDetail.setProperty("exception", e.getClass().getSimpleName());
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }
}
