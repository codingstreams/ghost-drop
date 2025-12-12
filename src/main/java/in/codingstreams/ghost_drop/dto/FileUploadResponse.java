package in.codingstreams.ghost_drop.dto;

import java.time.LocalDateTime;

public record FileUploadResponse(
    String accessCode,
    String fileName,
    LocalDateTime expiresAt,
    String downloadUrl
) {
}