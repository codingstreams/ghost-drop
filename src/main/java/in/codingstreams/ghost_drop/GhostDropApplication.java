package in.codingstreams.ghost_drop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GhostDropApplication {

  static void main(String[] args) {
    SpringApplication.run(GhostDropApplication.class, args);
  }

}
