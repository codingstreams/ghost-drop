package in.codingstreams.ghost_drop.repo;

import in.codingstreams.ghost_drop.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepo extends JpaRepository<FileMetadata, UUID> {
  Optional<FileMetadata> findByAccessCode(String accessCode);
  boolean existsByAccessCode(String accessCode);

  List<FileMetadata> findByMaxDownloadsEqualsOrExpiryDateBefore(Integer maxDownloads, Timestamp now);
}
