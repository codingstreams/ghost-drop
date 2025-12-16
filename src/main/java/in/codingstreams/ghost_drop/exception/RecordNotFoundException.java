package in.codingstreams.ghost_drop.exception;

public class RecordNotFoundException extends RuntimeException {
  public RecordNotFoundException(String accessCode) {
    super("No matching found file for access code: " + accessCode);
  }
}
