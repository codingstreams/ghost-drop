package in.codingstreams.ghost_drop.service.expiry;

import java.sql.Timestamp;

public interface ExpiryService {
  boolean isExpired(Timestamp expiryTime);
}
