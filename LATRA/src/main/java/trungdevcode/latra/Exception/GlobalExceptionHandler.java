package trungdevcode.latra.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        // Nhét đúng cái câu chửi (Ví dụ: "Giảm theo % không được vượt quá 100%!") vào biến message
        errorResponse.put("message", ex.getMessage());

        // Trả về mã lỗi 400 (Bad Request - Lỗi do người dùng nhập sai)
        // Khi dùng cách này, Spring Boot sẽ KHÔNG in đống log đỏ lòm ra màn hình Console nữa!
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
