package in.codingstreams.ghost_drop.exception;

import in.codingstreams.ghost_drop.config.ErrorDocsProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
  private final ErrorDocsProperties errorDocsProperties;

  @ExceptionHandler
  public ResponseEntity<@NonNull ProblemDetail> exceptionHandler(Exception e) {
    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setDetail("Something went wrong. Please try again.");
    problemDetail.setType(getUri("internal-server-error"));
    problemDetail.setProperty("exception", e.getClass().getSimpleName());
    problemDetail.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
  }

  @ExceptionHandler(InvalidMultipartFileException.class)
  public ProblemDetail handleInvalidMultipartFile(InvalidMultipartFileException ex,
                                                  HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Invalid Multipart File");
    problem.setDetail(ex.getMessage());
    problem.setType(getUri("invalid-multipart-file"));
    problem.setProperty("instance", request.getRequestURI());
    return problem;
  }

  @ExceptionHandler(MaxDownloadsExceededException.class)
  public ProblemDetail handleMaxDownloads(MaxDownloadsExceededException ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
    problem.setTitle("Max downloads exceeded");
    problem.setDetail(ex.getMessage());
    problem.setProperty("instance", request.getRequestURI());
    problem.setType(getUri("max-downloads"));
    return problem;
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problem.setTitle("File not found");
    problem.setDetail(ex.getMessage());
    problem.setProperty("instance", request.getRequestURI());
    problem.setType(getUri("file-not-found"));
    return problem;
  }

  private URI getUri(String path) {
    return UriComponentsBuilder
        .fromUriString(errorDocsProperties.getUrl())
        .pathSegment(path)
        .build()
        .toUri();
  }

}
