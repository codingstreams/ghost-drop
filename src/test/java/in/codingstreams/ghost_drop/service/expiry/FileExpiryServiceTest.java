package in.codingstreams.ghost_drop.service.expiry;

import in.codingstreams.ghost_drop.model.FileMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileExpiryServiceTest {
  @Mock
  private Clock clock;

  @InjectMocks
  private FileExpiryService fileExpiryService;

  @Nested
  @DisplayName("Tests for Core Expiration Logic")
  class CoreLogicTests {
    @BeforeEach
    void setUp() {
      Instant fixedInstant = Instant.parse("2025-01-01T00:00:00Z");
      when(clock.instant()).thenReturn(fixedInstant);
      when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }

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
  }

  @Nested
  @DisplayName("Tests for input validation (no clock usage)")
  class InputValidationTests {

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
}
