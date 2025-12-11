package in.codingstreams.ghost_drop.service.filestorage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  void init();

  String store(MultipartFile file);

  Resource load(String path);

  boolean delete(String path);

  void deleteAll();
}
