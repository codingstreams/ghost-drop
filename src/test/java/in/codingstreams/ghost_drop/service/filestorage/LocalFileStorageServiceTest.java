package in.codingstreams.ghost_drop.service.filestorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class LocalFileStorageServiceTest {
  @Autowired
  private LocalFileStorageService fileStorageService;

  @Value("${file.upload-dir}")
  private String uploadDir;

  private Path uploadDirPath;

  @BeforeEach
  void setUp() throws IOException {
    if (this.uploadDir != null) {
      this.uploadDirPath = Path.of(this.uploadDir);
      Files.createDirectories(this.uploadDirPath);
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    FileSystemUtils.deleteRecursively(uploadDirPath);
  }

  @Test
  @DisplayName("should create upload directory when init() is called")
  void shouldCreateUploadDir_WhenInitIsCalled() {
    fileStorageService.init();
    assertTrue(Files.exists(uploadDirPath));
    assertTrue(Files.isDirectory(uploadDirPath));
  }

  @Test
  @DisplayName("should store file successfully when valid file is provided")
  void shouldStoreFileSuccessfully() throws IOException {
    // given
    MockMultipartFile mockFile = new MockMultipartFile(
        "file", "hello.txt", "text/plain", "Hello World".getBytes()
    );

    // when
    String storedFileName = fileStorageService.store(mockFile);

    // then
    Path storedPath = uploadDirPath.resolve(storedFileName);

    assertThat(storedPath).exists();
    assertThat(Files.readString(storedPath)).isEqualTo("Hello World");
  }

  @Test
  @DisplayName("should throw exception when empty file is provided")
  void shouldThrowException_WhenEmptyFileIsProvided() throws IOException {
    // given
    MockMultipartFile mockFile = new MockMultipartFile(
        "file", "hello.txt", "text/plain", new byte[]{}
    );

    assertThrowsExactly(RuntimeException.class, () -> fileStorageService.store(mockFile));
  }

  @Test
  @DisplayName("should throw exception when invalid file path is provided")
  void shouldThrowException_WhenInvalidFilePathIsProvided() throws IOException {
    // given
    MockMultipartFile mockFile = new MockMultipartFile(
        "file", ".././hello.txt", "text/plain", new byte[]{}
    );

    assertThrowsExactly(RuntimeException.class, () -> fileStorageService.store(mockFile));
  }

  @Test
  @DisplayName("should throw exception when unable to store file")
  void shouldThrowException_WhenUnableToStoreFile() throws IOException {
    // given
    MultipartFile mockFile = Mockito.mock(MultipartFile.class);
    Mockito.when(mockFile.getOriginalFilename()).thenReturn("hello.txt");
    Mockito.when(mockFile.isEmpty()).thenReturn(false);
    Mockito.when(mockFile.getInputStream()).thenThrow(new IOException("No space left on device"));

    assertThrowsExactly(RuntimeException.class, () -> fileStorageService.store(mockFile));
  }

}
