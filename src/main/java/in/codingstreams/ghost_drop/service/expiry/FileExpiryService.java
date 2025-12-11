package in.codingstreams.ghost_drop.service.expiry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileExpiryService implements ExpiryService {
  private final Clock clock;

  @Override
  public boolean isExpired(Timestamp expiryTime) {
    var timestamp = Optional.ofNullable(expiryTime)
        .orElseThrow(() -> new RuntimeException("expiryTime is Null."));

    return LocalDateTime.now(clock).isAfter(timestamp.toLocalDateTime());
  }
}
