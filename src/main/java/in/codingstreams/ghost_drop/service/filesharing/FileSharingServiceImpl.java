package in.codingstreams.ghost_drop.service.filesharing;

import in.codingstreams.ghost_drop.config.AppConfigProperties;
import in.codingstreams.ghost_drop.config.FileStorageProperties;
import in.codingstreams.ghost_drop.dto.FileDownloadWrapper;
import in.codingstreams.ghost_drop.dto.FileUploadResponse;
import in.codingstreams.ghost_drop.model.FileMetadata;
import in.codingstreams.ghost_drop.repo.FileMetadataRepo;
import in.codingstreams.ghost_drop.service.filestorage.FileStorageService;
import in.codingstreams.ghost_drop.util.FileAccessCodeUtils;
import lombok.RequiredArgsConstructor;
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
  private final FileStorageService fileStorageService;
  private final FileMetadataRepo fileMetadataRepo;
  private final FileStorageProperties fileStorageProperties;
  private final AppConfigProperties appConfigProperties;
  private final Clock clock;

  @Override
  public FileUploadResponse uploadFile(MultipartFile file) {
    var fileName = fileStorageService.store(file);
    var storagePath = Path.of(this.fileStorageProperties.getUploadDir(), fileName).toString();
    var accessCode = FileAccessCodeUtils.generateAccessCode();

    var downloadUrl = UriComponentsBuilder.fromPath(appConfigProperties.getBaseUrl())
        .path("/download/")
        .path(accessCode)
        .toUriString();

    var expiryDate = Timestamp.valueOf(LocalDateTime.now(clock).plusDays(1));

    var fileMetadata = FileMetadata.builder()
        .fileName(file.getOriginalFilename())
        .fileType(file.getContentType())
        .expiryDate(expiryDate)
        .storagePath(storagePath)
        .storagePath(fileName)
        .accessCode(accessCode)
        .maxDownloads(MAX_DOWNLOADS)
        .build();

    var saved = fileMetadataRepo.save(fileMetadata);

    return new FileUploadResponse(
        accessCode,
        fileName,
        saved.getExpiryDate().toLocalDateTime(),
        downloadUrl
    );
  }

  @Override
  public FileDownloadWrapper getFile(String accessCode) {
    return null;
  }

  @Override
  public FileUploadResponse getFileInfo(String accessCode) {
    return null;
  }
}
