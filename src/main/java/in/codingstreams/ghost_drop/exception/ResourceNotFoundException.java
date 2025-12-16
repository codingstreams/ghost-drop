package in.codingstreams.ghost_drop.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String path) {
    super("File not found at path: " + path);
  }
}
