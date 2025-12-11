package in.codingstreams.ghost_drop.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.time.*;

@TestConfiguration
class TestClockConfig {
  @Bean
  public Clock testClock() {
    LocalDateTime fixedNow = LocalDateTime.of(2025, Month.DECEMBER, 11, 10, 52, 32);

    return Clock.fixed(fixedNow.atZone(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault());
  }
}