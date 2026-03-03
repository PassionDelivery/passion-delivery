package junwoo.example.shared;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
public class BaseEntityOnlyCreated extends AbstractEntity {
    private LocalDateTime createdAt;
    private UUID createdBy;
}
