package in.codingstreams.ghost_drop.service.expiry;

import in.codingstreams.ghost_drop.model.FileMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileExpiryServiceTest {
  @Autowired
  private ExpiryService fileExpiryService;

  @Autowired
  private Clock clock;

  @Test
  @DisplayName("should return true when timestamp is past")
  void shouldReturnTrue_WhenTimestampIsPast() {
    var fileMetadata = FileMetadata.builder()
        .expiryDate(Timestamp.valueOf(LocalDateTime.now(clock).minusHours(1)))
        .build();

    assertTrue(fileExpiryService.isExpired(fileMetadata.getExpiryDate()));
  }

  @Test
  @DisplayName("should return false when timestamp is future")
  void shouldReturnFalse_WhenTimestampIsFuture() {
    var fileMetadata = FileMetadata.builder()
        .expiryDate(Timestamp.valueOf(LocalDateTime.now(clock).plusHours(1)))
        .build();

    assertFalse(fileExpiryService.isExpired(fileMetadata.getExpiryDate()));
  }

  @Test
  @DisplayName("should return false when timestamp is same")
  void shouldReturnFalse_WhenTimestampIsSame() {
    var fileMetadata = FileMetadata.builder()
        .expiryDate(Timestamp.valueOf(LocalDateTime.now(clock)))
        .build();

    assertFalse(fileExpiryService.isExpired(fileMetadata.getExpiryDate()));
  }

  @Test
  @DisplayName("should return true when timestamp is exactly one second future")
  void shouldReturnTrue_WhenTimestampIsExactlyOneSecondFuture() {
    var fileMetadata = FileMetadata.builder()
        .expiryDate(Timestamp.valueOf(LocalDateTime.now(clock).minusSeconds(1)))
        .build();

    assertTrue(fileExpiryService.isExpired(fileMetadata.getExpiryDate()));
  }

  @Test
  @DisplayName("should throw RuntimeException when timestamp is null")
  void shouldThrowException_WhenTimestampIsNull() {
    var fileMetadata = FileMetadata.builder()
        .expiryDate(null)
        .build();

    assertThrowsExactly(
        RuntimeException.class,
        () -> fileExpiryService.isExpired(fileMetadata.getExpiryDate())
    );
  }
}
