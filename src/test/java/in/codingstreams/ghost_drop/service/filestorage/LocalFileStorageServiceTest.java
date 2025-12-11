package in.codingstreams.ghost_drop.service.filestorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
  void setUp() {
    if (this.uploadDir != null) {
      this.uploadDirPath = Path.of(this.uploadDir);
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    FileSystemUtils.deleteRecursively(uploadDirPath);
  }

  @Test
  void shouldCreateUploadDir_WhenInitIsCalled() {
    fileStorageService.init();
    assertTrue(Files.exists(uploadDirPath));
    assertTrue(Files.isDirectory(uploadDirPath));
  }

}
