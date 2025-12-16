package in.codingstreams.ghost_drop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error-docs")
@Getter
@Setter
public class ErrorDocsProperties {
  private String url;
}
