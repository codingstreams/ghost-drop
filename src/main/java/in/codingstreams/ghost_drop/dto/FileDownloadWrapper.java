package in.codingstreams.ghost_drop.dto;

import org.springframework.core.io.Resource;

public record FileDownloadWrapper(
    Resource resource,
    String contentType,
    String originalFileName
) {
}