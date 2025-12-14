package in.codingstreams.ghost_drop.scheduler;

import in.codingstreams.ghost_drop.config.FileStorageProperties;
import in.codingstreams.ghost_drop.model.FileMetadata;
import in.codingstreams.ghost_drop.repo.FileMetadataRepo;
import in.codingstreams.ghost_drop.service.filestorage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileCleanupScheduler {
  private final FileMetadataRepo fileMetadataRepo;
  private final FileStorageProperties fileStorageProperties;
  private final FileStorageService fileStorageService;
  private final Clock clock;

  @Scheduled(cron = "0 * * * * *") // every minute
  public void cleanupConsumedOrExpiredFiles() {
    var expiredOrConsumedFiles = fileMetadataRepo
        .findByMaxDownloadsEqualsOrExpiryDateBefore(0, Timestamp.valueOf(LocalDateTime.now(clock)));

    if (expiredOrConsumedFiles.isEmpty()) {
      log.debug("No consumed or expired files found for cleanup.");
      return;
    }

    for (FileMetadata file : expiredOrConsumedFiles) {
      var filePath = Path.of(fileStorageProperties.getUploadDir(), file.getStoragePath());
      try {
        boolean deleted = fileStorageService.delete(filePath);

        if (deleted) {
          log.info("Deleted file '{}' at path {}", file.getFileName(), filePath);
        } else {
          log.warn("File '{}' not found at path {}", file.getFileName(), filePath);
        }

        fileMetadataRepo.delete(file);
        log.debug("Deleted metadata record for file '{}'", file.getFileName());

      } catch (Exception e) {
        log.error("Failed to delete file '{}' at path {}", file.getFileName(), filePath, e);
      }
    }
  }
}

