package in.codingstreams.ghost_drop;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GhostDropApplication {

	public static void main(String[] args) {
		SpringApplication.run(GhostDropApplication.class, args);
	}

}
