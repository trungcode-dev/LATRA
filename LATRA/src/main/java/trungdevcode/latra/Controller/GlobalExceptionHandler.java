package trungdevcode.latra.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt lỗi logic truyền sai tham số (Ví dụ status truyền số 2, 3)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", 400, "error", "Bad Request", "message", ex.getMessage()));
    }

    // 2. Bắt lỗi không tìm thấy dữ liệu trong Database (Ví dụ tìm User ID = 999)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status", 404, "error", "Not Found", "message", ex.getMessage()));
    }

    // 3. Bắt lỗi gõ sai định dạng URL (Ví dụ ID bắt nhập số mà truyền chữ "abc")
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", 400, "error", "Type Mismatch", "message", "Tham số '" + ex.getName() + "' không đúng định dạng!"));
    }
}
