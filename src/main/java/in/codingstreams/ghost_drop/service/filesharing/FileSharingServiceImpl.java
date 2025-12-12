package in.codingstreams.ghost_drop.service.filesharing;

import in.codingstreams.ghost_drop.config.AppConfigProperties;
import in.codingstreams.ghost_drop.config.FileStorageProperties;
import in.codingstreams.ghost_drop.dto.FileDownloadWrapper;
import in.codingstreams.ghost_drop.dto.FileUploadResponse;
import in.codingstreams.ghost_drop.model.FileMetadata;
import in.codingstreams.ghost_drop.repo.FileMetadataRepo;
import in.codingstreams.ghost_drop.service.expiry.FileExpiryService;
import in.codingstreams.ghost_drop.service.filestorage.FileStorageService;
import in.codingstreams.ghost_drop.util.FileAccessCodeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileSharingServiceImpl implements FileSharingService {
  public static final int MAX_DOWNLOADS = 1;
  public static final int DEFAULT_EXPIRY_DAYS = 1;
  private final FileStorageService fileStorageService;
  private final FileMetadataRepo fileMetadataRepo;
  private final FileStorageProperties fileStorageProperties;
  private final AppConfigProperties appConfigProperties;
  private final Clock clock;
  private final FileExpiryService fileExpiryService;

  @Override
  public FileUploadResponse uploadFile(MultipartFile file) {
    var originalFilename = file.getOriginalFilename();
    var fileType = file.getContentType();
    var fileName = fileStorageService.store(file);
    var storagePath = Path.of(fileStorageProperties.getUploadDir(), fileName).toString();
    var accessCode = generateUniqueAccessCode();
    var downloadUrl = getDownloadUrl(accessCode);
    var expiryDate = Timestamp.valueOf(LocalDateTime.now(clock).plusDays(DEFAULT_EXPIRY_DAYS));

    var fileMetadata = FileMetadata.builder()
        .fileName(originalFilename)
        .fileType(fileType)
        .expiryDate(expiryDate)
        .storagePath(storagePath)
        .accessCode(accessCode)
        .maxDownloads(MAX_DOWNLOADS)
        .build();

    var saved = fileMetadataRepo.save(fileMetadata);

    return new FileUploadResponse(
        accessCode,
        originalFilename,
        saved.getExpiryDate().toLocalDateTime(),
        downloadUrl
    );
  }

  @Override
  @Transactional
  public FileDownloadWrapper getFile(String accessCode) {
    var fileMetadata = getFileMetadata(accessCode);

    // Update maxDownloads
    var maxDownloads = fileMetadata.getMaxDownloads();
    fileMetadata.setMaxDownloads(maxDownloads - 1);
    fileMetadataRepo.save(fileMetadata);

    var filePath = Path.of(fileStorageProperties.getUploadDir(), fileMetadata.getStoragePath());
    var resource = new FileSystemResource(filePath);

    return new FileDownloadWrapper(
        resource,
        fileMetadata.getFileType(),
        fileMetadata.getFileName()
    );
  }

  @Override
  public FileUploadResponse getFileInfo(String accessCode) {
    var fileMetadata = getFileMetadata(accessCode);
    var originalFilename = fileMetadata.getFileName();
    var downloadUrl = getDownloadUrl(accessCode);

    return new FileUploadResponse(
        accessCode,
        originalFilename,
        fileMetadata.getExpiryDate().toLocalDateTime(),
        downloadUrl
    );
  }

  private String generateUniqueAccessCode() {
    int tryCount = 100;

    while (tryCount-- > 0) {
      String accessCode = FileAccessCodeUtils.generateAccessCode();
      if (!fileMetadataRepo.existsByAccessCode(accessCode)) {
        return accessCode;
      }
    }

    throw new IllegalStateException("Unable to generate unique access code after 100 attempts");
  }

  private FileMetadata getFileMetadata(String accessCode) {
    var fileMetadata = fileMetadataRepo.findByAccessCode(accessCode)
        .orElseThrow(() -> new RuntimeException("No matching found file for access code: " + accessCode));

    var expiryDate = fileMetadata.getExpiryDate();
    if (fileExpiryService.isExpired(expiryDate)) {
      throw new RuntimeException("File is expired for access code: " + accessCode + ". File will be deleted shortly.");
    }

    if (fileMetadata.isConsumed()) {
      throw new RuntimeException("Max downloads exhausted for access code: " + accessCode + ". File will be deleted shortly.");
    }
    return fileMetadata;
  }

  private String getDownloadUrl(String accessCode) {
    return UriComponentsBuilder.fromUriString(appConfigProperties.getBaseUrl())
        .path("/download/")
        .path(accessCode)
        .toUriString();
  }
}
