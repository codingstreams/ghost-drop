package in.codingstreams.ghost_drop.service.filestorage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService {
  @Value("${file.upload-dir}")
  private String uploadDir;

  @Override
  @PostConstruct
  public void init() {
    log.info("Initializing upload directory...");

    if (uploadDir != null) {
      var uploadDirPath = Path.of(uploadDir);
      log.debug("Upload directory path resolved to: {}", uploadDirPath);

      try {
        Files.createDirectories(uploadDirPath);
        log.info("Upload directory created or already exists: {}", uploadDirPath);
      } catch (IOException e) {
        log.error("Failed to create upload directory: {}", uploadDirPath, e);
        throw new RuntimeException(e);
      }
    } else {
      log.warn("Upload directory is null. Skipping initialization.");
    }
  }

  @Override
  public String store(MultipartFile file) {
    // TODO: Implement actual file saving to uploadDir
    var filePath = StringUtils.cleanPath(file.getName());

    if (StringUtils.hasText("..")) {
      throw new RuntimeException("Invalid file path.");
    }

    var fileName = UUID.randomUUID().toString() + "-" + Path.of(filePath).getFileName();

    return fileName;
  }

  @Override
  public Resource load(String path) {
    return null;
  }

  @Override
  public boolean delete(String path) {
    return false;
  }

  @Override
  public void deleteAll() {

  }
}
