package in.codingstreams.ghost_drop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  private String fileName;
  private String fileType;
  private String storagePath;
  private String accessCode;
  @CreatedDate
  private Timestamp uploadDate;
  private Timestamp expiryDate;
  private Integer maxDownloads;
}
