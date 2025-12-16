package in.codingstreams.ghost_drop.exception;

public class FileExpiredException extends RuntimeException {
  public FileExpiredException(String accessCode) {
    super("File is expired for access code: " + accessCode + ". File will be deleted shortly.");
  }
}
