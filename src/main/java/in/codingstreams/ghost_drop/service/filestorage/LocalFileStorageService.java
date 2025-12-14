package in.codingstreams.ghost_drop.service.filestorage;

import in.codingstreams.ghost_drop.config.FileStorageProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {
  private final FileStorageProperties fileStorageProperties;

  @Override
  @PostConstruct
  public void init() {
    log.info("Initializing upload directory...");

    if (fileStorageProperties.getUploadDir() != null) {
      var uploadDirPath = Path.of(fileStorageProperties.getUploadDir());
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
    log.info("Starting to store file: {}", file.getOriginalFilename());

    var originalFilename = Optional.ofNullable(file.getOriginalFilename())
        .orElseThrow(() -> new RuntimeException("Filename cannot be null."));

    var filePath = StringUtils.cleanPath(Objects.requireNonNull(originalFilename));
    log.debug("Cleaned file path: {}", filePath);

    if (filePath.contains("..")) {
      log.error("Invalid file path detected: {}", filePath);
      throw new RuntimeException("Invalid file path.");
    }

    try {
      if (file.isEmpty()) {
        log.warn("Attempted to store empty file: {}", filePath);
        throw new RuntimeException("Cannot store empty file.");
      }

      var fileInputStream = file.getInputStream();
      var fileName = UUID.randomUUID() + "-" + Path.of(filePath).getFileName();
      log.debug("Generated unique filename: {}", fileName);

      Path destination = Path.of(fileStorageProperties.getUploadDir(), fileName);
      Files.copy(fileInputStream, destination, StandardCopyOption.REPLACE_EXISTING);

      log.info("File successfully stored at: {}", destination.toAbsolutePath());
      return fileName;
    } catch (IOException e) {
      log.error("Failed to store file: {}", filePath, e);
      throw new RuntimeException("File storage failed.", e);
    }
  }

  @Override
  public Resource load(Path path) {
    return new FileSystemResource(path);
  }

  @Override
  public boolean delete(Path path) {
    try {
      return Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAll() {

  }
}
