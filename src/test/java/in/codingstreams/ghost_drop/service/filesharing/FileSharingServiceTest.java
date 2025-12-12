package in.codingstreams.ghost_drop.service.filesharing;

import in.codingstreams.ghost_drop.config.AppConfigProperties;
import in.codingstreams.ghost_drop.config.FileStorageProperties;
import in.codingstreams.ghost_drop.model.FileMetadata;
import in.codingstreams.ghost_drop.repo.FileMetadataRepo;
import in.codingstreams.ghost_drop.service.filestorage.FileStorageService;
import in.codingstreams.ghost_drop.util.FileAccessCodeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
      MockMultipartFile mockFile = new MockMultipartFile(
          "file", "hello.txt", "text/plain", "Hello World".getBytes()
      );

      var accessCode = FileAccessCodeUtils.generateAccessCode();
      var fileName = accessCode + "-reports.pdf";

      when(fileStorageService.store(mockFile)).thenReturn(fileName);

      var fileMetadata = FileMetadata.builder()
          .fileName(fileName)
          .accessCode(accessCode)
          .expiryDate(Timestamp.valueOf(LocalDateTime.now(this.clock).plusDays(1)))
          .build();

      when(fileMetadataRepo.save(any(FileMetadata.class))).thenReturn(fileMetadata);
      var downloadUrl = UriComponentsBuilder.fromUriString(BASE_URL)
          .path("/download/")
          .path(accessCode)
          .toUriString();

      // When
      var fileUploadResponse = fileSharingService.uploadFile(mockFile);

      // Then
      assertEquals(fileName, fileUploadResponse.fileName());
      assertEquals(downloadUrl, fileUploadResponse.downloadUrl());
    }


  }
}
