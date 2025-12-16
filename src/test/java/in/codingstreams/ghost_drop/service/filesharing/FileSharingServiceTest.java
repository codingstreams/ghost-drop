package in.codingstreams.ghost_drop.service.filesharing;

import in.codingstreams.ghost_drop.config.AppConfigProperties;
import in.codingstreams.ghost_drop.config.FileStorageProperties;
import in.codingstreams.ghost_drop.model.FileMetadata;
import in.codingstreams.ghost_drop.repo.FileMetadataRepo;
import in.codingstreams.ghost_drop.service.filestorage.FileStorageService;
import in.codingstreams.ghost_drop.util.FileAccessCodeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileSharingServiceTest {
  public static final String BASE_URL = "http://localhost:8080/";
  @Mock
  private FileStorageProperties fileStorageProperties;

  @Mock
  private FileStorageService fileStorageService;

  @Mock
  private AppConfigProperties appConfigProperties;

  @Mock
  private FileMetadataRepo fileMetadataRepo;

  @Mock
  private Clock clock;

  @InjectMocks
  private FileSharingServiceImpl fileSharingService;

  @Nested
  @DisplayName("access code generation test")
  class AccessCodeGenerationTest {
    @Test
    @DisplayName("should return access code when there is no collision")
    void shouldReturnAccessCode_whenNoCollision() {
      try (var mocked = mockStatic(FileAccessCodeUtils.class)) {
        mocked.when(FileAccessCodeUtils::generateAccessCode).thenReturn("XGH-123");

        var accessCode = FileAccessCodeUtils.generateAccessCode();

        when(fileMetadataRepo.existsByAccessCode(accessCode)).thenReturn(false);

        var actual = fileSharingService.generateUniqueAccessCode();

        assertEquals(accessCode, actual);
      }
    }

    @Test
    @DisplayName("should throw IllegalStateException when unable to generate access code after 100 tries")
    void shouldThrowException_whenCollisionFor100Tries() {
      try (var mocked = mockStatic(FileAccessCodeUtils.class)) {
        mocked.when(FileAccessCodeUtils::generateAccessCode).thenReturn("XGH-123");

        var accessCode = FileAccessCodeUtils.generateAccessCode();

        when(fileMetadataRepo.existsByAccessCode(accessCode)).thenReturn(true);

        assertThrowsExactly(IllegalStateException.class,
            () -> fileSharingService.generateUniqueAccessCode());
      }
    }

    @Nested
    @DisplayName("file sharing service core logic test")
    class CoreLogicTests {
      @BeforeEach
      void setUp() {
        when(fileStorageProperties.getUploadDir()).thenReturn("test-uploads/");

        Instant fixedInstant = Instant.parse("2025-01-01T00:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        when(appConfigProperties.getBaseUrl()).thenReturn(BASE_URL);
      }

      @Test
      @DisplayName("should store file when valid file is provided")
      void shouldStoreFile_whenValidFileIsProvided() {
        try (var mocked = mockStatic(FileAccessCodeUtils.class)) {
          mocked.when(FileAccessCodeUtils::generateAccessCode).thenReturn("XGH-123");

          // Given
          var originalFilename = "hello.txt";
          MockMultipartFile mockFile = new MockMultipartFile(
              "file", originalFilename, "text/plain", "Hello World".getBytes()
          );

          var accessCode = FileAccessCodeUtils.generateAccessCode();
          var fileName = accessCode + "-reports.pdf";

          when(fileStorageService.store(mockFile)).thenReturn(fileName);


          var expiryDateTime = LocalDateTime.now(clock).plusDays(1);

          var fileMetadata = FileMetadata.builder()
              .fileName(originalFilename)
              .accessCode(accessCode)
              .expiryDate(Timestamp.valueOf(expiryDateTime))
              .build();

          when(fileMetadataRepo.save(any(FileMetadata.class))).thenReturn(fileMetadata);
          var downloadUrl = UriComponentsBuilder.fromUriString(BASE_URL)
              .path("/download/")
              .path(accessCode)
              .toUriString();

          // When
          var fileUploadResponse = fileSharingService.uploadFile(mockFile);

          // Then
          assertEquals(originalFilename, fileUploadResponse.fileName());
          assertEquals(downloadUrl, fileUploadResponse.downloadUrl());
          assertEquals(accessCode, fileUploadResponse.accessCode());
          assertEquals(expiryDateTime, fileUploadResponse.expiresAt());
        }
      }
    }
  }
}
