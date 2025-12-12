package in.codingstreams.ghost_drop.repo;

import in.codingstreams.ghost_drop.model.FileMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class FileMetadataRepoTest {

  @Autowired
  private FileMetadataRepo fileMetadataRepo;

  @Test
  @DisplayName("should save file metadata when valid entity is provided")
  void shouldSaveFileMetadata_WhenValidEntityIsProvided() {
    // Given
    var fileMetadata = FileMetadata.builder()
        .fileName("report.pdf")
        .fileType("application/pdf")
        .storagePath("/files/reports/2025/report.pdf")
        .accessCode("ACCESS123")
        .uploadDate(Timestamp.from(Instant.now()))
        .expiryDate(Timestamp.from(Instant.now().plusSeconds(86400))) // 1 day later
        .maxDownloads(5)
        .build();

    // When
    var saved = fileMetadataRepo.save(fileMetadata);

    // Then
    assertNotNull(saved.getId());
    assertEquals(fileMetadata.getFileName(), saved.getFileName());
    assertEquals(fileMetadata.getMaxDownloads(), saved.getMaxDownloads());
  }

  @Test
  @DisplayName("should return file metadata when searched by access code")
  void shouldReturnFileMetadata_WhenSearchedByAccessCode() {
    // Given
    var fileMetadata = FileMetadata.builder()
        .fileName("report.pdf")
        .fileType("application/pdf")
        .storagePath("/files/reports/2025/report.pdf")
        .accessCode("ACCESS123")
        .uploadDate(Timestamp.from(Instant.now()))
        .expiryDate(Timestamp.from(Instant.now().plusSeconds(86400))) // 1 day later
        .maxDownloads(5)
        .build();

    fileMetadataRepo.save(fileMetadata);

    // When
    var found = fileMetadataRepo.findByAccessCode("ACCESS123");

    // Then
    assertTrue(found.isPresent());
    assertEquals(fileMetadata.getFileName(), found.get().getFileName());
    assertEquals(fileMetadata.getMaxDownloads(), found.get().getMaxDownloads());
  }
}
