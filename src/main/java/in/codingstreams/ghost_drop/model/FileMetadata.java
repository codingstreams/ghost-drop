package in.codingstreams.ghost_drop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String fileType;

  @Column(nullable = false)
  private String storagePath;

  @Column(nullable = false, unique = true)
  private String accessCode;

  @Builder.Default
  @Column(nullable = false, updatable = false)
  private Timestamp uploadDate = new Timestamp(System.currentTimeMillis());

  @Column(nullable = false)
  private Timestamp expiryDate;

  @Builder.Default
  @Column(nullable = false)
  private Integer maxDownloads = -1;

  public boolean isConsumed() {
    return maxDownloads <= 0;
  }
}
