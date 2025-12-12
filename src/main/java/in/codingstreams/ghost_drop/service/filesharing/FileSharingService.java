package in.codingstreams.ghost_drop.service.filesharing;

import in.codingstreams.ghost_drop.dto.FileDownloadWrapper;
import in.codingstreams.ghost_drop.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileSharingService {
  FileUploadResponse uploadFile(MultipartFile file);

  FileDownloadWrapper getFile(String accessCode);

  FileUploadResponse getFileInfo(String accessCode);
}
