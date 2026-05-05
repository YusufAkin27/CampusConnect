package user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateStudentNumberException extends RuntimeException {

    public DuplicateStudentNumberException(String studentNumber) {
        super("Student number already in use: " + studentNumber);
    }
}
