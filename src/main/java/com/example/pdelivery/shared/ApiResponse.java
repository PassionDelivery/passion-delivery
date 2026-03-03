package junwoo.example.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
        T data
) {
    public static <T> ResponseEntity<ApiResponse<T>> of(T data, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return of(data, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> create(T data) {
        return of(data, HttpStatus.CREATED);
    }

}
