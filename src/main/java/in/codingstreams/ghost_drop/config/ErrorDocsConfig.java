package in.codingstreams.ghost_drop.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ErrorDocsProperties.class)
public class ErrorDocsConfig {
}
