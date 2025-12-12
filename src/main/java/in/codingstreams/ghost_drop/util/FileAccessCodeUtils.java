package in.codingstreams.ghost_drop.util;

import java.security.SecureRandom;

public class FileAccessCodeUtils {
  private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String DIGITS = "0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  private FileAccessCodeUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static String generateAccessCode() {
    StringBuilder sb = new StringBuilder(7); // 3 letters + dash + 3 digits

    // Generate 3 random letters
    for (int i = 0; i < 3; i++) {
      sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
    }

    // Add dash
    sb.append('-');

    // Generate 3 random digits
    for (int i = 0; i < 3; i++) {
      sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
    }

    return sb.toString();
  }

}
