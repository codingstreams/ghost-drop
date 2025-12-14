package in.codingstreams.ghost_drop.service.filestorage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
  void init();

  String store(MultipartFile file);

  Resource load(Path path);

  boolean delete(Path path);

  void deleteAll();
}
