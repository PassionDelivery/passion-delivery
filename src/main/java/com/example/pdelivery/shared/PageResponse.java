package junwoo.example.shared;

import org.springframework.data.domain.Slice;

import java.util.List;

public record PageResponse<T>(
        List<T> contents,
        boolean hasNext
) {
    public static <T> PageResponse<T> of(Slice<T> data) {
        return new PageResponse<>(data.getContent(), data.hasNext());
    }
}
